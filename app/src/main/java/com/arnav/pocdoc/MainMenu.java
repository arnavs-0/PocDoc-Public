package com.arnav.pocdoc;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;


import com.arnav.pocdoc.FoodAllergy.FoodImageInput;
import com.arnav.pocdoc.maps.MapsActivity;


import com.arnav.pocdoc.Authentication.Login;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import es.dmoral.toasty.Toasty;


public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    CardView emergency;
    Button med_diary, reminder, locator, allergy;

    Button go_remind;

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        emergency = findViewById(R.id.emergency_call);
        med_diary = findViewById(R.id.go_diary);
        locator = findViewById(R.id.go_locator);
        allergy = findViewById(R.id.go_symptom);
        go_remind = findViewById(R.id.go_reminder);


        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_home);

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmergencyDialog();
            }
        });

        med_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Diary.class));
            }
        });
        locator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
        allergy.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           startActivity(new Intent(getApplicationContext(), FoodImageInput.class));
                                       }
                                   });
        go_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Reminder.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
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


    public void showEmergencyDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_emg_title)
                .setMessage("Are you sure you want to make a call to emergency services?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO replace with 911
                        String dial = "tel:911";
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(dial));
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

}