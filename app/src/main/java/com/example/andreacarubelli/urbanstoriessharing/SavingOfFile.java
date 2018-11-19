package com.example.andreacarubelli.urbanstoriessharing;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavingOfFile extends AppCompatActivity implements FileInformation{

    public void createFolder(Context context) {
        File globalNotesFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER);

        if (!globalNotesFolder.exists()) {
            globalNotesFolder.mkdir();
            MediaScannerConnection.scanFile(context, new String[]{globalNotesFolder.toString()}, null, null);
        }
    }

    public File createImageFileFolder(Context context, String folderName, int numImg) throws IOException {
        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);
        if(!imageFolder.exists()) {
            imageFolder.mkdir();
            MediaScannerConnection.scanFile(context, new String[]{imageFolder.toString()}, null, null);
        }
        String imageFileName = "JPEG_" + numImg + "_";
        String ImageFilePath = imageFolder + File.separator + imageFileName;
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                imageFolder      /* directory */
        );
        return image;
    }

}
