package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GradeImageSavedActivity extends AppCompatActivity {

    private LinearLayout btnAddAnotherUnit, btnAddAdditionalInfo, btnFinished;

    public static String AppTitleText;
    public static String AppHeaderText;
    public static String AppPageText;

    TextView txtHeader, txtPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_image_saved);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName(AppTitleText));
        txtAccount.setText(Config.Worker_name);

        ImageViewActivity.IsPreviewFullScreen = false;

        txtHeader = (TextView)findViewById(R.id.txtHeader);

        txtHeader.setText(AppHeaderText);

        txtPage = (TextView)findViewById(R.id.txtPage);

        txtPage.setText(AppPageText);

        btnFinished = (LinearLayout) findViewById(R.id.btnFinished);
        btnFinished.setOnClickListener(new View.OnClickListener() {
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

        btnAddAdditionalInfo = (LinearLayout) findViewById(R.id.btnAddAdditionalInfo);
        btnAddAdditionalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AdditionInformationOptionsActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnAddAnotherUnit = (LinearLayout) findViewById(R.id.btnAddAnotherUnit);
        btnAddAnotherUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.IsParentImageName = null;
                Config.totalAdditionalPhoto = 0;
                Config.IsParentImage = true;

                ImageViewActivity.IsPreviewFullScreen = false;
                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
