package com.example.andreacarubelli.urbanstoriessharing;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.internal.ListenerHolder;

import java.io.File;
import java.util.ArrayList;

import static com.example.andreacarubelli.urbanstoriessharing.R.color.colorButton;

public class ModifyPhoto extends Activity {
    int numImgCancellate = 0;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_photo);
        Intent intent = getIntent();
        int numImmagine = intent.getExtras().getInt("numPhoto");
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

        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);

        ArrayList<Button> imageViews = new ArrayList<Button>();

        for(int i=0; i<=numImmagine; i++){

            LinearLayout layout = new LinearLayout(this);
            ImageView mImageView = new ImageView(this);
            MaterialButton elimina = new MaterialButton(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            elimina.setText("elimina");

            elimina.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorButton)));


            mImageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
            layout.addView(mImageView);
            layout.addView(elimina);

            String imageName = "JPEG_" + i + "_" + ".jpg";
            elimina.setTag(imageName);
            File img = new File(folder, imageName);
            if (img.exists()){
                gridlayout.addView(layout);
                Bitmap bitmap = BitmapFactory.decodeFile(folder.toString() + "/" + imageName);
                mImageView.setImageBitmap(bitmap);
                layout.setPadding(0,0,30,30);
                imageViews.add(elimina);
            }
            else mImageView.setImageResource(android.R.color.transparent);

        }

        for(Button imageView : imageViews){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = (String) view.getTag();
                    File img = new File(folder, str);
                    Dialog alert = onCreateDialog(img);
                    alert.show();
                }
            });
        }
    }

    public Dialog onCreateDialog(final File img){
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPhoto.this);
        builder.setMessage(R.string.vuoi_cancellare)
                .setPositiveButton(R.string.cancella, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        int numImmagine = intent.getExtras().getInt("numPhoto");
                        img.delete();
                        setResult(RESULT_OK, intent);
                        ModifyPhoto.this.finish();
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