package com.example.andreacarubelli.urbanstoriessharing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_TAKE_VIDEO = 2;
    public static final int CAMERA_PERMISSION = 123;
    public static final int VIDEO_PERMISSION = 999;
    public static final int STORAGE_PERMISSION = 125;


    private boolean mCameraPermissionGranted,mStoragePermissionGranted, mVideoPermissionGranted;
    ImageView mImageView;

    String folderName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    int numImg, numVid, numMic;

    Geocoder geocoder;
    List<Address> addresses;
    double lat=0, lng=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getStoragePermission();

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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ImageView backArrow = (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Button buttonPhoto = findViewById(R.id.photoButton);
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

        final Button buttonVideo = findViewById(R.id.videoButton);
        buttonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVideoPermission();
                if (mVideoPermissionGranted){
                    goToVideo();
                }
                else{
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Devi accettare i permessi per proseguire!", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        final Button buttonMic = findViewById(R.id.micButton);
        buttonMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), MicrophoneActivity.class);
                intent.putExtra("nomeCartella", folderName);
                intent.putExtra("numMic", numMic);
                startActivity(intent);
                numMic++;
            }
        });

        final Button buttonNote = findViewById(R.id.noteButton);
        buttonNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(view.getContext(), TextNoteActivity.class);
                intent.putExtra("nomeCartella", folderName);
                startActivity(intent);
            }
        });

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
                photoFile = createImageFile(this, folderName, numImg);
                numImg++;
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
            }
        }
    }

    public void goToVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = createVideoFile(this, folderName, numVid);
                numVid++;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }


}

