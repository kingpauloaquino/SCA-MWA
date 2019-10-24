package com.cheappartsguy.app.cpg;


import android.content.Intent;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.BufferedReader;
import java.io.DataInputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.File;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    public  ServiceWorker s_worker;
    public Context m_context;

    File myInternalFile;
    File myExternalFile;

    private String filename = "MySampleFile.txt";
    private String filepath = "CATPAL";
    private int uid = 1;

    SQLiteDatabase sqlDB;
    MySqlLite mysql;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.setTitle("CPG | Search");

        m_context = getApplicationContext();
        sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);

        mysql = new MySqlLite(sqlDB);
        mysql.execute("DELETE FROM list_of_boxes;");


        BackgroundService.RESET_DB = true;
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);
        startService(intent);

        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Button btnText = (Button) findViewById(R.id.btnText);
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = mysql.select("SELECT * FROM list_of_boxes");
                while(c.moveToNext())
                {
                    Log.d("DB_STATUS", "ID: " +c.getString(0));
                }
            }
        });
    }

//    public static void saveIntoDatabase(String json) {
//        try
//        {
//            JSONObject job = new JSONObject(json);
//            String box_counts = job.getString("box_count");
//            Log.d("BOX Count: ", box_counts);
//
//            int box_length = Integer.parseInt(box_counts);
//            if(box_length > 0) {
//
//                sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
//
//                for(int i = 0; i < box_length; i++) {
//                    String box_uid = job.getString("Id_" + i);
//                    String worker_id = job.getString("worker_of_" + i);
//                    String yard_id = job.getString("yard_id_" + i);
//                    String box_id = job.getString("box_id_" + i);
//                    String box_status = job.getString("box_status_" + i);
//
//                    Log.d("Results: box_uid", "> " + box_uid );
//                    Log.d("Results: worker_id", "> " + worker_id );
//                    Log.d("Results: yard_id", "> " + yard_id );
//                    Log.d("Results: box_id", "> " + box_id );
//                    Log.d("Results: box_status", "> " + box_status );
//                    mysql.insert("list_of_boxes", "'" + box_uid + "', '" + worker_id + "', '" + yard_id + "', '" + box_id + "', '" + box_status + "'");
//                }
//
//            }
//
//        }catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
}
