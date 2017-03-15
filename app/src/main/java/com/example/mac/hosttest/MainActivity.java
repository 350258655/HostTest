package com.example.mac.hosttest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btndexclass = (Button) findViewById(R.id.btn_dexclass);
        Button btnpathclass = (Button) findViewById(R.id.btn_pathclass);

        btnpathclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PathClassLoaderActivity.class));
            }
        });

        btndexclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DexClassLoaderActivity.class));
            }
        });

    }
}