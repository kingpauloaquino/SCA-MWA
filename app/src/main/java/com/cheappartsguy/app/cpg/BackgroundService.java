package com.cheappartsguy.app.cpg;


import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by king on 7/25/2016.
 */

public class BackgroundService extends IntentService {

    private ProgressDialog pDialog;
    public static SQLiteDatabase sqlDB;
    public MySqlLite mysql;
    public static boolean RESET_DB;
    private static final String TAG = "HelloService";
    public static boolean isRunning;
    public static boolean isStop;
    public static boolean stopBackgroundWorker;
    public static int counter  = 0;
    public static int laps  = 1;
    public String API_URL;

    public BackgroundService() {
        super(BackgroundService.class.getName());
        isRunning = false;
        Log.i(TAG, "Service onCreate");
        API_URL = Config.Host + Config.Url_get_all_boxes + Config.Token + "/" + Config.Worker_uid;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");
        try {
            if(!isStop) {
                do_async();
            }
            Log.d(TAG, "json -> " + API_URL);
        } catch (Exception e) {
            Log.d(TAG, "status -> " + e.toString());
        }
        this.stopSelf();
    }

    private Handler customHandler = new Handler();
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            isRunning = true;
            Log.d("TIMER_Handler", "Counter " + counter);
            if(counter == laps) {
                boolean hasInternet = Config.hostAvailableisOnline();
                Log.d("Response: ", "> " + hasInternet);
                if(hasInternet) {
                    customHandler.removeCallbacks(updateTimerThread);
                    Log.d("TIMER_Handler", "SHOULD BE STOPPED");
                    if(!isStop) {
                        do_async();
                    }
                    return;
                }
                laps = 60 * 120;
                counter = 0;
                customHandler.postDelayed(updateTimerThread, 0);
                return;
            }
            counter++;
            Log.d("TIMER_Handler", "COUNT: " + counter);
            customHandler.postDelayed(this, 1000);
        }
    };

    public void do_async() {
        new do_fetching().execute();
    }

    public class do_fetching extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                Log.d("Response: ", "> " + API_URL);
                json = json_help.makeServiceCall(API_URL, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            do_update_db(json);
        }

    }

    public void do_update_db(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String box_counts = job.getString("box_count");
                Log.d("BOX Count: ", box_counts);

                JSONArray jsonMainNode = job.optJSONArray("data");
                int lengthJsonArr = jsonMainNode.length();

                sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
                mysql = new MySqlLite(sqlDB);

                for(int i=0; i < lengthJsonArr; i++)
                {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String box_uid = jsonChildNode.optString("Id").toString();
                    String worker_id = jsonChildNode.optString("worker_of").toString();
                    String yard_id = jsonChildNode.optString("yard_id").toString();
                    String box_id = jsonChildNode.optString("box_id").toString();
                    String box_code = jsonChildNode.optString("box_code").toString();
                    String box_status = jsonChildNode.optString("box_status").toString();
                    try {
                        if(Integer.parseInt(box_status) == 0) {
                            box_status = "5";
                        }
                    }
                    catch(Exception e)
                    {
                        box_status = "5";
                    }
                    Log.d("Results: box_uid", "> " + box_uid );
                    Log.d("Results: worker_id", "> " + worker_id );
                    Log.d("Results: yard_id", "> " + yard_id );
                    Log.d("Results: box_id", "> " + box_id );
                    Log.d("Results: box_code", "> " + box_code );
                    Log.d("Results: box_status", "> " + box_status );
                    mysql.insert("list_of_boxes", "'" + box_uid + "', '" + worker_id + "', '" + yard_id + "', '" + box_id + "', '" + box_code + "', '" + box_status + "'");
                }

                return;

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        isRunning = false;
        laps = 60 * 120;
        counter = 0;
    }

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(BackgroundService.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide() {
        pDialog.dismiss();
    }

}