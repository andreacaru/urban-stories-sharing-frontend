package com.example.andreacarubelli.urbanstoriessharing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MicrophoneActivity extends AppCompatActivity {

    public static final int RECORD_PERMISSION = 124;
    private boolean mRecordPermissionGranted = false;
    private static final String LOG_TAG = "MicTest";

    MediaRecorder micFile = null;
    boolean registrazioneAvviata = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        isRecordPermissionGranted();

        Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");
        final int numMic = intent.getExtras().getInt("numMic");


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
                if(!registrazioneAvviata){
                    try{
                        registrazioneAvviata = true;
                        micFile = null;
                        micFile = createMicFile(view.getContext(), folderName, numMic);
                        Toast.makeText(getApplicationContext(), "Registrazione audio cominciata!", Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                }
                else{ Toast.makeText(getApplicationContext(), "Stai già registrando! Schiaccia su FERMA", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnStopRegistrazione = (Button) findViewById(R.id.btnStopRegistrazione);
        btnStopRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrazioneAvviata = false;
                if (micFile == null) {
                    Toast.makeText(getApplicationContext(), "Non hai ancora registrato nulla!", Toast.LENGTH_SHORT).show();
                } else {
                    stopRecording(micFile);
                    Toast.makeText(getApplicationContext(), "Registrazione audio terminata e salvata!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private MediaRecorder createMicFile (Context context, String folderName, int numMic) throws IOException {
        SavingOfFile folderFileMic = new SavingOfFile();
        MediaRecorder registrazione = folderFileMic.createMicFileFolder(context, folderName, numMic);
        return registrazione;
    }

    private void stopRecording (MediaRecorder mRecorder){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void isRecordPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            mRecordPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_PERMISSION);
        }
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

