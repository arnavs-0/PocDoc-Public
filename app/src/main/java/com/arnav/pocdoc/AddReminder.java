package com.arnav.pocdoc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import ca.antonious.materialdaypicker.MaterialDayPicker;


public class AddReminder extends Fragment {
    TextInputEditText name, dosage;
    MaterialSpinner pill_type_spinner;
    MaterialDayPicker day_picker;
    TimePicker time_picker;
    Button add;

    String[] pill_types;

    DatabaseReference database;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pill_types = getResources().getStringArray(R.array.medication_types);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        name = view.findViewById(R.id.add_reminder_medication_name);
        dosage = view.findViewById(R.id.add_reminder_medication_dosage);
        pill_type_spinner = view.findViewById(R.id.add_reminder_spinner);
        day_picker = view.findViewById(R.id.add_reminder_day_picker);
        time_picker = view.findViewById(R.id.add_reminder_time_picker);
        add = view.findViewById(R.id.add_reminder_add_button);

        database = FirebaseDatabase.getInstance().getReference("reminder").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

        pill_type_spinner.setItems(pill_types);

        add.setOnClickListener(view1 -> {
            String pill_type = pill_types[pill_type_spinner.getSelectedIndex()];
            List<MaterialDayPicker.Weekday> selected_days = day_picker.getSelectedDays();

            if (name.getText() != null && dosage.getText() != null && selected_days.size() > 0) {
                ArrayList<String> days = new ArrayList<>();
                for (MaterialDayPicker.Weekday day : selected_days) {
                    days.add(day.name());
                }
                ReminderItem adding_reminder = new ReminderItem(name.getText().toString(), pill_type, dosage.getText().toString(), days.toString(), time_picker.getHour(), time_picker.getMinute(), "@drawable/"+(!pill_type.equals("blank") ? pill_type.toLowerCase() : "medication_blank"));

                String id = database.push().getKey();

                database.child(Objects.requireNonNull(id)).setValue(adding_reminder);

                Reminder.scheduleNotification(id);

                Toast.makeText(getContext(), "New Reminder Added", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Please answer all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}