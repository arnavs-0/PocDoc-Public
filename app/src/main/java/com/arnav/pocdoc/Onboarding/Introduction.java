package com.arnav.pocdoc.Onboarding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.arnav.pocdoc.R;
import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;

public class Introduction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnRightOutListener(() -> {
            //Add intent here
            Intent intent = new Intent(getApplicationContext(), NotificationOnboard.class);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(), "Swiped out right", Toast.LENGTH_SHORT).show();
        });

    }
    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {

        PaperOnboardingPage scr1 = new PaperOnboardingPage("Hello, ", "Welcome to PocDoc, An Online Medical Helper",
                Color.parseColor("#bff9ff"), R.drawable.welcome_pic, R.drawable.hospital);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Diary", "Keep Track of Your Medical Events and Find a Possible Diagnosis",
                Color.parseColor("#a6dde3"), R.drawable.diary_pic, R.drawable.diary);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Allergy Finder", "Scan Foods to Find Possible Allergens using our AI Algorithm that classifies over 1,000 Foods",
                Color.parseColor("#88c8cf"), R.drawable.symptom_pic, R.drawable.symptoms);
        PaperOnboardingPage scr4 = new PaperOnboardingPage("Medication Reminder", "Reminder to Take Your Medications From Your Smartphone",
                Color.parseColor("#76bdc4"), R.drawable.reminder_pic, R.drawable.reminder);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        elements.add(scr4);
        return elements;
    }
}