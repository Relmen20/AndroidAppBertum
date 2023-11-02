package com.example.bertumApp2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ItemsList extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor ed;
    private ImageView search_result_1, search_result_2, search_result_3;
    private TextView cntOrig, cntNonOrig, cntBU, cntDiscont, cntVariants, textView2,
            cntTotalItems, sumRepairs, sumRepairsWork, tagPrice1, tagPrice2, tagPrice3,
            titleDetail1, titleDetail2, titleDetail3, jsonOut;
    private int cnt, low, high, cartSumRepairs, cartSumRepairsWork, cartCntRepairs, cntAllVariants = 0;

    private Button cartPrice1,cartPrice2, cartPrice3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences sharedPreferences = getSharedPreferences(Const.SHARE_STORE, MODE_APPEND);
        SharedPreferences.Editor ed;
        ed = sharedPreferences.edit();
        ed.putInt("cntPhoto", 0);
        ed.commit();

        cntOrig = findViewById(R.id.cntOrig);
        cntNonOrig = findViewById(R.id.cntNonOrig);
        cntBU = findViewById(R.id.cntBU);
        cntDiscont = findViewById(R.id.cntDiscont);
        cntVariants = findViewById(R.id.cntVariants);

        cntTotalItems = findViewById(R.id.cntTotalItems);
        sumRepairs = findViewById(R.id.sumRepairs);
        sumRepairsWork = findViewById(R.id.sumRepairsWork);

        search_result_1 = findViewById(R.id.search_result_1);
        tagPrice1 = findViewById(R.id.tagPrice1);
        cartPrice1 = findViewById(R.id.cartPrice1);
        titleDetail1 = findViewById(R.id.titleDetail1);

        search_result_2 = findViewById(R.id.search_result_2);
        tagPrice2 = findViewById(R.id.tagPrice2);
        cartPrice2 = findViewById(R.id.cartPrice2);
        titleDetail2 = findViewById(R.id.titleDetail2);

        search_result_3 = findViewById(R.id.search_result_3);
        tagPrice3 = findViewById(R.id.tagPrice3);
        cartPrice3 = findViewById(R.id.cartPrice3);
        titleDetail3 = findViewById(R.id.titleDetail3);
        
        jsonOut = findViewById(R.id.jsonOut);

        Random r = new Random();
        low = 1;
        high = 6;

        cnt = r.nextInt(high-low) + low;
        cntAllVariants += cnt;
        cntOrig.setText("ОРИГИНАЛ : " + cnt);

        cnt = r.nextInt(high-low) + low;
        cntAllVariants += cnt;
        cntNonOrig.setText("НЕОРИГИНАЛ : " + cnt);

        cnt = r.nextInt(high-low) + low;
        cntAllVariants += cnt;
        cntBU.setText("Б/У : " + cnt);

        cnt = r.nextInt(high-low) + low;
        cntAllVariants += cnt;
        cntDiscont.setText("УЦЕНКА : " + cnt);

        cntVariants.setText(cntAllVariants + " вариантов");

        low = 2500;
        high = 5500;
        cnt = r.nextInt(high-low) + low;
        tagPrice1.setText(String.valueOf(cnt) );


//        textView2.setText(getSharedValueStr("detail_title") +"["+ getSharedValueStr("detail_article") +"]");
        

        // по клику
        cartPrice1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // достаем сохраненные данные
                Log.d("BERTUM---------------", "OnClick");
                SharedPreferences getSharedPrefs = getSharedPreferences(Const.SHARE_STORE, MODE_APPEND);
                int cartSum = getSharedPrefs.getInt("cartSum", 0); // get saved summ
                int cartCnt = getSharedPrefs.getInt("cartCnt", 0); // get saved cnt

                // цена товара 1
                int price1 = Integer.parseInt(tagPrice1.getText().toString()); // get added price
                cartSumRepairs = cartSum + price1;
                cartCntRepairs = cartCnt + 1;

                sumRepairs.setText(String.valueOf(cartSumRepairs));
                cntTotalItems.setText(String.valueOf(cartCntRepairs));

                SharedPreferences sh = getSharedPreferences(Const.SHARE_STORE,MODE_PRIVATE);
                SharedPreferences.Editor ed;
                ed = sh.edit();

                Random r = new Random();
                low = 6000;
                high = 18500;
                cartSumRepairsWork = r.nextInt(high-low) + low;
                sumRepairsWork.setText(String.valueOf(cartSumRepairsWork) );
                ed.putInt("cartRepairWork", cartSumRepairsWork);

                ed.putInt("cartSum", cartSumRepairs);
                ed.putInt("cartCnt", cartCntRepairs);
                ed.commit();

                Toast.makeText(ItemsList.this, "Деталь добавлна в корзину", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sh = getSharedPreferences(Const.SHARE_STORE, MODE_APPEND);
        int b = sh.getInt("cartSum", 0);
        sumRepairs.setText(String.valueOf(b));
        int c = sh.getInt("cartRepairWork", 0);
        sumRepairsWork.setText(String.valueOf(c));
        int d = sh.getInt("cartCnt", 0);
        cntTotalItems.setText(String.valueOf(d));

        String jsonAiApi = sh.getString("jsonAiApi", "no json");

        jsonOut.setText(jsonAiApi);

        try {
            // get JSONObject from JSON file
//            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject obj = new JSONObject(jsonAiApi);
            // fetch JSONArray named users
//            JSONArray arDetails = obj.getJSONArray("mask");
            JSONObject objDetails = obj.getJSONObject("mask");
            // implement for loop for getting users list data
            for (int i = 0; i < objDetails.length(); i++) {
                // create a JSONObject for fetching single user data
                //JSONObject userDetail = objDetails.getJSONObject(i);

                // fetch email and name and store it in arraylist
//                personNames.add(userDetail.getString("name"));
//                emailIds.add(userDetail.getString("email"));
//                // create a object for getting contact data from JSONObject
//                JSONObject contact = userDetail.getJSONObject("contact");
//                // fetch mobile number and store it in arraylist
//                mobileNumbers.add(contact.getString("mobile"));
            }
            //jsonOut.setText(String.valueOf(objDetails.length()));

            JSONObject objects = new JSONObject (jsonAiApi);
            JSONObject objDetails2 = objects.getJSONObject("mask");
            JSONArray keys = objDetails2.names ();
            for (int i = 0; i < keys.length (); ++i) {
                String key = keys.getString (i);
                //String titleDetail = objDetails2.getString (key); //.getString ("Part_Name");

                JSONObject objDetail = objDetails2.getJSONObject(key);
                String titleDetail = objDetail.getString ("Part_Name");

                if(i==0){
                    search_result_1.setVisibility(View.VISIBLE);
                    titleDetail1.setVisibility(View.VISIBLE);
                    tagPrice1.setVisibility(View.VISIBLE);
                    cartPrice1.setVisibility(View.VISIBLE);
                    titleDetail1.setText(titleDetail);
                } else if (i == 1) {
                    search_result_2.setVisibility(View.VISIBLE);
                    titleDetail2.setVisibility(View.VISIBLE);
                    tagPrice2.setVisibility(View.VISIBLE);
                    cartPrice2.setVisibility(View.VISIBLE);
                    titleDetail2.setText(titleDetail);
                } else if (i == 2) {
                    search_result_3.setVisibility(View.VISIBLE);
                    titleDetail3.setVisibility(View.VISIBLE);
                    tagPrice3.setVisibility(View.VISIBLE);
                    cartPrice3.setVisibility(View.VISIBLE);
                    titleDetail3.setText(titleDetail);
                }
            }

        } catch (JSONException e) {
            Log.d("BERTUM------JSONException", e.getMessage() );
//            e.printStackTrace();
        }
    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(ItemsList.this, MainActivity.class));
//    }


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

}