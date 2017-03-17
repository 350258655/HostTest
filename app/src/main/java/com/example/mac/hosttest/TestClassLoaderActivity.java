package com.example.mac.hosttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TestClassLoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_class_loader);

        ClassLoader classLoader = getClassLoader();

        if(classLoader != null){
            Log.i("TAG", "当前的ClassLoader : "+classLoader.toString());

            while (classLoader.getParent() != null){
                //指向父类
                classLoader = classLoader.getParent();
                Log.i("TAG", "父类的ClassLoader : "+classLoader.toString());
            }

        }


    }
}
