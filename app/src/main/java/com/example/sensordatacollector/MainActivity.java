package com.example.sensordatacollector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensordatacollector.models.AppDatabase;
import com.example.sensordatacollector.models.SensorDataModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected SensorEventListener sensorEventListener;
    protected SensorManager sensorManager;
    protected WifiManager wifiManager;
    protected SoundMeter soundMeter;
    private Handler mHandler;
    private boolean recordData = false;
    private Runnable pollTask;
    private Runnable saveData;

    TextView txtLat;
    TextView txtMic;
    TextView txtWifi;
    TextView txtAcc;

    Button recordToggle;
    Button exportData;
    Button viewData;
    Button delData;

    double longitude, lattitude;
    double ax, ay, az;
    String wifiAPName;
    int wifiStrength;
    double micAmplitude;

    private boolean permssionGranted = false;

    final static int PERMISSION_REQUEST = 1023;
    private String [] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST:
                permssionGranted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permssionGranted ) finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundMeter.stop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        soundMeter.start();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
        mHandler = new Handler();
        soundMeter = new SoundMeter();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "Permission is needed to continue.", Toast.LENGTH_LONG);
            finish();
        }
        else {
            soundMeter.start();
        }
        txtLat = (TextView) findViewById(R.id.textview1);
        txtMic = (TextView) findViewById(R.id.textview2);
        txtWifi = (TextView) findViewById(R.id.textview3);
        txtAcc = (TextView) findViewById(R.id.textview4);
        recordToggle = (Button) findViewById(R.id.recordButton);
        exportData = (Button) findViewById(R.id.exportButton);
        viewData = (Button) findViewById(R.id.viewDBButton);
        delData = (Button) findViewById(R.id.deleteButton);

        ax = ay = az = longitude = lattitude = 0;
        wifiAPName = "N/A";
        wifiStrength = -1;
        micAmplitude = -1;


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pollTask = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(pollTask, 50);
                micAmplitude = soundMeter.getAmplitude();
            }
        };

        saveData = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(saveData, 500);
                SensorDataModel sdm = new SensorDataModel(
                        ax, ay, az, longitude, lattitude, micAmplitude, wifiStrength, wifiAPName
                );
                AppDatabase.getInstance(getApplicationContext()).sensorDataDAO().addItem(sdm);
            }
        };

        mHandler.postDelayed(pollTask, 50);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                txtLat = (TextView) findViewById(R.id.textview1);
                longitude = location.getLongitude();
                lattitude = location.getLatitude();
                txtLat.setText("Latitude:" + String.format("%.2f", longitude) + ", Longitude:" + String.format("%.2f", lattitude));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("Latitude","status");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("Latitude","enable");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("Latitude","disable");
            }

        };

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                    ax=sensorEvent.values[0];
                    ay=sensorEvent.values[1];
                    az=sensorEvent.values[2];

                    txtAcc.setText(
                            "Acc X = " + String.format("%.2f", ax)
                                    + " Y = " + String.format("%.2f", ay)
                                    + " Z = " + String.format("%.2f", az)
                    );
                }
                WifiInfo inf = wifiManager.getConnectionInfo();
                wifiAPName = inf.getSSID();
                wifiStrength = inf.getRssi();
                txtWifi.setText("WiFi AP Name: " + wifiAPName + " Signal Strength: " + wifiStrength);
                txtMic.setText("Microphone Amplitude level: " + micAmplitude);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSION_REQUEST);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        recordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordData){
                    ((Button) view).setText("Record Data");
                    recordData = false;
                    mHandler.removeCallbacks(saveData);
                }
                else{
                    ((Button) view).setText("Stop Recording");
                    recordData = true;
                    mHandler.postDelayed(saveData, 1);
                }
            }
        });

        viewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), RecyclerActivity.class);
                startActivity(it);
            }
        });

        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(getApplicationContext(), "Write Permission Needed!", Toast.LENGTH_LONG);
                }
                else{
                    AsyncTask tsk = new ExportDatabaseCSVTask();
                    tsk.execute();
                }
            }
        });

        delData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask atsk = new DelDbTask();
                atsk.execute(getApplicationContext());
            }
        });
    }

    private class DelDbTask extends AsyncTask<Object, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Database Cleared!", Toast.LENGTH_LONG);
        }

        @Override
        protected Void doInBackground(Object... contexts) {
            Context ctx = (Context) contexts[0];
            AppDatabase.getInstance(ctx).sensorDataDAO().deleteTable();
            return null;
        }
    }

    public class ExportDatabaseCSVTask extends AsyncTask<Object, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private AppDatabase userDatabase;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
            userDatabase = AppDatabase.getInstance(getApplicationContext());
        }

        protected Boolean doInBackground(Object... args) {

            File file = new File(getApplicationContext().getExternalFilesDir(null), "sensordataexport.csv");
            try {
                file.createNewFile();
                FileWriter fwrite = new FileWriter(file);
                fwrite.write(SensorDataModel.get_csv_header());
                fwrite.write('\n');
                List<SensorDataModel> sdata = userDatabase.sensorDataDAO().getAllItems();
                for(int i=0; i<sdata.size(); i++){
                    fwrite.write(sdata.get(i).get_display_string());
                    fwrite.write('\n');
                }
                fwrite.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) { this.dialog.dismiss(); }
            if (success) {
                Toast.makeText(getApplicationContext(), "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Export failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
