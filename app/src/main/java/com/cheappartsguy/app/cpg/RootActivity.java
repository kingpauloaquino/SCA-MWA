package com.cheappartsguy.app.cpg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;

public class RootActivity extends Activity {

    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.xml.anim_slide_in_left,
                    R.xml.anim_slide_out_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.xml.anim_slide_in_right,
                    R.xml.anim_slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }
}
