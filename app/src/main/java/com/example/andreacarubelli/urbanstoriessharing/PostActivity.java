package com.example.andreacarubelli.urbanstoriessharing;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private JSONObject jsonObject;

    String URL = "http://192.168.2.4:8001/api/";

    ProgressDialog progressdialog;


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
                if((contaFoto()==0) && (contaMic()==0) && (contaVideo()==0) && (contaNota()==0)) {
                    finish();
                }
                else {
                    Dialog alert = onCreateDialog();
                    alert.show();
                }
            }
        });

        final LinearLayout buttonPhoto = findViewById(R.id.fotoTestoFoto);
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

        final LinearLayout buttonVideo = findViewById(R.id.videoTestoVideo);
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

        final LinearLayout buttonMic = findViewById(R.id.micTestoMic);
        buttonMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (view.getContext(), MicrophoneActivity.class);
                intent.putExtra("nomeCartella", folderName);
                intent.putExtra("numMic", numMic);
                startActivityForResult(intent, REQUEST_TAKE_AUDIO);
            }
        });

        final LinearLayout buttonNote = findViewById(R.id.notaTestoNota);
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
            }
        });

        TextView invia = findViewById(R.id.invia);
        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controllaNota()) new UploadFile().execute();
                /*
                createCSV();
                if(controllaNota()){
                    uploadNota(folderName);
                    if(controllaFoto()) uploadImage(folderName);
                    if(controllaVideo()) uploadVideo(folderName);
                    if(controllaVocali()) uploadMic(folderName);
                    //uploadCSV(folderName);
                }
                */
            }
        });

        final TextView textNumFoto = findViewById(R.id.textNumFoto);
        final TextView textNumVideo = findViewById(R.id.textNumVideo);
        final TextView textNumVocali = findViewById(R.id.textNumVocali);


        invia.setVisibility(View.INVISIBLE);
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
        TextView invia = findViewById(R.id.invia);
        if (controllaNota()){
            invia.setVisibility(View.VISIBLE);
        }else invia.setVisibility(View.INVISIBLE);
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
        File noteFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File noteFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.NOTES);

        File[] list = noteFolderNew.listFiles();
        int count = 0;
        if(list!=null){
            for(File f: list){
                count++;
            }
        }
        return count;

    }

    //Recupero indirizzo automatimaticamente se il dispositivo recupera la posizione
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

    //Creo il CSV contenente latitudine, longitudine, nome del luogo
    private void createCSV(){
        String address, namePlace;
        EditText addressText = (EditText) findViewById(R.id.addressEditText);
        EditText nameText = (EditText) findViewById(R.id.nameEditText);
        address = addressText.getText().toString();
        namePlace = nameText.getText().toString();
        double lat=0, lng=0;
        Bundle bundle = getIntent().getExtras();
        if(getIntent().getExtras()!=null){
            lat = bundle.getDouble("latitude");
            lng = bundle.getDouble("longitude");
        }
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        if (!(folder.exists())){
            folder.mkdirs();
        }
        String nameFile = "Informazioni" + ".csv";
        try{
            String latitudine = Double.toString(lat);
            String longitudine = Double.toString(lng);

            File informationFile = new File (folder, nameFile);
            FileWriter writer = new FileWriter(informationFile);

            writer.append("Address" + "#" + "Place" + "#" + "Latitude" + "#" + "Longitude");
            writer.append('\n');
            writer.append(address + "#" + namePlace + "#" + latitudine + "#" + longitudine);

            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    //UPLOAD//

    //Upload Immagine
    public void uploadImage(String folderName){

        jsonObject = new JSONObject();
        HashMap<String, String> params = new HashMap<String, String>();
        Bundle bundle = getIntent().getExtras();
        if(getIntent().getExtras()!=null){
            lat = bundle.getDouble("latitude");
            lng = bundle.getDouble("longitude");
        }

        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File imageFolderaNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);


        File[] list = imageFolderaNew.listFiles();
            for(File f: list){

                final Bitmap bitmap = BitmapFactory.decodeFile(f.toString());
                params.put("size", Long.toString(f.length()));
                params.put("width", Integer.toString(bitmap.getWidth()));
                params.put("height", Integer.toString(bitmap.getHeight()));
                params.put("format", "jpg");
                params.put("image_data", imgToString(bitmap));
                params.put("latitude",  Double.toString(lat));
                params.put("longitude", Double.toString(lng));


                JsonObjectRequest jsonRequest = new JsonObjectRequest(URL + FileInformation.photos, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    response.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                MySingleton.getInstance(PostActivity.this).addToRequestQue(jsonRequest);

            }
    }

    //Upload Video
    public void uploadVideo(String folderName){

        jsonObject = new JSONObject();
        HashMap<String, String> params = new HashMap<String, String>();
        Bundle bundle = getIntent().getExtras();
        if(getIntent().getExtras()!=null){
            lat = bundle.getDouble("latitude");
            lng = bundle.getDouble("longitude");
        }

        File videoFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File videoFolderaNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.VIDEOS);

        File[] list = videoFolderaNew.listFiles();
        for(File f: list){

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(f.getPath());

            int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int bitrate = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            //int framerate = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE));

            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            retriever.release();

            params.put("size", Long.toString(f.length()));
            params.put("format", "mp4");
            params.put("duration", time);
            params.put("width", Integer.toString(width));
            params.put("height", Integer.toString(height));
            params.put("bit_rate", Integer.toString(bitrate));
            params.put("frame_rate", "30");
            params.put("video_data", vidToString(f));
            params.put("latitude",  Double.toString(lat));
            params.put("longitude", Double.toString(lng));

            JsonObjectRequest jsonRequest = new JsonObjectRequest(URL + FileInformation.videos, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                response.getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MySingleton.getInstance(PostActivity.this).addToRequestQue(jsonRequest);

        }
    }

    //Upload Note vocali
    public void uploadMic(String folderName)  {

        jsonObject = new JSONObject();
        HashMap<String, String> params = new HashMap<String, String>();
        Bundle bundle = getIntent().getExtras();
        if(getIntent().getExtras()!=null){
            lat = bundle.getDouble("latitude");
            lng = bundle.getDouble("longitude");
        }

        File micFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        final File micFolderaNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.AUDIO);

        File[] list = micFolderaNew.listFiles();
        for(File f: list){

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(f.getPath());

            int bitrate = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            retriever.release();

            params.put("size", Long.toString(f.length()));
            params.put("format", "3gp");
            params.put("duration", time);
            params.put("bit_rate", Integer.toString(bitrate));
            params.put("audio_data", micToString(f));
            params.put("latitude",  Double.toString(lat));
            params.put("longitude", Double.toString(lng));


            JsonObjectRequest jsonRequest = new JsonObjectRequest(URL + FileInformation.audios, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                response.getString("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MySingleton.getInstance(PostActivity.this).addToRequestQue(jsonRequest);

        }
    }

    //Upload Nota scritta
    public void uploadNota(String folderName){

        String text = readFile().toString();
        Bundle bundle = getIntent().getExtras();
        if(getIntent().getExtras()!=null){
            lat = bundle.getDouble("latitude");
            lng = bundle.getDouble("longitude");
        }

        jsonObject = new JSONObject();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("content", text);
        params.put("latitude", Double.toString(lat));
        params.put("longitude", Double.toString(lng));


        JsonObjectRequest jsonRequest = new JsonObjectRequest(URL + FileInformation.texts, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    response.getString("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        MySingleton.getInstance(PostActivity.this).addToRequestQue(jsonRequest);

    }

    //Upload CSV
    public void uploadCSV(String folderName){
        jsonObject = new JSONObject();

        File CSV = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "Informazioni.csv");

        if(CSV.exists()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");
                            Toast.makeText(PostActivity.this, Response, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            }
                    }

                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                    });

            MySingleton.getInstance(PostActivity.this).addToRequestQue(stringRequest);

        }else{
            Snackbar.make(getWindow().getDecorView().getRootView(), "Nessuna file multimediale inserito", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    //Converto immagine in Stringa Base64
    private String imgToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }

    //Converto video in Stringa Base64
    private String vidToString(File f){
        byte[] byteBufferString = new byte[8192];
        try {
            FileInputStream v_input = new FileInputStream(f.getPath());
            ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
            for (int readNum; (readNum = v_input.read(byteBufferString)) != -1;)
            {
                objByteArrayOS.write(byteBufferString, 0, readNum);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  Base64.encodeToString(byteBufferString, Base64.DEFAULT);
    }

    //Converto vocale in Stringa Base64
    private String micToString(File f){
        byte[] file = new byte[0];
        try {
            file = FileUtils.readFileToByteArray(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(file, Base64.DEFAULT);
    }

    private String fileToString(File f){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] file = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(file, Base64.DEFAULT);
    }

    //Controllo che ci siano foto da uppare
    private boolean controllaFoto(){
        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File imageFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);

        File[] list = imageFolderNew.listFiles();
        if(list!=null) return true;
        else return false;
    }

    //Controllo che ci siano video da uppare
    private boolean controllaVideo(){
        File videoFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File videoFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.VIDEOS);

        File[] list = videoFolderNew.listFiles();
        if(list!=null) return true;
        else return false;
    }

    //Controllo che ci siano vocali da uppare
    private boolean controllaVocali(){
        File micFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File micFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.AUDIO);

        File[] list = micFolderNew.listFiles();
        if(list!=null) return true;
        else return false;
    }

    //Controllo che ci sia la nota scritta
    private boolean controllaNota(){
        File noteFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File noteFolderNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.NOTES);

        File[] list = noteFolderNew.listFiles();
        if(list!=null) return true;
        else return false;
    }


    //Chiede all'utente se è sicuro di tornare indietro quando sono presenti o foto o video o nota vocale/scritta in quanto i dati andrebbero
    //  persi
    public Dialog onCreateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setMessage(R.string.dialog_message_post)
                .setPositiveButton(R.string.positive_message_post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PostActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.negative_message_post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        if((contaFoto()==0) && (contaMic()==0) && (contaVideo()==0) && (contaNota()==0)) {
            finish();
        }
        else {
            Dialog alert = onCreateDialog();
            alert.show();
        }
    }

    //Leggo contenuto nota scritta -> Metodo di appoggio per inviarla al DB
    private StringBuilder readFile (){
        final String path = Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.NOTES;

        String fileName= "Nota.txt";

        StringBuilder text = new StringBuilder();
        File file = new File(path, fileName);
        int numRighe = contaRighe(file);
        int count=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                count++;
                if(numRighe==1){
                    text.append(line);
                }
                else{
                    if(count == numRighe){
                        text.append(line);
                    }
                    else{
                        text.append(line);
                        text.append('\n');
                    }

                }
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text;
    }

    //conto righe all'interno della nota scritta -> metodo di appoggio per poi inviare la nota al DB
    public int contaRighe(File file){
        int count=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                count++;
            }
            br.close();
        }catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return count;
    }


    //ASYNCTASK QUANDO VENGONO UPPATI I FILE
    private class UploadFile extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressdialog = new ProgressDialog(PostActivity.this);
            progressdialog.setTitle("Upload");
            progressdialog.setMessage("Sto caricando i dati! Attendi...");
            progressdialog.setIndeterminate(true);
            //progressdialog.setMax(100);
            //progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressdialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            createCSV();
            if (controllaNota()) {
                uploadNota(folderName);
                if (controllaFoto()) uploadImage(folderName);
                if (controllaVideo()) uploadVideo(folderName);
                if (controllaVocali()) uploadMic(folderName);
                //uploadCSV(folderName);
                setResult(RESULT_OK);
                finish();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressdialog.setProgress(values[0]);

        }
    }

}