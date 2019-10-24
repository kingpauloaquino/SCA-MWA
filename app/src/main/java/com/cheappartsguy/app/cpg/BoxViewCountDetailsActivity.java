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
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

public class BoxViewCountDetailsActivity extends AppCompatActivity {

    ScrollView scrollBarCount;

    LinearLayout btnBack, btnViewUnits, btnBoxInformation;

    public TextView txtA, txtB, txtC, txtD, txtE, txtF, txtG, txtH, txtI, txtJ, txtK;

    public static String BOX_UID;

    String URL = ""; //"http://api.scrapcatapp.com/api/unit_count_dashboard_per_box/795edd365fd0e371ceaaf1ddd559a85d/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_view_count_details);

        URL = Config.Host + "/api/unit_count_dashboard_per_box/795edd365fd0e371ceaaf1ddd559a85d/";

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("COUNT DETAILS"));
        txtAccount.setText(Config.Worker_name);

        scrollBarCount = (ScrollView) findViewById(R.id.scrollBarCount);
        scrollBarCount.setScrollbarFadingEnabled(false);

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

        btnBoxInformation = (LinearLayout) findViewById(R.id.btnBoxInformation);
        btnBoxInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), BoxViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        URL = URL + Config.Seller_uid + "/" + Config.get_box_id;
        reset_text(false);

        new get_unit_information().execute();
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
            txtJ = (TextView) findViewById(R.id.boxJ);
            txtK = (TextView) findViewById(R.id.boxK);
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
        txtJ.setText("***");
        txtK.setText("***");
    }

    public void set_value(String a_, String b_, String c_, String d_, String e_, String f_, String g_, String h_, String i_, String j_, String k_) {
        txtA.setText(a_);
        txtB.setText(b_);
        txtC.setText(c_);
        txtD.setText(d_);
        txtE.setText(e_);
        txtF.setText(f_);
        txtG.setText(g_);
        txtH.setText(h_);
        txtI.setText(i_);
        txtJ.setText(j_);
        txtK.setText(k_);
    }

    public class get_unit_information extends AsyncTask<Void, Void, String> {
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
                String member_id = job.getString("member_id");
                Log.d("member_id: ", member_id);

                int IsPassed = Integer.parseInt(member_id);
                if(IsPassed > 0) {

                    String a = job.getString("account_name");
                    String b = job.getString("yard");
                    String c = job.getString("box_code");
                    String d = job.getString("full_units");
                    String e = job.getString("1_2_units");
                    String f = job.getString("3_4_units");
                    String g = job.getString("1_4_units");
                    String h = job.getString("empty_units");
                    String i = job.getString("q_units");
                    String j = job.getString("total_units");
                    String k = job.getString("total_full_equivalent_units");

                    set_value(
                            a,
                            b,
                            c,
                            d,
                            e,
                            f,
                            g,
                            h,
                            i,
                            j,
                            k
                    );
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
