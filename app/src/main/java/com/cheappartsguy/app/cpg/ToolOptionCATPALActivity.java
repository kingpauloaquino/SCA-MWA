package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.provider.Settings.Secure;

public class ToolOptionCATPALActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private ProgressDialog pDialog;
    public String box_number = null;
    public View view_;
    public String type_ = null;
    public String message_ = null;

    public SQLiteDatabase sqlDB;
    public MySqlLite mysql;

    public boolean YES_GO = false;

    public int ScreenCheckSizeIfUsing10Inches;

    public int width = 0, height = 370;

    public static ObjectNames notificationSizes;

    public static boolean isQRCode;

    public TelephonyManager telephony_manager;

    public Context app_context;

    public String LIST_BOXES_API_URL = Config.Host + Config.Url_get_all_boxes + Config.Token + "/" + Config.Worker_uid;

    public boolean unit_check_user = false;
    public boolean moduleRequest = false;

    public SQLiteDatabase CreatedDB() {
        sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
        mysql = new MySqlLite(sqlDB);
        mysql.execute("DROP TABLE IF EXISTS list_of_boxes;", true);
        return sqlDB;
    }

    public void InitService() {
        CreatedDB();
        BackgroundService.isStop = false;
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);
        startService(intent);
        Config.JustLogged = false;
    }

    static class ViewHolder {
        TextView actionTitle, actionUsername;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_option_catpal);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("DASHBOARD"));
        txtAccount.setText(Config.Worker_name);

        if (Config.JustLogged) {
            InitService();
        }

        boolean isServiceRunning = BackgroundService.isRunning;
        Log.d("Is Service Running", "" + isServiceRunning);
        if (!isServiceRunning) {
            InitService();
        }

        Config.totalAdditionalPhoto = 0;
        ImageViewActivity.AdditionalPhotoDir = false;
        Config.IsParentImageName = null;

        app_context = getApplicationContext();
        ScreenCheckSizeIfUsing10Inches = Config.getScreenOfTablet(getApplicationContext());

        LinearLayout btnViewBoxInformation = (LinearLayout) findViewById(R.id.btnViewBoxInformation);
        btnViewBoxInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_ = view;
                type_ = "VIEWBOX";

                message_ = "Confirming you would like to VIEW BOX<br />";
                message_ += "Information on a specific box?<br /><br />";

                message_ += "If <font color='#F5900F'><b>YES</b></font>, scan your selected box.<br /><br />";

                message_ += "Position the Box's QR Code completely<br />";
                message_ += "inside the scanner windows.<br /><br />";

                message_ += "When the QR Code is recognized, the<br />";
                message_ += "three yellow dots will turn into green<br />";
                message_ += "squares, then the requested screen will appear.<br />";

                ObjectNames sizes = new ObjectNames();
                sizes.tab8Sizes = new int[]{560, 600, 430, 600};
                sizes.tab10Sizes = new int[]{560, 600, 650, 600};
                sizes.tabS2Sizes = new int[]{1100, 600, 860, 600};

                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                Log.d("SCREEN_XX1", ScreenCheckSizeIfUsing10Inches + "");
                Log.d("SCREEN_XX2", notificationSizes.Height + "");

                showDialogForDynamic(
                        ToolOptionCATPALActivity.this,
                        "VIEW BOX INFORMATION",
                        message_, notificationSizes.Height,
                        "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 131);
            }
        });

        LinearLayout btnAddUnit = (LinearLayout) findViewById(R.id.btnAddUnit);
        btnAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.totalAdditionalPhoto = 0;
                BackgroundService.stopBackgroundWorker = true;
                Config.IsParentImage = true;

                isQRCode = true;
                view_ = view;
                type_ = "ADDUNIT";

                message_ = "Confirming you would like to add units to a<br />";
                message_ += "box that has a Scrap Cap App QR label?<br /><br />";

                message_ += "If <font color='#F5900F'><b>YES</b></font>, scan your selected box.<br /><br />";

                message_ += "Position the Box's QR Code completely<br />";
                message_ += "inside the scanner windows.<br /><br />";

                message_ += "When the QR Code is recognized, the<br />";
                message_ += "three yellow dots will turn into green<br />";
                message_ += "squares, then the requested screen will appear.<br />";

                ObjectNames sizes = new ObjectNames();
                sizes.tab8Sizes = new int[]{560, 600, 430, 600};
                sizes.tab10Sizes = new int[]{560, 600, 650, 600};
                sizes.tabS2Sizes = new int[]{1100, 600, 860, 600};

                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                Log.d("SCREEN_XX1", ScreenCheckSizeIfUsing10Inches + "");
                Log.d("SCREEN_XX2", notificationSizes.Height + "");

                showDialogForDynamic(
                        ToolOptionCATPALActivity.this,
                        "ADD UNITS TO A BOX",
                        message_, notificationSizes.Height,
                        "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 101);
            }
        });

        LinearLayout btnCreateBox = (LinearLayout) findViewById(R.id.btnCreateBox);
        btnCreateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageAlert("This feature can be accessed by pressing ''View Photos of Units in a Box''", "ALERT");
            }
        });

        LinearLayout btnPrintLabel = (LinearLayout) findViewById(R.id.btnPrintLabel);
        btnPrintLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*ObjectNames sizes = new ObjectNames();
                sizes.tab8Sizes = new int[] { 0, 0, 400, 600};
                sizes.tab10Sizes = new int[] { 0, 0, 650, 600};
                sizes.tabS2Sizes = new int[] { 0, 0, 830, 1100};

                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);*/

                message_ = "You cannot PRINT A BOX label from the<br />Mobile Worker App<br /><br />Please use the Web Worker App to Print a Box Label.";

                showDialogForDynamicError(
                        ToolOptionCATPALActivity.this,
                        "PRINT A BOX LABEL",
                        message_, 0, 0, 192, 1);

                //message_, notificationSizes.Width, notificationSizes.Height, 192);

            }
        });

        LinearLayout btnClose = (LinearLayout) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_ = view;
                type_ = "CLOSE";

                message_ = "Confirming you would like to Close a Box<br />";
                message_ += "so that no more units can be added to<br />";
                message_ += "this box?<br /><br />";

                message_ += "If <font color='#F5900F'><b>YES</b></font>, scan your selected box.<br /><br />";

                message_ += "Position the Box's QR Code completely<br />";
                message_ += "inside the scanner windows.<br /><br />";

                message_ += "When the QR Code is recognized, the<br />";
                message_ += "three yellow dots will turn into green<br />";
                message_ += "squares, then the requested screen will appear.<br />";

                ObjectNames sizes = new ObjectNames();
                sizes.tab8Sizes = new int[]{580, 600, 430, 600};
                sizes.tab10Sizes = new int[]{560, 600, 650, 600};
                sizes.tabS2Sizes = new int[]{1150, 600, 860, 600};

                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                Log.d("SCREEN_XX", notificationSizes.Height + "");

                showDialogForDynamic(
                        ToolOptionCATPALActivity.this,
                        "CLOSE A BOX",
                        message_, notificationSizes.Height,
                        "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 151);
            }
        });

        LinearLayout btnLogout = (LinearLayout) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDialogForLogout(ToolOptionCATPALActivity.this);
                showDialogForLogout(
                        ToolOptionCATPALActivity.this,
                        "CONFIRMATION",
                        "Confirming you want to log out?.", 0,
                        "YES, LOGOUT", "NO, BACK TO DASHBOARD", 199);
            }
        });

        LinearLayout btnTransfer = (LinearLayout) findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.transfer_clear(ToolOptionCATPALActivity.this);
                view_ = view;
                type_ = "TRANSFER1";

                message_ = "Confirming you want to transfer all the<br />";
                message_ += "Units from one box to another box?<br /><br />";

                message_ += "If <font color='#F5900F'><b>YES</b></font>, scan the box you will be taking<br />the units out of (Origin Box).<br /><br />";

                message_ += "Position the ORIGIN Box's QR Code completely<br />";
                message_ += "inside the scanner windows.<br /><br />";

                message_ += "When the QR Code is recognized, the<br />";
                message_ += "three yellow dots will turn into green<br />";
                message_ += "squares, then the requested screen will appear.<br />";

                ObjectNames sizes = new ObjectNames();
                sizes.tab8Sizes = new int[]{580, 600, 430, 600};
                sizes.tab10Sizes = new int[]{840, 600, 650, 600};
                sizes.tabS2Sizes = new int[]{1150, 600, 860, 600};

                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                Log.d("SCREEN_XX", notificationSizes.Height + "");

                showDialogForDynamic(
                        ToolOptionCATPALActivity.this,
                        "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                        message_, notificationSizes.Height,
                        "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 141);
            }
        });

        LinearLayout btnCommentOnBox = (LinearLayout) findViewById(R.id.btnCommentOnBox);
        btnCommentOnBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_ = view;
                type_ = "COMMENT";

                message_ = "Confirming you would like to add a<br />";
                message_ += "Comment to a specific box?<br /><br />";

                message_ += "If <font color='#F5900F'><b>YES</b></font>, scan your selected box.<br /><br />";

                message_ += "Position the Box's QR Code completely<br />";
                message_ += "inside the scanner windows.<br /><br />";

                message_ += "When the QR Code is recognized, the<br />";
                message_ += "three yellow dots will turn into green<br />";
                message_ += "squares, then the requested screen will appear.<br />";

                ObjectNames sizes = new ObjectNames();
                sizes.tab8Sizes = new int[]{560, 600, 430, 600};
                sizes.tab10Sizes = new int[]{840, 600, 650, 600};
                sizes.tabS2Sizes = new int[]{1120, 600, 860, 600};

                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                Log.d("SCREEN_XX", notificationSizes.Height + "");

                showDialogForDynamic(
                        ToolOptionCATPALActivity.this,
                        "COMMENT ON A BOX",
                        message_, notificationSizes.Height,
                        "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 184);
            }
        });

        LinearLayout btnViewUnit = (LinearLayout) findViewById(R.id.btnViewUnit);
        btnViewUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_ = view;
                type_ = "VIEWUNIT";

                message_ = "Confirming you would like to view the<br />";
                message_ += "individual photos of units in a box?<br /><br />";

                message_ += "If <font color='#F5900F'><b>YES</b></font>, scan your selected box.<br /><br />";

                message_ += "Position the Box's QR Code completely<br />";
                message_ += "inside the scanner windows.<br /><br />";

                message_ += "When the QR Code is recognized, the<br />";
                message_ += "three yellow dots will turn into green<br />";
                message_ += "squares, then the requested screen will appear.<br />";

                ObjectNames sizes = new ObjectNames();
                sizes.tab8Sizes = new int[]{560, 600, 430, 600};
                sizes.tab10Sizes = new int[]{840, 600, 650, 600};
                sizes.tabS2Sizes = new int[]{1120, 600, 860, 600};

                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                Log.d("SCREEN_XX", notificationSizes.Height + "");

                showDialogForDynamic(
                        ToolOptionCATPALActivity.this,
                        "VIEW UNITS IN A BOX",
                        message_, notificationSizes.Height,
                        "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 161);
            }
        });

        LinearLayout btnSync = (LinearLayout) findViewById(R.id.btnSync);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SyncCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

//        WifiManager m_wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//        String m_wlanMacAdd = m_wm.getConnectionInfo().getMacAddress();

        Config.Device_UID = Secure.getString(app_context.getContentResolver(), Secure.ANDROID_ID);

        Log.d("ANDROID ID", Config.Device_UID);

//        String shows;
//        for(int i = 0; i < 5; i++) {
//            shows = "";
//            for(int o = (5 - i); o >= 1; o--) {
//                shows += o + "";
//            }
//            Log.d("" + i, "" + shows);
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message_ = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDialogForLogout(ToolOptionCATPALActivity.this);
                return true;
            case R.id.action_refresh:
                CreatedDB();
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);
                startService(intent);
                Config.JustLogged = false;
                return true;
            case R.id.action_logout:
                //showDialogForLogout(ToolOptionCATPALActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            //showDialogForLogout(ToolOptionCATPALActivity.this);

            showDialogForLogout(
                    ToolOptionCATPALActivity.this,
                    "CONFIRMATION",
                    "Pressing Back Button when at the Dashboard will Log the User Out of the System.", 0,
                    "YES, LOGOUT", "NO, BACK TO DASHBOARD", 198);

        }
        return super.onKeyDown(keycode, event);
    }

    // QR Code Command

    public void scanQR(View v, String type, boolean isQRCode, String message) {
        try {
            view_ = v;
            message_ = message;
            Intent intent = new Intent(ACTION_SCAN);
            if(isQRCode) {
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // QR Coding
            }
            else {
                intent.putExtra("SCAN_MODE", "PRODUCT_MODE"); // Barcode Coding
            }

            Log.d("TYPE: ", "> " + type_);

            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(ToolOptionCATPALActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
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

    public String SCAN_RESULT = null;
    public String SCAN_RESULT_FORMAT = null;
    public String QR_BOX_ID = null;
    public String URL_EXECUTION = null;
    public String FROM_BOX = null;
    public String QR_BOX_SHOW_ID = null;

    private checkQrCode mTask;
    public String result_QR_BOX_ID;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                SCAN_RESULT = intent.getStringExtra("SCAN_RESULT");
                SCAN_RESULT_FORMAT = intent.getStringExtra("SCAN_RESULT_FORMAT");

                int box_uid = 0;
                int box_status = 0;
                String box_code = null;
                String[] results = null;
                try {
                    results = SCAN_RESULT.split("-");
                    QR_BOX_ID = results[0];
                    QR_BOX_SHOW_ID = results[1];
                    result_QR_BOX_ID = results[0];

                    Log.d("TYPE: ", ">QR_BOX_ID " + QR_BOX_ID);
                    Log.d("TYPE: ", ">QR_BOX_SHOW_ID " + QR_BOX_SHOW_ID);
                    box_status = mysql.getBoxStatus(QR_BOX_ID);
                    Log.d("TYPE: ", ">box_status " + box_status);

                    box_uid = mysql.findListOfBoxes(QR_BOX_ID);
                    box_code = mysql.getBoxRandomCode(QR_BOX_ID);
                    Log.d("TYPE: ", ">on " + type_);
                    Log.d("TYPE: ", ">box_id " + box_uid);
                    Log.d("TYPE: ", ">box_code " + box_code);

                    Config.get_box_id = Integer.toString(box_uid);
                    Config.get_box_id_show = box_code;
                    QR_BOX_SHOW_ID = box_code;
                } catch (Exception ex) {
                    Log.d("TYPE: ", ">on " + ex);
                }

                if (type_ == "ADDUNIT") {
                    String json = null;
                    ObjectNames sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{450, 600, 350, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{860, 600, 860, 600};

//                    if (box_status == 0) {
//                        moduleRequest = true;
//                        String pass = result_QR_BOX_ID;
//                        mTask = new checkQrCode(pass);
//                        mTask.execute();
//
//                        if (unit_check_user == true) {
//                            unit_check_user = false;
//                            return;
//                        } else {
//                            if (mTask.getStatus() != AsyncTask.Status.RUNNING) {
//                                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);
//
//                                //message_ = "This Box#: <font color='#b62727'><b>\" + QR_BOX_SHOW_ID + \"</b></font> not appears as a Box in your Account";
//                                message_ = "This Box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> not appears as a Box in your Account. <br /> Press <b>REFRESH</b> to update the Box#s on your Mobile App, or press <b>CANCEL</b> to scan a different Box.";
//                                //message_ = "Oops, Please wait 1 minute to completely load your boxes list.<br />";
//
//                                showDialogForDynamicError(
//                                        ToolOptionCATPALActivity.this,
//                                        "BOX NOT IN YOUR ACCOUNT",
//                                        message_, notificationSizes.Width, notificationSizes.Height, 153, 0);
//
//                                return;
//                            }
//                        }
//                    }

                    if (box_status == 3) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Closed</b></font>. You cannot add unit.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "ADD UNITS TO A BOX THAT HAS A QR LABEL",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 0);
                        return;
                    }

                    if (box_status == 4) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>For Sale</b></font>. You cannot add unit.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "ADD UNITS TO A BOX THAT HAS A QR LABEL",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 5) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is <font color='#b62727'><b>Pending</b></font>. You cannot add unit.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "ADD UNITS TO A BOX THAT HAS A QR LABEL",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 7) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Sold</b></font>. You cannot add unit.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "ADD UNITS TO A BOX THAT HAS A QR LABEL",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{430, 600, 390, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{860, 600, 860, 600};

                    if (box_uid > 0) {
                        message_ = "Confirming you want to add units to<br />Box#: <font color='#F5900F'><b>" + QR_BOX_SHOW_ID + "</b></font><br /><br />";
                        message_ += "Click <font color='#F5900F'><b>YES</b></font> to start taking photos.<br />";
                        message_ += "Click <font color='#7A7A7A'><b>NO</b></font> to go back to Dashboard.<br />";

                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);
                        Log.d("SCREEN_XX", notificationSizes.Height + "");

                        showDialogForDynamic(
                                ToolOptionCATPALActivity.this,
                                "ADD UNITS TO A BOX THAT HAS A QR LABEL",
                                message_, notificationSizes.Height,
                                "YES, ADD UNITS", "NO, BACK TO DASHBOARD", 103);
                    } else {
                        if (mTask.getStatus() != AsyncTask.Status.RUNNING) {
                            notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                            Log.d("SCREEN_XX", notificationSizes.Height + "");

                            //message_ = "This Box # <font color='#b62727'><b>"+ QR_BOX_SHOW_ID + "</b></font> is not linked to your account.<br /><br />Or please try to wait until the database updated.<br />";
                            message_ = "This Box is not linked to your account.";

                            showDialogForDynamicError(
                                    ToolOptionCATPALActivity.this,
                                    "ADD UNITS TO A BOX THAT HAS A QR LABEL",
                                    message_, notificationSizes.Width, notificationSizes.Height, 117, 1);
                        }
                    }

                    return;
                }

                //VIEWBOX

                if (type_ == "VIEWBOX") {
                    if (box_uid > 0) {
                        do_execute();
                    } else {
                        ObjectNames sizes = new ObjectNames();
                        sizes.tab8Sizes = new int[]{0, 0, 430, 600};
                        sizes.tab10Sizes = new int[]{0, 0, 650, 600};
                        sizes.tabS2Sizes = new int[]{0, 0, 860, 600};

                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "Box # <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is not linked to your account.<br /><br />Or please try to wait until the database updated.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "VIEW BOX INFORMATION",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                    }
                    return;
                }

                if (type_ == "VIEWUNIT") {
                    if (box_uid > 0) {
                        do_execute();
                    } else {
                        ObjectNames sizes = new ObjectNames();
                        sizes.tab8Sizes = new int[]{0, 0, 430, 600};
                        sizes.tab10Sizes = new int[]{0, 0, 650, 600};
                        sizes.tabS2Sizes = new int[]{0, 0, 860, 600};

                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "Box # <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is not linked to your account.<br /><br />Or please try to wait until the database updated.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "VIEW UINTS IN A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                    }
                    return;
                }

                if (type_ == "CLOSE") {
                    ObjectNames sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{450, 600, 350, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{860, 600, 860, 600};

                    /*if(box_status == 0) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>"+ QR_BOX_SHOW_ID + "</b></font>  is not linked to your account.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153);
                        return;
                    }*/

                    if (box_status == 3) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Closed</b></font>. You cannot close.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 155, 1);
                        return;
                    }

                    if (box_status == 4) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>For Sale</b></font>. You cannot close.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 155, 1);
                        return;
                    }

                    if (box_status == 5) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is <font color='#b62727'><b>Pending</b></font>. You cannot close.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 7) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Sold</b></font>. You cannot close.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{450, 600, 430, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{860, 600, 860, 600};

                    if (box_uid > 0) {
                        // BOX IS EMPTY ADDED VALIDATION BY RONALD MAY 24, 2018
                        if (box_status == 1) {
                            notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                            message_ = "This box is empty, <br /> are you sure want to close it?";

                            showDialogForDynamic(
                                    ToolOptionCATPALActivity.this,
                                    "CLOSE A BOX",
                                    message_, notificationSizes.Height,
                                    "YES", "NO, BACK TO DASHBOARD", 157);

                        } else {
                            notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                            message_ = "Confirming you want to Close Box #<br /><font color='#F5900F'><b>" + QR_BOX_SHOW_ID + "</b></font>";

                            Log.d("SCREEN_XX", notificationSizes.Height + "");

                            showDialogForDynamic(
                                    ToolOptionCATPALActivity.this,
                                    "CLOSE A BOX",
                                    message_, notificationSizes.Height,
                                    "YES, CLOSE THIS BOX", "NO, BACK TO DASHBOARD", 153);
                        }
                    } else {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This Box is not linked to your account.";
                        //message_ = "Box # <font color='#b62727'><b>"+ QR_BOX_SHOW_ID + "</b></font> is not linked to your account.<br /><br />Or please try to wait until the database updated.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 156, 1);
                    }

                    return;
                }

                if (type_ == "COMMENT") {

                    ObjectNames sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{450, 600, 370, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{860, 600, 860, 800};

                    if (box_status == 0) {
                        moduleRequest = false;
                        String pass = result_QR_BOX_ID;

                        mTask = new checkQrCode(pass);
                        mTask.execute();

                        if (unit_check_user == true)
                        {
                            unit_check_user = false;
                            return;
                        }
                        else
                        {
                            if (mTask.getStatus() != AsyncTask.Status.RUNNING) {
                                notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                                message_ = "Oops, Please wait 1 minute to completely load your boxes list.<br />";

                                showDialogForDynamicError(
                                        ToolOptionCATPALActivity.this,
                                        "COMMENT ON A BOX",
                                        message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                                return;
                            }
                        }
                    }

                    if (box_status == 3) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Closed</b></font>. You cannot add  a comment.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "COMMENT ON A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 5) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is <font color='#b62727'><b>Pending</b></font>. You cannot add  a comment.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "COMMENT ON A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 7) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Sold</b></font>. You cannot add a comment.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "COMMENT ON A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_uid > 0) {
                        Log.d("BOX_UID: ", "> " + box_number);
                        Config.get_box_id_show = QR_BOX_SHOW_ID;
                        AddCommentMessageActivity.HeaderTitle = "ADD A BOX COMMENT";
                        AddCommentMessageActivity.TitleMessage = "ADD A COMMENT ABOUT A BOX";
                        AddCommentMessageActivity.ScreenId = "MW-186";
                        intent = new Intent(getApplicationContext(), CommentBoxThreadActivity.class);
                        intent.putExtra("partNumber", box_number);
                        startActivity(intent);
                        finish();
                    } else {
                        if (mTask.getStatus() != AsyncTask.Status.RUNNING) {
                            sizes = new ObjectNames();
                            sizes.tab8Sizes = new int[]{0, 0, 430, 600};
                            sizes.tab10Sizes = new int[]{0, 0, 650, 600};
                            sizes.tabS2Sizes = new int[]{0, 0, 860, 600};

                            notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                            message_ = "Box # <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is not linked to your account.<br /><br />Or please try to wait until the database updated.<br />";

                            showDialogForDynamicError(
                                    ToolOptionCATPALActivity.this,
                                    "COMMENT ON A BOX",
                                    message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        }
                    }
                    return;
                }

                if (type_ == "TRANSFER1") {
                    ObjectNames sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{450, 600, 350, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{1100, 600, 730, 910};

                    /*if(box_status == 0) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is not linked to your account.<br /> Please choose another box.";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, notificationSizes.Width, notificationSizes.Height, 148);
                        return;
                    }*/

                    /*if(box_status == 2) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>"+ QR_BOX_SHOW_ID + "</b></font> is not linked to your account Please choose another box.";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, notificationSizes.Width, notificationSizes.Height, 153);
                        return;
                    }*/

                    if (box_status == 3) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Closed</b></font>. You cannot transfer.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 5) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is <font color='#b62727'><b>Pending</b></font>. You cannot transfer.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 7) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Sold</b></font>. You cannot transfer.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{580, 600, 390, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{1100, 600, 910, 600};

                    if (box_uid > 0) {
                        type_ = "TRANSFER1";
                        do_execute();
                    } else {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "Box is not linked to your account.<br /><br />Or please try to wait until the database updated.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, notificationSizes.Width, notificationSizes.Height, 148, 1);
                    }
                    return;
                }

                if (type_ == "TRANSFER2") {
                    ObjectNames sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{450, 600, 350, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{1100, 600, 730, 910};

                    /*if(box_status == 0) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "Oops, Please wait 1 minute to completely load your boxes list.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "COMMENT ON A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153);
                        return;
                    }*/

                    if (box_status == 3) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Closed</b></font>. You cannot transfer here.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "COMMENT ON A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 5) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is <font color='#b62727'><b>Pending</b></font>. You cannot transfer here.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "COMMENT ON A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    if (box_status == 7) {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "This box#: <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is already <font color='#b62727'><b>Sold</b></font>. You cannot transfer here.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "COMMENT ON A BOX",
                                message_, notificationSizes.Width, notificationSizes.Height, 153, 1);
                        return;
                    }

                    sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[]{390, 600, 390, 600};
                    sizes.tab10Sizes = new int[]{650, 600, 650, 600};
                    sizes.tabS2Sizes = new int[]{780, 600, 730, 910};

                    if (box_uid > 0) {
                        String ORIGIN_ID = Config.transfer_value(this, "ORIGIN");
                        String ORIGIN_SHOW_ID = Config.transfer_value(this, "ORIGIN_SHOW_ID");

                        int x = Integer.parseInt(ORIGIN_ID);
                        int y = Integer.parseInt(QR_BOX_ID);

                        Log.d(" > 1", x + "");
                        Log.d(" > 2", y + "");

                        if (x == y) {

                            sizes = new ObjectNames();
                            sizes.tab8Sizes = new int[]{0, 0, 370, 600};
                            sizes.tab10Sizes = new int[]{0, 0, 650, 600};
                            sizes.tabS2Sizes = new int[]{0, 0, 730, 910};

                            notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                            message_ = "Oops, the destination box# is same with the origin box#.<br />";

                            showDialogForDynamicError(
                                    ToolOptionCATPALActivity.this,
                                    "ERROR ALERT",
                                    message_, notificationSizes.Width, notificationSizes.Height, 145, 1);
                            return;
                        }

                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                        message_ = "Confirming you want to Transfer All<br />of the Units from Box#: <font color='#F5900F'><b>" + ORIGIN_SHOW_ID + "</b></font><br />to Box#: <font color='#F5900F'><b>" + QR_BOX_SHOW_ID + "</b></font><br />";

                        Log.d("SCREEN_XX", notificationSizes.Height + "");

                        showDialogForDynamic(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, notificationSizes.Height,
                                "YES, TRANSFER ALL", "NO, BACK TO DASHBOARD", 146);
                    } else {
                        notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

                        message_ = "Box # <font color='#b62727'><b>" + QR_BOX_SHOW_ID + "</b></font> is not linked to your account.<br /><br />Or please try to wait until the database updated.<br />";

                        showDialogForDynamicError(
                                ToolOptionCATPALActivity.this,
                                "ERROR ALERT",
                                message_, notificationSizes.Width, notificationSizes.Height, 142, 1);
                    }
                    return;
                }

            }
        }
    }

    public class checkQrCode extends AsyncTask<Void, Void, String> {
        private String data;

        public checkQrCode(String passedData) {
            data = passedData;
        }

        @Override
        protected void onPreExecute() {
            loaderShow("Please wait validating unit ownership...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                String url = Config.Host + Config.Url_check_box_status + Config.Token + "/" + data; //QR_BOX_ID

                Log.d("Response: ", "> " + url);
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            loaderHide();
            QrCodeStatus(json);
        }
    }

    private void QrCodeStatus(String json) {
        if (json != null) {
            try {
                JSONObject job = new JSONObject(json);
                String status = job.getString("Message");
                Log.d("Name: ", status);
                int status200 = Integer.parseInt(status);
                if (status200 == 202) {
                    String seller_id = job.getString("seller_id");
                    //String box_status = job.getString("box_status");
                    int sid = Integer.parseInt(seller_id);
                    //int b_status = Integer.parseInt(box_status);
                    Log.d("Name: ", String.valueOf(Config.Seller_uid));
                    if (sid != Config.Seller_uid) {
                        unit_check_user = true;
                        if (moduleRequest == true ) {
                            showDialogForDynamicError(
                                    ToolOptionCATPALActivity.this,
                                    "ERROR",
                                    "Box is not linked to your account.", notificationSizes.Width, notificationSizes.Height, 117, 1);

                        } else {
                            showDialogForDynamicError(
                                    ToolOptionCATPALActivity.this,
                                    "ERROR",
                                    "Box is not linked to your account.", notificationSizes.Width, notificationSizes.Height, 188, 1);
                        }
                        return;
                    }

                } else {
                    unit_check_user =false;
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showDialogForQRScanned(Activity activity, String title, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_confirmation_dialog_catpal);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        TextView text_dialog = (TextView) dialog.findViewById(R.id.text_dialog);
        text_dialog.setText(message);

        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        if (type_ == "LOGOUT2") {
            type_ = "LOGOUT";
            title_dialog.setBackgroundColor(Color.parseColor("#e77e23"));
            btn_dialog_YES.setBackgroundColor(Color.parseColor("#27ae61"));
        }
        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                do_execute();
            }
        });

        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDialogForLogout(Activity activity, String caption, String message, int height, String yes_button, String no_button, final int number) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_logout_dialog_box);

        /*int resize_screen = 350;
        if(ScreenCheckSizeIfUsing10Inches == 1) {
            resize_screen = 580;
        }
        else if(ScreenCheckSizeIfUsing10Inches == 2) {
            resize_screen = 700;
        }

        Log.d("SCREEN_XX", resize_screen + "");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = resize_screen;*/

        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);

        title_dialog.setText(caption);
        title_message.setText(Html.fromHtml(message));
        screen_code.setText("MW-" + number);
        btn_dialog_YES.setText(yes_button);
        btn_dialog_NO.setText(no_button);

        //title_message.setText("Confirming you want to log out?\n");

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                type_ = "LOGOUT";
                do_execute();
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
        title_message.setText(Html.fromHtml(message));
        screen_code.setText("MW-" + number);
        btn_dialog_YES.setText(yes_button);
        btn_dialog_NO.setText(no_button);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (number == 151 || number == 184 || number == 101) {
                    scanQR(v, null, true, null);
                    return;
                }

                if (number == 141) {
                    type_ = "TRANSFER1";
                    scanQR(view_, "TRANSFER1", true,null);
                    return;
                }

                if (number == 143) {
                    type_ = "TRANSFER2";
                    scanQR(view_, "TRANSFER2",true, null);
                    return;
                }

                if (number == 146) {
                    type_ = "TRANSFER2";
                    do_execute();
                    return;
                }

                if (number == 101) {
                    type_ = "ADDUNIT";
                    scanQR(view_, "ADDUNIT",true, null);
                    return;
                }

                if (number == 127) {
                    type_ = "ADDUNIT";
                    scanQR(view_, "ADDUNIT", true,null);
                    return;
                }

                if (number == 131) {
                    type_ = "VIEWBOX";
                    isAddUnitTrue = true;
                    scanQR(view_, "VIEWBOX", true,null);
                    return;
                }

                if (number == 161) {
                    type_ = "VIEWUNIT";
                    isAddUnitTrue = true;
                    scanQR(view_, "VIEWUNIT",true, null);
                    return;
                }

                do_execute();

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

    public void showDialogForDynamicError(Activity activity, String caption, String message, int width, int height, final int number, int button) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_custom_dialog_error);

        LinearLayout LinearCancel = (LinearLayout) dialog.findViewById(R.id.LinearCancel);
        LinearLayout LinearOptions = (LinearLayout) dialog.findViewById(R.id.LinearOptions);

        Button btn_dialog_DEFAULT = (Button) dialog.findViewById(R.id.btn_dialog_DEFAULT);
        Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        Button btn_dialog_CANCEL = (Button) dialog.findViewById(R.id.btn_dialog_CANCEL);
        int footer_page = 0;

        switch (button) {
            case 0: // OPTIONS
                if (LinearCancel.getVisibility() == View.VISIBLE) {
                    LinearCancel.setVisibility(View.GONE);
                }

                if (LinearOptions.getVisibility() != View.VISIBLE) {
                    LinearOptions.setVisibility(View.VISIBLE);
                }
                footer_page = 126;
                break;
            case 1: // CANCEL

                if (LinearOptions.getVisibility() == View.VISIBLE) {
                    LinearOptions.setVisibility(View.GONE);
                }

                if (LinearCancel.getVisibility() != View.VISIBLE) {
                    LinearCancel.setVisibility(View.VISIBLE);
                }
                footer_page = number;
                break;
            default:
                footer_page = number;
                break;
        }

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        //Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);

        title_dialog.setText(caption);
        title_message.setText(Html.fromHtml(message));
        screen_code.setText("MW-" + footer_page);

        btn_dialog_DEFAULT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_dialog_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                update_list_boxes();
            }
        });

        btn_dialog_CANCEL.setOnClickListener(new View.OnClickListener() {
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

    public void update_list_boxes() {
        new do_append_fetching().execute();
    }

    public class do_append_fetching extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            loaderShow("Please wait updating database...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                Log.d("Response: ", "> " + LIST_BOXES_API_URL);
                json = json_help.makeServiceCall(LIST_BOXES_API_URL, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            do_update_db(json);
            loaderHide();
            confirmScan();
        }
    }


    public void do_update_db(String json) {
        if (json != null) {
            try {
                JSONObject job = new JSONObject(json);
                String box_counts = job.getString("box_count");
                Log.d("BOX Count: ", box_counts);

                JSONArray jsonMainNode = job.optJSONArray("data");
                int lengthJsonArr = jsonMainNode.length();

                sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
                mysql = new MySqlLite(sqlDB);

                for (int i = 0; i < lengthJsonArr; i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String box_uid = jsonChildNode.optString("Id").toString();
                    String worker_id = jsonChildNode.optString("worker_of").toString();
                    String yard_id = jsonChildNode.optString("yard_id").toString();
                    String box_id = jsonChildNode.optString("box_id").toString();
                    String box_code = jsonChildNode.optString("box_code").toString();
                    String box_status = jsonChildNode.optString("box_status").toString();

                    try {
                        if (Integer.parseInt(box_status) == 0) {
                            box_status = "5";
                        }
                    } catch (Exception e) {
                        box_status = "5";
                    }

                    Log.d("Results: box_uid", "> " + box_uid);
                    Log.d("Results: worker_id", "> " + worker_id);
                    Log.d("Results: yard_id", "> " + yard_id);
                    Log.d("Results: box_id", "> " + box_id);
                    Log.d("Results: box_code", "> " + box_code);
                    Log.d("Results: box_status", "> " + box_status);

                    if (mysql.findListOfBoxes(box_uid) == 0) {
                        mysql.insert("list_of_boxes", "'" + box_uid + "', '" + worker_id + "', '" + yard_id + "', '" + box_id + "', '" + box_code + "', '" + box_status + "'");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void confirmScan() {
        //view_ = view;
        type_ = "ADDUNIT";

        message_ = "Database is successfully updated.<br /><br />";
        message_ += "Confirming you want to proceed and add units to this Box";

        //notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, "0", false);
        //Log.d("SCREEN_XX", notificationSizes.Height + "");

        showDialogForDynamic(
                ToolOptionCATPALActivity.this,
                "ADD UNITS TO A BOX THAT HAS A QR LABEL",
                message_, notificationSizes.Height,
                "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 127);

    }

    public void showDialogForDynamicNotification(Activity activity, String caption, String message, int height, final int number, final boolean IsNothing) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_alert_ok);

        //WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //lp.copyFrom(dialog.getWindow().getAttributes());
        //lp.height = height;

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

    private void do_execute() {

        if (type_ != "LOGOUT") {
            String[] results = SCAN_RESULT.split("-");
            Config.get_box_id = results[0];
            Log.d("BOX_UID: ", "> " + Config.get_box_id);
            box_number = Config.get_box_id;
        }

        if (type_ == "ADDUNIT") {
            Log.d("BOX_UID: ", "> " + box_number);
            Config.get_box_id_show = QR_BOX_SHOW_ID;
            Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
            intent.putExtra("partNumber", box_number);
            startActivity(intent);
            finish();
            return;
        }

        //VIEWBOX "VIEWUNIT"

        if (type_ == "VIEWBOX") {
            Log.d("BOX_UID: ", "> " + box_number);
            Config.get_box_id = box_number;
            Config.get_box_id_show = QR_BOX_SHOW_ID;
            Intent intent = new Intent(getApplicationContext(), BoxViewActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (type_ == "VIEWUNIT") {
            Log.d("BOX_UID: ", "> " + box_number);
            Config.get_box_id = box_number;
            Config.get_box_id_show = QR_BOX_SHOW_ID;
            Config.get_yard_id = mysql.get_yard_information(box_number);
            ;
            Log.d("yard_id: ", "> " + Config.get_yard_id);

            Intent intent = new Intent(getApplicationContext(), GalleryViewActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (type_ == "TRANSFER1") {
            Config.transfer_commit(this, "ORIGIN", QR_BOX_ID);
            Config.transfer_commit(this, "ORIGIN_SHOW_ID", QR_BOX_SHOW_ID);
            Log.d("BOX_UID: ", "> " + QR_BOX_ID);

            ObjectNames sizes = new ObjectNames();
            sizes.tab8Sizes = new int[]{580, 600, 390, 600};
            sizes.tab10Sizes = new int[]{650, 600, 650, 600};
            sizes.tabS2Sizes = new int[]{1150, 600, 860, 600};

            notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

            type_ = "TRANSFER2";

            message_ = "Confirming units will be coming from box<br />";
            message_ += "# <font color='#F5900F'><b>" + QR_BOX_SHOW_ID + "</b></font><br /><br />";

            message_ += "If <font color='#F5900F'><b>YES</b></font>, scan the box you will be putting<br />the units into (Destination Box).<br /><br />";

            message_ += "Position the DESTINATION Box's QR Code completely<br />";
            message_ += "inside the scanner windows.<br /><br />";

            message_ += "When the QR Code is recognized, the<br />";
            message_ += "three yellow dots will turn into green<br />";
            message_ += "squares, then the requested screen will appear.<br />";

            Log.d("SCREEN_XX", notificationSizes.Height + "");

            showDialogForDynamic(
                    ToolOptionCATPALActivity.this,
                    "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                    message_, notificationSizes.Height,
                    "YES, SCAN\nDESTINATION BOX", "NO, BACK TO\nDASHBOARD", 143);

            return;
        }

        if (type_ == "TRANSFER2") {
            String ORIGIN_UID = Config.transfer_value(this, "ORIGIN");
            Config.transfer_commit(this, "DESTINATION", QR_BOX_ID);
            Config.transfer_commit(this, "DESTINATION_SHOW_ID", QR_BOX_SHOW_ID);

            URL_EXECUTION = Config.Host + Config.Url_box_transfer + Config.Token + "/" + ORIGIN_UID + "/" + QR_BOX_ID;
            new do_execution().execute();
            return;
        }

        if (type_ == "CLOSE") {
            URL_EXECUTION = Config.Host + Config.Url_box_close + Config.Token + "/" + Config.get_box_id + "/3";
            Log.d("URL: ", "> " + URL_EXECUTION);
            new do_execution().execute();
            return;
        }

        if (type_ == "LOGOUT") {
            Config.flash_sharedPref(this);

            //Config.commit_login(ToolOptionCATPALActivity.this, "", "", "", "", "");

            Intent logout = new Intent(getApplicationContext(), LoginCATPALActivity.class);
            startActivity(logout);
            finish();
        }

    }

    public boolean isAddUnitTrue = false;

    public class do_execution extends AsyncTask<Void, Void, String> {
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
                String url = URL_EXECUTION;
                Log.d("Response: ", "> " + Config.Seller_uid);
                Log.d("Response: ", "> " + url);
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            loaderHide();
            postBack(json);
        }
    }

    private void postBack(String json) {
        if (json != null) {
            try {
                JSONObject job = new JSONObject(json);
                String status = job.getString("Message");
                Log.d("Name: ", status);
                int status200 = Integer.parseInt(status);
                if (status200 == 200) {
                    if (type_ == "TRANSFER2") {

                        int resize_screen = 390;
                        if (ScreenCheckSizeIfUsing10Inches == 1) {
                            resize_screen = 580;
                        } else if (ScreenCheckSizeIfUsing10Inches == 2) {
                            resize_screen = 700;
                        }

                        String ORIGIN_SHOW_ID = Config.transfer_value(this, "ORIGIN_SHOW_ID");
                        String DESTINATION_SHOW_ID = Config.transfer_value(this, "DESTINATION_SHOW_ID");

                        message_ = "Units from Box#: " + ORIGIN_SHOW_ID;
                        message_ += " were successfully\ntransferred to Box#: " + DESTINATION_SHOW_ID;

                        showDialogForDynamicNotification(
                                ToolOptionCATPALActivity.this,
                                "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                                message_, resize_screen, 147, false);

                        return;
                    }

                    if (type_ == "CLOSE") {
                        message_ = "You have successfully Closed Box\n" + QR_BOX_SHOW_ID;

                        int resize_screen = 390;
                        if (ScreenCheckSizeIfUsing10Inches == 1) {
                            resize_screen = 580;
                        } else if (ScreenCheckSizeIfUsing10Inches == 2) {
                            resize_screen = 700;
                        }

                        Log.d("SCREEN_XX", resize_screen + "");

                        showDialogForDynamicNotification(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, resize_screen, 154, false);
                        return;
                    }

                    messageAlert(type_ + " was successful.", "INFORMATION");
                }

                if (status200 == 404) {
                    if (type_ == "CLOSE") {
                        message_ = "This Box " + QR_BOX_SHOW_ID + " already closed.";
                        showDialogForDynamicNotification(
                                ToolOptionCATPALActivity.this,
                                "CLOSE A BOX",
                                message_, 390, 154, false);
                        return;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void messageAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        Config.transfer_clear(ToolOptionCATPALActivity.this);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void messageAlertMessage(String message, String title) {
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

    private void messageAlertTransfer1(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        type_ = "TRANSFER1";
                        scanQR(view_, "TRANSFER1", true, null);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void messageAlertTransfer2(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        FROM_BOX = Config.get_box_id;
                        scanQR(view_, "TRANSFER2", true,null);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(ToolOptionCATPALActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
