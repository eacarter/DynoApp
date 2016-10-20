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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class newDynoTestActivity extends Activity implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    boolean testStarted = false;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleClientApi;
    Location mLastLocation;
    CountDownTimer countdowntimer;
    TextView timer;
    TextView remaining;
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

        startTest(testStarted);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(5000);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleClientApi.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleClientApi.isConnected()) {
            mGoogleClientApi.disconnect();
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClientApi);

        if (mLastLocation != null) {
            startLocationUpdates();
        } else {
            Log.d("location", "No ciger");
        }
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClientApi, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleClientApi, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void startTest(boolean start) {

        if (start) {
            countdowntimer.start();
        }

    }

    @Override
    public void onLocationChanged(Location location) {

       // Data.setmSpeed(location.getSpeed());
        Data.setmLat(location.getLatitude());
        Data.setmLong(location.getLongitude());
        Log.d("Location", String.valueOf(Data.getmLat()+ ","+ Data.getmLong()));
    }

    public class TestCounter extends CountDownTimer {

        double temp;
        double speed;

        public TestCounter(long start, long interval){
            super(start, interval);
        }

            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished/1000));

                //onLocationChanged();


                  Log.d("speed", String.valueOf(temp));
                 // Log.d("Location", String.valueOf(Data.getmLat()+ ","+ Data.getmLong()));

            }

//            public final void cancel(){
//                Intent intent = new Intent(newDynoTestActivity.this, reportActivity.class);
//                intent.putExtra("speed",temp);
//                startActivity(intent);
//            }

            @Override
            public void onFinish() {
               // newDynoTestActivity.this.finish();
                Intent intent = new Intent(newDynoTestActivity.this, reportActivity.class);
                intent.putExtra("Speed",Data.getmSpeed());
                startActivity(intent);
            }
        }
    }


