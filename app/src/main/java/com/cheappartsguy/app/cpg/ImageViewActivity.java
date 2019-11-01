package com.cheappartsguy.app.cpg;


import com.cheappartsguy.app.cpg.AndroidMultiPartEntity.ProgressListener;
import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.IOException;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.content.Context;
import android.util.DisplayMetrics;

import android.hardware.Camera;


public class ImageViewActivity extends AppCompatActivity  {

    public static boolean IsPreviewFullScreen;
    public static SQLiteDatabase sqlDB;
    public MySqlLite mysql;

    public  ServiceWorker s_worker;
    private ProgressDialog pDialog;
    public boolean IsOkay;

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    public Context context;
    private ProgressBar progressBar;
    public String partNumber = null;
//    public static String dir_parent_folder_name = null;
    public String dir_folder_name = null;
//    public static String dir_folder_temp = null;
    private String filePath = null;
//    private TextView partNumberView;
    private ImageView imgPreview;
    private TextView upload_status;
//    private Button btnFull, btn34, btn12, btn14, btnEmpty, btnUnknown, btnTakePhoto, btnBack;
    long totalSize = 0;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static boolean SHOW_HIDE = true;
    private Uri fileUri;


//    public static String ImageFile = null;
//    public static String ImageName = null;
    public static String ContentValue = null;
    public static boolean AdditionalPhotoDir = false;
    public static String ParentImages;


    public int ScreenCheckSizeIfUsing10Inches;
    public static ObjectNames notificationSizes;
    public Context app_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        if(Build.VERSION.SDK_INT >= 23){
            check_storage_permission();
        }

        s_worker = new ServiceWorker();
        if(!s_worker.createDirIfNotExists(null)) {
            showAlert4("Please check your app permission.");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        upload_status = (TextView) findViewById(R.id.upload_status);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        this.imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SHOW_HIDE) {
                    SHOW_HIDE = false;
                    return;
                }
                SHOW_HIDE = true;
            }
        });

        if(!IsPreviewFullScreen) {
            new getBoxInformations().execute();
        }
        IsPreviewFullScreen = false;

        app_context = getApplicationContext();

        ScreenCheckSizeIfUsing10Inches = Config.getScreenOfTablet(getApplicationContext());
    }

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public void check_storage_permission() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)){
            Log.d("SCA", "Error: external storage is unavailable");
            return;
        }
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("SCA", "Error: external storage is read only.");
            return;
        }
        Log.d("SCA", "External storage is not read only or unavailable");

        if (ContextCompat.checkSelfPermission(this, // request permission when it is not granted.
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("SCA", "permission: WRITE_EXTERNAL_STORAGE: NOT granted!");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
            startActivity(i);
            finish();
            return false;
        }
        return super.onKeyDown(keycode, event);
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia() {
        imgPreview.setVisibility(View.VISIBLE);
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();
        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imgPreview.setImageBitmap(bitmap);
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                launchUploadActivity(false);
            } else {
                // failed to capture image
                launchUploadActivity(false);
            }
        }
    }

    private void launchUploadActivity(boolean isImage){

        filePath = fileUri.getPath();
        if(!isImage) {
            filePath = null;
        }

        IsPreviewFullScreen = false;
        previewMedia(isImage);
        Log.d("FILE1", filePath);

        if(AdditionalPhotoDir) {

            Log.d("Additional-Photo", filePath);

            if(Config.save_additiona_image(Config.ImageFile, Config.dir_parent_folder_name, ParentImages.replace(".", "-") )) {
                Intent i = new Intent(getApplicationContext(), AdditionInformationOptionsActivity.class);
                startActivity(i);
                finish();
            }
            return;
        }

        Log.d("Unit-Photo", filePath);
        Intent i = new Intent(getApplicationContext(), GradeImageActivity.class);
        i.putExtra("ImagePath", filePath);
        startActivity(i);
        finish();
    }
    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            imgPreview.setImageBitmap(bitmap);

        } else {
            imgPreview.setVisibility(View.GONE);
        }
    }

    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + ServiceWorker.Root + "/" + Config.dir_parent_folder_name,
                Config.dir_folder_temp);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.dir_folder_temp + " directory");
                return null;
            }
        }

        Random random = new Random();

        String rndm = random.nextInt(99999) + "";

        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss",
                Locale.getDefault()).format(new Date()) + "-" + rndm;

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            Config.ImageName = Config.Device_UID + timeStamp + ".jpg";
            Config.latest_image = Config.Device_UID + timeStamp + ".jpg";
            Config.ImageFile = mediaStorageDir.getPath() + File.separator + Config.ImageName;
            mediaFile = new File(Config.ImageFile);

            if(!AdditionalPhotoDir) {
                ParentImages = Config.ImageName;
            }

        }else {
            return null;
        }
        return mediaFile;
    }


    // uploading images

    /**
     * Uploading the file to server
     * */

//    public class createFolder extends AsyncTask<Void, Void, String> {
//        @Override
//        protected void onPreExecute() {
//            loaderShow("Please wait...");
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            // TODO: attempt authentication against a network service.
//            String json = null;
//            try {
//                Thread.sleep(100);
//                JSONHelper json_help = new JSONHelper();
//                String url = "http://cheappartsguy.com/mobile/create/temp/folder/" + partNumber;
//                Log.d("Response: ", "> " + url);
//                json = json_help.makeServiceCall(url, JSONHelper.GET);
//                Log.d("Response: ", "> " + json);
//                return json;
//            } catch (InterruptedException e) {
//            }
//
//            // TODO: register the new account here.
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final String json) {
//            loaderHide();
//            folderCreated(json);
//        }
//    }

//    private void folderCreated(String json) {
//        if (json != null) {
//            try
//            {
//                JSONObject job = new JSONObject(json);
//                String status = job.getString("Status");
//                Log.d("Name: ", status);
//
//                int status200 = Integer.parseInt(status);
//                if(status200 >= 200) {
//                   new UploadFileToServer().execute();
//                }
//            }catch(Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            progressBar.setProgress(0);
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            // Making progress bar visible
//            progressBar.setVisibility(View.VISIBLE);
//            // updating progress bar value
//            progressBar.setProgress(progress[0]);
//            // updating percentage value
//            upload_status.setVisibility(View.VISIBLE);
//            upload_status.setText("Uploading... | "+String.valueOf(progress[0]) + "%");
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//
//            String urlUploadImage = "http://138.128.118.2:8000/CPGU/upload_process.php?ref=mobile_m&uid=mobile_m&part="+partNumber+"&edited=NOT";
//
//            Log.d("URL_UPLOAD", urlUploadImage);
//
//            String responseString = null;
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(urlUploadImage);
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new ProgressListener() {
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
//                File sourceFile = new File(filePath);
//                // Adding file data to http body
//                entity.addPart("file", new FileBody(sourceFile));
//                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = "Upload was successful."; //EntityUtils.toString(r_entity);
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//            return responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);
//            showAlert(result);
//            super.onPostExecute(result);
//        }
//    }

//    private class SubmitContentGrade extends AsyncTask<Void, Integer, String> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            // TODO: attempt authentication against a network service.
//            String json = null;
//            try {
//
//                Config.ImageName = "mobile_m_" + Config.ImageName;
//                Thread.sleep(100);
//                JSONHelper json_help = new JSONHelper();
//                String url = Config.Host + Config.Url_content_images + Config.Token + "/" + Config.get_box_id + "/" + Config.ImageName + "/" + ContentValue;
//                Log.d("Response: ", "> " + url);
//                json = json_help.makeServiceCall(url, JSONHelper.GET);
//                Log.d("Response: ", "> " + json);
//                return json;
//            } catch (InterruptedException e) {
//            }
//
//            // TODO: register the new account here.
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final String json) {
//        }
//    }

    public static String _OriginBox;
    public String _ContentValue = null;
    private void doContent(String ContentValue) {
        _ContentValue = ContentValue;
        String message = "Unit Fullness Has Been Graded As " + _ContentValue;
        showDialogForFullnessGrade(
                ImageViewActivity.this,
                "CONFIRMING",
                message
        );
    }

    private boolean save_image() {
        dir_folder_name = ContentValue;
        if(!s_worker.createDirIfNotExists(Config.dir_parent_folder_name + "/" + dir_folder_name)) {
            showAlert4("Please check your app permission.");
            return false;
        }

        String path_destination = s_worker.get_file().getPath();
        s_worker.copy(Config.ImageFile, path_destination);
        return true;
    }

    public void showDialogForFullnessGrade(Activity activity, String title, String message){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_fullness_confirmation);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        TextView text_dialog = (TextView) dialog.findViewById(R.id.txtGradedAs);
        text_dialog.setText(message);

        Button btnBackToDashboard = (Button) dialog.findViewById(R.id.btnBackToDashboard);
        btnBackToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if(save_image()) {
                    Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        Button btnYesA = (Button) dialog.findViewById(R.id.btnYesA);
        btnYesA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnYesB = (Button) dialog.findViewById(R.id.btnYesB);
        btnYesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnYesC = (Button) dialog.findViewById(R.id.btnYesC);
        btnYesC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if(save_image()) {
                    _OriginBox = "Confirming Photo & Fullness Grade Have Been Saved";
                    AlertSelectNextActionB(ImageViewActivity.this, _OriginBox, "Please Select \n Your Next Action.");
                }
            }
        });

        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btnNo);
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void AlertSelectNextActionB(Activity activity, String title, String message){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_sna);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        TextView txt_sna = (TextView) dialog.findViewById(R.id.txt_sna);
        txt_sna.setText(message);

        Button btn_back_dashboard = (Button) dialog.findViewById(R.id.btn_back_dashboard);
        btn_back_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button btn_add_photo_comment = (Button) dialog.findViewById(R.id.btn_add_a);
        btn_add_photo_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showAlert2xx("It's being updated.");
            }
        });

        Button btn_add_a_comment = (Button) dialog.findViewById(R.id.btn_add_c);
        btn_add_a_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDialogForComment(
                        ImageViewActivity.this,
                        "ADD A COMMENT",
                        null);
            }
        });

        Button btn_add_photo_unit = (Button) dialog.findViewById(R.id.btn_add_b);
        btn_add_photo_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                captureImage();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showDialogForAnotherTakePhoto(Activity activity, String title, String message){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_confirmation_dialog_catpal);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        TextView text_dialog = (TextView) dialog.findViewById(R.id.text_dialog);
        text_dialog.setText(message);

        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                captureImage();
            }
        });

        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });
        dialog.show();
    }

    public String _comments = null;
    public EditText edit_comment;
    public void showDialogForComment(Activity activity, String title, String message){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_comment_dialog_catpal);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        edit_comment = (EditText) dialog.findViewById(R.id.edit_comment);

        Button btn_dialog_COMMENT = (Button) dialog.findViewById(R.id.btn_dialog_COMMENT);
        btn_dialog_COMMENT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showCommentConfirmationB(ImageViewActivity.this, "CONFIRMATION COMMENT", "Confirm save comment?");
            }
        });

        Button btn_dialog_CANCEL = (Button) dialog.findViewById(R.id.btn_dialog_CANCEL);
        btn_dialog_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                _OriginBox = "No Comment Saved";
                AlertSelectNextActionB(ImageViewActivity.this, _OriginBox, "Please Select \n Your Next Action.");
            }
        });
        dialog.show();
    }

    private void showCommentConfirmationB(Activity activity, String title, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_confirmation_dialog_catpal);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        TextView text_dialog = (TextView) dialog.findViewById(R.id.text_dialog);
        text_dialog.setText(message);

        Button btn_dialog_COMMENT = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        btn_dialog_COMMENT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                _comments = edit_comment.getText().toString();
                _comments = _comments.replace(" ", "%20");

                sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
                mysql = new MySqlLite(sqlDB);
                boolean result = mysql.insert("list_of_comments", "'" + Config.get_box_id + "', '" + Config.Worker_uid + "', '" + Config.ImageName + "', '" + _comments + "'");
                if(result) {
                    _OriginBox = "Comment Saved";
                    AlertSelectNextActionB(ImageViewActivity.this, _OriginBox, "Please Select \n Your Next Action.");
                }
                else {
                    showAlert2x("Oops, Something went wrong.");
                }
            }
        });

        Button btn_dialog_CANCEL = (Button) dialog.findViewById(R.id.btn_dialog_NO);
        btn_dialog_CANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDialogForComment(
                        ImageViewActivity.this,
                        "ADD A COMMENT",
                        null);
            }
        });
        dialog.show();
    }

    private class submitComment extends AsyncTask<Void, Integer, String> {
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
                String _img_name  = "mobile_m_" +  Config.ImageName;
                JSONHelper json_help = new JSONHelper();
                String url = Config.Host + Config.Url_post_comment + Config.Token + "/" + Config.get_box_id + "/" + _img_name + "/" + Config.Worker_uid +"/" + _comments;
                Log.d("Response: ", "> " + url);
                json = json_help.makeServiceCall(url, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (Exception e) {
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            doneComment(json);
            loaderHide();
        }
    }

    public void doneComment(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String status = job.getString("Message");
                Log.d("Status: ", status);
                int status200 = Integer.parseInt(status);
                if(status200 != 202) {
                    showAlert2x("Error");
                }
                else {
                    showAlert2x("Done");
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private class getBoxInformations extends AsyncTask<Void, Integer, String> {
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
                String url = Config.Host + Config.Url_get_box_information + Config.Token + "/" + Config.get_box_id;
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
            box_information(json);
            loaderHide();
        }
    }

    public void box_information(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String Message = job.getString("Message");

                int statusMessage = Integer.parseInt(Message);

                if(statusMessage == 404) {
                    String message_ = "No Box Found.";

                    ObjectNames sizes = new ObjectNames();
                    sizes.tab8Sizes = new int[] { 560, 600, 430, 600};
                    sizes.tab10Sizes = new int[] { 560, 600, 650, 600};
                    sizes.tabS2Sizes = new int[] { 1100, 600, 860, 600};

                    ScreenCheckSizeIfUsing10Inches = Config.getScreenOfTablet(getApplicationContext());

                    notificationSizes = Config.getDialogSizes(ScreenCheckSizeIfUsing10Inches, app_context, sizes, false);

                    int resize_screen = 390;
                    if(ScreenCheckSizeIfUsing10Inches == 1) {
                        resize_screen = 580;
                    }
                    else if(ScreenCheckSizeIfUsing10Inches == 2) {
                        resize_screen = 700;
                    }

                    showDialogForDynamicNotification(
                            ImageViewActivity.this,
                            "TRANSFER ALL UNITS FROM ONE BOX TO ANOTHER",
                            message_, resize_screen, 147, false);
                    return;
                }

                String status = job.getString("box_status");
                Log.d("Status: ", status);

                int status200 = Integer.parseInt(status);
                Log.d("status200", status200 + "");
                if(status200 != 3) {
                    Config.get_box_id_show = job.getString("box_code");
                    Config.get_yard_id = job.getString("yard_id");
                    Config.get_yard_name = job.getString("country") + ", " + job.getString("city");
                    partNumber = Config.get_yard_id + "-" + Config.get_box_id;

                    Config.dir_parent_folder_name = Config.get_yard_id + "_" + Config.get_box_id;
                    Config.dir_folder_temp = "temp";

                    if(!s_worker.createDirIfNotExists(Config.dir_parent_folder_name)) {
                        showAlert4("Please check your app permission.");
                        return;
                    }

                    captureImage();
                }
                else {
                    showAlert2("The box is closed!");
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to show alert dialog
     * */
//    private void showAlert(String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(message).setTitle("Response from Servers")
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // do nothing
//                        SHOW_HIDE = false;
////                        partNumberView.setVisibility(View.VISIBLE);
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//
//        if(message == "Upload was successful.") {
//            new SubmitContentGrade().execute();
//        }
//
//        upload_status.setText("");
//        upload_status.setVisibility(View.INVISIBLE);
//        progressBar.setVisibility(View.INVISIBLE);
//        progressBar.setProgress(0);
//        imgPreview.setVisibility(View.GONE);
////        partNumberView.setTextColor(Color.parseColor("#1F1F1F"));
//    }

    private void showAlert2(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlert2x(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("INFORMATION")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String message = "Confirming unit is " + _ContentValue + ".";
                        showDialogForFullnessGrade(
                                ImageViewActivity.this,
                                "CONFIRMING",
                                message
                        );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlert2xx(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("INFORMATION")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(_OriginBox != "NOK") {
                            AlertSelectNextActionB(ImageViewActivity.this, _OriginBox, "Please Select \n Your Next Action.");
                        }
                        else {
                            _ContentValue = ContentValue;
                            String message = "Unit graded as [ " + _ContentValue + " ].";
                            showDialogForFullnessGrade(
                                    ImageViewActivity.this,
                                    "CONFIRMING",
                                    message
                            );
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private AlertDialog showDialog3(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // uploading the file to server
//                new createFolder().execute();

                dir_folder_name = ContentValue;
                if(!s_worker.createDirIfNotExists(Config.dir_parent_folder_name + "/" + dir_folder_name)) {
                    showAlert4("Please check your app permission.");
                    return;
                }

                String path_desti = s_worker.get_file().getPath();
                s_worker.copy(Config.ImageFile, path_desti);

                String message = "Image was saved in queue, Would you like to take a photo?";
                showDialog5(ImageViewActivity.this,
                        "CONFIRMING",
                        message,
                        "Yes", "No").show();

            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    private void showAlert4(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlert5(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("ALERT")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private AlertDialog showDialog5(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                captureImage();
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return downloadDialog.show();
    }

    public void showDialogForDynamicNotification(Activity activity, String caption, String message, int height, final int number, final boolean IsNothing){

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
        title_message.setText(Html.fromHtml(message));
        screen_code.setText("MW-" + number);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();

            }
        });
        dialog.show();
        //dialog.getWindow().setAttributes(lp);
    }

    private void loaderShow(String Message)
    {
        pDialog = new ProgressDialog(ImageViewActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
