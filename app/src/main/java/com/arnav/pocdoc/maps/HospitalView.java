package com.arnav.pocdoc.maps;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arnav.pocdoc.Authentication.Login;
import com.arnav.pocdoc.Diary;
import com.arnav.pocdoc.FoodAllergy.FoodImageInput;
import com.arnav.pocdoc.MainMenu;
import com.arnav.pocdoc.MedicalId;
import com.arnav.pocdoc.R;
import com.arnav.pocdoc.Reminder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HospitalView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private HospitalDetails obj;
    private String location;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_view);

        location = getIntent().getStringExtra("loc");
        obj = (HospitalDetails)getIntent().getSerializableExtra("obj");
        TextView hospitalname =findViewById(R.id.hospitalname);
        TextView address = findViewById(R.id.address);
        TextView rating = findViewById(R.id.rating);
        TextView openinghrs = findViewById(R.id.openinghrs);
        Button directionbutton = findViewById(R.id.directions);

        drawerLayout = findViewById(R.id.drawer_layout_hospital_details);
        navigationView = findViewById(R.id.nav_view_hospital_details);
        toolbar = findViewById(R.id.toolbar_hospital_details);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        hospitalname.setText(obj.getHospitalName());
        address.setText(obj.getAddress());
        rating.setText(obj.getRating());
        openinghrs.setText(obj.getOpeningHours());

        directionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String direction="";

                direction= "https://www.google.com/maps/dir/?api=1" +
                        "&origin=" + location +
                        "&destination=" + obj.getLocationlatlng()[0] + "," + obj.getLocationlatlng()[1];

                Uri uri = Uri.parse(direction);

                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);

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
}