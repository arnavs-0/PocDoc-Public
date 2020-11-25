package com.arnav.pocdoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;
import com.arnav.pocdoc.Authentication.Login;
import com.arnav.pocdoc.FoodAllergy.FoodImageInput;
import com.arnav.pocdoc.maps.MapsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;

public class Reminder extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static Context context;

    ChipNavigationBar chipNavigationBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    public static void scheduleNotification(String id) {
        FirebaseDatabase.getInstance().getReference().child("reminder").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String dosage = Objects.requireNonNull(snapshot.child("dosage").getValue()).toString();
                String image = Objects.requireNonNull(snapshot.child("imageResource").getValue()).toString();
                int hour = Integer.parseInt(Objects.requireNonNull(snapshot.child("remindHour").getValue()).toString());
                int minute = Integer.parseInt(Objects.requireNonNull(snapshot.child("remindMinute").getValue()).toString());
                String repeat = Objects.requireNonNull(snapshot.child("repeat").getValue()).toString();
                ArrayList<String> days = new ArrayList<>();

                Collections.addAll(days, repeat.substring(1, repeat.length()-1).split(", "));

                Calendar time = Calendar.getInstance();
                String day = time.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

                if (!days.contains(Objects.requireNonNull(day).toUpperCase()) || time.get(Calendar.HOUR_OF_DAY) > hour || (time.get(Calendar.HOUR_OF_DAY) == hour && time.get(Calendar.MINUTE) >= minute)) {
                    do {
                        time.add(Calendar.DAY_OF_YEAR, 1);
                        day = time.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                    }
                    while (!days.contains(Objects.requireNonNull(day).toUpperCase()));
                }

                time.set(Calendar.HOUR_OF_DAY, hour);
                time.set(Calendar.MINUTE, minute);
                time.set(Calendar.SECOND, 0);

                PowerManager power_manager = (PowerManager) context.getSystemService(POWER_SERVICE);
                PowerManager.WakeLock wake_lock = null;
                if (power_manager != null) {
                    wake_lock = power_manager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "PocDoc: reminder");
                    wake_lock.acquire(500);
                }

                new NotifyMe.Builder(context)
                        .title("Medication Reminder")
                        .content("It's time to take "+dosage+" of "+name)
                        .large_icon(context.getResources().getIdentifier(image, "drawable", context.getPackageName()))
                        .addAction(new Intent(context.getApplicationContext(), Reminder.class), "OK")
                        .time(time)
                        .build();

                if (wake_lock != null) {
                    wake_lock.release();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        context = this;

        chipNavigationBar = findViewById(R.id.bottom_reminder_nav);
        drawerLayout = findViewById(R.id.drawer_layout_reminder);
        navigationView = findViewById(R.id.nav_view_reminder);
        toolbar = findViewById(R.id.toolbarReminder);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.nav_remind);
        toggle.syncState();
        chipNavigationBar.setItemEnabled(R.id.bottom_reminder_home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeReminder()).commit();
        bottomMenu();
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            switch (i) {
                case R.id.bottom_reminder_home:
                    fragment = new HomeReminder();
                    break;
                case R.id.bottom_reminder_edit:
                    fragment = new AddReminder();
                    break;
                case R.id.bottom_reminder_help:
                    fragment = new HelpReminder();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        });
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
}