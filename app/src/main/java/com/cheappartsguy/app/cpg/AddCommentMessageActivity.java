package com.cheappartsguy.app.cpg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class AddCommentMessageActivity extends AppCompatActivity {

    public static SQLiteDatabase sqlDB;

    public MySqlLite mysql;

    LinearLayout btnBack, btnClear, btnSave;

    EditText txtCommentBox;

    Context context;

    public static String HeaderTitle, TitleMessage, ScreenId;

    TextView title_dialog, txtScreenId, txtBack;

    boolean IsKeyboardActive = false;

    public int ScreenCheckSizeIfUsing10Inches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment_message);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        TextView titleTxtView = (TextView) view.findViewById(R.id.txtTitle);
        TextView txtAccount = (TextView) view.findViewById(R.id.txtAccount);

        String HeaderName = HeaderTitle == null ? "ADD A COMMENT" : HeaderTitle;
        titleTxtView.setText(Config.getAppName(HeaderName));
        txtAccount.setText(Config.Worker_name);

        ScreenCheckSizeIfUsing10Inches = Config.getScreenOfTablet(getApplicationContext());

        context = getApplicationContext();
        txtCommentBox = (EditText) findViewById(R.id.edit_comment);
        title_dialog = (TextView) findViewById(R.id.title_dialog);
        txtScreenId = (TextView) findViewById(R.id.txtScreenId);
        txtBack = (TextView) findViewById(R.id.txtBack);
        IsKeyboardActive = true;

        String MessageTitle = TitleMessage == null ? "ADD A COMMENT" : TitleMessage;
        title_dialog.setText(MessageTitle);

        String MessageScreenId = ScreenId == null ? "MW-109" : ScreenId;
        txtScreenId.setText(MessageScreenId);

        btnBack = (LinearLayout) findViewById(R.id.btnBack);

        /*if(ScreenId == "MW-186") {
            txtBack.setText("BACK TO DASHBOARD");
        }*/ // REMARKS BY RONALD 6/20/2018 THIS IS CONFUSING

        txtCommentBox.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(ScreenId == "MW-186") {
                    Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }*/ // REMARKS BY RONALD 6/20/2018 THIS IS CONFUSING

                Intent i = new Intent(getApplicationContext(), AdditionInformationOptionsActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnClear = (LinearLayout) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCommentBox.setText("");
            }
        });

        btnSave = (LinearLayout) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(ScreenId == "MW-186") {
                    int resize_screen = 370;
                    if(ScreenCheckSizeIfUsing10Inches == 1) {
                        resize_screen = 560;
                    }
                    else if(ScreenCheckSizeIfUsing10Inches == 2) {
                        resize_screen = 755;
                    }

                    showDialogForDynamic(AddCommentMessageActivity.this, "SAVE COMMENT", "Press YES to Save Comment.\nPress NO to return to Comment Page.\n", resize_screen, "YES, SAVE COMMENT", "NO, GO BACK", 187);
                    return;
                }*/ // REMARKS BY RONALD 6/20/2018 THIS IS CONFUSING

                int resize_screen = 380;

               /* if(ScreenCheckSizeIfUsing10Inches == 1) {
                    resize_screen = 570;
                }
                else if(ScreenCheckSizeIfUsing10Inches == 2) {
                    resize_screen = 745;
                }*/ // REMARKS BY RONALD 6/20/2018 THIS IS CONFUSING

                Log.d("SCREEN_XX", resize_screen + "");

                showDialogForDynamic(AddCommentMessageActivity.this, "SAVE COMMENT", "Press YES to Save Comment.\nPress NO to return to Comment Page.\n", resize_screen, "YES, SAVE COMMENT", "NO, GO BACK", 110);
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void save_comment() {

        String _comments = txtCommentBox.getText().toString();
        String EncodeComments = "";
        String DecodeComments = "";

        try {
            Log.d("String Encode", StringEncoder.Encode(_comments));
           // _comments = _comments.replace(" ", "%20").replace("\n", "%0A");
//            EncodeComments = StringEncoder.Encode(_comments);
            EncodeComments = (_comments).replace("\n","<br>");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            Log.d("String Encode", StringEncoder.Encode(_comments));
//            DecodeComments = StringEncoder.Decode(EncodeComments);
            DecodeComments = (EncodeComments);
            Log.d("String Decode", DecodeComments);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // _comments = _comments.replace(" ", "%20");  REMARKS BY RONALD MAY 21, 2018 BELOW SUPPORT MULTIPLE COMMENT LINES AND SPECIAL CHARACTER
        //_comments = _comments.replace(" ", "%20").replace("\n", "%0A");

        sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);

        mysql = new MySqlLite(sqlDB);

        //boolean result = mysql.insert("list_of_comments", "'" + Config.get_box_id + "', '" + Config.Worker_uid + "', '" + Config.ImageName + "', '" + _comments + "'"); REMARKS BY RONALD MAY 21, 2018 BELOW SUPPORT MULTIPLE COMMENT LINES AND SPECIAL CHARACTER
        boolean result = mysql.insert("list_of_comments", "'" + Config.get_box_id + "', '" + Config.Worker_uid + "', '" + Config.ImageName  + "', '" + EncodeComments + "'"); // PREVIOUS VARIABLE _comments 12/15/2017

        if(result) {
            GradeImageSavedActivity.AppTitleText = "SAVE COMMENT CONFIRMATION";
            GradeImageSavedActivity.AppHeaderText = "CONFIRMING COMMENT HAS BEEN SAVED";
            GradeImageSavedActivity.AppPageText = "MW-111";

            Intent i = new Intent(getApplicationContext(), GradeImageSavedActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "Oops, Something went wrong.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        Log.d("Sample", "asdsad");

        if(IsKeyboardActive) {
            IsKeyboardActive = false;
            hide(AddCommentMessageActivity.this);
        }
        else {
            IsKeyboardActive = true;
            show(AddCommentMessageActivity.this);
        }

        return super.onTouchEvent(event);
    }

    public static void show(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
    }

    public void hide(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
    }

    public void showDialogForDynamic(Activity activity, String caption, String message, int height, String yes_button, String no_button, final int number){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_logout_dialog_box);

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;*/

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);

        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MW-" + number);
        btn_dialog_YES.setText(yes_button);
        btn_dialog_NO.setText(no_button);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save_comment();

                dialog.dismiss();

                /*if(number == 187) {
                    Intent i = new Intent(getApplicationContext(), ToolOptionCATPALActivity.class); //
                    startActivity(i);
                    finish();
                    return;
                }*/ // REMARKS BY RONALD 6/20/2018 THIS IS CONFUSING

                // save_comment();
            }
        });
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        //dialog.getWindow().setAttributes(lp);
    }
}
