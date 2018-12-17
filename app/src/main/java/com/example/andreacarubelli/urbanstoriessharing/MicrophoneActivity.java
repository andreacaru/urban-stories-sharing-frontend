package com.example.andreacarubelli.urbanstoriessharing;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

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

        final Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");
        final int numMic = intent.getExtras().getInt("numMic");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_activity_mic);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        Button btnAvviaRegistrazione = (Button) findViewById(R.id.btnAvvioRegistrazione);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnAvviaRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!registrazioneAvviata){
                    try{
                        registrazioneAvviata = true;
                        micFile = null;
                        micFile = createMicFile(view.getContext(), folderName, numMic);
                        Snackbar.make(view, "Registrazione in corso...", Snackbar.LENGTH_LONG)
                                .show();
                        progressBar.setVisibility(View.VISIBLE);
                    }catch (IOException e){
                        Log.e(LOG_TAG, "prepare() failed");
                    }
                }
                else{
                    Snackbar.make(view, "Stai già registrando! Schiaccia su FERMA", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        Button btnStopRegistrazione = (Button) findViewById(R.id.btnStopRegistrazione);
        btnStopRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrazioneAvviata = false;
                if (micFile == null) {
                    Snackbar.make(view, "Non hai ancora registrato nulla!", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    stopRecording(micFile);
                    progressBar.setVisibility(View.INVISIBLE);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        ImageView backArrow = (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (micFile!=null){
                    Dialog alert = onCreateDialog(micFile);
                    alert.show();
                }
                else{
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(micFile!=null){
            Dialog alert = onCreateDialog(micFile);
            alert.show();
        }
        else{
            finish();
        }
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


    public Dialog onCreateDialog(final MediaRecorder micFiles){
        AlertDialog.Builder builder = new AlertDialog.Builder(MicrophoneActivity.this);
        builder.setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.salva_vocale, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        stopRecording(micFiles);
                        MicrophoneActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.cancella_vocale, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopRecording(micFiles);
                        removeFileAudio();
                        MicrophoneActivity.this.finish();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void removeFileAudio(){
        Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");
        final int numMic = intent.getExtras().getInt("numMic");

        File path = new File (Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.AUDIO);
        String micFileName = "/Registrazione_num_" + numMic + ".3gp";

        File fileAudio = new File(path, micFileName);
        if (fileAudio.exists()) fileAudio.delete();
        else  Snackbar.make(getWindow().getDecorView().getRootView(), "Non trovo il file", Snackbar.LENGTH_SHORT)
                .show();

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

