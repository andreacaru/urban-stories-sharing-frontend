package com.example.andreacarubelli.urbanstoriessharing;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

public class ModifyVideo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_video);

        ImageView backArrow = (ImageView) findViewById(R.id.freccia_indietro);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
