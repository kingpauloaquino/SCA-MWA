package com.cheappartsguy.app.cpg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ViewFullGradeImageActivity extends AppCompatActivity {

    public static String openByActivity ;
    public static boolean IsLastImageActive = false;

    public Button btnBack;

    private ImageView imgViewFull;

    public String FileImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_view_full_grade_image);

        imgViewFull = (ImageView) findViewById(R.id.image);

        FileImagePath = getIntent().getStringExtra("ImagePath");

        previewMedia(FileImagePath);

        btnBack = (Button) findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;
                if(ViewFullGradeImageActivity.openByActivity == "GradeImageActivity") {
                    if(IsLastImageActive) {
                        GradeImageActivity.LastImageSaved = FileImagePath;
                    }
                    else {
                        GradeImageActivity.FileImagePath = FileImagePath;
                    }

                    ImageViewActivity.IsPreviewFullScreen = false;
                    i = new Intent(getApplicationContext(), GradeImageActivity.class);
                    i.putExtra("ImagePath", FileImagePath);
                }
                else {
                    i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                }
                startActivity(i);
                finish();
            }
        });
    }

    private void previewMedia(String filePath) {
        imgViewFull.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imgViewFull.setImageBitmap(bitmap);
    }
}
