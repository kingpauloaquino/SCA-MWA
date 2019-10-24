package com.cheappartsguy.app.cpg;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ViewImageDetailedActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    public  ArrayList<ImageGalleryItem> ImageGallery;

    public String URL;

    public ImageView imageView;

    public Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_view_image_detailed);

        btnBack = (Button) findViewById(R.id.btnBack);

        imageView = (ImageView) findViewById(R.id.image);

        URL = getIntent().getStringExtra("url");

        URL = URL.replace("THMB", "ORGL");

        String title = getIntent().getStringExtra("title");

        Bitmap bitmap = getIntent().getParcelableExtra("image");

        btnBack.setText("CLICK HERE TO RETURN TO VIEW UNITS IN A BOX");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView.setImageBitmap(bitmap);

        new DownloadImageTask().execute();
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            loaderShow("Please wait... Downloading Images...");
            super.onPreExecute();
        }

        protected Bitmap doInBackground(Void... urls) {

            Bitmap bitmap = null;
            try {

                Log.d("DONE", URL);
                bitmap = do_downloading(URL);
                Log.d("DONE", bitmap + "");
                Thread.sleep(200);
            }
            catch (Exception ex) { }

            return bitmap;
        }

        public Bitmap do_downloading(String url) {
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Log.d("DONE", "YES");
            loaderHide();
            imageView.setImageBitmap(result);
        }
    }

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(ViewImageDetailedActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
