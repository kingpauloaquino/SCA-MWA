package com.cheappartsguy.app.cpg;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GradeImageConfirmingActivity extends AppCompatActivity {

    public static int GradedId;

    LinearLayout  btnRegrade, btnAddAdditionalInfo, btnFinished, btnAddAnotherUnit;

    TextView txtGradeStatus;

    LinearLayout btnGradeStatus;

    String FileImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_image_confirming);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("UNIT FULLNESS CONFIRMATION"));
        txtAccount.setText(Config.Worker_name);

        ImageViewActivity.IsPreviewFullScreen = false;

        btnGradeStatus          = (LinearLayout) findViewById(R.id.btnGradeStatus);
        txtGradeStatus          = (TextView) findViewById(R.id.txtGradeStatus);
        btnRegrade              = (LinearLayout) findViewById(R.id.btnRegrade);
//        btnAddAdditionalInfo    = (LinearLayout) findViewById(R.id.btnAddAdditionalInfo);
//        btnFinished             = (LinearLayout) findViewById(R.id.btnFinished);
        btnAddAnotherUnit       = (LinearLayout) findViewById(R.id.btnAddAnotherUnit);

        FileImagePath       = getIntent().getStringExtra("ImagePath");

        btnRegrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GradeOptionViewActivity.class);
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });

        GradeImageSavedActivity.AppTitleText = "UNIT FULLNESS SAVED";
        GradeImageSavedActivity.AppHeaderText = "CONFIRMING PHOTO AND FULLNESS GRADE HAVE BEEN SAVED";
        GradeImageSavedActivity.AppPageText = "MW-108";

        btnAddAnotherUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= 23){
                    check_storage_permission();
                }

                if(Config.save_image(Config.ImageFile, Config.dir_parent_folder_name, GradedId)) {

                    ImageViewActivity.ParentImages = Config.ImageName;
                    Config.transfer_commit(GradeImageConfirmingActivity.this, "LastImage", Config.dir_new_parent_folder_name + "/" + Config.ImageName);
                    Intent i = new Intent(getApplicationContext(), GradeImageSavedActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        ShowGradeStatus(btnGradeStatus, GradedId);
    }

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public void check_storage_permission() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)){
            Log.d("SCA", "Error: external storage is unavailable");
            return;
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("SCA", "Error: external storage is read only.");
            return;
        }
        Log.d("SCA", "External storage is not read only or unavailable");

        if (ContextCompat.checkSelfPermission(this, // request permission when it is not granted.
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("SCA", "permission: WRITE_EXTERNAL_STORAGE: NOT granted!");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void ShowGradeStatus(LinearLayout container, int grade_id) {

        TextView gradeText = (TextView)findViewById(R.id.txtGrade);
        ImageView gradeIcon = (ImageView)findViewById(R.id.imgGrade);

        String textStatusGrade = "UNIT FULLNESS HAS BEEN GRADED AS ";
        switch (grade_id) {
            case 5:
                gradeText.setText("EMPTY");
                gradeIcon.setImageResource(R.drawable.empty_icon);
                container.setBackgroundResource(R.drawable.empty_shape);

                textStatusGrade = textStatusGrade + "EMPTY";
                break;
            case 6:
                gradeText.setText("UNKNOWN");
                gradeIcon.setImageResource(R.drawable.unknown_icon);
                container.setBackgroundResource(R.drawable.unknown_shape);

                textStatusGrade = textStatusGrade + "UNKNOWN";
                break;
            case 4:
                gradeText.setText("1/4");
                gradeIcon.setImageResource(R.drawable.onefourthicon);
                container.setBackgroundResource(R.drawable.one4_shape);

                textStatusGrade = textStatusGrade + "1/4";
                break;
            case 3:
                gradeText.setText("1/2");
                gradeIcon.setImageResource(R.drawable.one_half_icon);
                container.setBackgroundResource(R.drawable.one2_shape);

                textStatusGrade = textStatusGrade + "1/2";
                break;
            case 2:
                gradeText.setText("3/4");
                gradeIcon.setImageResource(R.drawable.threefouthicon);
                container.setBackgroundResource(R.drawable.three4_shape);

                textStatusGrade = textStatusGrade + "3/4";
                break;
            default:
                gradeText.setText("FULL");
                gradeIcon.setImageResource(R.drawable.fullicon);
                container.setBackgroundResource(R.drawable.one5_shape);

                textStatusGrade = textStatusGrade + "FULL";
                break;
        }

        txtGradeStatus.setText(textStatusGrade);
    }
}
