package com.example.andreacarubelli.urbanstoriessharing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ModifyVoiceNote extends Activity {

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

        LinearLayout layout = findViewById(R.id.modifyVoiceNote);
        File voiceFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.AUDIO);

        ArrayList<ImageView> imageViews = new ArrayList<ImageView>();

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(30, 30, 0, 0); // llp.setMargins(left, top, right, bottom);

        for(int i=0; i<=numMic; i++){
            ImageView mImageView = new ImageView(this);
            TextView textView = new TextView(this);
            mImageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
            String voiceName = "/Registrazione_num_" + i + ".3gp";
            mImageView.setTag(voiceName);
            File img = new File(folder, voiceName);
            if (img.exists()){
                layout.addView(textView);
                layout.addView(mImageView);
                textView.setText("Registrazione " + i);
                textView.setLayoutParams(llp);
                mImageView.setPadding(15,15,0,15);
                mImageView.setImageResource(R.drawable.ic_mic_black_24dp);
                imageViews.add(mImageView);
            }
            else mImageView.setImageResource(android.R.color.transparent);

        }

        for(ImageView imageView : imageViews){
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyVoiceNote.this);
        builder.setMessage(R.string.vuoi_cancellare)
                .setPositiveButton(R.string.cancella, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        int numVoice = intent.getExtras().getInt("numMic");
                        mic.delete();
                        setResult(RESULT_OK, intent);
                        ModifyVoiceNote.this.finish();
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
