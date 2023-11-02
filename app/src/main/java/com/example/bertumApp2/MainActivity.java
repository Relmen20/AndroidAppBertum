package com.example.bertumApp2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;

import android.util.Base64;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final int GALLERY_REQ_CODE = 1000;
        private final int CAMERA_REQ_CODE = 100;
        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor ed;
    private ImageView clIcoDtp, m3dCarFrontRight, m3d_detail_1, m3d_detail_2;
    private ImageButton startBlockDisbld, startBlockEnbld, bottomPrices, starImageButtonEnable, starImageButtonDisable;
    private TextView cntPhoto, cntTotalItems, sumRepairs, sumRepairsWork, selectFileMedia;
    private Button selectCamera;
    private String msgPhotoAdded, msgDetailSelected;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startActivity(new Intent(MainActivity.this, DetailsListActivity.class));
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // make app fullscreen

        // Bottom block Prices - priceRepair, cntDetailsSelected, priceRepairWork
        bottomPrices = findViewById(R.id.bottomPrices);
        sumRepairs = findViewById(R.id.sumRepairs);
        sumRepairsWork = findViewById(R.id.sumRepairsWork);
        cntTotalItems = findViewById(R.id.cntTotalItems);
        // Top block actions - Media Gallery, Camera
        selectCamera = findViewById(R.id.selectCamera);
        clIcoDtp = findViewById(R.id.ico_dtp2);
        selectFileMedia = findViewById(R.id.selectFileMedia);
        // block 3d model
        m3dCarFrontRight  = findViewById(R.id.m3d_car_front_right);
        m3d_detail_1 = findViewById(R.id.m3d_detail_1);
        m3d_detail_2 = findViewById(R.id.m3d_detail_2);
        // Middle block - post photo to AI API
        startBlockDisbld = findViewById(R.id.btnStartDisbld);
        startBlockEnbld = findViewById(R.id.idBtnPostData2);
        cntPhoto = findViewById(R.id.cntPhoto);
        starImageButtonDisable = findViewById(R.id.starImageButtonDisable);
        starImageButtonEnable = findViewById(R.id.starImageButtonEnable);
        // messages text
        msgPhotoAdded  =  getResources().getString (R.string.msg_photo_added);
        msgDetailSelected  =  getResources().getString(R.string.msg_detail_selected);

        //Request for camera runtime permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
                    }, 100);
        }
        
        selectCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQ_CODE);
            }
        });

        bottomPrices.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, DetailsListActivity.class));
            }
        });
        
        clIcoDtp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setSharedValueInt(Const.CART_SUM, 0);
                setSharedValueInt(Const.CART_CNT, 0);
                setSharedValueInt(Const.CART_REPAIR_WORK, 0);
                setSharedValueInt(Const.CNT_PHOTO, 0);
                sumRepairs.setText(String.valueOf( getSharedValueInt(Const.CART_SUM) ));
                sumRepairsWork.setText(String.valueOf( getSharedValueInt(Const.CART_REPAIR_WORK) ));
                cntTotalItems.setText(String.valueOf( getSharedValueInt(Const.CART_CNT) ));
                cntPhoto.setText(String.valueOf( getSharedValueInt(Const.CNT_PHOTO) ));
                checkAiApiPostBlock();
            }
        });

        selectFileMedia.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });
        m3dCarFrontRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, Model3d.class));
            }
        });

        startBlockEnbld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AiWorking.class)
                        .putExtra(Const.PHOTO_BASE64, sharedPreferences.getString(Const.PHOTO_BASE64, "")));
            }
        });
        m3d_detail_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, String.format(msgDetailSelected, "Правая передняя дверь"), Toast.LENGTH_SHORT).show();
            }
        });
        m3d_detail_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, String.format(msgDetailSelected, "Стекло лобовое"), Toast.LENGTH_SHORT).show();
            }
        });

        starImageButtonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sh = getSharedPreferences(Const.SHARE_STORE, MODE_PRIVATE);
                startActivity(new Intent(MainActivity.this, PolygonLayout.class)
                        .putExtra(Const.PHOTO_BASE64, sharedPreferences.getString(Const.PHOTO_BASE64, "")));
            }
        });
//        bottomPrices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, ItemsList.class));
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data ){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == CAMERA_REQ_CODE || requestCode == GALLERY_REQ_CODE){
                if(requestCode == CAMERA_REQ_CODE){
                    saveFromBitmap(data);
                }else{
                    saveFromUri(data);
                }
                manageCntPhoto();
            }
        }
    }

    private void manageCntPhoto(){
        increaseCntPhoto();
        enableAiApiPostBlock();
        Toast.makeText(MainActivity.this, msgPhotoAdded, Toast.LENGTH_SHORT).show();
    }

    private void saveFromBitmap(Intent data){

        String photoBase64 = getBase64FromBitmap(data);
        setSharedValueStr(Const.PHOTO_BASE64, photoBase64);
    }

    private void saveFromUri(Intent data){
        String photoBase64 = getBase64FromUri(data);
        setSharedValueStr(Const.PHOTO_BASE64, photoBase64);
    }

    @Override
    public void onResume() {
        super.onResume();
        cntPhoto.setText(String.valueOf( getSharedValueInt(Const.CNT_PHOTO) ));
        sumRepairs.setText(String.valueOf( getSharedValueInt(Const.CART_SUM) ));
        cntTotalItems.setText(String.valueOf( getSharedValueInt(Const.CART_CNT) ));
        sumRepairsWork.setText(String.valueOf( getSharedValueInt(Const.CART_REPAIR_WORK) ));
    }

    private String getBase64FromUri(Intent data){
        Uri imageUri = data.getData();
        String strBase64;
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        strBase64 = convertBitmapToBase64(bitmap);
        return strBase64;
    }

    private String getBase64FromBitmap(Intent data){
        String strBase64;
        Bitmap bitmap;
        try {
            bitmap = (Bitmap) data.getExtras().get("data");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        strBase64 = convertBitmapToBase64(bitmap);
        return strBase64;
    }

    private String convertBitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baoStream);
        byte[] b = baoStream.toByteArray();
        String strBase64 = Base64.encodeToString(b, Base64.DEFAULT);
        return strBase64;
    }

    private void setSharedValueInt(String name, int value){
        ed = getSharedPreferencesEditor();
        ed.putInt(name, value);
        ed.commit();
    }

    private void setSharedValueStr(String name, String value){
        ed = getSharedPreferencesEditor();
        ed.putString(name, value);
        ed.commit();
    }

    private SharedPreferences.Editor getSharedPreferencesEditor(){
        sharedPreferences = getSharedPreferences(Const.SHARE_STORE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return editor;
    }

    private int getSharedValueInt(String name){
        sharedPreferences = getSharedPreferences(Const.SHARE_STORE,MODE_APPEND);
        return sharedPreferences.getInt(name, 0);
    }
    private String getSharedValueStr(String name){
        sharedPreferences = getSharedPreferences(Const.SHARE_STORE,MODE_APPEND);
        return sharedPreferences.getString(name, "");
    }

    private void increaseCntPhoto(){
        setSharedValueInt( Const.CNT_PHOTO, getSharedValueInt(Const.CNT_PHOTO) + 1 );
//        cntPhoto = findViewById(R.id.cntPhoto);
        cntPhoto.setText(String.valueOf( getSharedValueInt(Const.CNT_PHOTO) ));
    }

    private void checkAiApiPostBlock(){
        if(getSharedValueInt(Const.CNT_PHOTO) > 0){
            enableAiApiPostBlock();
        }else{
            disableAiApiPostBlock();
        }
    }

    private void enableAiApiPostBlock(){
        starImageButtonEnable.setVisibility(View.VISIBLE);
        startBlockEnbld.setVisibility(View.VISIBLE);
        cntPhoto.setVisibility(View.VISIBLE);
    }

    private void disableAiApiPostBlock(){
        starImageButtonEnable.setVisibility(View.GONE);
        startBlockEnbld.setVisibility(View.GONE);
        cntPhoto.setVisibility(View.GONE);
    }
}
