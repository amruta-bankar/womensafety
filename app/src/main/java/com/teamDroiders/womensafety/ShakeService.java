package com.teamDroiders.womensafety;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ShakeService extends Service {
    private static final String TAG = "ShakeService";
    private static final String CHANNEL_ID = "ShakeServiceChannel";
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ShakeService created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ShakeService started");

        // Start foreground service with notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SOS Alarm Running")
                .setContentText("Shake detected! Playing alarm.")
                .setSmallIcon(R.drawable.ic_sos)  // Ensure you have an SOS icon in drawable
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);

        // Play police siren sound
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.police_siren);
            Log.d(TAG, "MediaPlayer initialized");
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            Log.d(TAG, "Police siren started");
        }

        return START_STICKY;  // Ensures service restarts if killed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ShakeService destroyed");

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "MediaPlayer released");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // This service is not bound
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Shake Detection Service",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
