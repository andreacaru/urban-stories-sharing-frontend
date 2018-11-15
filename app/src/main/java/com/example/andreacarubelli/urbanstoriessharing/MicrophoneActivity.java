package com.example.andreacarubelli.urbanstoriessharing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;

public class MicrophoneActivity extends AppCompatActivity {

    public static final int RECORD_PERMISSION = 124;
    private boolean mRecordPermissionGranted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private MediaRecorder mRecorder = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    ImageButton btnMic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_activity_mic);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        btnMic = (ImageButton) findViewById(R.id.micImageButton);
    }

    protected void goBack(View view) {
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RECORD_PERMISSION:
                mRecordPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!mRecordPermissionGranted) finish();

    }

    public void onTouchMicButton(View v){
        ImageButton myButton = (ImageButton) findViewById(R.id.micImageButton);
        myButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mRecorder.setOutputFile(mFileName);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                        try {
                            mRecorder.prepare();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "prepare() failed");
                        }

                        mRecorder.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                }
                return true;
            }
        });

    }


}

