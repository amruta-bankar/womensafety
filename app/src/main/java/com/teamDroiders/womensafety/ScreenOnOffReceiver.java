package com.teamDroiders.womensafety;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.content.Context.MODE_PRIVATE;

public class ScreenOnOffReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenOnOffReceiver";
    private static final int SOS_TRIGGER_COUNT = 3;
    private static final int SIREN_TRIGGER_COUNT = 6;

    private MediaPlayer mediaPlayer;
    private int powerBtnTapCount = 0;
    private int timer = 0;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private String Value1, Value2, Value3, Value4, Value;

    private final CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            timer++;
            Log.d(TAG, "Timer: " + timer);
        }

        @Override
        public void onFinish() {
            timer = 0;
            powerBtnTapCount = 0;
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_SCREEN_ON.equals(action)) {
            if (powerBtnTapCount == 0) {
                powerBtnTapCount++;
                countDownTimer.start();
            } else if (timer < 30) {
                powerBtnTapCount++;
            } else {
                resetTapCount();
            }
            Log.d(TAG, "Power button tapped: " + powerBtnTapCount);
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.police_siren);
        }

        if (powerBtnTapCount == SIREN_TRIGGER_COUNT) {
resetTapCount();
        }

        if (powerBtnTapCount == SOS_TRIGGER_COUNT) {
            stopSiren();
            retrieveEmergencyContacts(context);
            sendEmergencyMessage(context);
        }
    }

    private void resetTapCount() {
        powerBtnTapCount = 0;
        timer = 0;
        countDownTimer.cancel();
    }

    private void startSiren() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
        resetTapCount();
    }

    private void stopSiren() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.setLooping(false);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void retrieveEmergencyContacts(Context context) {
        SharedPreferences getShared = context.getSharedPreferences("demo", MODE_PRIVATE);
        Value1 = getShared.getString("phone1", "").trim();
        Value2 = getShared.getString("phone2", "").trim();
        Value3 = getShared.getString("phone3", "").trim();
        Value4 = getShared.getString("phone4", "").trim();
        Value = getShared.getString("msg", "I am in danger, please come fast...").trim();
    }

    private void sendEmergencyMessage(Context context) {
        sendLocationMessage(context);
        if (!TextUtils.isEmpty(Value1) && ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makeEmergencyCall(context);
        }
    }

    private void makeEmergencyCall(Context context) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + Value1));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(callIntent);
        Log.d(TAG, "Calling: " + Value1);
    }

    private void sendLocationMessage(final Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted!");
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context.getApplicationContext());
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                String message = "Emergency!";
                Location location = task.getResult();

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                    message += "\nMy Location: " + latitude + ", " + longitude + "\nFind me here: " + mapsLink;
                    sendSMS(message);
                } else {
                    Log.e(TAG, "Failed to retrieve location. Requesting new location updates...");
                    requestNewLocationData(context);
                }
            }
        });
    }

    private void requestNewLocationData(Context context) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setNumUpdates(1);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.e(TAG, "Still unable to fetch location.");
                    return;
                }

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;

                    String message = "Emergency!\nMy Location: " + latitude + ", " + longitude + "\nFind me here: " + mapsLink;
                    sendSMS(message);
                }
            }
        }, null);
    }

    private void sendSMS(String message) {
        String[] phoneNumbers = {Value1, Value2, Value3, Value4};
        SmsManager smsManager = SmsManager.getDefault();
        boolean isNumberPresent = false;

        for (String phoneNumber : phoneNumbers) {
            if (!TextUtils.isEmpty(phoneNumber)) {
                isNumberPresent = true;
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
        }

        if (isNumberPresent) {
            Log.d(TAG, "SMS sent: " + message);
        } else {
            Log.e(TAG, "No valid phone numbers available!");
        }
    }
}
