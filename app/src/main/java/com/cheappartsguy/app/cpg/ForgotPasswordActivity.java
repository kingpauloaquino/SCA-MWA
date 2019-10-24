package com.cheappartsguy.app.cpg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public EditText email;
    public ConnectivityManager connectivity_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setTitle("ScrapCatApp FORGOT PASSWORD");

        getSupportActionBar().hide();


        email = (EditText) this.findViewById(R.id.email);
        Button btn_submit = (Button) this.findViewById(R.id.email_sign_in_button);
        Button btn_cancel = (Button) this.findViewById(R.id.cancel_in_button);

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectivity_manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                Boolean ethernet = Config.haveNetworkConnection(connectivity_manager);
                if (!ethernet) {
                    // NO INTERNET CONNECTION
                    showDialogForDynamicError(
                            ForgotPasswordActivity.this,
                            "ALERT",
                            "An internet connection is required to login. Make sure that  <font color='#01579B'><b>Wi-fi</b></font> or <font color='#01579B'><b>cellular mobile data</b></font> is turned on, then try again.",
                            "15", 1);
                    return;
                } else {
                    String EdUser = email.getText().toString();

                    if (TextUtils.isEmpty(EdUser)) {
                        showDialogForDynamicError(
                                ForgotPasswordActivity.this,
                                "ERROR",
                                "Sorry, the email you have entered is not in our system. <br />Please try again or ask your Seller Administrator",
                                "09", 1);
                    } else {
                        new forgot_password().execute();
                    }
                }
            }
        });

    }

    public class forgot_password extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            loaderShow("Please wait...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                String User = email.getText().toString();
                String url = Config.Host + Config.Url_forgot_password + Config.Token + "/" + User ;
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            loaderHide();
            forgot_password_validate(json);
        }
    }

    private void forgot_password_validate(String json) {
        if (json != null) {
            try {
                JSONObject job = new JSONObject(json);
                String Status = job.getString("Code");

                int jsonStatus = Integer.parseInt(Status);

                switch (jsonStatus) {
                    case 200: // SUCCESS
                        //txtView_ErrorMsg.setText("A password reset instructions was sent to your email address.");
                        //txtView_ErrorMsg.setTextColor(Color.parseColor("#00FF00"));
                        //txtView_ErrorMsg.setVisibility(View.VISIBLE);

                        showDialogForDynamic(
                                ForgotPasswordActivity.this,
                                "Password reset email sent",
                                "We just send a message to the email you provided with a link to reset your password. <br /> Please check your inbox and follow the instructions in the mail. <br /> If you do not receive the password reset message within a few moments, please check your spam folder or other filtering tools.",
                                0, "08",
                                false);

                        break;
                    case 407: // SENDING EMAIL FAILED
                        //txtView_ErrorMsg.setText("Sending email failed.");
                        //txtView_ErrorMsg.setTextColor(Color.parseColor("#00FF00"));
                        //txtView_ErrorMsg.setVisibility(View.VISIBLE);
                        showDialogForDynamicError(
                                ForgotPasswordActivity.this,
                                "ERROR",
                                "Sorry, this password request could not be sent. Please try again, make sure your email/username is correct.",
                                "09", 1);

                        break;
                    case 404: // NO USERNAME AND EMAIL FOUND
                        //txtView_ErrorMsg.setText("No username or Email found.");
                        //txtView_ErrorMsg.setTextColor(Color.parseColor("#00FF00"));
                        //txtView_ErrorMsg.setVisibility(View.VISIBLE);
                        showDialogForDynamicError(
                                ForgotPasswordActivity.this,
                                "ERROR",
                                "No user can be found for this username/email you provided. please try again.",
                                "09", 1);
                        break;
                    default:
                        break;
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageAlert("There's an error.", "ALERT");
        }
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
        pDialog = new ProgressDialog(ForgotPasswordActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void showDialogForDynamic(Activity activity, String caption, String message, int height, final String number, final boolean IsNothing){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_alert_ok);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);

        title_dialog.setText(caption);
        title_message.setText(Html.fromHtml(message));
        screen_code.setText("MW-" + number);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent i = new Intent(getApplicationContext(), LoginCATPALActivity.class);
                startActivity(i);
                finish();

            }
        });

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

    public void showDialogForDynamicError(Activity activity, String caption, String message, String  number, int button) {

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

        LinearLayout LinearCancel = (LinearLayout) dialog.findViewById(R.id.LinearCancel);
        LinearLayout LinearOptions = (LinearLayout) dialog.findViewById(R.id.LinearOptions);

        Button btn_dialog_DEFAULT = (Button) dialog.findViewById(R.id.btn_dialog_DEFAULT);
        Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        Button btn_dialog_CANCEL = (Button) dialog.findViewById(R.id.btn_dialog_CANCEL);

        switch (button) {
            case 0: // OPTIONS
                if (LinearCancel.getVisibility() == View.VISIBLE) {
                    LinearCancel.setVisibility(View.GONE);
                }

                if (LinearOptions.getVisibility() != View.VISIBLE) {
                    LinearOptions.setVisibility(View.VISIBLE);
                }
                break;
            case 1: // CANCEL

                if (LinearOptions.getVisibility() == View.VISIBLE) {
                    LinearOptions.setVisibility(View.GONE);
                }

                if (LinearCancel.getVisibility() != View.VISIBLE) {
                    LinearCancel.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        //Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        title_dialog.setText(caption);
        title_message.setText(Html.fromHtml(message));
        screen_code.setText("MW-" + number);

        btn_dialog_DEFAULT.setOnClickListener(new View.OnClickListener() {
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

}
