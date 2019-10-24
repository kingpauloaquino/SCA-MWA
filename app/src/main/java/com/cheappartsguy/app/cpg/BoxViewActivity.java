package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class BoxViewActivity extends AppCompatActivity {


    public TextView txtA, txtB, txtC, txtD, txtE, txtF, txtG, txtH, txtI;

    LinearLayout btnBack, btnViewUnits, btnCountDetails;

    String URL = ""; //"http://api.scrapcatapp.com/api/box_information_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/" + Config.get_box_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_view);

        URL = Config.Host + "/api/box_information_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/" + Config.get_box_id;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("BOX INFORMATION"));
        txtAccount.setText(Config.Worker_name);

        reset_text(false);

        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnViewUnits = (LinearLayout) findViewById(R.id.btnViewUnits);
        btnViewUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GalleryViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnCountDetails = (LinearLayout) findViewById(R.id.btnCountDetails);
        btnCountDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BoxViewCountDetailsActivity.class);
                startActivity(i);
                finish();
            }
        });

        new get_box_information().execute();
    }

    public void reset_text(boolean IsTrue) {

        if(!IsTrue) {
            txtA = (TextView) findViewById(R.id.boxA);
            txtB = (TextView) findViewById(R.id.boxB);
            txtC = (TextView) findViewById(R.id.boxC);
            txtD = (TextView) findViewById(R.id.boxD);
            txtE = (TextView) findViewById(R.id.boxE);
            txtF = (TextView) findViewById(R.id.boxF);
            txtG = (TextView) findViewById(R.id.boxG);
            txtH = (TextView) findViewById(R.id.boxH);
            txtI = (TextView) findViewById(R.id.boxI);
        }

        txtA.setText("***");
        txtB.setText("***");
        txtC.setText("***");
        txtD.setText("***");
        txtE.setText("***");
        txtF.setText("***");
        txtG.setText("***");
        txtH.setText("***");
        txtI.setText("***");
    }

    public void set_value(String a_, String b_, String c_, String d_, String e_, String f_, String g_, String h_, String i_) {
        txtA.setText(a_);
        txtB.setText(b_);
        txtC.setText(c_);
        txtD.setText(d_);
        txtE.setText(e_);
        txtF.setText(f_);
        txtG.setText(g_);
        txtH.setText(h_);
        txtI.setText(i_);
    }

    public class get_box_information extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            reset_text(true);
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
                Log.d("Response: ", "> " + URL);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            populate_seller_dashboard(json);
        }
    }

    private void populate_seller_dashboard(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String box_uid = job.getString("box_uid");
                Log.d("box_uid: ", box_uid);

                int IsPassed = Integer.parseInt(box_uid);
                if(IsPassed > 0) {
                    String a = job.getString("account_name");
                    String b = job.getString("yard_name");
                    String c = job.getString("box_code");
                    String d = job.getString("units_in_the_box");
                    String e = job.getString("box_status");
                    String f = job.getString("last_synch_by");
                    String g = job.getString("last_synch_time");
                    String h = job.getString("appraisal_complete");
                    String i = job.getString("included_in_lot_number");

                    set_value(
                            a,
                            b,
                            c,
                            d,
                            e,
                            f,
                            g,
                            h,
                            i
                    );
                }

                return;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
