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

    public LinearLayout btnBack, btnAddAdditionalUnit, btnBarCode, btnAddAdditionalPhoto, btnAddComment;

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
                messageAlertTransfer2("This Feature is Coming Soon.", "INFORMATION");
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

}
