package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.InputStream;
import java.util.ArrayList;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GalleryViewActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    public String URL;

    private GridView gridView;

    private ViewImageGalleryActivity ViewImageGalleryAdapter;

    public  ArrayList<ImageGalleryItem> ImageGallery;

//    public String[] ImageUrlList;

    public LinearLayout btnViewBoxInfo, btnBack, btnAudit;

    public TextView txtBoxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("VIEW UNITS IN A BOX"));
        txtAccount.setText(Config.Worker_name);

//        Config.get_box_id = "792"; //"258"; //

        URL = Config.Host +  Config.Url_fetch_images_view_gallery + Config.Token + "/" + Config.get_box_id;

        Log.d("LOAD", URL);

        txtBoxId = (TextView) findViewById(R.id.txtBoxId);
        gridView = (GridView) findViewById(R.id.gridView);

        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnAudit = (LinearLayout) findViewById(R.id.btnAudit);
        btnAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AuditViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnViewBoxInfo = (LinearLayout) findViewById(R.id.btnViewBoxInfo);
        btnViewBoxInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BoxViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageGalleryItem item = (ImageGalleryItem) parent.getItemAtPosition(position);

                //Create intent
                Intent intent = new Intent(GalleryViewActivity.this, ViewImageDetailedActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());
                intent.putExtra("url", item.getUrl());

                //Start details activity
                startActivity(intent);
            }
        });

        new get_images_url().execute();
    }

    public void do_reload() {
        ViewImageGalleryAdapter = new ViewImageGalleryActivity(this, R.layout.activity_view_image_gallery, getData());
        gridView.setAdapter(ViewImageGalleryAdapter);
    }

    private ArrayList<ImageGalleryItem> getData() {
        return ImageGallery;
    }

    public class get_images_url extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {

            txtBoxId.setText("BOX #: ********");

            loaderShow("Please wait... Downloading Images...");

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                json = json_help.makeServiceCall(URL, JSONHelper.GET);

                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            // MODIFIED BY RONALD JUNE 21, 2018 ADDRESS THE ISSUE OF EMPTY BOX USING THE PREVIUOS UNIT IMAGES
            if (json != null) {
                JSONObject imgs = null;
                try {
                    imgs = new JSONObject(json);
                    String Status = imgs.getString("Message");
                    if (Status.equals("202")) {
                        json_list(json);
                    } else {
                        json_list(null);
                        Config.ImageGalleryContainer = null;
                        loaderHide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //json_list(json); PREVIOUS CODE HERE
        }
    }

    private void json_list(String json) {
        if (json != null) {
            try
            {
                JSONObject jsonObj = new JSONObject(json);

                String img_count       = jsonObj.optString("box_count").toString();

                Log.d("Message: ", img_count);

                JSONArray jsonMainNode = jsonObj.optJSONArray("list_of_all_box");

                int lengthJsonArr = jsonMainNode.length();

                Log.d("Message: ", lengthJsonArr + "");

                Integer i_count = Integer.parseInt(img_count);

                Config.ImageUrlList = new String[i_count];

                for(int i = 0; i < lengthJsonArr; i++)
                {
                    /****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    /******* Fetch node values **********/
                    String cats_unique_number      = jsonChildNode.optString("cats_unique_number").toString();
                    String cats_content      = jsonChildNode.optString("cats_content").toString();
                    String img_url           = jsonChildNode.optString("img_url").toString();

                    Config.ImageUrlList[i] = cats_unique_number + ";" + cats_content + ";" + img_url;

                    Log.d("Message: ", img_url);
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            new DownloadImageTask().execute();
        } else {
            Log.d("EMPTY", "TESTING EMPTY");
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        protected Bitmap doInBackground(Void... urls) {

            Bitmap bitmap = null;
            try {

                ImageGallery = new ArrayList<>();
                Config.ImageGalleryContainer = new ArrayList<>();

                int t_count_img = Config.ImageUrlList.length;

                for (int i = 0; i < t_count_img; i++) {
                    String[] img_data =  Config.ImageUrlList[i].split(";");

                    String img_id = img_data[0];
                    int grade_id = Integer.parseInt(img_data[1]);
                    String img_url = img_data[2];

                    Log.d("DONE", img_url);

                    bitmap = do_downloading(img_url);
                    Log.d("DONE", bitmap + "");

                    ImageGallery.add(new ImageGalleryItem(bitmap, "#" +img_id, grade_id, img_url, new int[] {160, 120}));
                    Config.ImageGalleryContainer.add(new ImageGalleryItem(bitmap, "#" +img_id, grade_id, img_url, new int[] {210, 170}));
                    Thread.sleep(200);
                }
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
            do_reload();

            txtBoxId.setText("BOX #: " + Config.get_box_id_show);
            loaderHide();
        }
    }

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(GalleryViewActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
