package com.nexusunsky.liuhao.raindeom;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EmojieRainViewer mRainViewer;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.baselayout);
        mRainViewer = new EmojieRainViewer(MainActivity.this, rootLayout);

        button = (Button) findViewById(R.id.rain_trigger);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRainViewer.preparEmojiRain(30, R.drawable.yuanbao, 3000, new EmojieRainViewer.ITriggerCondition() {
                    @Override
                    public boolean triggerCondition(Map<ObjectAnimator, ObjectAnimator> objectAnimator) {
                        return true;
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRainViewer.releaseResource();
    }
}
