package com.example.testapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MenuItem switch_lbl;

    private boolean diagnostic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diagnostic = false;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    public void click(View view) {

        //pobranie id klikniętego elementu
        int src_id = view.getId();

        if(src_id == R.id.button2)
        {
            Intent intent = new Intent(MainActivity.this,Window2.class);
            startActivity(intent);
            return;
        }
        if(src_id == R.id.button_first)
        {
            Intent intent = new Intent(MainActivity.this,Barometer.class);
            startActivity(intent);
        }
    }

    public void refresh()
    {
        if(diagnostic)
        {
            TextView diag_lbl = (TextView) findViewById(R.id.diagnostic_lbl);
            diag_lbl.setVisibility(View.VISIBLE);
            //getMenuInflater().inflate(R.menu.menu_main,main);
            //MenuItem switch_lbl = main.findItem(R.id.diagnostic);
            switch_lbl.setTitle(R.string.standard);
        }
        else
        {
            TextView diag_lbl = (TextView) findViewById(R.id.diagnostic_lbl);
            diag_lbl.setVisibility(View.INVISIBLE);
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
    public void switchDiagnostic()
    {
        diagnostic = !diagnostic;
    }
}