package com.arnav.pocdoc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeReminder extends Fragment {
    RecyclerView recycler;

    FirebaseRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_reminder, container, false);

        recycler = view.findViewById(R.id.reminder_recycler_view);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setHasFixedSize(true);
        fetch();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("reminder").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        FirebaseRecyclerOptions<ReminderItem> options = new FirebaseRecyclerOptions.Builder<ReminderItem>()
                .setQuery(query, snapshot -> new ReminderItem(
                        Objects.requireNonNull(snapshot.child("name").getValue()).toString(),
                        Objects.requireNonNull(snapshot.child("type").getValue()).toString(),
                        Objects.requireNonNull(snapshot.child("dosage").getValue()).toString(),
                        Objects.requireNonNull(snapshot.child("repeat").getValue()).toString(),
                        Integer.parseInt(Objects.requireNonNull(snapshot.child("remindHour").getValue()).toString()),
                        Integer.parseInt(Objects.requireNonNull(snapshot.child("remindMinute").getValue()).toString()),
                        Objects.requireNonNull(snapshot.child("imageResource").getValue()).toString()
                ))
                .build();
         adapter = new FirebaseRecyclerAdapter<ReminderItem, ReminderViewHolder>(options) {
            @NotNull
            @Override
            public ReminderViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                return new ReminderViewHolder(LayoutInflater.from(Objects.requireNonNull(getContext()).getApplicationContext()).inflate(R.layout.reminder_card, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NotNull HomeReminder.ReminderViewHolder holder, int position, @NotNull ReminderItem model) {
                String repeat = model.getRepeat();

                holder.medication_name.setText("Medication Name: "+model.getName());
                holder.type.setText("Medication Type: "+model.getType());
                holder.dosage.setText("Dosage: "+model.getDosage());
                holder.repeat.setText("Days: "+repeat.substring(1, repeat.length()-1));
                holder.reminder_time.setText("Time: "+String.format("%d:%02d", model.getRemindHour(), model.getRemindMinute()));
                holder.pill_image.setImageResource(getResources().getIdentifier(model.getImageResource(), "drawable", Objects.requireNonNull(getContext()).getPackageName()));
            }
        };
        recycler.setAdapter(adapter);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView medication_name, type, dosage, repeat, reminder_time;
        ImageView pill_image;

        public ReminderViewHolder(View itemView) {
            super(itemView);

            medication_name = itemView.findViewById(R.id.reminder_medication_name);
            type = itemView.findViewById(R.id.reminder_type);
            dosage = itemView.findViewById(R.id.reminder_dosage);
            repeat = itemView.findViewById(R.id.reminder_repeat);
            reminder_time = itemView.findViewById(R.id.reminder_time);
            pill_image = itemView.findViewById(R.id.reminder_pill_image);
        }
    }
}
