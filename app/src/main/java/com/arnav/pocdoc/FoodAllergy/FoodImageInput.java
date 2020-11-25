package com.arnav.pocdoc.FoodAllergy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;

import com.arnav.pocdoc.Authentication.Login;
import com.arnav.pocdoc.Diary;
import com.arnav.pocdoc.MainActivity;
import com.arnav.pocdoc.MainMenu;
import com.arnav.pocdoc.MedicalId;
import com.arnav.pocdoc.R;
import com.arnav.pocdoc.Reminder;
import com.arnav.pocdoc.maps.MapsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FoodImageInput extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    ImageView imageView;
    Button upload, next;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    public static final List<String> listArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_image_input);
        MultiSpinnerSearch searchSpinner = findViewById(R.id.allergies_list_spinner);
        imageView = findViewById(R.id.food_image);
        upload = findViewById(R.id.buttonLoadPicture);
        next = findViewById(R.id.next_image_allergy);

        drawerLayout = findViewById(R.id.drawer_layout_foodin);
        navigationView = findViewById(R.id.nav_view_foodin);
        toolbar = findViewById(R.id.toolbar_foodin);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_symptom);


        final List<KeyPairBoolData> list = MainActivity.foods;


        searchSpinner.setItems(list, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for(int i=0; i<items.size(); i++) {
                    if(items.get(i).isSelected()) {
                        Log.i("TAG", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        listArray.add(items.get(i).getName());
                    }
                }
            }
        });
        searchSpinner.setColorSeparation(true);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnImage();
            }
        });
    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void returnImage() {
        if (imageUri == null) {
            Toast.makeText(getApplicationContext(), "Please Choose a Valid Image", Toast.LENGTH_LONG).show();
        } else{
            Intent intent = new Intent(getApplicationContext(), FoodAllergyResult.class);
            String image = imageUri.toString();
            intent.putExtra("image", image);
            startActivity(intent);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            Log.i("URI", imageUri.toString());
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
