package com.example.andreacarubelli.urbanstoriessharing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class NoteActivity extends Activity {

    int numNote;
    private static final String LOG_TAG = "NoteTest";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");

        ImageView backArrow =  (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button sendButton = (Button) findViewById(R.id.SendButtonNote);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File noteFile = null;
                try{
                    noteFile = createNoteFile(view.getContext(), folderName, numNote);
                    numNote++;
                } catch (IOException e){
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });

    }


    private File createNoteFile (Context context, String folderName, int numNote) throws IOException{
        SavingOfFile folderFileNote = new SavingOfFile();
        File note = folderFileNote.createNoteFileFolder(context, folderName, numNote);
        return note;
    }

}
