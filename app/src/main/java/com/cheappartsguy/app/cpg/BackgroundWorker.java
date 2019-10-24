package com.cheappartsguy.app.cpg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.net.Socket;
import org.json.*;
import com.loopj.android.http.*;

import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;

/**
 * Created by king on 7/23/2016.
 */
public class BackgroundWorker {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public SyncCATPALActivity SyncActivity;

    public static String UPLOAD_URL = null;
    public static String CATPAL_NUMBER = null;
    public static String IMAGE_FULL_PATH = null;
    public static String IMAGE_TEMP = null;

    private Object mPauseLock;
    public static boolean mPaused;
    public static boolean mFinished;
    public static boolean mCompletelyDone;

    public static int counter_loop;
    public static int root_loop_length;
    public static int sub_loop_length;


    public BackgroundWorker() {
        mPauseLock = new Object();
        mPaused = false;
        mFinished = false;
    }

    public void run() {
        while (!mFinished) {

            String _status = mFinished ? "Finished" : "Running";
            Log.d("Status Woker: ", _status);

            try {
                mFinished = true;
                Log.d("Status Woker: ", "j2");

                onPause();
                do_process();
            } catch (Exception e) {
                Log.d("Status Woker: ", "Something went wrong.");
            }
        }
    }

    public void onPause() {
        mFinished = true;
    }

    public void onResume() {
        mFinished = false;
    }

    // to be processed


    public boolean do_process() {
        try {
            ServiceWorker sw = new ServiceWorker();
            File[] dirFiles = sw.readDirList( null );
            root_loop_length = dirFiles.length;

            Log.d("Test22: ", root_loop_length + "");

            if (root_loop_length != 0) {
                counter_loop = 0;
                for (int i = 0; i < root_loop_length; i++) {

                    String fname1 = dirFiles[i].getName();
                    Log.d("Filename1: ", fname1);
                    folderIsEmpty(fname1);

                    File[] sub_1 = sw.readDirList( fname1 );
                    if (sub_1.length != 0) {
                        for (int s = 0; s < sub_1.length; s++) {

                            String fname2 = fname1 + "/" + sub_1[s].getName();
                            if(!fname2.contains("temp")) {
                                Log.d("Filename2: ", fname2);
                                do_image_scan(sw, fname2);
                            }

                        }
                    }
                    counter_loop = i;
                }
                return true;
            }
        }
        catch (Exception ex) {}
        return false;
    }

    public void do_image_scan( ServiceWorker sw, String fn) {
        try {
            int ctr = 1;
            File[] files = sw.readDirList( fn );
            if (files.length != 0) {
                for (int s = 0; s < files.length; s++) {
                    String fname3 =  fn + "/" + files[s].getName();
                    String real_path = sw.get_root(true) + "/" + fname3;
                    Log.d("Filename31: ", fname3);
                    Log.d("Filename32: ", real_path);
                    Log.d("Filename33: ", fn.replace("/", "_"));
                    File file = new File(real_path);
                    if (file.isFile()) {
                        do_array_process(real_path);
                        Log.d("Filename34: ", ctr + "");
                        ctr++;
                    }
                }
            }
        }
        catch (Exception ex) { }
    }

    public void do_array_process(String image_file) {

        this.IMAGE_FULL_PATH = image_file;
        Log.d("Filename4: ", this.IMAGE_FULL_PATH);

        String[] splits = this.IMAGE_FULL_PATH.split("/");
        this.CATPAL_NUMBER = splits[6] + '_' + splits[7];
//        this.UPLOAD_URL = "http://138.128.118.2:8000/CPGU/upload_process.php?ref=mobile_m&uid=mobile_m&part="+this.CATPAL_NUMBER+"&edited=CATPAL";

        this.UPLOAD_URL = Config.Images_Host + "/CPGU/upload_process.php?ref=mobile_m&uid=mobile_m&part="+this.CATPAL_NUMBER+"&edited=CATPAL";

        Log.d("IMAGE TO BE UPLOADED: ", this.IMAGE_FULL_PATH);
        Log.d("SERVER URL: ", this.UPLOAD_URL);

        GradeContentObject objContent = new GradeContentObject();
        objContent.BoxId = splits[6];
        objContent.ImageName = splits[8];
        objContent.ImagePath = this.IMAGE_FULL_PATH;
        objContent.GradeValue = splits[7];
        objContent.UrlOfServer = this.UPLOAD_URL;
        SyncCATPALActivity.Images_Data.add(objContent);
    }

    static int count_index = 0;
    public static GradeContentObject returnListIndex() {
        GradeContentObject d = new GradeContentObject();
        for (GradeContentObject data : SyncCATPALActivity.Images_Data) {
            Log.d("Uid ", count_index + "");
            Log.d("Box Id ", data.BoxId);
            Log.d("Image Name ", data.ImageName);
            Log.d("Image Grade ", data.GradeValue);
            Log.d("Server Url ", data.UrlOfServer);

            d.BoxId = data.BoxId;
            d.ImageName = data.ImageName;
            d.GradeValue = data.GradeValue;
            d.UrlOfServer = data.UrlOfServer;
            count_index++;
            return d;
        }
        return d;
    }

    public void RemoveToImageList(String image_file) {
        File _file = new File(image_file);
        boolean delete_image = _file.delete(); // deleteFile(_file.getName());
        if(delete_image) {
            Log.d("TIMER_Runnable", "Deleted " + image_file);
            return;
        }
        Log.d("TIMER_Runnable", "Not Deleted " + image_file);
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
                Log.d("Folder", "Deleting " + child);
            }
        }
        fileOrDirectory.delete();
        Log.d("Folder", "Root Deleting");
    }

    public boolean folderIsEmpty(String Dir) {
        File directory = new File(Dir);
        if (directory.isDirectory()) {
            String[] files = directory.list();
            if (files.length == 0) {
                deleteRecursive(directory);
                return true;
            }
            return false;
        }
        return true;
    }

}
