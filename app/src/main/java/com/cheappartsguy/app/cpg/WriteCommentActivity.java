package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class WriteCommentActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    TextView txtBoxId;

    EditText txtComments;

    LinearLayout btnBackToCommentThread, btnPostComment;

    public int ScreenCheckSizeIfUsing10Inches;

    public String URL = "";//  "http://api.scrapcatapp.com/api/box_comments_mobile/{token}/{user_uid}/{box_uid}/{box_comments}";

    public String EncodeComments = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("ADD A BOX COMMENT"));
        txtAccount.setText(Config.Worker_name);

        ScreenCheckSizeIfUsing10Inches = Config.getScreenOfTablet(getApplicationContext());

        txtComments = (EditText) findViewById(R.id.txtComments);
        txtBoxId = (TextView) findViewById(R.id.txtBoxId);
        txtBoxId.setText("COMMENTS ON BOX #: " + Config.get_box_id_show);

        btnBackToCommentThread = (LinearLayout) findViewById(R.id.btnBackToCommentThread);
        btnBackToCommentThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CommentBoxThreadActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnPostComment = (LinearLayout) findViewById(R.id.btnPostComment);
        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtComments.getText().toString() == "") {
                    return;
                }

                int resize_screen = 370;
                if (ScreenCheckSizeIfUsing10Inches == 1) {
                    resize_screen = 580;
                } else if (ScreenCheckSizeIfUsing10Inches == 2) {
                    resize_screen = 740;
                }

                //String _URL = Config.Host + "/api/box_comments_mobile/"+ Config.Token +"/" + Config.Worker_uid + "/" + Config.get_box_id; //COMMENT OUT DUE TO CHANGES ON COMMENT SAVING //COMMENT OUT MAY 18, 2018
                URL = Config.Host + "/api/v2/box_comments_mobile";
                // URL = _URL + "/" + txtComments.getText().toString().replace(" ", "%20"); //COMMENT OUT MAY 18, 2018

                String message_ = "Press YES to Save Comment\n";
                message_ += "Press NO to return to Comment Page\n";
                showDialogForDynamic(
                        WriteCommentActivity.this,
                        "SAVE COMMENT",
                        message_, resize_screen,
                        "YES, SAVE COMMENT", "NO, GO BACK", 187);
            }
        });

        // COMMENT OUT MAY 18, 2018 ISSUE ON VIRTUAL KEYBOARD
        /*txtComments.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(txtComments.getText().toString().contains("Enter your comment here...")) {
                        txtComments.setText("");
                    }
                    return true;
                }
                return false;
            }
        });*/
    }


    public class post_comments extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loaderShow("Please wait...");

            try {
                Log.d("String Encode", StringEncoder.Encode(txtComments.getText().toString()));
                EncodeComments = (txtComments.getText().toString()).replace("\n","<br>").replace(" ", "%20");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;

            URL = URL + "?user_id=" + Config.Worker_uid.toString() + "&box_uid=" + Config.get_box_id + "&box_comments=" + EncodeComments;

            try {

                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                json = json_help.makeServiceCall(URL, JSONHelper.GET);
                Log.d("Response: ", "> " + URL);
                Log.d("Response: ", "> " + json);
                Log.i("Server response", json);

                return json;
                //InterruptedException
            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            if (json != "NoInternet") {
                get_comments_list(json);
            }
        }
    }

    private void get_comments_list(String json) {
        if (json != null) {
            try {
                //JSONObject jsonObj = new JSONObject(json); // REMARKS MAY 23, 2018 DUE TO JSON RETURN VALUE IS NUMERIC NO NEED TO CONVERT
                //String Message = jsonObj.getString("Message"); // REMARKS MAY 23, 2018 DUE TO JSON RETURN VALUE IS NUMERIC NO NEED TO CONVERT
                //Log.d("Message: ", Message); // REMARKS MAY 23, 2018 DUE TO JSON RETURN VALUE IS NUMERIC NO NEED TO CONVERT
                int IsPassed = Integer.parseInt(json);

                if (IsPassed == 202) {

                    int resize_screen = 380;

                    if (ScreenCheckSizeIfUsing10Inches == 1) {
                        resize_screen = 580;
                    } else if (ScreenCheckSizeIfUsing10Inches == 2) {
                        resize_screen = 740;
                    }

                    String message_ = "Comment has been successfully saved!\n";

                    showDialogForDynamic(
                            WriteCommentActivity.this,
                            "ADD A BOX COMMENT",
                            message_, resize_screen, 189);
                } else {
                    messageAlert("Your comment did not sent.", "INFORMATION");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loaderHide();
    }

    private void messageAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(WriteCommentActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void showDialogForDynamicError(Activity activity, String caption, String message, final int number) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_custom_dialog_error);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;

        if(width > 0) {
            lp.width = width;
        }*/

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        title_dialog.setText(caption);
        title_message.setText(Html.fromHtml(message));
        screen_code.setText("MW-" + number);

        btn_dialog_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //dialog.getWindow().setAttributes(lp);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.50f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        } else {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.6f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void showDialogForDynamic(Activity activity, String caption, String message, int height, String yes_button, String no_button, final int number) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_logout_dialog_box);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;*/

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);

        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MW-" + number);
        btn_dialog_YES.setText(yes_button);
        btn_dialog_NO.setText(no_button);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Log.d("URL: ", URL);
                    new post_comments().execute();
            }
        });
        btn_dialog_NO.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        //dialog.getWindow().setAttributes(lp);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.50f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        } else {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.6f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void showDialogForDynamic(Activity activity, String caption, String message,
                                     int height, final int number) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_alert_ok);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;*/

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);

        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MW-" + number);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });
        dialog.show();
        //dialog.getWindow().setAttributes(lp);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.50f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        } else {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.6f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}
