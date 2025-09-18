package com.example.catala;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private MusicService musicService;
    private boolean isBound = false;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Initialize SoundManager
        soundManager = SoundManager.getInstance(this);

        // Bind to MusicService
        Intent musicIntent = new Intent(this, MusicService.class);
        bindService(musicIntent, serviceConnection, BIND_AUTO_CREATE);

        // Sound SeekBar
        SeekBar seekBarSound = findViewById(R.id.seekBarSound);
        seekBarSound.setProgress(70); // Default sound volume
        seekBarSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float volume = progress / 100f;
                    soundManager.setVolume(volume); // Adjust pop sound volume
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Music SeekBar
        SeekBar seekBarMusic = findViewById(R.id.seekBarMusic);
        seekBarMusic.setProgress(70); // Default music volume
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float volume = progress / 100f;
                    if (isBound) {
                        musicService.setVolume(volume); // Adjust music volume
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Brightness SeekBar
        SeekBar seekBarBrightness = findViewById(R.id.seekBarBrightness);
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        int savedBrightness = sharedPreferences.getInt("Brightness", 60); // Default to 50%
        seekBarBrightness.setProgress(savedBrightness);
        setAppBrightness(savedBrightness);

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setAppBrightness(progress); // Apply brightness change
                    sharedPreferences.edit().putInt("Brightness", progress).apply(); // Save the brightness value
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Back Button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            soundManager.playClickSound(this); // Play sound when button is clicked
            Intent intent = new Intent(SettingsActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setAppBrightness(int brightness) {
        // Convert brightness percentage (0-100) to a scale of 0.0 - 1.0
        float brightnessLevel = brightness / 100f;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightnessLevel;
        getWindow().setAttributes(layoutParams);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
