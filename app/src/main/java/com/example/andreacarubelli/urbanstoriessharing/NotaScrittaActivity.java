package com.example.andreacarubelli.urbanstoriessharing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class NotaScrittaActivity extends Activity {
    private static final String LOG_TAG = "NoteTest";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota_scritta);
        Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");
        final int numNote = intent.getExtras().getInt("numNota");

        ImageView backArrow =  (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText textInput = (EditText) findViewById(R.id.noteEditText);

        Button sendButton = (Button) findViewById(R.id.SendButtonNote);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String text = textInput.getText().toString();
                    createNoteFile(view.getContext(), folderName, numNote, text);
                    Toast.makeText(getApplicationContext(), "Nota testuale salvata!", Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    Log.e(LOG_TAG, "prepare() failed");
                }
                finish();
            }
        });

    }

    private void createNoteFile (Context context, String folderName, int numNote, String text) throws IOException{
        SavingOfFile folderFileNote = new SavingOfFile();
        folderFileNote.createNoteFileFolder(context, folderName, numNote, text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}

}
