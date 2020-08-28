package com.example.testapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
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

    public void click(View view) {

        //pobranie id klikniętego elementu
        int src_id = view.getId();

        if(src_id == R.id.button2) {
            Intent intent = new Intent(MainActivity.this, PageOne.class);
            startActivity(intent);
            return;
        }
        if(src_id == R.id.button_first) {
            Intent intent = new Intent(MainActivity.this, PageTwo.class);
            startActivity(intent);
        }
    }

    public void refresh()
    {
        TextView diag_lbl = findViewById(R.id.diagnostic);
        if(diagnostic) {
            diag_lbl.setVisibility(View.VISIBLE);
            switch_lbl.setTitle(R.string.standard);
        }
        else {
            diag_lbl.setVisibility(View.INVISIBLE);
            switch_lbl.setTitle(R.string.diagnostic);
        }
    }

    public void click(MenuItem item) {
        int src_id = item.getItemId();
        if(src_id==R.id.diagnostic) {
            switchDiagnostic();
            refresh();
        }
    }

    public void switchDiagnostic() {
        diagnostic = !diagnostic;
    }
}