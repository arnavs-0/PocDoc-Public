package com.arnav.pocdoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.arnav.pocdoc.Authentication.Login;
import com.arnav.pocdoc.FoodAllergy.FoodImageInput;
import com.arnav.pocdoc.maps.MapsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import es.dmoral.toasty.Toasty;
import okhttp3.*;



public class AddDiary extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;


    OkHttpClient client = new OkHttpClient();
    Request request;

    ArrayList<Symptom> symptoms = MainActivity.getSymptoms();
    ArrayList<Symptom> added_symptoms = new ArrayList<>();
    List<KeyPairBoolData> symptoms_list;

    LinearLayout form_layout;
    TextInputEditText title;
    TextInputEditText body;
    Slider pain_slider;
    MultiSpinnerSearch symptom_spinner;
    Button add_button;
    BottomNavigationView bottomNavigationView;

    SimpleDateFormat date_formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    private Map<String, Object> map;
    private DatabaseReference mDatabase;

    private void addFirebase() {
        mDatabase.setValue(map)
                .addOnCompleteListener(task -> {
                    Toast.makeText(AddDiary.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Diary.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddDiary.this, "Failed", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        form_layout = findViewById(R.id.diary_form);
        title = findViewById(R.id.add_diary_title);
        body = findViewById(R.id.add_diary_details);
        pain_slider = findViewById(R.id.diary_pain_level);
        symptom_spinner = findViewById(R.id.diary_symptom_spinner);
        add_button = findViewById(R.id.diary_add_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation_diary_add);


        drawerLayout = findViewById(R.id.drawerLayoutAddDiary);
        navigationView = findViewById(R.id.nav_view_adddiary);
        toolbar = findViewById(R.id.toolbar_adddiary);


        bottomNavigationView.setSelectedItemId(R.id.add_diary_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        symptoms_list = new ArrayList<>();
        for (int i=0; i < symptoms.size(); i++) {
            KeyPairBoolData key_pair = new KeyPairBoolData();
            key_pair.setId(i+1);
            key_pair.setName(symptoms.get(i).name);
            key_pair.setSelected(false);
            symptoms_list.add(key_pair);
        }

        pain_slider.addOnChangeListener((slider, value, fromUser) -> {
            if (slider.getValue() <= 3) {
                slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor(getString(R.color.diary_green))));
                slider.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(getString(R.color.diary_green))));
            }
            else if (slider.getValue() <= 7) {
                slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor(getString(R.color.diary_yellow))));
                slider.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(getString(R.color.diary_yellow))));
            }
            else {
                slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor(getString(R.color.diary_red))));
                slider.setTrackActiveTintList(ColorStateList.valueOf(Color.parseColor(getString(R.color.diary_red))));
            }
        });

        symptom_spinner.setSearchEnabled(true);
        symptom_spinner.setSearchHint("Symptom Name");
        symptom_spinner.setEmptyTitle("");
        symptom_spinner.setItems(symptoms_list, selectedItems -> {
            added_symptoms = new ArrayList<>();
            for (KeyPairBoolData pair : selectedItems) {
                for (Symptom symptom : symptoms) {
                    if (pair.getName().equals(symptom.getName())) {
                        added_symptoms.add(symptom);
                        break;
                    }
                }
            }
        });

        add_button.setOnClickListener(view -> {
            if (!Objects.requireNonNull(title.getText()).toString().isEmpty()) {
                String color;
                if (pain_slider.getValue() <= 3) {
                    color = "@drawable/green";
                } else if (pain_slider.getValue() <= 7) {
                    color = "@drawable/yellow";
                } else {
                    color = "@drawable/red";
                }
                mDatabase = FirebaseDatabase.getInstance().getReference().child("diary").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).push();
                ArrayList<String> stringList = new ArrayList<>();
                for (Symptom added_symptom : added_symptoms) {
                    stringList.add(added_symptom.name);
                }
                ArrayList<String> ids = new ArrayList<>();
                for (Symptom added_symptom : added_symptoms) {
                    ids.add(added_symptom.id);
                }

                String notes = Objects.requireNonNull(body.getText()).toString();
                String empty = getString(R.string.empty_text);

                map = new HashMap<>();
                map.put("date", date_formatter.format(new Date()));
                map.put("title", title.getText().toString());
                map.put("content", !notes.isEmpty() ? notes : empty);
                map.put("color", color);
                map.put("symptoms", stringList.size() > 0 ? stringList : '['+empty+']');

                Log.d("names", stringList.toString());

                JSONObject request_body = new JSONObject();
                try {
                    // TODO get gender % age from medical_id in Firebase and put it in request_body (the tags are "gender" and "age")
                    request_body.put("gender", "male");
                    request_body.put("age", "20");
                    request_body.put("symptoms", new JSONArray(ids));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                request = new Request.Builder()
                        .url(getString(R.string.base_api_url)+"/diagnosis")
                        .post(RequestBody.create(request_body.toString(), MediaType.parse("application/json")))
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        AddDiary.this.runOnUiThread(() -> {
                            try {
                                String tag = "diagnosis";
                                StringBuilder diagnosis = new StringBuilder();
                                JSONArray response_json = new JSONObject(response.body().string()).optJSONArray("conditions");
                                for (int i = 0; i < Objects.requireNonNull(response_json).length(); i++) {
                                    JSONObject condition = response_json.optJSONObject(i);
                                    String name = condition.optString("name");
                                    double probability = condition.optDouble("probability");
                                    if (!diagnosis.toString().isEmpty()) {
                                        diagnosis.append(", ");
                                    }
                                    diagnosis.append(String.format("%s (%.2f%%)", name, probability));
                                }
                                if (response_json.length() > 0) {
                                    map.put(tag, diagnosis.toString());

                                    AlertDialog diagnosis_dialog = new AlertDialog.Builder(AddDiary.this)
                                            .setTitle("Diagnosis")
                                            .setView(R.layout.diagnosis_dialog)
                                            .setPositiveButton("Close", (dialogInterface, i) -> addFirebase())
                                            .show();

                                    TextView diagnosis_text = diagnosis_dialog.findViewById(R.id.diagnosis);
                                    diagnosis_text.setText(getString(R.string.bullet_point)+diagnosis.toString().replace(", ", "\n\n"+getString(R.string.bullet_point)));
                                }
                                else {
                                    map.put(tag, getString(R.string.empty_text));
                                    addFirebase();
                                }
                            }
                            catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
            else if (title.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                break;
            case R.id.nav_signout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                Toasty.success(getApplicationContext(), "Logged Out!", Toast.LENGTH_LONG, true).show();
                //Toast.makeText(getApplicationContext(), "Signed Out",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_diary:
                Intent diary = new Intent(getApplicationContext(), Diary.class);
                startActivity(diary);
                break;
            case R.id.nav_hospital:
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                break;
            case R.id.nav_symptom:
                startActivity(new Intent(getApplicationContext(), FoodImageInput.class));
                break;
            case R.id.nav_remind:
                startActivity(new Intent(getApplicationContext(), Reminder.class));
                break;
            case R.id.nav_id:
                startActivity(new Intent(getApplicationContext(), MedicalId.class));
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download PocDoc Today!");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.home_diary_nav:
                Intent intent = new Intent(getApplicationContext(), Diary.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.add_diary_nav:
                return true;
        }
        return false;
    };
}