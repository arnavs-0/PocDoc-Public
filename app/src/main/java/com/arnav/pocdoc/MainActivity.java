package com.arnav.pocdoc;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.arnav.pocdoc.Authentication.Login;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;


public class MainActivity extends AppCompatActivity {
ImageView logo, bg, title;
LottieAnimationView lottieAnimationView;
  
Context context;

OkHttpClient client = new OkHttpClient();
Request request;
static final ArrayList<Symptom> symptoms = new ArrayList<>();
public static final List<KeyPairBoolData> foods = new ArrayList<>();
public static ArrayList<Symptom> getSymptoms() {
   return symptoms;
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        request = new Request.Builder()
                .url(getString(R.string.base_api_url)+"/symptoms")
                .get()
                .build();

        context = this;


        try {
            JSONArray jArray = new JSONArray(loadJSONFromAsset());

            Log.e("MAINACTIVITY", "JSON ARRAY CREATED");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo_inside = jArray.getJSONObject(i);
                KeyPairBoolData obj = new KeyPairBoolData();
                obj.setName(jo_inside.getString("foods").toUpperCase());
                obj.setId(i + 1);
                obj.setSelected(false);
                foods.add(obj);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray response_json = new JSONObject(response.body().string()).optJSONArray("symptoms");
                    for (int i=0; i < response_json.length(); i++) {
                        JSONObject symptom = response_json.getJSONObject(i);
                        String id = symptom.optString("id");
                        String name = symptom.optString("name");
                        symptoms.add(new Symptom(id, name));
                    }
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

            }
        });
        logo = findViewById(R.id.logo_splash);
        bg = findViewById(R.id.bg_splash);
        title = findViewById(R.id.title_splash);
        lottieAnimationView = findViewById(R.id.animation_view);

        bg.animate().translationY(-4000).setDuration(1500).setStartDelay(4000);
        logo.animate().translationY(3000).setDuration(1500).setStartDelay(4000);
        title.animate().translationY(3000).setDuration(1500).setStartDelay(4000);
        lottieAnimationView.animate().translationY(3000).setDuration(1500).setStartDelay(4000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                MainActivity.this.runOnUiThread(() -> startActivity(new Intent(MainActivity.this, Login.class)));

            }
        }, 6000);
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("foods.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
