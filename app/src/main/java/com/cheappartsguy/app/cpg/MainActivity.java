package com.cheappartsguy.app.cpg;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog pDialog;

    public String partNumber;
    public String int_partNumber;
    public String sub_partNumber;

    public String saveUrl;
    public TextView part_number_id;
    public TextView item_name;
    public EditText upc_code;
    public EditText model;
    public EditText manufacturer;
    public EditText owner;
    public EditText weight;
    public EditText qty;
    public Spinner warehouse;
    public Spinner category;
    public Spinner condition;
    public Button btnsave;
    public Context context;
    public String user_id;
    public SearchView search;
    public static boolean bypassCamera = false;


    // Session Manager Class
    SessionManagerHelper session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CPG | Add Listing");
        setSupportActionBar(toolbar);

        // Session class instance
        session = new SessionManagerHelper(getApplicationContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        user_id = user.get(SessionManagerHelper.KEY_NAME);

        btnsave = (Button) findViewById(R.id.btnAddItemOnly);
        part_number_id = (TextView) findViewById(R.id.part_id);
        new getPartNumber().execute((String) null);

        item_name = (EditText) findViewById(R.id.item_name_id);
        upc_code = (EditText) findViewById(R.id.upc_code_id);
        model = (EditText) findViewById(R.id.model_id);
        manufacturer = (EditText) findViewById(R.id.manu_id);
        owner = (EditText) findViewById(R.id.owner_id);
        weight = (EditText) findViewById(R.id.weight_id);
        qty = (EditText) findViewById(R.id.qty_id);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bypassCamera = false;
                validateInput();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bypassCamera = true;
                validateInput();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.Warehouse();
        this.Category();
        this.Condition();
    }

    public void Warehouse()
    {
        warehouse = (Spinner) findViewById(R.id.ddlWarehouse);
        ArrayList<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter;
        list.add("-- Warehouse --");
        list.add("Los Angeles");
        list.add("Hawaii");
        list.add("CDG");
        list.add("CP1");
        list.add("LA-PBS");
        list.add("LA-JMZ");
        list.add("SFD");
        list.add("HI-DBO");
        list.add("MBT");
        list.add("HEB");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warehouse.setAdapter(adapter);
    }

    public void Category()
    {
        category = (Spinner) findViewById(R.id.ddlCatergory);
        ArrayList<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter;
        list.add("-- Category --");
        list.add("Automobile & Truck Parts");
        list.add("Machinery & Heavy Equipment");
        list.add("Hardware & Building Materials");
        list.add("Tools and Industrial Parts");
        list.add("Computers & Accessories");
        list.add("Household & Office Items");
        list.add("Sports & Medical Equipment");
        list.add("Recyclable Material");
        list.add("Miscellaneous Items");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
    }

    public void Condition()
    {
        condition = (Spinner) findViewById(R.id.ddlCondition);
        ArrayList<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter;
        list.add("-- Condition --");
        list.add("NEW");
        list.add("GREAT");
        list.add("GOOD");
        list.add("POOR");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        condition.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            part_number_id.setVisibility(View.VISIBLE);
            resetAllFields();
            return true;
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.searchView1) {
//            Config.IsSearch = Integer.parseInt(user_id);
//            Intent i = new Intent(getApplicationContext(), SearchActivity.class);
//            startActivity(i);
//            finish();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class getPartNumber extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            part_number_id.setText("Fetching...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            // Creating service handler class instance
            String json = null;
            try {
                // Simulate network access.
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                String url = "http://www.cheappartsguy.com/api/part-number/"+user_id;
                Log.d("Response: ", "> " + url);
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            PartNumber_showAlert(json);
            super.onPostExecute(json);
        }
    }

    private void PartNumber_showAlert(String json) {
        String PartNumber = "N/A";
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                PartNumber = job.getString("Part_Number");
                int_partNumber = job.getString("Date_Format");
                sub_partNumber = job.getString("Random_Number");
                Log.d("Name: ", PartNumber);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        part_number_id.setText(PartNumber);
    }

    public void validateInput()
    {
        int wh_id =  warehouse.getSelectedItemPosition();
        int ct_id =  category.getSelectedItemPosition();
        int cn_id =  condition.getSelectedItemPosition();
        String cn_string =  condition.getSelectedItem().toString();

        String in_id = item_name.getText().toString();
        String sr_id = upc_code.getText().toString();
        String md_id = model.getText().toString();
        String mf_id = manufacturer.getText().toString();
        String on_id = owner.getText().toString();
        String wg_id = weight.getText().toString();
        String qt_id = qty.getText().toString();

        if(in_id.length() == 0) {
            messageAlert("Check item name", "Alert");
            return;
        }

        if(sr_id.length() == 0) {
            sr_id = "N/A";
        }
        if(md_id.length() == 0) {
            md_id = "N/A";
        }
        if(mf_id.length() == 0) {
            mf_id = "N/A";
        }
        if(on_id.length() == 0) {
            on_id = "N/A";
        }
        if(wg_id.length() == 0) {
            wg_id = "0";
        }
        if(qt_id.length() == 0) {
            qt_id = "0";
        }

        if(wh_id == 0) {
            messageAlert("Check warehouse location", "Alert");
            return;
        }
        if(ct_id == 0) {
            messageAlert("Check category", "Alert");
            return;
        }
        if(cn_id == 0) {
            messageAlert("Check condition", "Alert");
            return;
        }

        partNumber = part_number_id.getText().toString();
        saveUrl = "";
        saveUrl +="?wh=" + wh_id;
        saveUrl +="&ip=" + int_partNumber;
        saveUrl +="&sp=" + sub_partNumber;
        saveUrl +="&pn=" + partNumber;
        saveUrl +="&item=" + in_id.replace(" ", "%20");
        saveUrl +="&cat=" +ct_id;
        saveUrl +="&model=" + md_id.replace(" ", "%20");
        saveUrl +="&serial=" + sr_id.replace(" ", "%20");
        saveUrl +="&man=" + mf_id.replace(" ", "%20");
        saveUrl +="&con=" + cn_string;
        saveUrl +="&own=" + on_id.replace(" ", "%20");
        saveUrl +="&wg=" + wg_id.replace(" ", "%20");
        saveUrl +="&qt=" + qt_id.replace(" ", "%20");
        saveUrl +="&u_id="+user_id;
        Log.d("API: ", saveUrl);
        new saveAddItem().execute();
    }

    public class saveAddItem extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            // Creating service handler class instance
            String json = null;
            try {
                // Simulate network access.
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                String url = "http://www.cheappartsguy.com/api/mobile/add-item" + saveUrl;
                Log.d("Response: ", "> " + url);
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            SaveItem_showAlert(json);
            super.onPostExecute(json);
        }
    }

    private void SaveItem_showAlert(String json) {
        String EncodedStatus = "N/A";
        String tempPartNumber = null;
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                EncodedStatus = job.getString("EncodedStatus");
                if(EncodedStatus == "NOK") {
                    messageAlert("Save wasn't successful.", "Information");
                    return;
                }

                if(bypassCamera) {
                    messageAlert("Save was successful.", "Information");
                    resetAllFields();
                    return;
                }
                else {
                    messageAlert("Save was successful.", "Information");
                    tempPartNumber = partNumber;
                    resetAllFields();

                    Intent i = new Intent(MainActivity.this, ImageViewActivity.class);
                    i.putExtra("partNumber", tempPartNumber);
                    startActivity(i);
                    Log.d("Name: ", EncodedStatus);
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

    private void resetAllFields() {
        warehouse.setSelection(0);
        category.setSelection(0);
        condition.setSelection(0);
        item_name.setText("");
        upc_code.setText("");
        model.setText("");
        manufacturer.setText("");
        owner.setText("");
        weight.setText("");
        qty.setText("");
        new getPartNumber().execute((String) null);
    }
}
