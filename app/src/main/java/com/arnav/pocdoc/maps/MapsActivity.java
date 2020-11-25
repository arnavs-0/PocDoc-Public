package com.arnav.pocdoc.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import es.dmoral.toasty.Toasty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.arnav.pocdoc.Authentication.Login;
import com.arnav.pocdoc.Diary;
import com.arnav.pocdoc.FoodAllergy.FoodImageInput;
import com.arnav.pocdoc.MainMenu;
import com.arnav.pocdoc.MedicalId;
import com.arnav.pocdoc.R;
import com.arnav.pocdoc.Reminder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private Location location;
    private MarkerOptions currmarker;
    private ArrayList<HospitalDetails> results;
    private Button searchbutton;
    private ArrayList<MarkerOptions> m;
    private NiceSpinner sp;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchbutton = (Button)findViewById(R.id.search);
        drawerLayout = findViewById(R.id.drawer_layout_maps);
        navigationView = findViewById(R.id.nav_view_maps);
        toolbar = findViewById(R.id.toolbar_hospital_map);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_hospital);

        sp = (NiceSpinner) findViewById(R.id.category);
        String[] options = {"Hospital","Pharmacy"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapsActivity.this,android.R.layout.simple_spinner_item,options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkingPermissions();

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            location = mMap.getMyLocation();
                            mMap.clear();

                            if(location==null)
                                return;
                            currmarker = new MarkerOptions();
                            LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
                            currmarker.position(latlng);
                            currmarker.title("My Location");
                            currmarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            currmarker.snippet("current location");
                            mMap.addMarker(currmarker);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,15.0f));

                            results = ApiQuery.ping(location,sp.getSelectedItem().toString().toLowerCase());

                            if(results==null)
                            {
                                Toast.makeText(getApplicationContext(),"Result null",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(results.size()==0)
                                Toast.makeText(getApplicationContext(),"Please try again in few moments",Toast.LENGTH_SHORT).show();

                            m=new ArrayList();

                            for(int i=0;i<results.size();i++)
                            {
                                m.add(new MarkerOptions().position(new LatLng(results.get(i).getLocationlatlng()[0],results.get(i).getLocationlatlng()[1])));
                                m.get(i).snippet(i+"");
                            }

                            if(m==null)
                                return;

                            for(int i=0;i<m.size();i++)
                                mMap.addMarker(m.get(i));

                        }
                    });
                }
                catch(Exception e)
                {
                    Log.d("searchbutton",e.toString());
                }

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getSnippet().equals("current location"))
                {
                    Toast.makeText(getApplicationContext(),"My Location",Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(getApplicationContext(),HospitalView.class);
                intent.putExtra("obj",results.get(Integer.parseInt(marker.getSnippet())));
                Location l = mMap.getMyLocation();
                StringBuilder sb = new StringBuilder();
                sb.append(l.getLatitude()+","+l.getLongitude());
                String loc = sb.toString();
                intent.putExtra("loc",loc);
                startActivity(intent);
                return true;
            }
        });
    }
    private boolean checkingPermissions()
    {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this,"Location Services required",Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
            }
        }
        else{
            mMap.setMyLocationEnabled(true);
        }

        if(!checkPlayServices())
        {
            Toast.makeText(this,"Please install Google Play Services.!",Toast.LENGTH_SHORT).show();
        }

        LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled("gps"))
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Cannot provide the location services", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000);
            } else {
                finish();
            }

            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

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