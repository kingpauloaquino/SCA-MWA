package com.cheappartsguy.app.cpg;

/**
 * Created by king on 8/25/2016.
 */

import java.io.File;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MySqlLite {

    private static String TAG = "MySqlLite";
    public static String DB_NAME ="CatPalMobileDb";
    private SQLiteDatabase db;

    public MySqlLite(SQLiteDatabase database)
    {
        db = database;
        created_table("list_of_boxes", "box_uid VARCHAR, worker_id VARCHAR, yard_id VARCHAR, box_id VARCHAR, box_code VARCHAR, box_status INT");
        created_table("list_of_comments", "box_id VARCHAR, worker_id VARCHAR, image_name VARCHAR, comment VARCHAR");
        created_table("list_of_barcode", "worker_id VARCHAR, image_name VARCHAR, barcode VARCHAR");
    }

    public void created_table(String name, String fields_with_data_type) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ name +" ("+ fields_with_data_type +");");
        Log.d(TAG, "table was created.");
    }

    public boolean insert(String table, String values) {
        try {
            db.execSQL("INSERT INTO "+ table +" VALUES("+ values +");");
            Log.d(TAG, "DONE");
            return true;
        }
        catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return false;
    }

    public boolean execute(String query) {
        try {
            db.execSQL(query);
            Log.d(TAG, "DONE");
            return true;
        }
        catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return false;
    }

    public Cursor select(String query) {
        Cursor c = null;
        try {
             c = db.rawQuery(query, null);
        }
        catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return c;
    }

    public int findListOfBoxes(String UID) {

        Log.d("String UID", UID);

        Cursor c = this.select("SELECT * FROM list_of_boxes WHERE box_uid = '"+UID+"';");
        while(c.moveToNext())
        {
            int box_uid = Integer.parseInt(c.getString(0));
            if(box_uid > 0) {
                return box_uid;
            }
        }
        return 0;
    }

    public String getBoxRandomCode(String UID) {

        Log.d("String UID", UID);

        Cursor c = this.select("SELECT * FROM list_of_boxes WHERE box_uid = '"+UID+"';");
        while(c.moveToNext())
        {
            String box_code = c.getString(4).toString();
            return box_code;
        }
        return null;
    }

    public int getBoxStatus(String UID) {

        Log.d("String UID", UID);

        Cursor c = this.select("SELECT * FROM list_of_boxes WHERE box_uid = '"+UID+"';");
        while(c.moveToNext())
        {
            int box_status = Integer.parseInt(c.getString(5));
            return box_status;
        }
        return 0;
    }

    public String get_yard_information(String UID) {

        Log.d("String UID", UID);

        String Yard_Id = null;
        Cursor c = this.select("SELECT yard_id FROM list_of_boxes WHERE box_uid = '"+UID+"';");
        while(c.moveToNext())
        {
            Yard_Id = c.getString(0).toString();
        }
        return Yard_Id;
    }



}
