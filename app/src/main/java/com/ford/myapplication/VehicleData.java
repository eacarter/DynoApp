package com.ford.myapplication;

import android.location.Location;

/**
 * Created by eric on 10/19/16.
 */
public class VehicleData {

   double mSpeed;
    double mLong;
    double mLat;
    double altitude;
    int rpm;
    double engTorque;
    double extTemps;
    double horsePower;
    Location disToPosition;

    public double getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(double power) {
        this.horsePower = ((getEngTorque() * getRpm())/5252);
    }

    public double getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public double getEngTorque() {
        return engTorque;
    }

    public void setEngTorque(double engTorque) {
        this.engTorque = engTorque;
    }

    public double getExtTemps() {
        return extTemps;
    }

    public void setExtTemps(double extTemps) {
        this.extTemps = extTemps;
    }

    public Location getDisToPosition() {
        return disToPosition;
    }

    public void setDisToPosition(Location disToPosition) {
        this.disToPosition = disToPosition;
    }

    public double metersToMiles(double speed){
        double milesSpeed = speed*2.236936;
        return milesSpeed;
    }
}
