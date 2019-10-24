package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AuditViewActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    public LinearLayout btnBack, btnBackViewUnit;

    private GridView gridView1, gridView2;

    public ArrayList<ImageGalleryItem> ImageGallery1;

    public ArrayList<ImageGalleryItem> ImageGallery2;

    public String[] ImageUrlList1, ImageUrlList2 ;

    public TextView txtBoxId;

//    public int TrimPositionId = 0;

    public int countImageAdd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("BOX AUDIT"));
        txtAccount.setText(Config.Worker_name);

        ImageUrlList1 = new String[127];
        ImageUrlList2 = new String[127];

        gridView1 = (GridView) findViewById(R.id.gridView1);
        gridView2 = (GridView) findViewById(R.id.gridView2);

        ImageGallery1 = Config.ImageGalleryContainer;

        ImageGallery2 = new ArrayList<>();

        txtBoxId = (TextView) findViewById(R.id.txtBoxId);
        txtBoxId.setText("BOX #: " + Config.get_box_id_show + "\nCLICK IMAGE TO MOVE BETWEEN BELOW SCREENS");

        if (ImageGallery1 != null) {
            PopulateImages();
        }

        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnBackViewUnit = (LinearLayout) findViewById(R.id.btnBackViewUnit);
        btnBackViewUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GalleryViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                ImageGalleryItem item = (ImageGalleryItem) parent.getItemAtPosition(position);
//                //Create intent
//                Intent intent = new Intent(AuditViewActivity.this, ViewImageDetailedActivity.class);
//                intent.putExtra("title", item.getTitle());
//                intent.putExtra("image", item.getImage());
//                intent.putExtra("url", item.getUrl());
//                //Start details activity
//                startActivity(intent);

                Log.d("TrimPositionId", position + "");
                ArrayListTrimming(position, true);
            }
        });

//        gridView1.setLongClickable(true);
//        gridView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("TrimPositionId", position + "");
//                ArrayListTrimming(position, true);
//                return false;
//            }
//        });

//        gridView2.setLongClickable(true);
//        gridView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("TrimPositionId", position + "");
//                ArrayListTrimming(position, false);
//                return false;
//            }
//        });

        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                ImageGalleryItem item = (ImageGalleryItem) parent.getItemAtPosition(position);
//                //Create intent
//                Intent intent = new Intent(AuditViewActivity.this, ViewImageDetailedActivity.class);
//                intent.putExtra("title", item.getTitle());
//                intent.putExtra("image", item.getImage());
//                intent.putExtra("url", item.getUrl());
//                //Start details activity
//                startActivity(intent);

                Log.d("TrimPositionId", position + "");
                ArrayListTrimming(position, false);
            }
        });
    }

    public void PopulateImages() {
        // SORT ARRAY INTO DESCEDING ORDER (getUrl) PREVIOUS VALUE 12/13/2017 RONALD
        Collections.sort(getData1(), new Comparator<ImageGalleryItem>() {
            @Override
            public int compare(ImageGalleryItem lhs, ImageGalleryItem rhs) {
                return rhs.getTitle()
                        .compareTo(
                                lhs.getTitle());
            }
        });

        ViewImageGalleryActivity ViewImageGalleryAdapter1 = new ViewImageGalleryActivity(this, R.layout.activity_view_image_gallery, getData1());
        gridView1.setAdapter(ViewImageGalleryAdapter1);
    }

    private ArrayList<ImageGalleryItem> getData1() {
        return ImageGallery1;
    }

    private void ArrayListTrimming(int position, boolean IsGridViewOrigin) {

        ArrayList<ImageGalleryItem> ImageGalleryTrim ;
        ArrayList<ImageGalleryItem> ImageGalleryTrimmed = new ArrayList<>();

        if(IsGridViewOrigin) {

           ImageGalleryTrim = getData1();
            String Url2 = ImageGalleryTrim.get(position).getUrl();
            String Title2 = ImageGalleryTrim.get(position).getTitle();
            int Grade2 = ImageGalleryTrim.get(position).getGrade();
            Bitmap bitmap2 = ImageGalleryTrim.get(position).getImage();

            Log.d("ImageGallery2", Url2 + "");
            ImageGallery2.add(new ImageGalleryItem(bitmap2, Title2, Grade2, Url2, new int[] {160, 120}));

            for (ImageGalleryItem img_item : ImageGalleryTrim) {

                int index = ImageGalleryTrim.indexOf(img_item);
                Log.d("index", index + "");

                String Url = img_item.getUrl();
                String Title = img_item.getTitle();
                int Grade = img_item.getGrade();
                Bitmap bitmap = img_item.getImage();
                Log.d("TrimPositionId", Url + "");

                if(position != index) {
                    Log.d("ImageGallery1", Url + "");
                    ImageGalleryTrimmed.add(new ImageGalleryItem(bitmap, Title, Grade, Url, new int[] {210, 170}));
                }
            }

            ImageGallery1 = null;
            ImageGallery1 = ImageGalleryTrimmed;
        }
        else {

            ImageGalleryTrim = getData2();

            String Url1 = ImageGalleryTrim.get(position).getUrl();
            String Title1 = ImageGalleryTrim.get(position).getTitle();
            int Grade1 = ImageGalleryTrim.get(position).getGrade();
            Bitmap bitmap1 = ImageGalleryTrim.get(position).getImage();

            Log.d("ImageGallery2", Url1 + "");
            ImageGallery1.add(new ImageGalleryItem(bitmap1, Title1, Grade1, Url1, new int[] {210, 170}));

            for (ImageGalleryItem img_item : ImageGalleryTrim) {

                int index = ImageGalleryTrim.indexOf(img_item);
                Log.d("index", index + "");

                String Url = img_item.getUrl();
                String Title = img_item.getTitle();
                int Grade = img_item.getGrade();
                Bitmap bitmap = img_item.getImage();
                Log.d("TrimPositionId", Url + "");

                if(position != index) {
                    Log.d("ImageGallery1", Url + "");
                    ImageGalleryTrimmed.add(new ImageGalleryItem(bitmap, Title, Grade, Url, new int[] {160, 120}));
                }
            }

            ImageGallery2 = null;
            ImageGallery2 = ImageGalleryTrimmed;
        }

        PopulateImages();
        gridView2Populate();
    }


    // Second Gridview

    public void gridView2Populate() {
       Collections.reverse(getData2());

        ViewImageGalleryActivity ViewImageGalleryAdapter2 = new ViewImageGalleryActivity(this, R.layout.activity_view_image_gallery, getData2());
        gridView2.setAdapter(ViewImageGalleryAdapter2);
    }

    private ArrayList<ImageGalleryItem> getData2() {
        return ImageGallery2;
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {

            loaderShow("Please wait...");
            super.onPreExecute();
        }

        protected Bitmap doInBackground(Void... urls) {

            Bitmap bitmap = null;
            try {

//                ImageGallery2 = new ArrayList<>();
//
//                int t_count_img = ImageUrlList.length;
//
//                Log.d("t_count_img", t_count_img + "");
//
//                for (int i = 0; i < t_count_img; i++) {
//                    String img_url =  ImageUrlList[i];
//
//                    Log.d("img_url", img_url);
//
//                    if(img_url != "") {
//
//                        bitmap = do_downloading(img_url);
//
//                        Log.d("DONE", bitmap + "");
//
//                        ImageGallery2.add(new ImageGalleryItem(bitmap, "Image#" + (i + 1), img_url));
//                    }
//                    Thread.sleep(200);
//                }
            }
            catch (Exception ex) { }

            return bitmap;
        }

        public Bitmap do_downloading(String url) {
            String urldisplay = url;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Log.d("DONE", "YES");

            PopulateImages();
            gridView2Populate();
            loaderHide();
        }
    }

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(AuditViewActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
