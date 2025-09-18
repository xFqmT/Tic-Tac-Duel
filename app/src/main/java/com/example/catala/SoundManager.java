package com.example.catala;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer mediaPlayer;
    private float volume = 0.7f; // Default volume (70%)

    // Private constructor to enforce Singleton pattern
    private SoundManager(Context context) {
        // Initialize MediaPlayer with application context to avoid memory leaks
        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.pop);
        mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer()); // Release after each use
    }

    // Get the singleton instance
    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    // Play the click sound
    public void playClickSound(Context context) {
        try {
            // Initialize MediaPlayer only if it's not already initialized or released
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.pop);
                mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer()); // Release after playing
            }

            // Set the volume if MediaPlayer is available
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace(); // Log any errors in case of failure
        }
    }

    // Set volume
    public void setVolume(float newVolume) {
        volume = newVolume; // Update volume
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume); // Update volume on existing MediaPlayer instance
        }
    }

    // Release MediaPlayer resources after use to prevent memory leaks
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null; // Avoid memory leaks
        }
    }
}
