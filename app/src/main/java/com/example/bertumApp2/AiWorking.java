package com.example.bertumApp2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.os.AsyncTask;

public class AiWorking extends AppCompatActivity {

    private SharedPreferences sh;
    private SharedPreferences.Editor ed;

    private String  photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_working);

        this.photo = getIntent().getStringExtra(Const.PHOTO_BASE64);

        try {
            String response = new NetworkTask().execute(this.photo).get();
            if (response != null) {
                setSharedValueStr("jsonAiApi1", response);
                Toast.makeText(AiWorking.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AiWorking.this, "response == null", Toast.LENGTH_SHORT).show();
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        finish();
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String photo = params[0];

            byte[] binaryData = Base64.decode(photo, Base64.DEFAULT);

            MediaType MEDIA_TYPE = MediaType.parse("image/jpeg");
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = RequestBody.create(MEDIA_TYPE, binaryData);

            MultipartBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "page_2_5.jpg", requestBody)
                    .build();

            Request request = new Request.Builder()
                    .url("http://194.67.104.42:8000/files")
                    .post(multipartBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    return null;
                }

                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void setSharedValueStr(String name, String value) {
        ed = getSharedPreferencesEditor();
        ed.putString(name, value);
        ed.commit();
    }

    private SharedPreferences.Editor getSharedPreferencesEditor() {
        sh = getSharedPreferences(Const.SHARE_STORE, MODE_PRIVATE);
        return sh.edit();
    }
}
