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
        Button btnsimpleloader = (Button) findViewById(R.id.btn_simpleloader);
        Button btntestloader = (Button) findViewById(R.id.btn_testloader);
        Button btndexclass = (Button) findViewById(R.id.btn_dexclass);
        Button btnpathclass = (Button) findViewById(R.id.btn_pathclass);

        /**
         * PathClassLoader
         */
        btnpathclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PathClassLoaderActivity.class));
            }
        });

        /**
         * DexClassLoader
         */
        btndexclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DexClassLoaderActivity.class));
            }
        });


        btntestloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestClassLoaderActivity.class));
            }
        });


        btnsimpleloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SimpleLoaderActivity.class));
            }
        });


    }
}
