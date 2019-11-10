package com.example.sensordatacollector.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "sensorData")
public class SensorDataModel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private double ax, ay, az, longitude, lattitude, micAmp;
    private int wifiStrength;
    private String wifiSSID;

    private String timestamp;


    public SensorDataModel(double ax, double ay, double az, double longitude, double lattitude, double micAmp, int wifiStrength, String wifiSSID) {
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.micAmp = micAmp;
        this.wifiStrength = wifiStrength;
        this.wifiSSID = wifiSSID;

        Date currentTime = Calendar.getInstance().getTime();
        this.timestamp = currentTime.toString();
        this.id = 0;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAx() {
        return ax;
    }

    public void setAx(double ax) {
        this.ax = ax;
    }

    public double getAy() {
        return ay;
    }

    public void setAy(double ay) {
        this.ay = ay;
    }

    public double getAz() {
        return az;
    }

    public void setAz(double az) {
        this.az = az;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getMicAmp() {
        return micAmp;
    }

    public void setMicAmp(double micAmp) {
        this.micAmp = micAmp;
    }

    public int getWifiStrength() {
        return wifiStrength;
    }

    public void setWifiStrength(int wifiStrength) {
        this.wifiStrength = wifiStrength;
    }

    public String getWifiSSID() {
        return wifiSSID;
    }

    public void setWifiSSID(String wifiSSID) {
        this.wifiSSID = wifiSSID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String get_display_string(){
        StringBuilder sb  = new StringBuilder();
        sb.append(timestamp).append(", ");
        sb.append(String.format("%.2f", ax)).append(", ");
        sb.append(String.format("%.2f", ay)).append(", ");
        sb.append(String.format("%.2f", az)).append(", ");
        sb.append(String.format("%.2f", longitude)).append(", ");
        sb.append(String.format("%.2f", lattitude)).append(", ");
        sb.append(String.format("%.2f", micAmp)).append(", ");
        sb.append(wifiSSID).append(", ");
        sb.append(wifiStrength);
        return  sb.toString();
    }

    public static String get_csv_header(){
        StringBuilder sb  = new StringBuilder();
        sb.append("Timestamp").append(", ");
        sb.append("Accelerometer X").append(", ");
        sb.append("Accelerometer Y").append(", ");
        sb.append("Accelerometer Z").append(", ");
        sb.append("GPS Longitude").append(", ");
        sb.append("GPS Lattitude").append(", ");
        sb.append("Microphone Amplitude").append(", ");
        sb.append("WiFi SSID").append(", ");
        sb.append("WiFi Strength");
        return sb.toString();
    }

}
