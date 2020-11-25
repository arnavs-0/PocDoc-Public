package com.arnav.pocdoc.Onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.arnav.pocdoc.MedicalId;
import com.arnav.pocdoc.R;

public class NotificationOnboard extends AppCompatActivity {

    ImageView back, done, content, circle;
    Button yes;

    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_onboard);

        back = findViewById(R.id.back_notification);
        content = findViewById(R.id.content_notification);
        done = findViewById(R.id.done);
        yes = findViewById(R.id.yes);
        circle = findViewById(R.id.circle);
        content.setVisibility(View.VISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Introduction.class);
                startActivity(intent);
                finish();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content.setVisibility(View.INVISIBLE);
                done.setVisibility(View.VISIBLE);
                circle.setVisibility(View.VISIBLE);
                Drawable drawable = done.getDrawable();

                if (drawable instanceof AnimatedVectorDrawableCompat){
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                } else if (drawable instanceof AnimatedVectorDrawable){
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();
                }
                startActivity(new Intent(getApplicationContext(), MedicalId.class));
                
            }
        });
    }
}