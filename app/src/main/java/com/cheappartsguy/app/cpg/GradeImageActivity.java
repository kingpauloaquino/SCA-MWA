package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;

public class GradeImageActivity extends AppCompatActivity {

    private ImageView imgImageShow;

    private LinearLayout imgViewFull;

    private Button btnLastSavedPhoto, btnCameraTimeOut, btnDelete, btnRetake;

    private LinearLayout btnGradeUnitFullness;

    TextView txtBoxNumber;

    public static String FileImagePath = null;
    public static String LastImageSaved = null;
    public static boolean Is = false;

    public ServiceWorker s_worker;

    public boolean IsLastImageActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_image);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("ADD A UNIT IN A BOX"));
        txtAccount.setText(Config.Worker_name);

        ImageViewActivity.IsPreviewFullScreen = false;

        txtBoxNumber        = (TextView) findViewById(R.id.txtBoxNumber);
        txtBoxNumber.setText("BOX #: " + Config.get_box_id_show);

        imgImageShow        = (ImageView) findViewById(R.id.imgImageShow);

        FileImagePath       = getIntent().getStringExtra("ImagePath");
        
        Log.d("FILE2", FileImagePath);

        previewMedia(FileImagePath);

        imgViewFull         = (LinearLayout) findViewById(R.id.btnImgViewFull);

        s_worker = new ServiceWorker();

        imgViewFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFullGradeImageActivity.openByActivity = "GradeImageActivity";
                ImageViewActivity.IsPreviewFullScreen = true;
                Intent i = new Intent(getApplicationContext(), ViewFullGradeImageActivity.class);
                if(IsLastImageActive) {
                    i.putExtra("ImagePath", LastImageSaved);
                    ViewFullGradeImageActivity.IsLastImageActive = IsLastImageActive;
                }
                else {
                    i.putExtra("ImagePath", FileImagePath);
                    ViewFullGradeImageActivity.IsLastImageActive = IsLastImageActive;
                }
                i.putExtra("IsGradeImage", "YES");
                startActivity(i);
                finish();
            }
        });

        btnLastSavedPhoto = (Button) findViewById(R.id.btnLastSavedPhoto);
        btnLastSavedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsLastImageActive) {
                    IsLastImageActive = false;
                    previewMedia(FileImagePath);
                }
                else {
                    IsLastImageActive = true;
                    LastImageSaved = Config.transfer_value(GradeImageActivity.this, "LastImage");
                    previewMedia(LastImageSaved);
                }
            }
        });

        btnCameraTimeOut = (Button) findViewById(R.id.btnCameraTimeOut);
        btnCameraTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s_worker.delete(Config.ImageFile)) {
                    Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        btnRetake = (Button) findViewById(R.id.btnRetake);
        btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnGradeUnitFullness = (LinearLayout) findViewById(R.id.btnGradeUnitFullness);
        btnGradeUnitFullness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsLastImageActive) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Oops. View Last Saved Photo is Active", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Intent i = new Intent(getApplicationContext(), GradeOptionViewActivity.class);
                i.putExtra("ImagePath", FileImagePath);
                startActivity(i);
                finish();
            }
        });
    }

    private void previewMedia(String filePath) {
        imgImageShow.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imgImageShow.setImageBitmap(bitmap);
    }
}
