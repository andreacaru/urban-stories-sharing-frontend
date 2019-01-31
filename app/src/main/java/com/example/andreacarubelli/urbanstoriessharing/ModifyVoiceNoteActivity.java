package com.example.andreacarubelli.urbanstoriessharing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ModifyVoiceNoteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_voice_note);

        Intent intent = getIntent();
        int numMic = intent.getExtras().getInt("numMic");
        String folderName = intent.getExtras().getString("nomeCartella");

        ImageView backArrow = (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GridLayout gridlayout = findViewById(R.id.grid_view);
        gridlayout.setColumnCount(3);

        //LinearLayout layout = findViewById(R.id.modifyVoiceNote);
        File voiceFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.AUDIO);

        ArrayList<Button> imageViews = new ArrayList<Button>();

        //LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //llp.setMargins(30, 30, 0, 0); // llp.setMargins(left, top, right, bottom);

        for(int i=0; i<=numMic; i++){

            LinearLayout layout = new LinearLayout(this);
            ImageView mImageView = new ImageView(this);
            TextView textView = new TextView(this);
            MaterialButton elimina = new MaterialButton(this);

            layout.setOrientation(LinearLayout.VERTICAL);

            elimina.setText("elimina");
            textView.setText("Registrazione " + i);

            elimina.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButton)));

            mImageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
            String voiceName = "/Registrazione_num_" + i + ".3gp";
            layout.addView(textView);
            layout.addView(mImageView);
            layout.addView(elimina);

            elimina.setTag(voiceName);
            File img = new File(folder, voiceName);

            if (img.exists()){
                gridlayout.addView(layout);
                layout.setPadding(0,0,30,30);
                mImageView.setImageResource(R.drawable.ic_mic_black_24dp);
                imageViews.add(elimina);
            }
            else mImageView.setImageResource(android.R.color.transparent);

        }

        for(Button imageView : imageViews){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = (String) view.getTag();
                    File mic = new File(folder, str);
                    Dialog alert = onCreateDialog(mic);
                    alert.show();
                }
            });
        }
    }

    public Dialog onCreateDialog(final File mic){
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyVoiceNoteActivity.this);
        builder.setMessage(R.string.vuoi_cancellare)
                .setPositiveButton(R.string.cancella, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        int numVoice = intent.getExtras().getInt("numMic");
                        mic.delete();
                        setResult(RESULT_OK, intent);
                        ModifyVoiceNoteActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.mantieni, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
