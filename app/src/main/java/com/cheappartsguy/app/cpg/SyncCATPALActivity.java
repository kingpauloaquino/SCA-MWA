package com.cheappartsguy.app.cpg;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SyncCATPALActivity extends AppCompatActivity {

    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    private ProgressDialog pDialog;

    public static TextView txtIndividualProgress, txtCompletedA, txtCompletedB, txtScreenId;
    public static long totalSize;

    public static ProgressBar progressBar, progressBarCompleted;

    private static int uploaded = 0;
    private static int errors = 0;

    static int ctr = 0;
    static int content_ctr = 0;
    public static int isStart;

    public static int count_allImages = 0;

    public static boolean synchingProcessDone;

    private static LinearLayout btnSync, btnBack, mw_171, mw_170;
    public static BackgroundWorker BGWorker;

    public static List<GradeContentObject> Images_Content;
    public static List<GradeContentObject> Images_Data;

    public static GradeContentObject ToBeUploadData;

    public SQLiteDatabase sqlDB;
    public MySqlLite mysql;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 7;

    public static final int PERMISSIONS_REQUEST_GET_ACCOUNT = 133;

    public int ScreenCheckSizeIfUsing10Inches;

    public static ObjectNames notificationSizes;

    public static Context app_context;

    public ConnectivityManager connectivity_manager;

    public SQLiteDatabase CreatedDB() {
        sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
        mysql = new MySqlLite(sqlDB);
        return sqlDB;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_catpal);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("SYNCH PROCESS"));
        txtAccount.setText(Config.Worker_name);

        BackgroundService.isStop = true;
        Log.d("IsStop", BackgroundService.isStop + "");

        Images_Content = new ArrayList<GradeContentObject>();
        Images_Data = new ArrayList<GradeContentObject>();
        txtIndividualProgress = (TextView) findViewById(R.id.txtIndividualProgress);
        txtCompletedA = (TextView) findViewById(R.id.txtCompletedA);
        txtCompletedB = (TextView) findViewById(R.id.txtCompletedB);
        txtScreenId = (TextView) findViewById(R.id.txtScreenId);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarCompleted = (ProgressBar) findViewById(R.id.progressBarCompleted);

        CreatedDB();

        app_context = getApplicationContext();

        ScreenCheckSizeIfUsing10Inches = Config.getScreenOfTablet(app_context);

        txtScreenId = (TextView) findViewById(R.id.txtScreenId);
        mw_170 = (LinearLayout) findViewById(R.id.mw_170);
        mw_171 = (LinearLayout) findViewById(R.id.mw_171);

        txtScreenId.setText("MW-171");
        btnSync = (LinearLayout) findViewById(R.id.btnStartSynching);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    boolean IsGranted = isStoragePermissionGranted();
//                    if (IsGranted) {
//                        do_to();
//                    } else {
//                        Toast.makeText(SyncCATPALActivity.this, "No such permission to access storage!", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                }

                BackgroundWorker bbb = new BackgroundWorker();
                bbb.do_process();
            }
        });

        synchingProcessDone = false;
        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });

        BackgroundService.laps = 60 * 500;
        Log.d("Laps", BackgroundService.laps + "");

        connectivity_manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    String TAG = "TAG";

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission is granted");
                return true;
            } else {
                Log.i(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.i(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted " + grantResults[0] + " | " + PackageManager.PERMISSION_GRANTED, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No such permission to access account! " + grantResults[0] + " | " + PackageManager.PERMISSION_GRANTED, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void do_to() {
        mw_170.setVisibility(View.VISIBLE);
        mw_171.setVisibility(View.GONE);
        txtScreenId.setText("MW-170");

        ctr = 0;
        JSONHelper.UploadedImgCount = 0;
        JSONHelper.NotUploadedImgCount = 0;

        Images_Data = new ArrayList<GradeContentObject>();
        Images_Content = new ArrayList<GradeContentObject>();

        /// added Clearing History

        Images_Data.clear();
        Images_Content.clear();

        /// added Clearing History

        Log.d("TEST1x",  "asdsad");
        int resize_screen = 400;
        if (ScreenCheckSizeIfUsing10Inches == 1) {
            resize_screen = 710;
        } else if (ScreenCheckSizeIfUsing10Inches == 2) {
            resize_screen = 710;
        }

        BGWorker = new BackgroundWorker();
        if (BGWorker.do_process()) {
            count_allImages = Images_Data.size();

            if (count_allImages == 0) {

                Log.d("SCREEN_XX", resize_screen + "");

                showDialogForDynamic(
                        SyncCATPALActivity.this,
                        "SYNCH UNIT PHOTO AND INFORMATION",
                        "Oops, No images found.", resize_screen, 0, false);

                mw_170.setVisibility(View.GONE);
                mw_171.setVisibility(View.VISIBLE);
                txtScreenId.setText("MW-171");

                Log.d("TEST1x",  "ABBB");
                return;
            }

            Log.d("TEST1x",  "BCCC");

            progressBarCompleted.setMax(count_allImages);
            Log.d("TOTAL_IMAGES", "" + count_allImages);
            execute_uploading();

            return;
        }

        Log.d("TEST1x",  "CDDD");
        showDialogForDynamic(
                SyncCATPALActivity.this,
                "SYNCH UNIT PHOTO AND INFORMATION",
                "Oops, No images found.", resize_screen, 0, false);
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {

            ObjectNames sizes = new ObjectNames();
            sizes.tab8Sizes = new int[]{0, 0, 360, 600};
            sizes.tab10Sizes = new int[]{0, 0, 650, 600};
            sizes.tabS2Sizes = new int[]{0, 0, 860, 600};

            notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, true);

            String message_ = "Oops, Your not able to go back, it's being synced.\n";

            showDialogForDynamicError(
                    SyncCATPALActivity.this,
                    "ERROR ALERT",
                    message_, notificationSizes.Width, notificationSizes.Height, 172, 1);

        }
        return super.onKeyDown(keycode, event);
    }

    public void showDialogForQRScanned(Activity activity, String title, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_confirmation_dialog_catpal);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        TextView text_dialog = (TextView) dialog.findViewById(R.id.text_dialog);
        text_dialog.setText(message);

        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        btn_dialog_YES.setText("OK");
        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);
        btn_dialog_NO.setVisibility(View.INVISIBLE);
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void run_handler(int status) {
        isStart = status;
        h2.postDelayed(run, 0);
    }

    int timer = 500;
    //runs without timer be reposting self
    Handler h2 = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            if (isStart == 1) {
                h2.postDelayed(run, timer);
                Start();
                return;
            }
            h2.postDelayed(run, 0);
            Stop();
        }
    };

    public void run_dialog(int status) {
        isStart = status;
        h2_dialog.postDelayed(run, 0);
    }

    //runs without timer be reposting self
    Handler h2_dialog = new Handler();
    Runnable run_dialog = new Runnable() {
        @Override
        public void run() {
            if (isStart == 1) {
                h2_dialog.postDelayed(run_dialog, timer);
                loaderShow("Please wait...");
                return;
            }
            h2_dialog.postDelayed(run_dialog, 0);
            loaderHide();
        }
    };

    private void Start() {
        Log.d("TIMER_Handler", "Counter " + ctr);
        if (ctr == 3) {
            h2.removeCallbacks(run);
            Log.d("TIMER_Handler", "SHOULD BE STOPPED");
            try {
                BGWorker = new BackgroundWorker();
                BGWorker.run();
            } catch (Exception e) {
            }
        }
        if (ctr == 2) {
            timer = 0;
        }
        ctr++;
    }

    private void Stop() {
        h2.removeCallbacks(run);
        Log.d("TIMER_Handler", "STOPPED");
    }

    public void execute_uploading() {
        new UploadFileToServer().execute();
    }

    public void execute_remove_index() {
        if (Images_Data.size() > 0) {
            Images_Data.remove(0);
            execute_uploading();
        }
    }

    public class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
            progressBarCompleted.setProgress(0);
            txtIndividualProgress.setText("0%");

            txtCompletedA.setText("0 of " + count_allImages);
            txtCompletedB.setText("0 of " + count_allImages);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // updating progress bar value
            progressBar.setProgress(progress[0]);
            txtIndividualProgress.setText(progress[0] + "%");
            // updating percentage value

            txtCompletedA.setText(JSONHelper.UploadedImgCount + " of " + count_allImages);
            txtCompletedB.setText(JSONHelper.UploadedImgCount + " of " + count_allImages);
            progressBarCompleted.setProgress(JSONHelper.UploadedImgCount);
        }

        @Override
        protected String doInBackground(Void... params) {
            int code = 0;
            try {
                for (GradeContentObject d : Images_Data) {

                    Log.d("Image Name " + content_ctr, d.ImageName);
                    Log.d("Image Name " + content_ctr, d.ImagePath);
                    Log.d("Image Url " + content_ctr, d.UrlOfServer);

                    code = uploadFile(d);
                    Thread.sleep(3000);

                    if (code == -1) {
                        return "NoInternet";
                    }

                    if (code == 200) {
                        JSONHelper.UploadedImgCount++;
                    } else {
                        JSONHelper.NotUploadedImgCount++;
                    }
                }
            } catch (Exception ex) {
            }
            return null;
        }

        @SuppressWarnings("deprecation")
        private int uploadFile(GradeContentObject data) {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(data.UrlOfServer);

            int statusCode = 0;

            Boolean ethernet = Config.haveNetworkConnection(connectivity_manager);
            if (!ethernet) {
                return -1;
            }

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                BackgroundWorker.IMAGE_TEMP = data.ImagePath;
                File sourceFile = new File(BackgroundWorker.IMAGE_TEMP);
                // Adding file data to http body
                entity.addPart("file", new FileBody(sourceFile));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = "Upload was successful."; //EntityUtils.toString(r_entity);
                    GradeContentObject objContent = new GradeContentObject();
                    objContent.BoxId = data.BoxId;
                    objContent.ImageName = data.ImageName;
                    objContent.ImagePath = data.ImagePath;
                    objContent.GradeValue = data.GradeValue;
                    Images_Content.add(objContent);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
                Log.d("HTTP", statusCode + "");
                Log.d("Filename3: ", responseString);
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return statusCode;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                if (result.contains("NoInternet")) {
                    showDialogForDynamicError(SyncCATPALActivity.this, "Internet Connection", "ScrapCATapp Mobile Worker app cannot connect to your account via the internet connection please check your internet connection and try again.", 0, 350, 80, 1);
                    return;
                }
            }

            txtCompletedA.setText(JSONHelper.UploadedImgCount + " of " + count_allImages);
            txtCompletedB.setText(JSONHelper.UploadedImgCount + " of " + count_allImages);
            progressBarCompleted.setProgress(JSONHelper.UploadedImgCount);

            done_Synching();

            super.onPostExecute(result);

        }
    }

    public void done_Synching() {
        String messages = "ScrapCATapp Synch Process Result:\n\n";
        messages += JSONHelper.UploadedImgCount + " of " + count_allImages + " Successfully Synched\n";
        messages += JSONHelper.NotUploadedImgCount + " Errors\n\n";
        messages += "Press OK to Synch Additional Information.";

        int resize_screen = 460;
        if (ScreenCheckSizeIfUsing10Inches == 1) {
            resize_screen = 650;
        } else if (ScreenCheckSizeIfUsing10Inches == 2) {
            resize_screen = 910;
        }

        Log.d("SCREEN_XX", resize_screen + "");

        showDialogForDynamic(
                SyncCATPALActivity.this,
                "SYNCH UNIT PHOTO AND INFORMATION",
                messages, resize_screen, 173, true);
    }

    // grade content

    public void do_upload_comments() {
        new doContentImageGrade().execute();
    }

    public class doContentImageGrade extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            loaderShow("Please wait...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            content_ctr = 0;
            for (GradeContentObject grade : Images_Content) {
                Log.d("Box Id " + content_ctr, grade.BoxId);
                result = do_execute(grade);

                BGWorker.RemoveToImageList(grade.ImagePath);
                content_ctr++;

                Log.d("INSIDE_LOOP", "" + content_ctr);
            }

            Log.d("OUTSIDE_LOOP", "" + "x");
            result = null;
            return result;
        }

        public String do_execute(GradeContentObject grade) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                String[] box_uid = grade.BoxId.split("_");
                JSONHelper json_help = new JSONHelper();
                String xImageFile = "mobile_m_" + grade.ImageName;
                String url = Config.Host + "/api/upload_cats_worker_mobile/795edd365fd0e371ceaaf1ddd559a85d/" + box_uid[1] + "/" + xImageFile + "/" + grade.GradeValue;

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
        protected void onPostExecute(String json) {
            do_comment_process();
        }
    }

    public void do_comment_process() {
        new submitComment().execute();
    }

    // process comment

    private class submitComment extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            content_ctr = 0;
            Cursor c = mysql.select("SELECT * FROM list_of_comments;");
            while (c.moveToNext()) {
                Log.d("box_id", "" + c.getString(0));
                Log.d("worker_id", "" + c.getString(1));
                Log.d("image_name", "" + c.getString(2));
                Log.d("comment", "" + c.getString(3));

                if (c.getString(3).length() > 0) {
                    String ImageName = "mobile_m_" + c.getString(2);
                    do_execute(c.getString(0), c.getString(1), ImageName, c.getString(3));
                }

                Log.d("INSIDE_LOOP", "" + content_ctr);
            }
            Log.d("OUTSIDE_LOOP", "" + "x");
            result = null;
            return result;
        }

        public String do_execute(String BoxId, String WorkerId, String ImageName, String Comment) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                // RONALD CHANGE POSTING OF COMMENTS TO POST METHOD TO ACCOMODATE LONG COMMENTS AND SPECIAL CHARACTER
                HttpClient httpClient = new DefaultHttpClient();
                // replace with your url + Config.Token
                //String url = Config.Host + Config.Url_post_comment + Config.Token + "/" + BoxId + "/" + ImageName + "/" + WorkerId +"/" + Comment;
                HttpPost httpPost = new HttpPost(Config.Host + "/api/v2/cats_comments_mobile");

                //Log.d("Http Post COMMENTS:", Config.Host + "/api/v2/cats_comments_mobile/");
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
                Log.d("Http Post COMMENTS:", Comment);
                nameValuePair.add(new BasicNameValuePair("cats_images_name", ImageName));
                nameValuePair.add(new BasicNameValuePair("user_id", WorkerId));
                nameValuePair.add(new BasicNameValuePair("comments", Comment));
                //Encoding POST data
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                } catch (UnsupportedEncodingException e) {
                    // log exception
                    e.printStackTrace();
                }

                Log.d("Name Pair:", Comment);

                //POST data
                try {
                    Log.d("Http Post Inside:", "HTTP POSTING");
                    HttpResponse response = httpClient.execute(httpPost);
                    String server_response = EntityUtils.toString(response.getEntity());

                    Log.i("Server response", server_response);

                    int status = Integer.parseInt(server_response);
                    Log.d("Http Post after:", server_response);

                    if (status == 202) {
                        String query = "DELETE FROM list_of_comments WHERE box_id = '" + BoxId + "';";
                        boolean xStatus = mysql.execute(query);
                        Log.d("Name: ", "" + xStatus);
                    } else {
                        Log.d("Name: ", "Error");
                    }
                    return json;
                } catch (UnsupportedEncodingException e) {
                    // log exception
                    e.printStackTrace();
                }

                //making POST request.
                /*try {
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    Log.d("Http Post Response:", response.toString());
                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }*/


                // THIS WAS COMMENT OUT BY RONALD AGUIRRE AT 12/15/2017 CHANGE TO POST METHOD
                /*String _img_name  = "mobile_m_" +  ImageName;
                JSONHelper json_help = new JSONHelper();
                String url = Config.Host + Config.Url_post_comment + Config.Token + "/" + BoxId + "/" + ImageName + "/" + WorkerId +"/" + Comment;
                Log.d("Response: ", "> " + url);
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                Log.d("Response: ", "> " + json);

                JSONObject job = new JSONObject(json);
                String Status = job.getString("Message");
                Log.d("Name: ", Status);

                int status = Integer.parseInt(Status);
                if(status == 202) {
                    String query = "DELETE FROM list_of_comments WHERE box_id = '"+BoxId+"';";
                    boolean xStatus = mysql.execute(query);
                    Log.d("Name: ", "" + xStatus);
                }
                else {
                    Log.d("Name: ", "Error");
                }
                return json;*/
            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            loaderHide();
            String messages = "Synching Process is complete, If all of the Units did not Synch Successfully, Please return to the Dashboard and re-select Synch Unit Photos/Information.";

            int resize_screen = 440;
            if (ScreenCheckSizeIfUsing10Inches == 1) {
                resize_screen = 635;
            } else if (ScreenCheckSizeIfUsing10Inches == 2) {
                resize_screen = 830;
            }

            Log.d("SCREEN_XX", resize_screen + "");
            showDialogForDynamic(
                    SyncCATPALActivity.this,
                    "SYNCH UNIT PHOTO AND INFORMATION",
                    messages, resize_screen, 174, true);
            super.onPostExecute(json);
        }
    }

      private void loaderShow(String Message) {
        pDialog = new ProgressDialog(SyncCATPALActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide() {
        pDialog.dismiss();
    }

    public void showDialogForDynamic(Activity activity,
                                     String caption,
                                     String message,
                                     int height,
                                     final int number,
                                     final boolean IsNothing) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_alert_ok);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;*/

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);

        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MW-" + number);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (number == 0) {
                    Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }

                if (!IsNothing) {
                    dialog.dismiss();
                    return;
                }

                if (number == 173) {
                    do_upload_comments();
                    dialog.dismiss();
                    return;
                }

                if (number == 174 || number == 0) {
                    Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }
            }
        });

        dialog.show();

        //dialog.getWindow().setAttributes(lp);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.50f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        } else {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.7f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void showDialogForDynamicError(Activity activity,
                                          String caption,
                                          String message,
                                          int width,
                                          int height,
                                          final int number, int button) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_custom_dialog_error);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;

        if(width > 0) {
            lp.width = width;
        }*/

        LinearLayout LinearCancel = (LinearLayout) dialog.findViewById(R.id.LinearCancel);
        LinearLayout LinearOptions = (LinearLayout) dialog.findViewById(R.id.LinearOptions);

        Button btn_dialog_DEFAULT = (Button) dialog.findViewById(R.id.btn_dialog_DEFAULT);
        Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        Button btn_dialog_CANCEL = (Button) dialog.findViewById(R.id.btn_dialog_CANCEL);

        switch (button) {
            case 0: // OPTIONS
                if (LinearCancel.getVisibility() == View.VISIBLE) {
                    LinearCancel.setVisibility(View.GONE);
                }

                if (LinearOptions.getVisibility() != View.VISIBLE) {
                    LinearOptions.setVisibility(View.VISIBLE);
                }
                break;
            case 1: // CANCEL

                if (LinearOptions.getVisibility() == View.VISIBLE) {
                    LinearOptions.setVisibility(View.GONE);
                }

                if (LinearCancel.getVisibility() != View.VISIBLE) {
                    LinearCancel.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        //Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MW-" + number);

        btn_dialog_DEFAULT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (number == 80) {
                    Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                    startActivity(i);
                    finish();
                    return;
                } else {
                    dialog.dismiss();
                }

            }
        });

        //dialog.getWindow().setAttributes(lp);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.50f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        } else {
            // Get screen width and height in pixels
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.6f);
            // Set alert dialog height equal to screen height 70% 0.7f
            int dialogWindowHeight = (int) (displayHeight);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = lp.height; //dialogWindowHeight;

            dialog.getWindow().setAttributes(layoutParams);
        }
    }

}

