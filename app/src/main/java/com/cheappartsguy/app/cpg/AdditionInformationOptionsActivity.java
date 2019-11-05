package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AdditionInformationOptionsActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    public LinearLayout btnBack, btnAddAdditionalUnit, btnBarCode, btnAddAdditionalPhoto, btnAddComment;

    public SQLiteDatabase sqlDB;
    public static MySqlLite mysql;
    public static String SCAN_RESULT ;

    public static String LatestParentImageName;


    public SQLiteDatabase CreatedDB() {
        sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
        mysql = new MySqlLite(sqlDB);
//        mysql.execute("DROP TABLE IF EXISTS list_of_boxes;");
        return sqlDB;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition_information_options);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("ADD ADDITIONAL INFORMATION"));
        txtAccount.setText(Config.Worker_name);

        CreatedDB();

        ImageViewActivity.AdditionalPhotoDir = false;

        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.IsParentImageName = null;
                Config.totalAdditionalPhoto = 0;
                Config.IsParentImage = true;
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnAddAdditionalUnit = (LinearLayout) findViewById(R.id.btnAddAdditionalUnit);
        btnAddAdditionalUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.IsParentImageName = null;
                Config.totalAdditionalPhoto = 0;
                Config.IsParentImage = true;
                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnBarCode = (LinearLayout) findViewById(R.id.btnBarCode);
        btnBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Config.IsParentImageName ==  null) {
                    showDialogForDynamicError(AdditionInformationOptionsActivity.this, "ERROR ALERT", "No parent image found, please restart the app.");
                    return;
                }

                scanQR(view, "");
            }
        });

        btnAddAdditionalPhoto = (LinearLayout) findViewById(R.id.btnAddAdditionalPhoto);
        btnAddAdditionalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.IsParentImage = false;
                ImageViewActivity.AdditionalPhotoDir = true;
                Config.totalAdditionalPhoto++;

                Log.d("totalAdditionalPhoto", Config.totalAdditionalPhoto + "");
                if(Config.totalAdditionalPhoto > 10) {
                    showDialogForDynamicError(AdditionInformationOptionsActivity.this, "ERROR ALERT", "You are limited to 10 Additional Photos per 1 Unit.");
                    return;
                }

                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnAddComment = (LinearLayout) findViewById(R.id.btnAddComment);
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Config.IsParentImageName ==  null) {
                    showDialogForDynamicError(AdditionInformationOptionsActivity.this, "ERROR ALERT", "No parent image found, please restart the app.");
                    return;
                }

                Intent i = new Intent(getApplicationContext(), AddCommentMessageActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void scanQR(View v, String message) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "MODE"); // Barcode Coding

            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                SCAN_RESULT = intent.getStringExtra("SCAN_RESULT");
                Log.d("PARENT_IMAGE", Config.ImageName);
                Log.d("SCAN_RESULT", SCAN_RESULT);
                Log.d("WORKER_UID", Config.Worker_uid.toString());

                String message_ = "Confirming you want to add this <br />Barcode#: <font color='#F5900F'><b>" + SCAN_RESULT + "</b></font><br /><br />";
                message_ += "Click <font color='#F5900F'><b>YES</b></font> to save the barcode.<br />";
                message_ += "Click <font color='#7A7A7A'><b>NO</b></font> to discard the barcode.<br />";

                showDialogForDynamic(
                        this,
                        "BARCODE SCANNED INFORMATION",
                        message_, 0,
                        "YES, SAVE IT", "NO, DISCARD IT", 103);


            }
        }
    }

    public void save_barcode() {
        String value = "'" + Config.Worker_uid.toString() + "', '" + Config.IsParentImageName + "', '" + SCAN_RESULT + "'";
        Log.d("QUERY_INSERT", value);
        mysql.insert("list_of_barcode", value);

        Cursor c = mysql.select("SELECT * FROM list_of_barcode;");

        while (c.moveToNext()) {
            Log.d("WORKER_UID_SQL", "" + c.getString(0));
            Log.d("PARENT_IMAGE_SQL", "" + c.getString(1));
            Log.d("SCAN_RESULT_SQL", "" + c.getString(2));
        }
    }

    public void showDialogForDynamic(Activity activity, String caption, String message, int height, String yes_button, String no_button, final int number) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_logout_dialog_box);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);

        title_dialog.setText(caption);
        title_message.setText(Html.fromHtml(message));
        btn_dialog_YES.setText(yes_button);
        btn_dialog_NO.setText(no_button);
        screen_code.setText("MW - 119");

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                save_barcode();

                showDialogForDynamicNotification(AdditionInformationOptionsActivity.this, "BARCODE SCANNED INFORMATION", "Barcode was added successful.");
            }
        });
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
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

    public void showDialogForDynamicNotification(Activity activity,
                                     String caption,
                                     String message) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_alert_ok);


        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);

        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MW - 120");

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
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
            int dialogWindowWidth = (int) (displayWidth * 0.7f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void showDialogForDynamicError(Activity activity,
                                          String caption,
                                          String message) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_custom_dialog_error);


        LinearLayout LinearCancel = (LinearLayout) dialog.findViewById(R.id.LinearCancel);
        LinearLayout LinearOptions = (LinearLayout) dialog.findViewById(R.id.LinearOptions);

        Button btn_dialog_DEFAULT = (Button) dialog.findViewById(R.id.btn_dialog_DEFAULT);
        Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        Button btn_dialog_CANCEL = (Button) dialog.findViewById(R.id.btn_dialog_CANCEL);

        btn_dialog_OK.setVisibility(View.GONE);
        btn_dialog_CANCEL.setVisibility(View.GONE);
        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        //Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        title_dialog.setText(caption);
        title_message.setText(message);

        btn_dialog_DEFAULT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

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

    public static void drop_barcode_table() {

        try {
            Cursor c = mysql.select("SELECT * FROM list_of_barcode;");

            if(c.getCount() > 0) {
                mysql.execute("DROP TABLE IF EXISTS list_of_barcode;", true);
            }
        }
        catch (Exception ex) {

        }
    }

}
