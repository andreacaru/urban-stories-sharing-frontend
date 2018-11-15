package com.example.andreacarubelli.urbanstoriessharing;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavingOfFile extends AppCompatActivity implements PostActivity.FileInformation{

    private String getFolderName () {
        String folderName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return folderName;
    }

    int i = 0;
    String folderName = getFolderName();

    public void createFolder(){
        File globalNotesFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                PostActivity.FileInformation.ROOT_FOLDER + "/" + PostActivity.FileInformation.NOTES_FOLDER);

        globalNotesFolder.mkdir();
        File notesFolder = new File(globalNotesFolder.getPath() + "/" + folderName);
        MediaScannerConnection.scanFile(this, new String[] {notesFolder.toString()}, null, null);
    }

    public File createImageFileFolder() throws IOException {
        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                PostActivity.FileInformation.ROOT_FOLDER + "/" + PostActivity.FileInformation.NOTES_FOLDER + "/" + folderName + PostActivity.FileInformation.PICTURES);
        if(!imageFolder.exists()) {
            imageFolder.mkdir();
            MediaScannerConnection.scanFile(this, new String[]{imageFolder.toString()}, null, null);
        }
        String imageFileName = "JPEG_" + i + "_";
        i++;
        String ImageFilePath = imageFolder + File.separator + imageFileName;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                imageFolder      /* directory */
        );
        return image;
    }

}
