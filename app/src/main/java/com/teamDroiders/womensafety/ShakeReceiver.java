package com.teamDroiders.womensafety;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.Log;

public class ShakeReceiver extends BroadcastReceiver implements SensorEventListener {
    private static final String TAG = "ShakeReceiver";
    private static final int SHAKE_THRESHOLD = 20; // Reduced for better sensitivity
    private static final int SHAKE_COUNT_RESET_TIME_MS = 80000;
    private static final int REQUIRED_SHAKE_COUNT = 5;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private MediaPlayer mediaPlayer;
    private long lastShakeTime;
    private int shakeCount = 0;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");
        mContext = context;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Accelerometer registered");
            } else {
                Log.e(TAG, "No Accelerometer found!");
            }
        } else {
            Log.e(TAG, "SensorManager is null!");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastShakeTime;

            double acceleration = Math.sqrt(x * x + y * y + z * z);
            Log.d(TAG, "Acceleration: " + acceleration);

            if (timeDifference > SHAKE_COUNT_RESET_TIME_MS) {
                shakeCount = 0; // Reset shake count if time gap is too large
                Log.d(TAG, "Shake count reset due to timeout");
            }

            if (acceleration > SHAKE_THRESHOLD) {
                shakeCount++;
                lastShakeTime = currentTime;
                Log.d(TAG, "Shake detected! Count: " + shakeCount);
            }

            if (shakeCount >= REQUIRED_SHAKE_COUNT) {
                Log.d(TAG, "Shake threshold reached, triggering SOS");
                triggerSOS(mContext);
                shakeCount = 0; // Reset count after triggering
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used, but required for implementation
    }

    private void triggerSOS(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot trigger SOS");
            return;
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.police_siren);
            Log.d(TAG, "MediaPlayer initialized");
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            Log.d(TAG, "Police siren started");
        }
    }

    public void unregisterSensor() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Sensor unregistered");
        }
    }
}
