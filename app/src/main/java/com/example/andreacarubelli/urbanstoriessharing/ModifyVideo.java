package com.example.andreacarubelli.urbanstoriessharing;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;

public class ModifyVideo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_video);

        Intent intent = getIntent();
        int numVideo = intent.getExtras().getInt("numVideo");
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
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.VIDEOS);

        ArrayList<ImageView> videoViews = new ArrayList<ImageView>();

        for(int i=0; i<=numVideo; i++){
            ImageView mImageView = new ImageView(this);
            mImageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
            String videoName = "Video_num_" + i + "_"+ ".mp4";
            mImageView.setTag(videoName);
            File video = new File(folder, videoName);
            if (video.exists()){
                gridlayout.addView(mImageView);
                Bitmap bitmap = getThumblineImage(folder.toString() + "/" + videoName);
                mImageView.setImageBitmap(bitmap);
                mImageView.setPadding(0,0,30,30);
                videoViews.add(mImageView);
            }
            else mImageView.setImageResource(android.R.color.transparent);
        }

        for(ImageView imageView : videoViews){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = (String) view.getTag();
                    File video = new File(folder, str);
                    Dialog alert = onCreateDialog(video);
                    alert.show();
                }
            });
        }

    }

    public static Bitmap getThumblineImage(String videoPath) {
        return ThumbnailUtils.createVideoThumbnail(videoPath, MINI_KIND);
    }

    public Dialog onCreateDialog(final File video){
        AlertDialog.Builder builder = new AlertDialog.Builder(ModifyVideo.this);
        builder.setMessage(R.string.vuoi_cancellare)
                .setPositiveButton(R.string.cancella, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        int numVideo = intent.getExtras().getInt("numVideo");
                        video.delete();
                        setResult(RESULT_OK, intent);
                        ModifyVideo.this.finish();
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
