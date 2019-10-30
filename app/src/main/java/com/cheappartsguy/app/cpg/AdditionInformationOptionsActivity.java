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

        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnAddAdditionalUnit = (LinearLayout) findViewById(R.id.btnAddAdditionalUnit);
        btnAddAdditionalUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnBarCode = (LinearLayout) findViewById(R.id.btnBarCode);
        btnBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scanQR(view, "");
            }
        });

        btnAddAdditionalPhoto = (LinearLayout) findViewById(R.id.btnAddAdditionalPhoto);
        btnAddAdditionalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageViewActivity.AdditionalPhotoDir = true;
                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(i);
                finish();

            }
        });

        btnAddComment = (LinearLayout) findViewById(R.id.btnAddComment);
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddCommentMessageActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void messageAlertTransfer2(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void scanQR(View v, String message) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE"); // Barcode Coding

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

                String SCAN_RESULT = intent.getStringExtra("SCAN_RESULT");
                Log.d("PARENT_IMAGE", Config.ImageName);
                Log.d("SCAN_RESULT", SCAN_RESULT);
                Log.d("WORKER_UID", Config.Worker_uid.toString());

                String value = "'" + Config.Worker_uid.toString() + "', '" + Config.ImageName + "', '" + SCAN_RESULT + "'";
                Log.d("QUERY_INSERT", value);

                mysql.insert("list_of_barcode", value);

                Cursor c = mysql.select("SELECT * FROM list_of_barcode;");

                while (c.moveToNext()) {
                    Log.d("WORKER_UID_SQL", "" + c.getString(0));
                    Log.d("PARENT_IMAGE_SQL", "" + c.getString(1));
                    Log.d("SCAN_RESULT_SQL", "" + c.getString(2));
                }

            }
        }
    }

    public static void drop_barcode_table() {
        mysql.execute("DROP TABLE IF EXISTS list_of_barcode;");
    }

}
