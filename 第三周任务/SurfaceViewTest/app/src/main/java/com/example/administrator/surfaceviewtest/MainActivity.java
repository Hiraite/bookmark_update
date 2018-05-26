package com.example.administrator.surfaceviewtest;

import android.content.pm.ApplicationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;
    private FrameAnimationView frameAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        startButton = findViewById(R.id.startButton);
        stopButton =  findViewById(R.id.stopButton);
        frameAnimationView = findViewById(R.id.mSurfaceView);
        initButtonListener();
    }

    private void initButtonListener() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] mBitmapResourceIds = getBitmapResourceIds();
                frameAnimationView.setBitmapResourceIds(mBitmapResourceIds);
                frameAnimationView.startAnimation();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameAnimationView.stop();
            }
        });
    }

    private int[] getBitmapResourceIds() {
        int pngNum = 16;
        int[] resourceIds = new int[pngNum];

        for(int i = 0; i < pngNum; i++){
            String name = "word_detail_recording_" + (i+1) ;
            resourceIds[i] = getResources().getIdentifier(
                    name,
                    "drawable",
                    getPackageName());
        }
        return resourceIds;
    }

}
