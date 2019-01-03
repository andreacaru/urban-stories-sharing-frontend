package com.example.andreacarubelli.urbanstoriessharing;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Send extends Activity{

    private JSONObject jsonObject;
    //ArrayList<Uri> imagesUriList;
    ArrayList<String> encodedImageList;
    String URL = "";

    public void uploadImage(Context context, String folderName){

        jsonObject = new JSONObject();
        encodedImageList = new ArrayList<>();
        //RequestQueue requestQueue = Volley.newRequestQueue(Send.this);

        File imageFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName);
        File imageFolderaNew = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                FileInformation.ROOT_FOLDER + "/" + FileInformation.NOTES_FOLDER + "/" + folderName + "/" + FileInformation.PICTURES);

        File[] list = imageFolderaNew.listFiles();
        if(list!=null){
            for(File f: list){
                final Bitmap bitmap = BitmapFactory.decodeFile(imageFolderaNew.toString());
                final String img = imgToString(bitmap);


                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("Myresponse",""+response);
                        Toast.makeText(Send.this, ""+response, Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Mysmart",""+error);
                        Toast.makeText(Send.this, ""+error, Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> param = new HashMap<>();

                        String images = imgToString(bitmap);
                        Log.i("Mynewsam",""+images);
                        param.put("image",images);
                        return param;
                    }
                };

                //requestQueue.add(stringRequest);

            }
        }else{
            Snackbar.make(getWindow().getDecorView().getRootView(), "Nessuna immagine inserita", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private String imgToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imgBytes, Base64.DEFAULT);

    }

}
