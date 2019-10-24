package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GradeOptionViewActivity extends AppCompatActivity {

    private Button gradeBtnEmpty, gradeBtnUnknown, gradeBtnOneFourth, gradeBtnOneHalf, gradeBtnThreeFourth, gradeBtnFull;
    private LinearLayout btnBackViewFullGradeImage;

    String FileImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_option_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("UNIT FULLNESS"));
        txtAccount.setText(Config.Worker_name);

        FileImagePath       = getIntent().getStringExtra("ImagePath");

        gradeBtnEmpty = (Button) findViewById(R.id.gradeBtnEmpty);
        gradeBtnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GradeImageConfirmingActivity.class);
                GradeImageConfirmingActivity.GradedId = 5;
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });

        gradeBtnUnknown = (Button) findViewById(R.id.gradeBtnUnknown);
        gradeBtnUnknown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GradeImageConfirmingActivity.class);
                GradeImageConfirmingActivity.GradedId = 6;
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });

        gradeBtnOneFourth = (Button) findViewById(R.id.gradeBtnOneFourth);
        gradeBtnOneFourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GradeImageConfirmingActivity.class);
                GradeImageConfirmingActivity.GradedId = 4;
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });

        gradeBtnOneHalf = (Button) findViewById(R.id.gradeBtnOneHalf);
        gradeBtnOneHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GradeImageConfirmingActivity.class);
                GradeImageConfirmingActivity.GradedId = 3;
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });

        gradeBtnThreeFourth = (Button) findViewById(R.id.gradeBtnThreeFourth);
        gradeBtnThreeFourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GradeImageConfirmingActivity.class);
                GradeImageConfirmingActivity.GradedId = 2;
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });

        gradeBtnFull = (Button) findViewById(R.id.gradeBtnFull);
        gradeBtnFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GradeImageConfirmingActivity.class);
                GradeImageConfirmingActivity.GradedId = 1;
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });

        btnBackViewFullGradeImage = (LinearLayout) findViewById(R.id.btnBackViewFullGradeImage);
        btnBackViewFullGradeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFullGradeImageActivity.IsLastImageActive = false;
                ViewFullGradeImageActivity.openByActivity = "GradeImageActivity";
                ImageViewActivity.IsPreviewFullScreen = true;
                Intent i = new Intent(getApplicationContext(), ViewFullGradeImageActivity.class);
                i.putExtra("ImagePath", FileImagePath);
                i.putExtra("IsGradeImage", "YES");
                startActivity(i);
                finish();
            }
        });
    }

    public static String _OriginBox;
    public String _ContentValue = null;
    private void doContent(String ContentValue) {
        _ContentValue = ContentValue;
        String message = "Unit Fullness Has Been Graded As " + _ContentValue;
    }
}
