package com.example.testapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;


public class PageOne extends AppCompatActivity {

    private boolean diagnostic;
    private float lastX, lastY, lastZ;
    long oldDate;

    TextView textLIGHT_reading;
    MenuItem switch_lbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diagnostic = false;
        oldDate = System.currentTimeMillis();
        setContentView(R.layout.activity_page_one);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SENSOR MANAGER
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Akcelerometr
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(accelerationSensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            openDialog("Couldn't find accelerometer");
        }

        //Rotacja
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationSensor != null) {
            //rotationVector found
            sensorManager.registerListener(rotationSensorListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            openDialog("Couldn't find vector rotation sensor");
        }

        //wczytywanie sensora odpowiedzialnego o odczyt lumenów
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        textLIGHT_reading = findViewById(R.id.light_lbl);
        if (lightSensor != null) {
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            openDialog("Couldn't find light sensor");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch_lbl = menu.findItem(R.id.diagnostic);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.diagnostic) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private SensorEventListener accelerationSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                float deltaX = Math.abs(lastX - event.values[0]);
                float deltaY = Math.abs(lastY - event.values[1]);
                float deltaZ = Math.abs(lastZ - event.values[2]);

                TextView labelX = findViewById(R.id.x_axis_val);
                labelX.setText(String.format("%.3f",deltaX));
                TextView labelY = findViewById(R.id.y_axis_val);
                labelY.setText(String.format("%.3f",deltaY));
                TextView labelZ = findViewById(R.id.z_axis_val);
                labelZ.setText(String.format("%.3f",deltaZ));

                if (deltaX < 2)
                    deltaX = 0;
                if (deltaY < 2)
                    deltaY = 0;
                if (deltaZ < 2)
                    deltaZ = 0;

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];

                TextView statusLabel = findViewById(R.id.state_lbl);
                float tmp = Math.abs(deltaX) + Math.abs(deltaY) + Math.abs(deltaZ);

                if (tmp == 0) {
                    if (System.currentTimeMillis() - oldDate < 1000) {
                        return;
                    }
                    statusLabel.setText(getString(R.string.standing));
                    GifImageView giv = findViewById(R.id.gif1);
                    Uri imgUri = Uri.parse("android.resource://com.example.testapplication/drawable/staying_new");
                    giv.setImageURI(imgUri);
                } else {
                    if (System.currentTimeMillis() - oldDate < 500) {
                        return;
                    }
                    statusLabel.setText(getString(R.string.running));
                    GifImageView giv = findViewById(R.id.gif1);
                    Uri imgUri = Uri.parse("android.resource://com.example.testapplication/drawable/moving_new");
                    giv.setImageURI(imgUri);
                }
                oldDate = System.currentTimeMillis();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    private SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                textLIGHT_reading = findViewById(R.id.light_lbl);
                textLIGHT_reading.setText("LIGHT: " + event.values[0]);
                float value = event.values[0] / 2;
                if (value > 255) {
                    value = 255.f;
                }
                Button button = findViewById(R.id.button);
                View root = button.getRootView();
                root.setBackgroundColor(Color.rgb(140, 200, Math.round(value)));
            }
        }
    };

    private SensorEventListener rotationSensorListener = new SensorEventListener() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                TextView rotationXlbl = findViewById(R.id.rotationX);
                TextView rotationYlbl = findViewById(R.id.rotationY);
                TextView rotationZlbl = findViewById(R.id.rotationZ);
                rotationXlbl.setText("X: " + String.format("%.2f", event.values[0]));
                rotationYlbl.setText("Y: " + String.format("%.2f", event.values[1]));
                rotationZlbl.setText("Z: " + String.format("%.2f", event.values[2]));
                GifImageView giv = findViewById(R.id.gif1);
                giv.setRotation(event.values[2] * 180 - 90);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    public void openDialog(String message) {
        AlertionDialog alertionDialog = new AlertionDialog(message);
        alertionDialog.show(getSupportFragmentManager(), "Alert");
    }

    public void click(View view) {
        int src_id = view.getId();
        if (src_id == R.id.imageView2) {
            Intent intent;
            intent = new Intent(PageOne.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void switchDiagnostic() {
        diagnostic = !diagnostic;
    }

    public void refresh() {
        TextView diag_lbl = findViewById(R.id.diagnostic_lbl3);
        if (diagnostic) {
            diag_lbl.setVisibility(View.VISIBLE);
            TextView tmp = findViewById(R.id.x_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.z_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.x_axis_val);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.y_axis_val);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.z_axis_val);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.light_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.vector_rotate_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.rotationX);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.rotationY);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.rotationZ);
            tmp.setVisibility(View.VISIBLE);
            tmp = findViewById(R.id.state_lbl);
            tmp.setVisibility(View.VISIBLE);
            switch_lbl.setTitle(R.string.standard);
        } else {
            diag_lbl.setVisibility(View.INVISIBLE);
            TextView tmp = findViewById(R.id.x_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.z_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.x_axis_val);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.y_axis_val);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.z_axis_val);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.light_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.vector_rotate_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.rotationX);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.rotationY);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.rotationZ);
            tmp.setVisibility(View.INVISIBLE);
            tmp = findViewById(R.id.state_lbl);
            tmp.setVisibility(View.INVISIBLE);
            switch_lbl.setTitle(R.string.diagnostic);
        }
    }

    public void click(MenuItem item) {
        int src_id = item.getItemId();
        if (src_id == R.id.diagnostic) {
            switchDiagnostic();
            refresh();
        }
    }
}