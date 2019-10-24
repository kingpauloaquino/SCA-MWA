package com.cheappartsguy.app.cpg;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by king on 7/18/2016.
 */
public class ServiceWorker {

    public static String Root = "ScrapCATapp_Temp_Images"; //ScrapCatApp
    public static File mediaStorageDir;

    public File get_file() {
        return mediaStorageDir;
    }

    public String get_root(boolean IsRoot) {

        File f = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), this.Root);

        if(!IsRoot) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        }

        return f.getPath();
    }

    public boolean createDirIfNotExists(String Path) {
        boolean ret = true;

        // External sdcard location
        mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                this.Root);

        if(Path != null) {
            mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + this.Root,
                    Path);
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("TravellerLog :: ", "Oops! Failed create "
                        + this.Root + " directory");

                ret = false;
            }
        }

        return ret;
    }

    public File[] readDirList(String Path) {

        // gets the files in the directory
        File fileDirectory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                this.Root);

        if(Path != null) {
            fileDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + this.Root,
                    Path);
        }

        // lists all the files into an array
        File[] dirFiles = fileDirectory.listFiles();

        return dirFiles;

//        if (dirFiles.length != 0) {
//            //loops through the array of files, outputing the name to console
//            for (int ii = 0; ii < dirFiles.length; ii++) {
//                String fileOutput = dirFiles[ii].toString();
//                System.out.println(fileOutput);
//            }
//        }
    }

    public void copy(String from, String destination) {

        try{

            File a_file =new File(from);

            if(a_file.renameTo(new File(destination + "/" + a_file.getName()))){

                Log.e("TravellerLog :: ", "File is moved successful!");
            }else{

                Log.e("TravellerLog :: ", "File is failed to move!");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean delete(String filename) {
        try{
            File a_file =new File(filename);
            if(a_file.delete())
            {
                Log.e("TravellerLog :: ", "File is removed successful!");
                return true;
            }else{
                Log.e("TravellerLog :: ", "File is failed to removed!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
