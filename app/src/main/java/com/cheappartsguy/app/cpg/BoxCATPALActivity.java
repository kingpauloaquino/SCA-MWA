package com.cheappartsguy.app.cpg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BoxCATPALActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private ProgressDialog pDialog;
    public String box_number = null;
    public View view_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_catpal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("ScrapCatApp WORKER");

        TextView yard_name = (TextView) findViewById(R.id.yard_name);
        yard_name.setText(Config.get_yard_name);

        Button btn_submit = (Button) findViewById(R.id.scanner);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQR(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                i = new Intent(getApplicationContext(), MainCATPALActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.action_main:
                i = new Intent(getApplicationContext(), OptionCATPALActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(getApplicationContext(), MainCATPALActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keycode, event);
    }

    public class createFolder extends AsyncTask<Void, Void, String> {
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
                String url = "http://cheappartsguy.com/mobile/create/temp/folder/" + box_number;
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
            boxCreated(json);
        }
    }

    private void boxCreated(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String status = job.getString("Status");
                Log.d("Name: ", status);

                int status200 = Integer.parseInt(status);
                if(status200 >= 200) {
                    Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                    i.putExtra("partNumber", box_number);
                    startActivity(i);
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public class addBox extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            loaderShow("Adding Box! Please wait...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                String url = Config.Host + Config.Url_set_box + Config.Token + "/" + Config.Seller_uid + "/" + Config.Worker_uid + "/" + Config.get_yard_id;
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
            addBoxStatus(json);
        }
    }

    private void addBoxStatus(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String status = job.getString("Message");
                Log.d("Name: ", status);
                int status200 = Integer.parseInt(status);
                if(status200 >= 202) {
                    messageAlert("Add box was successful.", "INFORMATION");
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
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

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(BoxCATPALActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    // QR Code Command

    public void scanQR(View v) {
        try {
            view_ = v;
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(BoxCATPALActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public String SCAN_RESULT = null;
    public String SCAN_RESULT_FORMAT = null;
    public String QR_BOX_ID = null;
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                SCAN_RESULT = intent.getStringExtra("SCAN_RESULT");
                SCAN_RESULT_FORMAT = intent.getStringExtra("SCAN_RESULT_FORMAT");

                String[] results = SCAN_RESULT.split("-");
                QR_BOX_ID = results[0];
                new checkQrCode().execute();
            }
        }
    }

    public class checkQrCode extends AsyncTask<Void, Void, String> {
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
                String url = Config.Host + Config.Url_check_box_status + Config.Token + "/" + QR_BOX_ID
                        ;
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
            QrCodeStatus(json);
        }
    }

    private void QrCodeStatus(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String status = job.getString("Message");
                Log.d("Name: ", status);
                int status200 = Integer.parseInt(status);
                if(status200 == 202) {
                    String seller_id = job.getString("seller_id");
                    String box_status = job.getString("box_status");
                    int sid = Integer.parseInt(seller_id);
                    int b_status = Integer.parseInt(box_status);

                    if(sid != Config.Seller_uid) {
                        messageAlert("ALERT", "This BOX CODE is not yours.");
                        return;
                    }

                    if(b_status == 3) {
                        messageAlert("ALERT", "This BOX CODE is closed.");
                        return;
                    }

                    String[] results = SCAN_RESULT.split("-");
                    String message = "BOX#: " + results[1] + ", Do you want to proceed to take a photo?";
                    showDialog2(BoxCATPALActivity.this,
                            "CONFIRMING",
                            message,
                            "Yes", "No").show();
                }
                else {
                    String message = "Oops, Invalid box#, Do you want to retry?";
                    showDialog3(BoxCATPALActivity.this,
                            "CONFIRMING",
                            message,
                            "Yes", "No").show();
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private AlertDialog showDialog2(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                String contents = SCAN_RESULT;
                String format = SCAN_RESULT_FORMAT;

                String[] results = SCAN_RESULT.split("-");

                Config.get_box_id = results[0];
                Log.d("BOX_UID: ", "> " + Config.get_box_id);

                box_number = Config.get_yard_id + "-" + Config.get_box_id;
                new createFolder().execute();
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    private AlertDialog showDialog3(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                scanQR(view_);
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }


}
