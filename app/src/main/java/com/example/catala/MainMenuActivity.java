package com.example.catala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        applyBrightness();
        soundManager = SoundManager.getInstance(this);

        // Start music service
        startService(new Intent(this, MusicService.class));

        // Play Button - Goes to MainActivity
        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playClickSound(MainMenuActivity.this);
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Settings Button - Goes to SettingsActivity
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playClickSound(MainMenuActivity.this);
                Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Welcome Button - Returns to WelcomeScreen with clean stack
        Button btnWelcome = findViewById(R.id.btnWelcome);
        btnWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playClickSound(MainMenuActivity.this);
                Intent intent = new Intent(MainMenuActivity.this, WelcomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Finish current activity
            }
        });
    }

    @Override
    public void onBackPressed() {
        // When back pressed, go to WelcomeScreen with clean stack
        soundManager.playClickSound(this);
        Intent intent = new Intent(this, WelcomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void applyBrightness() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int brightness = sharedPreferences.getInt("Brightness", 60);
        float brightnessLevel = brightness / 100f;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightnessLevel;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyBrightness();

        // Restart music if it was stopped (optional)
        startService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't stop music here to keep it playing between activities
    }
}