package com.arnav.pocdoc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;
import net.steamcrafted.loadtoast.LoadToast;

import java.util.Objects;

import static android.graphics.Color.GRAY;

public class MedicalId extends AppCompatActivity {
    Button patient_edit, general_edit, medical_edit, continue_med_id;

    // Patient Info
    TextView location, patient_id, email, insurance;
    // General
    TextView age, gender, height, weight, blood_type, race;
    // Medical
    TextView conditions, family_history, blood_pressure, blood_sugar, allergies, habits, medication, medication_names;

    Dialog loading_dialog;
    MedicalIdEdit edit_dialog;

    ProgressBar loading_bar;

    Context context;

    protected void open_dialog(String name, Bundle data) {
        edit_dialog = new MedicalIdEdit(name, data, loading_dialog);
        loading_dialog.show();
        edit_dialog.show(getSupportFragmentManager(), "medical_id_edit");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_id);

        final FoldingCell patient = findViewById(R.id.patient_info);
        final FoldingCell general = findViewById(R.id.general_cell);
        final FoldingCell medical = findViewById(R.id.medical_cell);
        patient_edit = findViewById(R.id.patient_edit);
        general_edit = findViewById(R.id.general_edit);
        medical_edit = findViewById(R.id.medical_edit);
        continue_med_id = findViewById(R.id.continue_med_id);

        location = findViewById(R.id.location_id);
        patient_id = findViewById(R.id.patient_id);
        email = findViewById(R.id.email_med);
        insurance = findViewById(R.id.insurance_med);

        age = findViewById(R.id.age_id);
        gender = findViewById(R.id.gender_id);
        height = findViewById(R.id.height_id);
        weight = findViewById(R.id.weight_id);
        blood_type = findViewById(R.id.blood_id);
        race = findViewById(R.id.race_id);

        conditions = findViewById(R.id.condition_id);
        family_history= findViewById(R.id.family_id);
        blood_pressure = findViewById(R.id.pressure_id);
        blood_sugar = findViewById(R.id.sugar_id);
        allergies = findViewById(R.id.allergies_id);
        habits = findViewById(R.id.habit_id);
        medication = findViewById(R.id.otc_id);
        medication_names = findViewById(R.id.rx_id);

        loading_dialog = new Dialog(this);
        loading_dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        loading_dialog.setCancelable(false);

        loading_bar = new ProgressBar(this);

        loading_dialog.setContentView(loading_bar);

        context = MedicalId.this;
        final LoadToast lt = new LoadToast(context);

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient.toggle(false);
            }
        });

        patient_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("Location", new Object[]{R.drawable.ic_outline_pin_drop_24, "location"});
                data.putSerializable("Patient ID", new Object[]{R.drawable.ic_outline_credit_card_24, "patientID"});
                data.putSerializable("Email", new Object[]{R.drawable.ic_outline_email_24, "email"});
                data.putSerializable("Insurance", new Object[]{R.drawable.ic_outline_verified_user_24, "insurance"});
                open_dialog("Patient Information", data);
            }
        });

        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                general.toggle(false);
            }
        });

        general_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("Age", new Object[]{R.drawable.heart_blue, "age"});
                data.putSerializable("Gender", new Object[]{R.drawable.gender_blue, "gender"});
                data.putSerializable("Height", new Object[]{R.drawable.icons8_height_96, "height"});
                data.putSerializable("Weight", new Object[]{R.drawable.icons8_scale_64, "weight"});
                data.putSerializable("Blood Type", new Object[]{R.drawable.blue_blood, "bloodType"});
                data.putSerializable("Race", new Object[]{R.drawable.icons8_multicultural_people_100, "race"});
                open_dialog("General", data);
            }
        });

        medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                medical.toggle(false);
            }
        });

        medical_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("Conditions", new Object[]{R.drawable.icons8_medical_history_96, "conditions"});
                data.putSerializable("Family History", new Object[]{R.drawable.icons8_family_96, "familyHistory"});
                data.putSerializable("BP Level", new Object[]{R.drawable.icons8_pressure_30, "bloodPressure"});
                data.putSerializable("Sugar", new Object[]{R.drawable.icons8_glucometer_30, "sugar"});
                data.putSerializable("Allergies", new Object[]{R.drawable.icons8_sneeze_96, "allergies"});
                data.putSerializable("Habits", new Object[]{R.drawable.icons8_no_food_96, "habits"});
                data.putSerializable("OTC Medication", new Object[]{R.drawable.pill_hand, "otc"});
                data.putSerializable("RX Medication Name", new Object[]{R.drawable.prescription, "rx"});
                open_dialog("Medical", data);
            }
        });

        continue_med_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lt.setBorderColor(GRAY);
                lt.setTranslationY(150);
                lt.setText("Verifying...");
                lt.show();
                lt.success();
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        FirebaseDatabase.getInstance().getReference().child("medical_id").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child("location").exists() || !snapshot.child("location").getValue().toString().equals(R.string.empty_text)){
                    location.setText(snapshot.child("location").getValue().toString());
                    location.setText("Yes");
                } else {
                    location.setText(R.string.empty_text);
                }
                if(snapshot.child("patientID").exists() || !snapshot.child("patientID").getValue().toString().equals(R.string.empty_text)){
                    patient_id.setText(snapshot.child("patientID").getValue().toString());
                    patient_id.setText("Yes");
                } else {
                    patient_id.setText(R.string.empty_text);
                }

                if(snapshot.child("email").exists()){
                    email.setText(snapshot.child("email").getValue().toString());
                    email.setText("Yes");
                } else {
                    email.setText(R.string.empty_text);
                }



                if(snapshot.child("insurance").exists()){
                    insurance.setText(snapshot.child("insurance").getValue().toString());
                    insurance.setText("Yes");
                } else {
                    insurance.setText(R.string.empty_text);
                }

                if(snapshot.child("age").exists()){
                    age.setText(snapshot.child("age").getValue().toString());
                    age.setText("Yes");
                } else {
                    age.setText(R.string.empty_text);
                }

                if(snapshot.child("gender").exists()){
                    gender.setText(snapshot.child("gender").getValue().toString());
                    gender.setText("Yes");
                } else {
                    gender.setText(R.string.empty_text);
                }

                if(snapshot.child("height").exists()){
                    height.setText(snapshot.child("height").getValue().toString());
                    height.setText("Yes");
                } else {
                    height.setText(R.string.empty_text);
                }

                if(snapshot.child("weight").exists()){
                    weight.setText(snapshot.child("weight").getValue().toString());
                    weight.setText("Yes");
                } else {
                    weight.setText(R.string.empty_text);
                }

                if(snapshot.child("bloodType").exists()){
                    blood_type.setText(snapshot.child("bloodType").getValue().toString());
                    blood_type.setText("Yes");
                } else {
                    blood_type.setText(R.string.empty_text);
                }

                if(snapshot.child("race").exists()){
                    race.setText(snapshot.child("race").getValue().toString());
                    race.setText("Yes");
                } else {
                    race.setText(R.string.empty_text);
                }



                if(snapshot.child("conditions").exists()){
                    conditions.setText(snapshot.child("conditions").getValue().toString());
                    conditions.setText("Yes");
                } else {
                    conditions.setText(R.string.empty_text);
                }

                if(snapshot.child("familyHistory").exists()){
                    family_history.setText(snapshot.child("familyHistory").getValue().toString());
                    family_history.setText("Yes");
                } else {
                    family_history.setText(R.string.empty_text);
                }

                if(snapshot.child("bloodPressure").exists()){
                    blood_pressure.setText(snapshot.child("bloodPressure").getValue().toString());
                    blood_pressure.setText("Yes");
                } else {
                    blood_pressure.setText(R.string.empty_text);
                }

                if(snapshot.child("sugar").exists()){
                    blood_sugar.setText(snapshot.child("sugar").getValue().toString());
                    blood_sugar.setText("Yes");
                } else {
                    blood_sugar.setText(R.string.empty_text);
                }

                if(snapshot.child("allergies").exists()){
                    allergies.setText(snapshot.child("allergies").getValue().toString());
                    allergies.setText("Yes");
                } else {
                    allergies.setText(R.string.empty_text);
                }

                if(snapshot.child("habits").exists()){
                    habits.setText(snapshot.child("habits").getValue().toString());
                    habits.setText("Yes");
                } else {
                    habits.setText(R.string.empty_text);
                }

                if(snapshot.child("atc").exists()){
                    medication.setText(snapshot.child("atc").getValue().toString());
                    medication.setText("Yes");
                } else {
                    medication.setText(R.string.empty_text);
                }

                if(snapshot.child("rx").exists()){
                    medication_names.setText(snapshot.child("rx").getValue().toString());
                    medication_names.setText("Yes");
                } else {
                    medication_names.setText(R.string.empty_text);
                }








//                medication_names.setText(snapshot.child("rx").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        return super.onCreateView(parent, name, context, attrs);
    }
}