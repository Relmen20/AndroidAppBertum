package com.example.bertumApp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsListActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor ed;
    private LinearLayout ll_parent;
    private TextView responseTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_list);

        responseTV = findViewById(R.id.idTVResponse);
        // TEST get JSON AI API
        postDataUsingVolley("123456789", "fasdfasdf");
        String jsonAiApi = null;
        jsonAiApi = getSharedValueStr("jsonAiApi");

        try {
            // get JSONObject from JSON file
            String titleDetail, part_name, article;
            JSONObject objects = new JSONObject (jsonAiApi);
            JSONArray mask  = objects.getJSONArray("mask");
            for (int i = 0; i < mask.length(); i++)
            {
                JSONObject record = mask.getJSONObject(i);
                part_name = record.getString("Part_Name");
                article = record.getString("Article");
                generateDetailBlock(part_name, article);
            }

            JSONObject objDetails2 = objects.getJSONObject("settings");
            JSONArray keys = objDetails2.names ();
            for (int i = 0; i < keys.length(); ++i) {
                String key = keys.getString (i);
                JSONObject objDetail = objDetails2.getJSONObject(key);
                titleDetail = objDetail.getString ("view");
                if(i==0){
                    responseTV.setText(titleDetail);
                }
            }
        } catch (JSONException e) {
            Log.d("BERTUM------JSONException", e.getMessage() );
//            e.printStackTrace();
        }

    }

    private void generateDetailBlock(String title, String detail_article){
        // Dynamic ids for controls
        int layoutRelativeId = ViewCompat.generateViewId();
        int imageViewId = ViewCompat.generateViewId();
        int textTitleId = ViewCompat.generateViewId();
        int textPriceId = ViewCompat.generateViewId();
        int btnPricesId = ViewCompat.generateViewId();
        LinearLayout ll_parent = findViewById(R.id.ll_details);
        // Create detail Relative layout
        RelativeLayout ll_new_block = new RelativeLayout(this);
        RelativeLayout.LayoutParams elemParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        elemParams.setMargins( 0,  convertDpToPixels(this, 20),  0,  0);
        ll_new_block.setLayoutParams(elemParams);
        ll_new_block.setId(layoutRelativeId);
        ll_new_block.setGravity(Gravity.CENTER_HORIZONTAL);
        ll_new_block.addView(genImageView(imageViewId ));
        // Create TextView Detail Title
        title += " ["+detail_article+"]";
        ll_new_block.addView(genTextView(textTitleId, title, imageViewId));
        // Create TextView Price
        // ll_new_block.addView(genTextView(textPriceId, " 20023"));
        // Create Button
        ll_new_block.addView(genButton(btnPricesId, "Цены", imageViewId , detail_article));

        ll_parent.addView(ll_new_block);

    }

    private TextView genTextView(int elem_id, String title, int parent_id) {
        TextView elem = new TextView(this);
//        elem.setLayoutParams(
//            new LayoutParams(
//                LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT
//            )
//        );
        elem.setId(elem_id);
        elem.setText(title);
//        elem.setBackgroundColor(0xff66ff66);
        elem.setPadding(convertDpToPixels(this, 10), 0, convertDpToPixels(this, 10), 0);// in pixels (left, top, right, bottom)
        RelativeLayout.LayoutParams elemParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)elem.getLayoutParams();
//        elemParams.addRule(RelativeLayout.ALIGN_START, parent_id );
        elemParams.addRule(RelativeLayout.ALIGN_TOP, parent_id );
        elemParams.setMargins( convertDpToPixels(this, 0),  convertDpToPixels(this, 150),  0,  0);
        elem.setLayoutParams(elemParams);

        return elem;
    }

    private ImageView genImageView(int elem_id){
        ImageView elem = new ImageView(this);
        elem.setLayoutParams(
            new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        );
        elem.setImageResource(R.drawable.search_result_2);
        elem.setId(elem_id);
        return elem;
    }

    private Button genButton(int elem_id, String title, int parent_id, String detail_article){
        Button elem = new Button(this);
        elem.setLayoutParams(
            new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            )
        );
        elem.setId(elem_id);
        elem.setText(title);
        RelativeLayout.LayoutParams elemParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)elem.getLayoutParams();
        elemParams.addRule(RelativeLayout.ALIGN_START, parent_id );
        elemParams.setMargins( convertDpToPixels(this, 215),  convertDpToPixels(this, 277),  0,  0);

        final String da = detail_article;
        final String ta = title;

        elem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.save detail id
                setSharedValueStr("detail_article", da );
                setSharedValueStr("detail_title", ta );
                // 2.redirect to page-Prices
//                startActivity(new Intent(DetailsListActivity.this, DetailPricesActivity.class));
                startActivity(new Intent(DetailsListActivity.this, ItemsList.class));
                //Toast.makeText(DetailsListActivity.this, da, Toast.LENGTH_SHORT).show();
            }
        });

        elem.setLayoutParams(elemParams);
        return elem;
    }

    private void postDataUsingVolley(final String photoId, final String photoBase64) {
        String url = "http://h304809427.nichost.ru/api/get_segments_test.php";
        RequestQueue queue = Volley.newRequestQueue(DetailsListActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseTV.setText("Response from the API is :" + response); // comment for a while
                setSharedValueStr("jsonAiApi", response);
//                startActivity(new Intent(DetailsListActivity.this, ItemsList.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // handling error on below line.
                Toast.makeText(DetailsListActivity.this, "Fail to get response..", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("rmPoints", "0");
                params.put("photoBase64", photoBase64);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);

        Toast.makeText(DetailsListActivity.this, "Фотография успешно отправлена", Toast.LENGTH_SHORT).show();

        // post the data.
        queue.add(request);
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

    private int convertDpToPixels(Context mContext, int valueDp) {
        Resources r = mContext.getResources();
        int valuePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                valueDp,
                r.getDisplayMetrics()
        );
        return valuePx;
    }

}