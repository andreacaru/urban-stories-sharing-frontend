package com.example.andreacarubelli.urbanstoriessharing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MicrophoneActivity extends AppCompatActivity {

    public static final int RECORD_PERMISSION = 124;
    private boolean mRecordPermissionGranted = false;
    int numMic;
    private static final String LOG_TAG = "MicTest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_activity_mic);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        ImageView backArrow = (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnAvviaRegistrazione = (Button) findViewById(R.id.btnAvvioRegistrazione);
        btnAvviaRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File micFile = null;
                try{
                    micFile = createMicFile(view.getContext(), folderName, numMic);
                    numMic++;
                }catch (IOException e){
                    Log.e(LOG_TAG, "prepare() failed");
                }

            }
        });

    }

    private File createMicFile (Context context, String folderName, int numMic) throws IOException {
        SavingOfFile folderFileMic = new SavingOfFile();
        File reg = folderFileMic.createMicFileFolder(context, folderName, numMic);
        return reg;
        //implementare creazione file audio in SavingOfFile
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

}

