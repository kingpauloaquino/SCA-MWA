package com.cheappartsguy.app.cpg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomActionBarActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_action_bar);

        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) findViewById(R.id.txtAccount);

        txtTitle.setText("");
        txtAccount.setText("");


    }

}
