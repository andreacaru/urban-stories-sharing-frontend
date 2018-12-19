package com.example.andreacarubelli.urbanstoriessharing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextNoteActivity extends Activity {
    private static final String LOG_TAG = "NoteTest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_note);
        Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");

        ImageView backArrow =  (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText textInput = (EditText) findViewById(R.id.noteEditText);

        Button sendButton = (Button) findViewById(R.id.SendButtonNote);

        textInput.setText(readFile().toString());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                        String text = textInput.getText().toString();
                        createNoteFile(view.getContext(), folderName, text);
                        Snackbar.make(view, "Nota testuale salvata", Snackbar.LENGTH_SHORT)
                            .show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 1500);
                    } catch (IOException e){
                    Log.e(LOG_TAG, "prepare() failed");
                }
                }

        });
    }


    private void createNoteFile (Context context, String folderName, String text) throws IOException{
        SavingOfFile folderFileNote = new SavingOfFile();
        folderFileNote.createNoteFileFolder(context, folderName, text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}

    private StringBuilder readFile (){
        Intent intent = getIntent();
        final String folderName = intent.getExtras().getString("nomeCartella");

        final String path = Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.NOTES;

        String fileName= "Nota.txt";

        StringBuilder text = new StringBuilder();
        File file = new File(path, fileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text;
    }

}
