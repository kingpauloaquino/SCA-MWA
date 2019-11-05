package com.cheappartsguy.app.cpg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kingpauloaquino on 3/6/2016.
 */
public class Config {

    // commit login information

    private static SharedPreferences sharedPref;
    private static Activity _activity;

    public static String Device_UID;
    public static int totalAdditionalPhoto;
    public static boolean IsParentImage;
    public static String IsParentImageName;

    public static List<ObjectNames> ObjectNamesList;

    public static ObjectNames getDialogSizes(int screen_size, Context context, ObjectNames sizes, boolean isError) {

        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        ObjectNames n = new ObjectNames();

        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            Log.d("DEVICE", "TABLET");
            if(screen_size == 1) {
                if(isError) {
                    n.Height = sizes.tab10Sizes[2];
                    n.Width = sizes.tab10Sizes[3];
                }
                else {
                    n.Height = sizes.tab10Sizes[0];
                    n.Width = sizes.tab10Sizes[1];
                }
            }
            else if(screen_size == 2) {

                if(isError) {
                    n.Height = sizes.tabS2Sizes[2];
                    n.Width = sizes.tabS2Sizes[3];
                }
                else {
                    n.Height = sizes.tabS2Sizes[0];
                    n.Width = sizes.tabS2Sizes[1];
                }
            }
            else {
                if(isError) {
                    n.Height = sizes.tab8Sizes[2];
                    n.Width = sizes.tab8Sizes[3];
                }
                else {
                    n.Height = sizes.tab8Sizes[0];
                    n.Width = sizes.tab8Sizes[1];
                }
            }
        }else{
            Log.d("DEVICE", "MOBILE");
            n.Height = sizes.mHeight;
            n.Width = sizes.mWidth;
        }

        return n;
    }

    public static boolean haveNetworkConnection(ConnectivityManager cm) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static int getScreenOfTablet(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int screen_id = 0;

        if(width >= 1900 && width < 2000) {
            screen_id = 1;
        }

        else if(width >= 2000 && width < 2500) {
            screen_id = 2;
        }

        return screen_id;
    }

    public static String getActualScreenSize(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        String size = "Your Actual Screen Size:\n\n";
        size += "WIDTH: " + width + "\nHEIGHT: " + height;

        return size;
    }

    public static void commit_login(Activity activity, String username, String password, String worker_id, String worker_name, String seller_id) {
//        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        sharedPref = activity.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("worker_id", worker_id);
        editor.putString("worker_name", worker_name);
        editor.putString("seller_id", seller_id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public static ObjectNames is_already_logged(Activity activity) {
        sharedPref = activity.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        ObjectNames names = new ObjectNames();

        names.Worker_id = 0;
        if(sharedPref.getString("worker_id", "") != "" ) {
            names.Worker_id = Integer.parseInt(sharedPref.getString("worker_id", ""));
        }

        names.Seller_id = 0;
        if(sharedPref.getString("seller_id", "") != "" ) {
            names.Seller_id = Integer.parseInt(sharedPref.getString("seller_id", ""));
        }

        names.Username = sharedPref.getString("username", "");
        names.Password = sharedPref.getString("password", "");
        names.Worker_name = sharedPref.getString("worker_name", "");
        return names;
    }

    public static void flash_sharedPref(Activity activity) {
        SharedPreferences mysharedpred = activity.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        mysharedpred.edit().clear().commit();
    }

    public static void transfer_commit(Activity activity, String key, String value) {
        sharedPref = activity.getSharedPreferences("MySharedPrefTrans", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String transfer_value(Activity activity, String key) {
        sharedPref = activity.getSharedPreferences("MySharedPrefTrans", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static void transfer_clear(Activity activity) {
        SharedPreferences mysharedpred = activity.getSharedPreferences("MySharedPrefTrans", Context.MODE_PRIVATE);
        mysharedpred.edit().clear().commit();
    }




    ////


    public static void current_parent_commit(Activity activity, String key, String value) {
        sharedPref = activity.getSharedPreferences("ImageParentName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String current_parent_value(Activity activity, String key) {
        sharedPref = activity.getSharedPreferences("ImageParentName", Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static void current_parent_clear(Activity activity) {
        SharedPreferences mysharedpred = activity.getSharedPreferences("ImageParentName", Context.MODE_PRIVATE);
        mysharedpred.edit().clear().commit();
    }


    public static boolean hostAvailableisOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    // Image Content

    public static String latest_image = null;

    public static String ImageFile = null;

    public static String ImageName = null;

    public static String dir_parent_folder_name = null;

    public static String dir_folder_temp = null;

    public static String dir_new_parent_folder_name = null;

    public static boolean save_image(String source, String destination, int grade) {

        ServiceWorker s_worker = new ServiceWorker();
        if(!s_worker.createDirIfNotExists(destination + "/" + grade)) {
            return false;
        }

        dir_new_parent_folder_name = s_worker.get_file().getPath();
        s_worker.copy(source, dir_new_parent_folder_name);
        return true;
    }

    public static boolean save_additiona_image(String source, String destination, String parent_image) {

        ServiceWorker s_worker = new ServiceWorker();
        if(!s_worker.createDirIfNotExists(destination + "/additional-photo/" + parent_image)) {
            return false;
        }

        dir_new_parent_folder_name = s_worker.get_file().getPath();
        s_worker.copy(source, dir_new_parent_folder_name);
        return true;
    }

    public static ArrayList<ImageGalleryItem> ImageGalleryContainer;

    public static String[] ImageUrlList;

    public static String getAppName(String append_name) {
        return append_name;
    }

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://img.scrapcatapp.com/CPGU/upload_process.php?ref=mobile_m&uid=mobile_m&part=21212121&edited=NOT";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    public static int IsSearch = 0;

    public static boolean JustLogged = false;
    public static Integer Worker_uid;
    public static Integer Seller_uid;

    public static int dev_settings = 0; // 0 = dev, staging, 1 = live
    public static int radioSelectedElement=-1;

    public static String Token = "795edd365fd0e371ceaaf1ddd559a85d";
    public static String Host = "http://api.local.scrapcatapp.com"; // default
    public static String Images_Host = "http://img.scrapcatapp.com"; // defualt

    public static String Images_Host_Dev = "http://192.168.1.203";

    //http://api.local.scrapcatapp.com
    public static String Worker_name;

    public static String Url_get_login = "/api/signin_mobile/"; // parameter token + email + password

    public static String get_yard_id;
    public static String get_yard_name;
    public static String Url_get_yard_list = "/api/listofyard_per_user_mobile/"; // parameter token + seller_uid

    public static String get_box_id_show;
    public static String get_box_id;
    public static String Url_get_box_list = "/api/listofbox_per_yard_and_user_mobile/"; // parameter token + seller_uid + yard_id

    public static String get_set_box_id;
    public static String Url_set_box = "/api/create_box_mobile/"; // parameter token + seller_uid + worker_uid + yard_id

    public static String Url_check_box_status = "/api/qr_code_check_mobile/"; // parameter token + box_uid

    public static String Url_box_close = "/api/box_status_update_mobile/"; // parameter token + box_uid + box_status

    public static String Url_box_delete = "/api/temp_deleted_box_mobile/"; // parameter token + box_uid

    public static String Url_box_transfer = "/api/transfer_box_to_another_box_mobile/"; // parameter token + from_box_uid + to_box_uid

    public static String Url_content_images = "/api/upload_cats_worker_mobile/"; // parameter token + from_box_uid + img_name + content

    public static String Url_get_box_information = "/api/box_info_mobile/"; // parameter token + box_uid

    public static String Url_post_comment = "/api/cats_comments_mobile/"; // parameter token + box_uid + images_name + user_id + comment

    public static String Url_get_all_boxes_old = "/api/list_of_boxes_mobile/"; // parameter token + user_id

    public static String Url_get_all_boxes = "/api/v3/list_of_boxes_mobile/"; // parameter token + user_id

    public static String Url_fetch_images_view_gallery = "/api/list_of_cats_per_yard_grader_revised/"; // token + box_uid

    public static String Url_forgot_password = "/api/v2/forgot/password/"; // parameter token + user_id

    //public static String Url_post_bacode_v1 = "/api/v1/barcode/"; //{token}/{box_uid}/{barcode_value}/{vin_number} // for barcode

    //public static String Url_post_bacode = "/api/v2/barcode/"; //{token}/{box_uid}/{img_name}/{barcode_value} // for barcode
}