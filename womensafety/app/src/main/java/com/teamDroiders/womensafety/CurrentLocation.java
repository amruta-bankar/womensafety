package com.teamDroiders.womensafety;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class CurrentLocation extends AppCompatActivity {

    private Location mLastLocation;
    private TextView mLatitudeText, mLongitudeText;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = CurrentLocation.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeText = findViewById(R.id.latitude_text);
        mLongitudeText = findViewById(R.id.longitude_text);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                showSnackbar(R.string.textwarn, R.string.settings, view -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }
        }
    }

    private void showSnackbar(int mainTextStringId, int actionStringId, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showSnackbar(R.string.textwarn, android.R.string.ok, view -> requestLocationPermission());
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (!checkPermissions()) {
            Log.e(TAG, "Location permission not granted");
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                mLastLocation = task.getResult();
                updateUI();
            } else {
                Log.w(TAG, "getLastLocation:exception", task.getException());
            }
        });
    }

    private void updateUI() {
        if (mLastLocation != null) {
            if (mLatitudeText != null) {
                mLatitudeText.setText(String.format(Locale.ENGLISH, "Latitude: %f", mLastLocation.getLatitude()));
            }
            if (mLongitudeText != null) {
                mLongitudeText.setText(String.format(Locale.ENGLISH, "Longitude: %f", mLastLocation.getLongitude()));
            }
        }
    }
}
