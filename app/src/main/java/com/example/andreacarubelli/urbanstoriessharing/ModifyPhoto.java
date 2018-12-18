package com.example.andreacarubelli.urbanstoriessharing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.internal.ListenerHolder;

import java.io.File;
import java.util.ArrayList;

public class ModifyPhoto extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_photo);
        Intent intent = getIntent();
        int numImmagine = intent.getExtras().getInt("numPhoto");
        String folderName = intent.getExtras().getString("nomeCartella");
        numImmagine--;

        ImageView backArrow = (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout layout = findViewById(R.id.modifyPhoto);
        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);

        ArrayList<ImageView> imageViews = new ArrayList<ImageView>();

        for(int i=0; i<=numImmagine; i++){
            ImageView mImageView = new ImageView(this);
            mImageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
            layout.addView(mImageView);
            String imageName = "JPEG_" + i + "_" + ".jpg";
            mImageView.setTag(imageName);
            File img = new File(folder, imageName);
            Bitmap bitmap = BitmapFactory.decodeFile(folder.toString() + "/" + imageName);
            if (img.exists()){
                mImageView.setImageBitmap(bitmap);
            }
            imageViews.add(mImageView);
        }

        for(ImageView imageView : imageViews){
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
                        decrementaNumImg(numImmagine);
                        img.delete();
                        ModifyPhoto.this.finish();
                    }
                })
                .setNegativeButton(R.string.mantieni, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ModifyPhoto.this.finish();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public int decrementaNumImg(int numImg){
        numImg--;
        return numImg;
    }

}

/*
    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPhoto.this);
                    builder.setMessage(R.string.dialog_message)
                            .setPositiveButton(R.string.salva_vocale, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {
        img.delete();
        ModifyPhoto.this.finish();
        }
        })
        .setNegativeButton(R.string.cancella_vocale, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {
        ModifyPhoto.this.finish();
        }
        });
// Create the AlertDialog object and return it
//return builder.create();


for(ImageView imgView : imageViews){
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPhoto.this);
                        builder.setMessage(R.string.dialog_message)
                                .setPositiveButton(R.string.salva_vocale, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        img.delete();
                                        ModifyPhoto.this.finish();
                                    }
                                })
                                .setNegativeButton(R.string.cancella_vocale, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ModifyPhoto.this.finish();
                                    }
                                });
                            // Create the AlertDialog object and return it
                            //return builder.create();
                    }
                });
            }
*/