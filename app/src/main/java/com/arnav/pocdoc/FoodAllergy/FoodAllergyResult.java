package com.arnav.pocdoc.FoodAllergy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import es.dmoral.toasty.Toasty;
import io.grpc.Channel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arnav.pocdoc.Authentication.Login;
import com.arnav.pocdoc.Diary;
import com.arnav.pocdoc.MainMenu;
import com.arnav.pocdoc.MedicalId;
import com.arnav.pocdoc.R;
import com.arnav.pocdoc.Reminder;
import com.arnav.pocdoc.maps.MapsActivity;
import com.clarifai.channel.ClarifaiChannel;
import com.clarifai.credentials.ClarifaiCallCredentials;
import com.clarifai.grpc.api.Concept;
import com.clarifai.grpc.api.Data;
import com.clarifai.grpc.api.Image;
import com.clarifai.grpc.api.Input;
import com.clarifai.grpc.api.MultiOutputResponse;
import com.clarifai.grpc.api.PostModelOutputsRequest;
import com.clarifai.grpc.api.V2Grpc;
import com.clarifai.grpc.api.status.StatusCode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoodAllergyResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button results, retry;
    ImageView food_image;
    ProgressBar progress;
    TextView ai_result;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    public static final List<String> list = FoodImageInput.listArray;
    public static final ArrayList<String> percentage = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_allergy_result);
        results = findViewById(R.id.results);
        retry = findViewById(R.id.retry_allergy);
        food_image = findViewById(R.id.food_image_results);
        ai_result = findViewById(R.id.results_food);
        progress = findViewById(R.id.progress_ai);

        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("image");

        drawerLayout = findViewById(R.id.drawer_layout_foodout);
        navigationView = findViewById(R.id.nav_view_foodout);
        toolbar = findViewById(R.id.toolbar_foodout);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        food_image.setImageURI(Uri.parse(imageUri));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FoodImageInput.class);
                startActivity(intent);
                finish();
            }
        });
        results.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ClarifaiPost asyncTask = new ClarifaiPost();
                asyncTask.execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class ClarifaiPost extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                    Toast.makeText(getApplicationContext(), "Cannot Analyze without Permission", Toast.LENGTH_LONG).show();
                    return;
                }

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            String imageUri = getIntent().getExtras().getString("image");
            File imageFile = new File(Objects.requireNonNull(Uri.parse(imageUri).getPath()));
            if (imageUri == null) {
                Toast.makeText(getApplicationContext(), "Not Found", Toast.LENGTH_LONG).show();
                return null;
            }
            Uri image = Uri.parse(imageUri);
            Log.i("URI", image.toString());
            Path pathNew = Paths.get(getRealPathFromUri(getApplicationContext(), image));
            Log.i("URI", pathNew.toString());

            Channel channel = ClarifaiChannel.INSTANCE.getJsonChannel();
            V2Grpc.V2BlockingStub stub = V2Grpc.newBlockingStub(channel)
                    .withCallCredentials(new ClarifaiCallCredentials("YOUR_CLARIFAI_CREDENTIALS"));

            MultiOutputResponse response = null;
            try {
                response = stub.postModelOutputs(
                        PostModelOutputsRequest.newBuilder()
                                .setModelId("YOUR_MODEL_ID")
                                .addInputs(
                                        Input.newBuilder().setData(
                                                Data.newBuilder().setImage(
                                                        Image.newBuilder().setBase64(ByteString.copyFrom(Files.readAllBytes(pathNew)))
                                                )
                                        )
                                )
                                .build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.getStatus().getCode() != StatusCode.SUCCESS) {
                throw new RuntimeException("Request failed, status: " + response.getStatus());
            }

            final ArrayList<String> matches = new ArrayList<>();
            for (Concept c : response.getOutputs(0).getData().getConceptsList()) {

                for (int i = 0; i < response.getOutputsCount(); i++) {

                    matches.add(c.getName().toUpperCase());
                    percentage.add(Math.round(c.getValue() * 100) + "%");
                }

                String s = String.format("%12s: %,.2f", c.getName(), c.getValue());
                Log.i("ANALYSIS", s);
            }
            return matches;
        }

        @Override
        protected void onPostExecute(ArrayList<String> matches) {
            super.onPostExecute(matches);

            if (matches.size() == 0 || matches.isEmpty()){
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            } else {
                int arraySize = matches.size();
                ai_result.setText("");
                for (int i = 0; i < arraySize; i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).toUpperCase().contains(matches.get(i))){
                            ai_result.append("There is a possibility of: " + matches.get(i) + " at a " +  percentage.get(i));
                            ai_result.append("\n");
                        }
                    }
                }
                if (ai_result == null || ai_result.length() == 0) {
                    ai_result.append("There are no visible allergic threats");
                }
                progress.setVisibility(View.GONE);
            }

        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
}