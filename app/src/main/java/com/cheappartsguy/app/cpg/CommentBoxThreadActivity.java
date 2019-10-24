package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheappartsguy.app.cpg.StringEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class CommentBoxThreadActivity extends AppCompatActivity {

    TextView txtCommentView, txtWorker, txtBoxId;

    LinearLayout btnBack, btnWriteComment;

    public String URL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_box_thread);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);
        titleTxtView.setText(Config.getAppName("ADD A BOX COMMENT"));
        txtAccount.setText(Config.Worker_name);

        txtCommentView = (TextView) findViewById(R.id.txtCommentView);
        txtBoxId = (TextView) findViewById(R.id.txtBoxId);
        txtBoxId.setText("COMMENTS ON BOX #: " + Config.get_box_id_show);

        btnBack = (LinearLayout) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnWriteComment = (LinearLayout) findViewById(R.id.btnWriteComment);
        btnWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), WriteCommentActivity.class);
                startActivity(i);
                finish();
            }
        });

        txtCommentView.setMovementMethod(new ScrollingMovementMethod());

        URL =  Config.Host + "/api/box_comments_list_mobile/"+ Config.Token +"/" + Config.get_box_id;

        new get_comments().execute();
    }

    public class get_comments extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                json = json_help.makeServiceCall(URL, JSONHelper.GET);
                Log.d("Response: ", "> " + URL);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            get_comments_list(json);
        }
    }

    private void get_comments_list(String json) {
        if (json != null) {
            try
            {
                JSONObject jsonObj = new JSONObject(json);
                String Message = jsonObj.getString("Message");
                Log.d("Message: ", Message);
                int IsPassed = Integer.parseInt(Message);

                String comments = "";
                if(IsPassed == 202) {
                    JSONArray jsonMainNode = jsonObj.optJSONArray("result");
                    int lengthJsonArr = jsonMainNode.length();
                    for(int i=0; i < lengthJsonArr; i++)
                    {
                        /****** Get Object for each JSON node.***********/
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        /******* Fetch node values **********/
                        String first_name = "";
                        String DecodeComments = "";
                        first_name = jsonChildNode.optString("first_name").toString();
                        try {
                            DecodeComments = StringEncoder.Decode(jsonChildNode.optString("box_comments").toString());
                        } catch (UnsupportedEncodingException e) {
                            comments = "";
                            e.printStackTrace();
                        }
                        //<font color='#01579B'><b>Wi-fi</b></font>
                        comments += "<font color='#01579B'><b>" + first_name + "</b></font>"  + " : \t" + DecodeComments.replace("\n", "<br/>") + "<br/>"; //"\n"
                        // String box_comments      = jsonChildNode.optString("box_comments").toString(); COMMENT OUT RONALD AGUIRRE 12/15/2017
                        // String box_comments      = jsonChildNode.optString("box_comments").toString();
                    }
                    if(lengthJsonArr == 0) {
                        comments = "No Comment.";
                    }
                }
                // CLEAR TEXTVIEW BEFORE WE PUT THE RETURN VALUE FROM JASON
                txtCommentView.setText("");
                // SET THE VALUE OF COMMENTS
                txtCommentView.setText(Html.fromHtml(comments));
                //txtCommentView.setText(txtCommentView); COMMENT OUT BY RONALD TO SUPPORT FORMATING OF TEXT
                return;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        // OLD SCRIPT OF KING REMARKS MAY 18, 2018
        //if (json != null) {
        //    try
        //    {
        //        JSONObject jsonObj = new JSONObject(json);
        //       String Message = jsonObj.getString("Message");
        //       Log.d("Message: ", Message);
        //       int IsPassed = Integer.parseInt(Message);

        //        String comments = "";
        //        if(IsPassed == 202) {
        //           JSONArray jsonMainNode = jsonObj.optJSONArray("result");
        //            int lengthJsonArr = jsonMainNode.length();
        //           for(int i=0; i < lengthJsonArr; i++)
        //           {
        //                /****** Get Object for each JSON node.***********/
        //               JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
        //               /******* Fetch node values **********/
        //               String first_name        = jsonChildNode.optString("first_name").toString();
        //               String box_comments      = jsonChildNode.optString("box_comments").toString();
        //               comments += first_name + ": " + box_comments + "\n";
        //           }

        //            if(lengthJsonArr == 0) {
        //                comments = "No Comment.";
        //           }
        //       }
        //       txtCommentView.setText(comments);
        //       return;
        //   }catch(Exception e)
        //   {
        //       e.printStackTrace();
        //   }
        //}
    }
}
