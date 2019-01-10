package com.example.andreacarubelli.urbanstoriessharing;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
        final EditText textInput = (EditText) findViewById(R.id.noteEditText);

        ImageView backArrow =  (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textInput.length() == 0) finish();
                else {
                    Dialog alert = onCreateDialog();
                    alert.show();
                }
            }
        });

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
        int numRighe = contaRighe(file);
        int count=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                count++;
                if(numRighe==1){
                    text.append(line);
                }
                else{
                    if(count == numRighe){
                        text.append(line);
                    }
                    else{
                        text.append(line);
                        text.append('\n');
                    }

                }
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text;
    }

    public int contaRighe(File file){
        int count=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                count++;
            }
            br.close();
        }catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return count;
    }

    public Dialog onCreateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TextNoteActivity.this);
        builder.setMessage(R.string.dialog_message_note)
                .setPositiveButton(R.string.positive_message_post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TextNoteActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.negative_message_post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        final EditText textInput = (EditText) findViewById(R.id.noteEditText);
        if(textInput.length() == 0){
            finish();
        }
        else {
            Dialog alert = onCreateDialog();
            alert.show();
        }
    }




}
