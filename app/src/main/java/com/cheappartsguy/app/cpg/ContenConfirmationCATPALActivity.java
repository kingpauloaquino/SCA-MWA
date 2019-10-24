package com.cheappartsguy.app.cpg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContenConfirmationCATPALActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conten_confirmation_catpal);
        setTitle("CONFIRMATION OF GRADING FULLNESS");

        Button btnEmpty = (Button) findViewById(R.id.btnEmpty);
        btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message_ = "Add a comment on this unit.";

                showDialogForQRScanned(ContenConfirmationCATPALActivity.this,
                        "ADD A COMMENT",
                        message_);
            }
        });
    }

    public void showDialogForQRScanned(Activity activity, String title, String message){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_comment_dialog_catpal);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        TextView text_dialog = (TextView) dialog.findViewById(R.id.text_dialog);
        text_dialog.setText(message);

        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_COMMENT);
        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_CANCEL);
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Back button is disabled.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return super.onKeyDown(keycode, event);
    }
}
