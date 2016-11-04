package com.ford.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class newDynoTestActivity extends Activity implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    boolean testStarted = false;
    boolean mRequestingLocationUpdates;
    double speed;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleClientApi;
    Location mLastLocation;
    CountDownTimer countdowntimer;
    TextView timer;
    TextView remaining;

    TextView lat;
    TextView log;

    VehicleData Data = new VehicleData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dyno_test);

        testStarted = true;

        buildGoogleApiClient();

        countdowntimer = new TestCounter(30000, 1000);

        remaining = (TextView) findViewById(R.id.timeText);
        timer = (TextView) findViewById(R.id.timing);
        lat = (TextView) findViewById(R.id.lat);
        log = (TextView) findViewById(R.id.log);

        startTest(testStarted);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testStarted = false;
                countdowntimer.cancel();
                countdowntimer.onFinish();
            }
        });
    }

    public synchronized void buildGoogleApiClient() {
        mGoogleClientApi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleClientApi.connect();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mGoogleClientApi.isConnected() && mRequestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mGoogleClientApi.isConnected()){
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        testStarted = false;

        if (mGoogleClientApi.isConnected()) {
            mGoogleClientApi.disconnect();
            startTest(testStarted);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mLastLocation == null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClientApi);
        }

            startLocationUpdates();

    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClientApi, mLocationRequest, this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleClientApi, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
       mGoogleClientApi.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if(!mGoogleClientApi.isConnected()) {
            buildGoogleApiClient();
        }
    }


    private void startTest(boolean start) {
        if (start) {
            countdowntimer.start();
        }
        else{
            countdowntimer.cancel();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation != null) {
            speed = metersToMiles(Math.sqrt(
                    Math.pow(location.getLongitude() - mLastLocation.getLongitude(), 2.0) +
                    Math.pow(location.getLatitude() - mLastLocation.getLatitude(), 2.0)) /
                    (location.getTime() - this.mLastLocation.getTime()));

            if(location.hasSpeed()){
               speed = metersToMiles(location.getSpeed());
                mLastLocation = location;
            }
        }
        Data.setmLat(location.getLatitude());
        Data.setmLong(location.getLongitude());
        Data.setAltitude(location.getAltitude());
    }

    public double metersToMiles(double mph){
        speed = mph*(3600/1600);
        return speed;
    }

    public class TestCounter extends CountDownTimer {
        double speed;

        public TestCounter(long start, long interval){
            super(start, interval);
        }

            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished / 1000));

                if(speed > Data.getmSpeed()){
                    Data.setmSpeed(speed);
                }

                lat.setText(String.valueOf(Data.getmSpeed()));
                log.setText(String.valueOf(Data.getAltitude()));

                Log.d("Location", String.valueOf(Data.getmLat()+ ","+ Data.getmLong()+","+Data.getmSpeed()));
            }


            @Override
            public void onFinish() {
                  newDynoTestActivity.this.finish();
                  Intent intent = new Intent(newDynoTestActivity.this, reportActivity.class);
                  intent.putExtra("Speed", speed);
                  intent.putExtra("Altitude", Data.getAltitude());
                  intent.putExtra("fromMain", false);
                  startActivity(intent);
            }
        }
    }


