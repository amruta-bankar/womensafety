package com.teamDroiders.womensafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
    private static final int SPLASH_TIMEOUT = 4000; // 4 seconds as a backup timeout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Initialize Lottie Animation View
        lottieAnimationView = findViewById(R.id.lottie_layer_name);

        // Start the animation
        lottieAnimationView.playAnimation();
        Log.d("SplashActivity", "Lottie animation started");

        // Set an animation listener
        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("SplashActivity", "Animation ended, transitioning to MainActivity");
                launchMainActivity();
            }
        });

        // Backup handler in case animation does not trigger listener
        new Handler(Looper.getMainLooper()).postDelayed(this::launchMainActivity, SPLASH_TIMEOUT);
    }

    private void launchMainActivity() {
        Log.d("SplashActivity", "Starting MainActivity...");
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Prevents returning to SplashActivity
        startActivity(intent);
        finish(); // Close SplashActivity
    }
}
