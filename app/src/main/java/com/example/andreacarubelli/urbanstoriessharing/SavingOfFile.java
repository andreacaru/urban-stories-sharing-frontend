package com.example.andreacarubelli.urbanstoriessharing;

import android.content.Context;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import static android.content.Context.MODE_PRIVATE;
import static java.io.File.createTempFile;

public class SavingOfFile implements FileInformation {

    private MediaRecorder mRecorder;

    public File createImageFileFolder(Context context, String folderName, int numImg) throws IOException {
        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        if(!imageFolder.exists()) {
            imageFolder.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{imageFolder.toString()}, null, null);
        }

        File imageFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);
        if(!imageFolderNew.exists()) {
            imageFolderNew.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{imageFolderNew.toString()}, null, null);
        }

        String imageFileName = "JPEG_" + numImg + "_" + ".jpg";
        File image = new File (imageFolderNew, imageFileName);
        return image;
    }

    public File createVideoFileFolder(Context context, String folderName, int numVid) throws IOException {
        File videoFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);

        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{videoFolder.toString()}, null, null);
        }

        File videoFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.VIDEOS);

        if (!videoFolderNew.exists()) {
            videoFolderNew.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{videoFolderNew.toString()}, null, null);
        }

        String videoFileName = "Video_num_" + numVid + "_"+ ".mp4";
        File video = new File(videoFolderNew, videoFileName);

        return video;
    }

    public MediaRecorder createMicFileFolder(Context context, String folderName, int numMic) throws  IOException{
        File micFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        if(!micFolder.exists()) {
            micFolder.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{micFolder.toString()}, null, null);
        }

        File micFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.AUDIO);
        if(!micFolderNew.exists()) {
            micFolderNew.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{micFolderNew.toString()}, null, null);
        }

        String micFileName = micFolderNew.toString() + "/Registrazione_num_" + numMic + ".3gp";

        MediaRecorder mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(micFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
        }

        mRecorder.start();

        return mRecorder;
    }

    public void createNoteFileFolder(Context context, String folderName, String text) throws IOException{
        File noteFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        if(!noteFolder.exists()) {
            noteFolder.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{noteFolder.toString()}, null, null);
        }

        File noteFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.NOTES);
        if(!noteFolderNew.exists()) {
            noteFolderNew.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{noteFolderNew.toString()}, null, null);
        }

        String noteFileName = "Nota" + ".txt";
        try {
            File noteFile = new File(noteFolderNew, noteFileName);
            FileWriter writer = new FileWriter(noteFile);
            writer.append(text);
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
