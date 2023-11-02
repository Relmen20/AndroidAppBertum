package com.example.bertumApp2;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bertumApp2.model.Point;
import com.example.bertumApp2.model.Polygon;
import com.example.bertumApp2.model.ViewDrawer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class PolygonLayout extends AppCompatActivity {
    private FrameLayout frameLayout;
    private RelativeLayout relativeLayout;
    private ViewDrawer viewDrawer;
    private ImageView pickedDetail;
    private TextView textView5;
    private SharedPreferences sh;
    private Button buttonBuy;
    private String photo;
    private List<Polygon> polygons;

    @SuppressLint({"MissingInflatedId", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polygons);

        pickedDetail = findViewById(R.id.picked_detail);
        buttonBuy = findViewById(R.id.cartPrice1);
        frameLayout = findViewById(R.id.frame_layout);
        relativeLayout = findViewById(R.id.relativeLayoutSurface);
        textView5 = findViewById(R.id.textView5);


        try {

            // TODO: берем фото сделанное пользователем из предыдущего окна, приложенного методом .putExtra()
            photo = getIntent().getStringExtra(Const.PHOTO_BASE64);

            // TODO: метеодом convertToPolygons() парсим пришедшую с сервера строку из кэша в лист полигонов
            sh = getSharedPreferences(Const.SHARE_STORE, MODE_PRIVATE);
            System.out.println((getSharedValueStr("jsonAiApi1")));
            polygons = convertToPolygons(getSharedValueStr("jsonAiApi1"));

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(PolygonLayout.this, "ERROR", Toast.LENGTH_SHORT).show();
            finish();
        }



        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        //TODO: в конструктор viewDrawer идут (контекст(обязательно), фотка в строке, лист полигонов, повернут ли экран, окно для вывода текста);
        viewDrawer = new ViewDrawer(PolygonLayout.this, photo, polygons, isLandscape, pickedDetail, buttonBuy, textView5);
        relativeLayout.addView(viewDrawer);
//        {
//
//            sh = getSharedPreferences(Const.SHARE_STORE, MODE_PRIVATE);
//            //TODO: временная фотка из ресурсов
//            photo = drawableToBase64(getResources(), R.drawable.polo_from_internet);
//            //TODO: временный блок, чтобы достать временный json из память
//            InputStream is = null;
//            try {
//                is = getAssets().open("photo_from_inet.jon");
//                String json = new Scanner(is).useDelimiter("\\A").next();
//                polygons = convertToPolygons(json);
//            } catch (IOException e) {
//                Toast.makeText(PolygonLayout.this, "Пустой ответ сервера", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
    }

    //TODO: метод для конвертации строки json в лист полигонов
    public static List<Polygon> convertToPolygons(String json) {
        Gson gson = new Gson();
        JsonObject data = gson.fromJson(json, JsonObject.class);

        List<Polygon> polygons = new ArrayList<>();
        JsonArray maskArray = data.getAsJsonArray("mask");

        for(JsonElement jsonObject: maskArray){

            JsonObject maskObject = jsonObject.getAsJsonObject();

            String name = maskObject.get("Part_Name").getAsString();
            String rusName = maskObject.get("Part_Name_rus").getAsString();
            String article = maskObject.get("Article").getAsString();

            JsonArray pointsArray = maskObject.getAsJsonArray("Points");

            List<Point> points = new ArrayList<>();
            for (int j = 0; j < pointsArray.size(); j++) {
                JsonArray pointArray = pointsArray.get(j).getAsJsonArray();
                float x = pointArray.get(0).getAsFloat();
                float y = pointArray.get(1).getAsFloat();
                points.add(new Point(x, y));
            }

            if (name.contains("Large")) {
                Polygon polygon = new Polygon(name, article, points, rusName);
                polygons.add(polygon);
            }
        }

        return polygons;
    }

    private String getSharedValueStr(String name){
        sh = getSharedPreferences(Const.SHARE_STORE, Context.MODE_PRIVATE);
        return sh.getString(name, "");
    }

    //TODO: временный метод для изменения фотрмата фотки в Base64
//    public String drawableToBase64(Resources resources, int drawableResourceId) {
//        try {
//            InputStream inputStream = resources.openRawResource(drawableResourceId);
//            byte[] bytes = new byte[inputStream.available()];
//            inputStream.read(bytes);
//            inputStream.close();
//            return Base64.encodeToString(bytes, Base64.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public static List<Polygon> convertToPolygons(String json) {
//        Gson gson = new Gson();
//        JsonObject obj = gson.fromJson(json, JsonObject.class);
//        JsonArray array = obj.get("mask").getAsJsonArray();
//
//        List<Polygon> polygons = new ArrayList<>();
//        for(int i=0; i<array.size(); i++) {
//            JsonObject polyObj = array.get(i).getAsJsonObject();
//
//            String name = polyObj.get("Part_Name").getAsString();
//            String article = polyObj.get("Article").getAsString();
//
//            JsonArray pointsArray = polyObj.get("Points").getAsJsonArray();
//
//            List<Point> points = new ArrayList<>();
//            for(int j=0; j<pointsArray.size(); j++) {
//                JsonArray pointArray = pointsArray.get(j).getAsJsonArray();
//                float x = pointArray.get(0).getAsFloat();
//                float y = pointArray.get(1).getAsFloat();
//                points.add(new Point(x, y));
//            }
//            if(name.contains("Large")){
//                Polygon polygon = new Polygon(name, article, points);
//                polygons.add(polygon);
//            }
//        }
//        return polygons;
//    }
}
