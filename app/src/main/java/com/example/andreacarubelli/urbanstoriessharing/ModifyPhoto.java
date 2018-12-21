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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.internal.ListenerHolder;

import java.io.File;
import java.util.ArrayList;

public class ModifyPhoto extends Activity {
    int numImgCancellate = 0;

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

        GridLayout gridlayout = new GridLayout(this);
        gridlayout.setColumnCount(3);
        LinearLayout layout = findViewById(R.id.modifyPhoto);
        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);

        ArrayList<ImageView> imageViews = new ArrayList<ImageView>();

        for(int i=0; i<=numImmagine; i++){
            ImageView mImageView = new ImageView(this);
            mImageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
            String imageName = "JPEG_" + i + "_" + ".jpg";
            mImageView.setTag(imageName);
            File img = new File(folder, imageName);
            if (img.exists()){
                layout.addView(mImageView);
                Bitmap bitmap = BitmapFactory.decodeFile(folder.toString() + "/" + imageName);
                mImageView.setImageBitmap(bitmap);
                imageViews.add(mImageView);
            }
            else mImageView.setImageResource(android.R.color.transparent);

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