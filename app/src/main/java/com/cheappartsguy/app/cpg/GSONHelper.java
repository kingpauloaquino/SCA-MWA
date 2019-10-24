//package com.cheappartsguy.app.cpg;
//
///**
// * Created by kingpauloaquino on 3/9/2016.
// */
//
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import android.util.Log;
//
//public class GSONHelper {
//
//    public GsonBuilder gsonBuilder;
//    public InputStream inputStream;
//    public Reader reader;
//    public Gson gson;
//
//    public void requestFromUrl(String SERVER_URL)
//    {
//        try {
//            //Create an HTTP client
//            HttpClient client = new DefaultHttpClient();
//            HttpPost post = new HttpPost(SERVER_URL);
//
//            //Perform the request and check the status code
//            HttpResponse response = client.execute(post);
//            StatusLine statusLine = response.getStatusLine();
//            if(statusLine.getStatusCode() == 200) {
//                HttpEntity entity = response.getEntity();
//                inputStream = entity.getContent();
//            }
//        } catch(Exception ex) {
//            Log.e("GSON", "Failed to parse JSON due to: " + ex);
//        }
//    }
//
//    public void readRequest() {
//        try {
//            //Read the server response and attempt to parse it as JSON
//            reader = new InputStreamReader(inputStream);
//
//            gsonBuilder = new GsonBuilder();
//            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
//            gson = gsonBuilder.create();
//            inputStream.close();
//
//        } catch (Exception ex) {
//            Log.e("GSON", "Failed to parse JSON due to: " + ex);
//        }
//    }
//
//}
