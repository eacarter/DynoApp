package com.ford.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class reportActivity extends AppCompatActivity {

    TextView speed;
    TextView altitude;
    TextView zero2sixty;
    TextView rpm;
    TextView torque;
    TextView extTemp;
    TextView horPow;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        extras = getIntent().getExtras();


        speed=(TextView)findViewById(R.id.speedNum);
        altitude=(TextView)findViewById(R.id.altNum);
        zero2sixty=(TextView)findViewById(R.id.zeroSixtyNum);
        rpm=(TextView)findViewById(R.id.rpmNum);
        torque=(TextView)findViewById(R.id.torqueNum);
        extTemp=(TextView)findViewById(R.id.extTempNum);
        horPow=(TextView)findViewById(R.id.horsePowerNum);

        speed.setText(String.valueOf(extras.getDouble("Speed")));

    }

    @Override
    public void onBackPressed(){
        finish();
        Intent intent = new Intent(reportActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
