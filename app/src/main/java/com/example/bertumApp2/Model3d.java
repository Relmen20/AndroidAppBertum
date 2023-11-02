package com.example.bertumApp2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Model3d extends AppCompatActivity {

    private ImageView imgGallery;
    private TextView textBase64;
    private Button btnGallery;
    private final int GALLERY_REQ_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model3d);
        imgGallery = findViewById(R.id.imgGallery);
        btnGallery = findViewById(R.id.btnGallery);
        textBase64 = findViewById(R.id.textBase64);

        btnGallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data ){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                imgGallery.setImageURI(data.getData());
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoStream);
                    byte[] b = baoStream.toByteArray();
                    String photoBase64 = Base64.encodeToString(b, Base64.DEFAULT);
                    textBase64.setText(photoBase64);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}