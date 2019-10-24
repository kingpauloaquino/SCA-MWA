package com.cheappartsguy.app.cpg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainCATPALActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    private static String lots_number = null;
    private static String yard_number = null;
    private static String box_number = null;

    public EditText txt_lotsNumber;
    public Spinner box_pull_down_menu;
    public Button btn_submit, btn_camera;

    Button[] btnWord = new Button[5];
    LinearLayout linear_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_catpal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("ScrapCatApp WORKER");

        new getBoxListing().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), OptionCATPALActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.action_refresh:
                linear_layout.removeAllViews();
                new getBoxListing().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(getApplicationContext(), OptionCATPALActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keycode, event);
    }

    public class getBoxListing extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            loaderShow("Please wait...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                String url = Config.Host + Config.Url_get_yard_list + Config.Token + "/" + Config.Seller_uid;
                Log.d("Response: ", "> " + Config.Seller_uid);
                Log.d("Response: ", "> " + url);
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            loaderHide();
            populateBoxList(json);
        }
    }

    private void populateBoxList(String json) {
        if (json != null) {
            try
            {
                linear_layout = (LinearLayout) findViewById(R.id.catpal_main);
                JSONObject job = new JSONObject(json);
                String box_count = job.getString("yard_count");
                Log.d("Name: ", box_count);

                TextView title = new TextView(this);
                title.setText("Choose Yard");
                title.setGravity(Gravity.CENTER);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                title.setTextSize(30);
                title.setLayoutParams(
                        paramLinearLayout(0, 20, 0, 20)
                );
                linear_layout.addView(title);
                linear_layout.setOrientation(LinearLayout.VERTICAL);  //Can also be done in xml by android:orientation="vertical"

                int box_count_int = Integer.parseInt(box_count);
                for (int i = 0; i < box_count_int; i++) {
                    LinearLayout row = new LinearLayout(this);
                    String yardID = job.getString("yard_id_"+i);
                    String yardName = job.getString("country_"+i) + ", " + job.getString("city_"+i);

                    Button btnWord = new Button(this);
                    btnWord.setId(i);
                    btnWord.setLayoutParams(
                            paramLinearLayout(0, 2, 0, 0)
                    );
                    btnWord.setTag(yardID + "-" + yardName);
                    btnWord.setText(yardName);
                    btnWord.setPadding(25, 25, 25, 25);
                    btnWord.setTextSize(20);
                    btnWord.setTextColor(Color.parseColor("#ffffff"));
                    btnWord.setBackgroundColor(Color.parseColor("#D15792"));
                    btnWord.setTypeface(btnWord.getTypeface(), Typeface.BOLD);
                    btnWord.setOnClickListener(btnClicked);
                    row.addView(btnWord);
                    linear_layout.addView(row);
                }

//                int box_count_int = Integer.parseInt(box_count);
//                for(int i = 0; i < box_count_int; i++) {
//
//                    LinearLayout row = new LinearLayout(this);
//                    String yardID = job.getString("yard_id_"+i);
//                    String yardName = job.getString("country_"+i) + " - " + job.getString("city_"+i);
//
//                    Log.d("yard_name > ", yardName);
//
//                    btnWord[i] = new Button(this);
//                    btnWord[i].setHeight(50);
//                    btnWord[i].setWidth(50);
//                    btnWord[i].setPadding(25, 25, 25, 25);
//                    btnWord[i].setTextSize(20);
//                    btnWord[i].setTextColor(Color.parseColor("#000000"));
//                    btnWord[i].setBackgroundColor(Color.parseColor("#D15792"));
//                    btnWord[i].setTypeface(btnWord[i].getTypeface(), Typeface.BOLD);
//                    btnWord[i].setTag(yardID + "-" + yardName);
//                    btnWord[i].setOnClickListener(btnClicked);
//                    btnWord[i].setLayoutParams(
//                            paramLinearLayout(0, 2, 0, 0)
//                    );
//
//                    btnWord[i].setText(yardName);
//                    row.addView(btnWord[i]);
//                    linear_layout.addView(row);
//                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    View.OnClickListener btnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            Log.d("TAG: ", "> " + tag.toString());
            String[] yards = tag.toString().split("-");
            Config.get_yard_id = yards[0];
            Config.get_yard_name = yards[1];
            Log.d("YARD_UID: ", "> " + Config.get_yard_id);
            Intent i = new Intent(getApplicationContext(), BoxCATPALActivity.class);
            startActivity(i);
            finish();
        }
    };

    private LinearLayout.LayoutParams paramLinearLayout(Integer left, Integer top, Integer right, Integer bottom ) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private void messageAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loaderShow(String Message)
    {
        pDialog = new ProgressDialog(MainCATPALActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
