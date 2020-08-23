package com.example.testapplication;

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
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Vibrator;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import pl.droidsonroids.gif.GifImageView;



public class Window2 extends AppCompatActivity {

    private boolean diagnostic;

    private double value = 0.0;
    private float lastX, lastY, lastZ;

    long oldDate;

    public Vibrator crystal;

    TextView textLIGHT_reading;

    MenuItem switch_lbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diagnostic = false;
        value = 0.0;
        oldDate = System.currentTimeMillis();
        setContentView(R.layout.activity_window2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GifImageView giv = (GifImageView) findViewById(R.id.gif1);
        Uri imgUri = Uri.parse("android.resource://com.example.testapplication/drawable/staying_new");

        //Init Vibrator
        crystal = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        //SENSOR MANAGER
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Akcelerometr
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer !=null)
        {
            //accelerometer found
            sensorManager.registerListener(accelerationSensorListener, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
            float vibrateThreshold = accelerometer.getMaximumRange() / 2;
        }
        else
        {
            openDialog("Couldn't find accelerometer");
        }

        //Rotacja
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(rotationSensor !=null)
        {
            //rotationVector found
            sensorManager.registerListener(rotationSensorListener, rotationSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            openDialog("Couldn't find vector rotation sensor");
        }

        //wczytywanie sensora odpowiedzialnego o odczyt lumenów
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        textLIGHT_reading = (TextView) findViewById(R.id.light_lbl);
        if(lightSensor != null)
        {
            //light sensor found
            sensorManager.registerListener(lightSensorListener,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);

        }
        else
        {
            openDialog("Couldn't find light sensor");
        }
        //refresh();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch_lbl = menu.findItem(R.id.diagnostic);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.diagnostic) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private SensorEventListener accelerationSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                float deltaX = Math.abs(lastX - event.values[0]);
                float deltaY = Math.abs(lastY - event.values[1]);
                float deltaZ = Math.abs(lastZ - event.values[2]);

                if (deltaX < 2)
                    deltaX = 0;
                if (deltaY < 2)
                    deltaY = 0;
                if (deltaZ < 2)
                    deltaZ = 0;

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];

                TextView labelX = (TextView) findViewById(R.id.x_axis_val);
                labelX.setText(String.valueOf(deltaX));
                TextView labelY = (TextView) findViewById(R.id.y_axis_val);
                labelY.setText(String.valueOf(deltaY));
                TextView labelZ = (TextView) findViewById(R.id.z_axis_val);
                labelZ.setText(String.valueOf(deltaZ));

                TextView statusLabel = (TextView) findViewById(R.id.state_lbl);
                ;

                float tmp = Math.abs(deltaX) + Math.abs(deltaY) + Math.abs(deltaZ);

                if (tmp == 0) {
                    if (System.currentTimeMillis() - oldDate < 2000)
                        return;
                    statusLabel.setText(getString(R.string.standing));
                    GifImageView giv = (GifImageView) findViewById(R.id.gif1);
                    Uri imgUri = Uri.parse("android.resource://com.example.testapplication/drawable/staying_new");
                    giv.setImageURI(imgUri);
                }
//                else if (tmp > 0 && tmp < 5) {
//                    if (System.currentTimeMillis() - oldDate < 500)
//                        return;
//                    statusLabel.setText(getString(R.string.walking));
//                    GifImageView giv = (GifImageView) findViewById(R.id.gif1);
//                    Uri imgUri = Uri.parse("android.resource://com.example.testapplication/drawable/walking");
//                    giv.setImageURI(imgUri);
//                    oldDate = System.currentTimeMillis();
//                }
            else {
                    if (System.currentTimeMillis() - oldDate < 500)
                        return;
                    statusLabel.setText(getString(R.string.running));
                    GifImageView giv = (GifImageView) findViewById(R.id.gif1);
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

    private SensorEventListener lightSensorListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                textLIGHT_reading = (TextView) findViewById(R.id.light_lbl);
                textLIGHT_reading.setText("LIGHT: " + event.values[0]);
                Float value = event.values[0];
                if(value>255)
                    value=255.f;
                Button button = (Button) findViewById(R.id.button);
                View root = button.getRootView();
                root.setBackgroundColor(Color.rgb(150,150,Math.round(value)));
                        //Color.BLACK+Math.round(value)*500);
            }
        }

    };

    private SensorEventListener rotationSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            {
                TextView rotationXlbl = (TextView) findViewById(R.id.rotationX);
                TextView rotationYlbl = (TextView) findViewById(R.id.rotationY);
                TextView rotationZlbl = (TextView) findViewById(R.id.rotationZ);
                rotationXlbl.setText("X: "+ String.format("%.2f",event.values[0]));
                rotationYlbl.setText("Y: "+ String.format("%.2f",event.values[1]));
                rotationZlbl.setText("Z: "+ String.format("%.2f",event.values[2])); // wartosc 0 == północ
                GifImageView giv = (GifImageView) findViewById(R.id.gif1);
                giv.setRotation(event.values[2]*180 - 90);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    public void openDialog(String message)
    {
        AlertionDialog alertionDialog = new AlertionDialog(message);
        alertionDialog.show(getSupportFragmentManager(), "Alert");
    }

    public void click(View view) {

        int src_id = view.getId();
        if(src_id==R.id.imageView2)
        {
            Intent intent;
            intent = new Intent(Window2.this,MainActivity.class);
            startActivity(intent);
        }
        if(src_id==R.id.button)
        {
            value += 1.5;
            TextView label1 = (TextView) findViewById(R.id.x_axis_val);
            label1.setText(String.valueOf(value));

            crystal.vibrate(50);
        }
    }

    public void switchDiagnostic()
    {
        diagnostic = !diagnostic;
    }
    public void refresh()
    {
        if(diagnostic)
        {
            TextView diag_lbl = (TextView) findViewById(R.id.diagnostic_lbl3);
            diag_lbl.setVisibility(View.VISIBLE);
            TextView tmp = (TextView) findViewById(R.id.x_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.z_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.x_axis_val);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.y_axis_val);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.z_axis_val);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.light_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.vector_rotate_lbl);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.rotationX);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.rotationY);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.rotationZ);
            tmp.setVisibility(View.VISIBLE);
            tmp = (TextView) findViewById(R.id.state_lbl);
            tmp.setVisibility(View.VISIBLE);
            switch_lbl.setTitle(R.string.standard);
        }
        else
        {
            TextView diag_lbl = (TextView) findViewById(R.id.diagnostic_lbl3);
            diag_lbl.setVisibility(View.INVISIBLE);
            TextView tmp = (TextView) findViewById(R.id.x_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.z_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.x_axis_val);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.y_axis_val);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.z_axis_val);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.y_axis_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.light_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.vector_rotate_lbl);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.rotationX);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.rotationY);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.rotationZ);
            tmp.setVisibility(View.INVISIBLE);
            tmp = (TextView) findViewById(R.id.state_lbl);
            tmp.setVisibility(View.INVISIBLE);
            switch_lbl.setTitle(R.string.diagnostic);
        }

    }
    public void click(MenuItem item) {
        int src_id = item.getItemId();
        if(src_id==R.id.diagnostic)
        {

            switchDiagnostic();
            refresh();
        }
    }
}