package com.example.andreacarubelli.urbanstoriessharing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_TAKE_VIDEO = 2;
    public static final int REQUEST_TAKE_AUDIO = 3;
    public static final int CAMERA_PERMISSION = 123;
    public static final int VIDEO_PERMISSION = 999;
    public static final int STORAGE_PERMISSION = 125;
    public static final int REQUEST_CANCELLED_PHOTO = 1234;
    public static final int REQUEST_CANCELLED_VIDEO = 1235;
    public static final int REQUEST_CANCELLED_AUDIO = 1236;


    private boolean mCameraPermissionGranted,mStoragePermissionGranted, mVideoPermissionGranted;
    ImageView mImageView;

    String folderName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    int numImg, numVid, numMic, numNota;

    Geocoder geocoder;
    List<Address> addresses;
    double lat=0, lng=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getStoragePermission();

        getAddress();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ImageView backArrow = (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final ImageView buttonPhoto = findViewById(R.id.photoButton);
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraPermission();
                if (mCameraPermissionGranted){
                    goToCamera();
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Devi accettare i permessi per proseguire!", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        final ImageView buttonVideo = findViewById(R.id.videoButton);
        buttonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideoPermission();
                if (mVideoPermissionGranted){
                    goToVideo();
                }
                else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Devi accettare i permessi per proseguire!", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        final ImageView buttonMic = findViewById(R.id.micButton);
        buttonMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), MicrophoneActivity.class);
                intent.putExtra("nomeCartella", folderName);
                intent.putExtra("numMic", numMic);
                startActivityForResult(intent, REQUEST_TAKE_AUDIO);
            }
        });

        final ImageView buttonNote = findViewById(R.id.noteButton);
        buttonNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(view.getContext(), TextNoteActivity.class);
                intent.putExtra("nomeCartella", folderName);
                startActivity(intent);
            }
        });

        final Button modifyPhoto = findViewById(R.id.btnModificaFoto);
        final Button modifyVideo = findViewById(R.id.btnModificaVideo);
        final Button modifyVocalNote = findViewById(R.id.btnModificaNotaVocale);
        final Button modifyNota = findViewById(R.id.btnModificaNota);

        modifyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ModifyPhoto.class);
                intent.putExtra("nomeCartella", folderName);
                intent.putExtra("numPhoto", numImg);
                startActivityForResult(intent, REQUEST_CANCELLED_PHOTO);
            }
        });

        modifyVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ModifyVideo.class);
                intent.putExtra("nomeCartella", folderName);
                intent.putExtra("numVideo", numVid);
                startActivityForResult(intent, REQUEST_CANCELLED_VIDEO);
            }
        });

        modifyVocalNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ModifyVoiceNote.class);
                intent.putExtra("nomeCartella", folderName);
                intent.putExtra("numMic", numMic);
                startActivityForResult(intent, REQUEST_CANCELLED_AUDIO);
            }
        });

        modifyNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TextNoteActivity.class);
                intent.putExtra("nomeCartella", folderName);
                startActivity(intent);
                numNota++;
            }
        });

        TextView invia = findViewById(R.id.invia);
        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCSV();
            }
        });

        final TextView textNumFoto = findViewById(R.id.textNumFoto);
        final TextView textNumVideo = findViewById(R.id.textNumVideo);
        final TextView textNumVocali = findViewById(R.id.textNumVocali);

        textNumFoto.setVisibility(View.INVISIBLE);
        textNumVideo.setVisibility(View.INVISIBLE);
        textNumVocali.setVisibility(View.INVISIBLE);
        modifyPhoto.setVisibility(View.INVISIBLE);
        modifyVideo.setVisibility(View.INVISIBLE);
        modifyVocalNote.setVisibility(View.INVISIBLE);
        modifyNota.setVisibility(View.INVISIBLE);


        final TextView numFoto = findViewById(R.id.numFoto);
        final TextView numVideo = findViewById(R.id.numVideo);
        final TextView numVocali = findViewById(R.id.numVocali);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setNumFile();
    }

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION);
        }
    }

    private void getVideoPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mVideoPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    VIDEO_PERMISSION);
        }
    }

    private void getStoragePermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraPermissionGranted=true;
                    goToCamera();
                }
            }

            case VIDEO_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mVideoPermissionGranted=true;
                    if (!mCameraPermissionGranted){
                        goToVideo();
                        }
                }
            }

            case STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER);
                    if(!folder.exists()) {
                        folder.mkdirs();
                        MediaScannerConnection.scanFile(this, new String[]{folder.toString()}, null, null);
                    }
                    mStoragePermissionGranted=true;
                }
            }
        }
    }

    public void goToCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(this, folderName, getNumImg(numImg));
            } catch (IOException ex) {
                // Error occurred while creating the File
                Snackbar.make(getWindow().getDecorView().getRootView(), "Ho avuto un problema nella creazione dell'immagine!", Snackbar.LENGTH_SHORT)
                        .show();
            }
            // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    setResult(RESULT_OK, takePictureIntent);
                }
            }
        }

    public void goToVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = createVideoFile(this, folderName, numVid);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Snackbar.make(getWindow().getDecorView().getRootView(), "Ho avuto un problema nella creazione del video!", Snackbar.LENGTH_SHORT)
                        .show();
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
                setResult(RESULT_OK, takeVideoIntent);
            }
        }
    }

    private File createImageFile (Context context, String folderName, int numImg) throws IOException {
        SavingOfFile folderFileImage = new SavingOfFile();
        File image = folderFileImage.createImageFileFolder(context, folderName, numImg);
        return image;
    }

    private File createVideoFile (Context context, String folderName, int numVid) throws IOException {
        SavingOfFile folderFileVideo = new SavingOfFile();
        File video = folderFileVideo.createVideoFileFolder(context, folderName, numVid);
        return video;
    }

    private void setNumFile(){

        final Button modifyPhoto = findViewById(R.id.btnModificaFoto);
        final Button modifyVideo = findViewById(R.id.btnModificaVideo);
        final Button modifyVocalNote = findViewById(R.id.btnModificaNotaVocale);
        final Button modifyNota = findViewById(R.id.btnModificaNota);

        final TextView textNumFoto = findViewById(R.id.textNumFoto);
        final TextView textNumVideo = findViewById(R.id.textNumVideo);
        final TextView textNumVocali = findViewById(R.id.textNumVocali);

        final TextView numFoto = findViewById(R.id.numFoto);
        final TextView numVideo = findViewById(R.id.numVideo);
        final TextView numVocali = findViewById(R.id.numVocali);

        int numImmagine = contaFoto();
        int numVideos = contaVideo();
        int numNotaVocale = contaMic();
        int numNota = contaNota();


        numFoto.setText(" "+ numImmagine);
        numVideo.setText(" " + numVideos);
        numVocali.setText(" " + numNotaVocale);


        if (numImmagine == 0){
            numFoto.setVisibility(View.INVISIBLE);
            textNumFoto.setVisibility(View.INVISIBLE);
            modifyPhoto.setVisibility(View.INVISIBLE);
        } else {
            numFoto.setVisibility(View.VISIBLE);
            textNumFoto.setVisibility(View.VISIBLE);
            modifyPhoto.setVisibility(View.VISIBLE);
            numFoto.setText(" "+ numImmagine);
        }

        if(numVideos == 0){
            numVideo.setVisibility(View.INVISIBLE);
            textNumVideo.setVisibility(View.INVISIBLE);
            modifyVideo.setVisibility(View.INVISIBLE);
        } else {
            textNumVideo.setVisibility(View.VISIBLE);
            numVideo.setVisibility(View.VISIBLE);
            modifyVideo.setVisibility(View.VISIBLE);
            numVideo.setText(" " + numVideos);
        }

        if(numNotaVocale == 0){
            numVocali.setVisibility(View.INVISIBLE);
            textNumVocali.setVisibility(View.INVISIBLE);
            modifyVocalNote.setVisibility(View.INVISIBLE);
        } else {
            textNumVocali.setVisibility(View.VISIBLE);
            numVocali.setVisibility(View.VISIBLE);
            modifyVocalNote.setVisibility(View.VISIBLE);
            numVocali.setText(" " + numNotaVocale);
        }

        if(numNota == 0){
            modifyNota.setVisibility(View.INVISIBLE);
        } else {
            modifyNota.setVisibility(View.VISIBLE);
        }


    }

    private int getNumImg(int numImg){
        return numImg;
    }
    private int getNumVid(int numVid){
        return numVid;
    }
    private int getNumMic(int numMic){
        return numMic;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            numImg++;
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKE_VIDEO) {
            numVid++;
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKE_AUDIO) {
            numMic++;
        }
    }

    private int contaFoto(){
        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File imageFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);

        File[] list = imageFolderNew.listFiles();
        int count = 0;
        if(list!=null){
            for(File f: list){
                count++;
            }
        }
        return count;
    }

    private int contaVideo(){
        File videoFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File videoFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.VIDEOS);

        File[] list = videoFolderNew.listFiles();
        int count = 0;
        if(list!=null){
            for(File f: list){
                count++;
            }
        }
        return count;
    }

    private int contaMic(){
        File voiceFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File voiceFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.AUDIO);

        File[] list = voiceFolderNew.listFiles();
        int count = 0;
        if(list!=null){
            for(File f: list){
                count++;
            }
        }
        return count;

    }

    private int contaNota(){
        File voiceFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File voiceFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.NOTES);

        File[] list = voiceFolderNew.listFiles();
        int count = 0;
        if(list!=null){
            for(File f: list){
                count++;
            }
        }
        return count;

    }

    private void getAddress(){
        EditText addressText = (EditText) findViewById(R.id.addressEditText);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Bundle bundle = getIntent().getExtras();
        if(getIntent().getExtras()!=null){
            lat = bundle.getDouble("latitude");
            lng = bundle.getDouble("longitude");
        }

        try{
            List<Address> addresses = geocoder.getFromLocation(lat,lng, 1);
            if (addresses!=null & lat!=0 & lng!=0){
                Address fetchedAddress = addresses.get(0);
                String Address = fetchedAddress.getAddressLine(0).toString();
                addressText.setText(Address, TextView.BufferType.EDITABLE);
            }
            else {
                Snackbar.make(getWindow().getDecorView().getRootView(), "Non sono riuscito a recuperare la posizione!", Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        catch (IOException e){}
    }

    private void createCSV(){
        String address, namePlace;
        EditText addressText = (EditText) findViewById(R.id.addressEditText);
        EditText nameText = (EditText) findViewById(R.id.nameEditText);
        address = addressText.getText().toString();
        namePlace = nameText.getText().toString();
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        if (!(folder.exists())){
            folder.mkdirs();
        }
        String nameFile = "Informazioni" + ".csv";
        try{
            File informationFile = new File (folder, nameFile);
            FileWriter writer = new FileWriter(informationFile);
            writer.append(address);
            writer.append('\n');
            writer.append(namePlace);
            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

}