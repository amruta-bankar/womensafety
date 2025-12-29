package com.teamDroiders.womensafety;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SmsActivity extends AppCompatActivity {

    TextInputEditText txt_pnumber1, txt_msg, txt_pnumber2, txt_pnumber3, txt_pnumber4;
    Button Save;

    /**
     * LocationManager :- This class provides access to the system location services.
     * These services allow applications to obtain periodic updates of the device's geographical location,
     * or to be notified when the device enters the proximity of a given geographical location.
     */

    /**
     * FusedLocationProvider :- The fused location provider is one of the location APIs in Google Play services.
     * It manages the underlying location technology and provides a simple API so that you can specify
     * requirements at a high level, like high accuracy or low power.
     * It also optimizes the device's use of battery power.
     * The fused location provider  is used specifically to retrieve the device's last known location
     */

    FusedLocationProviderClient fusedLocationProviderClient;
    String prevStarted = "yesSms";
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();

            final AlertDialog.Builder alert = new AlertDialog.Builder(SmsActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);

            Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
            alert.setView(mView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            btn_okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sms);

        txt_msg = findViewById (R.id.txt_sms);

        txt_pnumber1 = findViewById (R.id.txt_phone_number1);
        txt_pnumber2 = findViewById (R.id.txt_phone_number2);
        txt_pnumber3 = findViewById (R.id.txt_phone_number3);
        txt_pnumber4 = findViewById (R.id.txt_phone_number4);
        Save = findViewById (R.id.btn_send_sms);

        // link :- https://google-developer-training.github.io/android-developer-advanced-course-concepts/unit-4-add-geo-features-to-your-apps/lesson-7-location/7-1-c-location-services/7-1-c-location-services.html
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (this);


        //saving data code demo

        Save.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

//                v=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 400 milliseconds

                String phone1 = txt_pnumber1.getText ().toString ();
                String phone2 = txt_pnumber2.getText ().toString ();
                String phone3 = txt_pnumber3.getText ().toString ();
                String phone4 = txt_pnumber4.getText ().toString ();
                String msg = txt_msg.getText ().toString ();

                /**
                 * SharedPreferences.Editor :- Interface used for modifying values in a SharedPreferences object.
                 * All changes you make in an editor are batched, and not copied back to the original SharedPreferences
                 * until you call commit() or apply().
                 *
                 * To write to a shared preferences file, SharedPreferences.Editor is created by calling edit() on the
                 * SharedPreferences.
                 */
                SharedPreferences shrd = getSharedPreferences ("demo", MODE_PRIVATE);
                SharedPreferences.Editor editor = shrd.edit ();
                editor.putString ("phone1", phone1);
                editor.putString ("phone2", phone2);
                editor.putString ("phone3", phone3);
                editor.putString ("phone4", phone4);
                editor.putString ("msg", msg);
                editor.apply ();
                txt_pnumber1.setText (phone1);
                txt_pnumber2.setText (phone2);
                txt_pnumber3.setText (phone3);
                txt_pnumber4.setText (phone4);
                txt_msg.setText (msg);
                if (!txt_pnumber1.getText ().toString ().equals ("") && !txt_pnumber2.getText ().toString ().equals ("") && !txt_pnumber3.getText ().toString ().equals ("") && !txt_pnumber4.getText ().toString ().equals ("")) {
                    Toast.makeText (SmsActivity.this, "Saved...", Toast.LENGTH_SHORT).show ();

                } else if (!txt_pnumber1.getText ().toString ().equals ("") || !txt_pnumber2.getText ().toString ().equals ("") || !txt_pnumber3.getText ().toString ().equals ("") || !txt_pnumber4.getText ().toString ().equals ("")) {
                    Toast.makeText (SmsActivity.this, "Saved but some fields are blank...", Toast.LENGTH_SHORT).show ();

                } else
                    Toast.makeText (SmsActivity.this, "Please enter the numbers first...", Toast.LENGTH_SHORT).show ();


            }
        });

        /**
         * SharedPreferences :- Android provides many ways of storing data of an application, one of the way is called as shared preference.
         * SharedPreferences object help you save simple application data as a name/value pairs - you specify a name for the data you want to save,
         * and then both it and its value will be saved automatically to an XML file for you. when you will close the application and then open it again then,
         * you will find these values as it was before closing the app , means they will be retrived.
         *
         * link :- https://www.geeksforgeeks.org/shared-preferences-in-android-with-examples/
         * link :- https://www.youtube.com/watch?v=LHaxq3YdxZU&t=1s
         */

        /**
         * getSharedPreferences() :- Shared Preferences allow you to save and retrieve data in the form of key,value pair.
         * In order to use shared preferences, you have to call a method getSharedPreferences() that returns a SharedPreference
         * instance pointing to the file that contains the values of preferences.
         */

        //Getting the value of shared preference back
        SharedPreferences getShared = getSharedPreferences ("demo", MODE_PRIVATE);
        String Value1 = getShared.getString ("phone1", "");
        txt_pnumber1.setText (Value1);
        String Value2 = getShared.getString ("phone2", "");
        txt_pnumber2.setText (Value2);
        String Value3 = getShared.getString ("phone3", "");
        txt_pnumber3.setText (Value3);
        String Value4 = getShared.getString ("phone4", "");
        txt_pnumber4.setText (Value4);
        String Value = getShared.getString ("msg", "I am in danger, please come fast...");
        txt_msg.setText (Value);


//saving data code demo ends here
    }




    public void btn_send(View view) {
        tryIt ();
    }
    public  void tryIt(){
        if(!txt_pnumber1.getText ().toString ().trim ().equals ("")) {
            /**
             * ContextCompat :- It is a class for replacing some work with base context.
             * For example if you used before something like getContext().getColor(R.color.black);
             * Now its deprecated since android 6.0 (API 22+) so you should use: getContext().getColor(R.color.black,theme);
             */
            if (ContextCompat.checkSelfPermission (SmsActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                makeCall ();
            } else {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }else{
            Toast.makeText (this,"Please enter first number...",Toast.LENGTH_LONG);
        }
        int permissionCheck = ContextCompat.checkSelfPermission (this, Manifest.permission.SEND_SMS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if ( ContextCompat.checkSelfPermission (SmsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                SendLocationMessage ();

            } else {

                /**
                 * ActivityCompat :- public interface ActivityCompat. A helper for accessing features in Activity in a backwards
                 * compatible fashion. Construct this by using getActivityCompat(Activity) .
                 * This helper augments the included methods with data on instant apps.
                 */
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }

        } else {
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }


    }
    //Calling function
    private void makeCall() {
        Intent intent = new Intent (Intent.ACTION_CALL);
        String phoneNumber = txt_pnumber1.getText ().toString ();
        if (phoneNumber.trim ().isEmpty ()) {
            Toast.makeText (SmsActivity.this, "Please enter first number to make a call..", Toast.LENGTH_SHORT).show ();

        } else {

            intent.setData (Uri.parse ("tel:" + phoneNumber));
        }
        startActivity (intent);
    }

    private void SendLocationMessage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                StringBuilder message = new StringBuilder(txt_msg.getText().toString().trim());

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Google Maps Navigation Link
                    String mapsLink = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;

                    // Append lat, long, and navigation link to the message
                    message.append("\nMy Location: ").append(latitude).append(", ").append(longitude)
                            .append("\nFind me here: ").append(mapsLink);
                } else {
                    message.append("\nUnable to fetch location. Please check GPS settings.");
                    Toast.makeText(SmsActivity.this, "Location unavailable. Make sure GPS is enabled.", Toast.LENGTH_SHORT).show();
                }

                // Get phone numbers
                String[] phoneNumbers = {
                        txt_pnumber1.getText().toString().trim(),
                        txt_pnumber2.getText().toString().trim(),
                        txt_pnumber3.getText().toString().trim(),
                        txt_pnumber4.getText().toString().trim()
                };

                boolean isNumberPresent = false;
                SmsManager smsManager = SmsManager.getDefault();

                for (String phoneNumber : phoneNumbers) {
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        isNumberPresent = true;
                        smsManager.sendTextMessage(phoneNumber, null, message.toString(), null, null);
                    }
                }

                if (isNumberPresent) {
                    Toast.makeText(SmsActivity.this, "Message sent with location!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SmsActivity.this, "No phone numbers provided!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:

                if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }
                else {
                    Toast.makeText (this, "You don't have required permissions", Toast.LENGTH_SHORT).show ();
                }

                break;
        }
    }
    static BroadcastReceiver broadcastReceiver = new BroadcastReceiver () {
        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...
        }
    };
    @Override
    // main menu means logout button has created in actionbar.
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.instructions_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_Detailed_instructions) {
            startActivity(new Intent(getApplicationContext(), SosInsructionsActivity.class));
            return true;
        } else if (id == R.id.action_Short_instructions) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(SmsActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.custom_dialog, null);

            Button btn_okay = mView.findViewById(R.id.btn_okay);
            alert.setView(mView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            btn_okay.setOnClickListener(v -> alertDialog.dismiss());

            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}