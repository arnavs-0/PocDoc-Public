package com.arnav.pocdoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;

import com.arnav.pocdoc.Authentication.Login;
import com.arnav.pocdoc.FoodAllergy.FoodImageInput;
import com.arnav.pocdoc.maps.MapsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class Diary extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView DiaryRecyclerView;
    @SuppressLint("StaticFieldLeak")
    EditText search;
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

   private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        DiaryRecyclerView = findViewById(R.id.diary_rw);
        search = findViewById(R.id.search_diary_recycle);

        bottomNavigationView = findViewById(R.id.bottom_navigation_diary);
        bottomNavigationView.setSelectedItemId(R.id.home_diary_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        drawerLayout = findViewById(R.id.drawer_layout_diary);
        navigationView = findViewById(R.id.nav_view_diary);
        toolbar = findViewById(R.id.toolbar_diary);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_diary);

        DiaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DiaryRecyclerView.setHasFixedSize(true);
        fetch();

    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.home_diary_nav:
                return true;
            case R.id.add_diary_nav:
                Intent intent = new Intent(getApplicationContext(), AddDiary.class);
                startActivity(intent);
                return true;
        }
        return false;
    };

    private void fetch(){
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("diary").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        FirebaseRecyclerOptions<DiaryItem> options =
                new FirebaseRecyclerOptions.Builder<DiaryItem>()
                        .setQuery(query, snapshot -> new DiaryItem(
                                Objects.requireNonNull(snapshot.child("title").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("content").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("date").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("color").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("symptoms").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("diagnosis").getValue()).toString()))
                        .build();


        adapter = new FirebaseRecyclerAdapter<DiaryItem, DiaryViewHolder>(options) {
            @NonNull
            @Override
            public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layout;
                layout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_card, parent, false);
                return new DiaryViewHolder(layout);
            }

            @Override
            public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position, DiaryItem diaryItem) {
                holder.status.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_transition_animation));
                holder.container.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_scale_animation));

                holder.title.setText(diaryItem.getTitle());
                holder.content.setText(diaryItem.getContent());
                holder.date.setText(diaryItem.getDate());
                holder.status.setImageResource(getResources().getIdentifier(diaryItem.alertLevel , "drawable", getPackageName()));

                String symptoms = diaryItem.getSymptoms();
                symptoms = symptoms.substring(1, symptoms.length()-1);

                StringBuilder details = new StringBuilder();
                details.append("Date: ").append(diaryItem.getDate())
                        .append("\n\nTitle: ").append(diaryItem.getTitle())
                        .append("\n\nNotes: ").append(diaryItem.getContent())
                        .append("\n\nExperienced Symptoms: ").append(symptoms)
                        .append("\n\nDiagnosis: ").append(diaryItem.getDiagnosis());

                holder.itemView.setOnClickListener(view ->  new MaterialAlertDialogBuilder(Diary.this)
                        .setTitle("Details")
                        .setMessage(details)
                        .setIcon(getResources().getIdentifier(diaryItem.alertLevel , "drawable", getPackageName()))
                        .setPositiveButton("OK", null)
                        .show());
            }
        };
        DiaryRecyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    public static class DiaryViewHolder extends RecyclerView.ViewHolder{
        TextView title, content, date;
        ImageView status;
        RelativeLayout container;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_desc);
            date = itemView.findViewById(R.id.tv_date);
            status = itemView.findViewById(R.id.img_status_card);
            container = itemView.findViewById(R.id.diary_content_container);
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