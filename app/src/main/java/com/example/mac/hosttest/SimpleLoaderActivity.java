package com.example.mac.hosttest;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.same.IFun;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class SimpleLoaderActivity extends AppCompatActivity {


    /**
     * 插件apk的存放目录
     */
    private String apkDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "plugin";


    /**
     * 插件apk的名称
     */
    private String apkName = "plugin.apk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_loader);
        Button btnloader = (Button) findViewById(R.id.btn_loader);
        Button btncopy = (Button) findViewById(R.id.btn_copy);
        Button btninterface = (Button) findViewById(R.id.btn_interface);
        Button btnreflect = (Button) findViewById(R.id.btn_reflect);


        btncopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一，将APK从assets拷贝到内部存储中
                copyApkFile(apkName);


            }
        });


        btnloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第二，获取未安装APK的包名
                String apkPackageName = getUninstallApkInfo(apkDir + File.separator + apkName);

            }
        });


        btnreflect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取DexClassLoader
                DexClassLoader dexClassLoader = getDexClassLoader();

                //通过反射的方式去加载
                byReflect(dexClassLoader);
            }
        });


        btninterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取BaseDexClassLoader
                DexClassLoader loader = getBaseDexClassLoader();
                //通过接口的方式去加载
                byInterface(loader);
            }
        });


    }

    /**
     * 通过接口的方式去加载
     *
     * @param loader
     */
    private void byInterface(DexClassLoader loader) {

        try {

            //1、加载Class
            Class clazz = loader.loadClass("com.same.FunTest");
            //2、创建Class实例
            Object object = clazz.newInstance();


            Log.i("TAG", "Ifun接口的类加载器 ：" + IFun.class.getClassLoader());
            Log.i("TAG", "FunTest的类加载器 : " + clazz.getClassLoader());


            //3、转换类型
            IFun iFun = (IFun) object;

            //4、调用方法
            String result = iFun.func();

            Log.i("TAG", "通过接口动态加载：" + result);
            Toast.makeText(SimpleLoaderActivity.this, "通过接口动态加载：" + result, Toast.LENGTH_SHORT).show();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }


    /**
     * 通过反射的方式去加载
     *
     * @param loader
     */
    private void byReflect(DexClassLoader loader) {

        try {
            //加载Class
            Class clazz = loader.loadClass("com.example.mac.plugintest.ReflectTest");
            //遍历类里所有方法
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Log.i("TAG", "方法：" + methods[i].toString());
            }


            /**
             * 第一、获取某个方法的实例的时候，第一个参数是方法名字，后面是参数类型
             * 第二、调用某个方法的时候，第一个参数是传入类的实例，后面是传入参数，然后最后获取返回值
             */
            //method = clazz.getMethod("reflect2", int.class, String.class);
            //method.invoke(clazz.newInstance(), 20, "张三");

            //通过方法名称获取方法
            Method fifth = clazz.getDeclaredMethod("fifth");
            //把方法设为public，在外部可以调用
            fifth.setAccessible(true);
            //调用方法
            String result = (String) fifth.invoke(clazz.newInstance());

            Log.i("TAG", "获取的结果是：" + result);

            Toast.makeText(SimpleLoaderActivity.this, "获取的结果是：" + result, Toast.LENGTH_SHORT).show();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取DexClassLoader对象
     *
     * @return
     */
    private DexClassLoader getDexClassLoader() {
        //在应用安装目录下创建一个名为app_dex文件夹目录,如果已经存在则不创建
        // /data/data/com.example.dynamicloadapk/app_dex
        File ooptimizedDirectoryFile = getDir("dex", Context.MODE_PRIVATE);

        Log.i("TAG", "这个路径是什么: " + ooptimizedDirectoryFile.toString());

        //构造DexClassLoader
        DexClassLoader loader = new DexClassLoader(apkDir + File.separator + apkName,
                ooptimizedDirectoryFile.getPath(), null, ClassLoader.getSystemClassLoader());


        return loader;
    }

    /**
     * 获取DexClassLoader
     *
     * @return
     */
    private DexClassLoader getBaseDexClassLoader() {
        File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "class.jar");// 外部路径
        File dexOutputDir = this.getDir("dex", 0);// 无法直接从外部路径加载.dex文件，需要指定APP内部路径作为缓存目录（.dex文件会被解压到此目录）
        DexClassLoader dexClassLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),dexOutputDir.getAbsolutePath(), null, getClassLoader());
        return dexClassLoader;
    }


    /**
     * 获取未安装应用的包名
     *
     * @param archiveFilePath
     * @return
     */
    private String getUninstallApkInfo(String archiveFilePath) {
        String apkPackName = null;
        PackageManager pm = this.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);

        if (packageInfo != null) {
            ApplicationInfo info = packageInfo.applicationInfo;
            apkPackName = info.packageName;
        }
        return apkPackName;
    }

    /**
     * 将APK从assets拷贝到内部存储中
     *
     * @param apkName
     */
    public void copyApkFile(String apkName) {
        //要将APK拷贝到的那个文件夹
        File file = new File(apkDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        //要将APK拷贝到的那个文件
        File apk = new File(apkDir + File.separator + apkName);

        if (apk.exists()) {
            apk.delete();
            Toast.makeText(SimpleLoaderActivity.this, "删除原有插件", Toast.LENGTH_SHORT).show();
        }

        try {
            //文件输出流，要将apk写入到这里
            FileOutputStream fos = new FileOutputStream(apk);
            InputStream is = getAssets().open(apkName);
            BufferedInputStream bis = new BufferedInputStream(is);

            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = bis.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
                fos.flush();
            }

            fos.close();
            bis.close();
            is.close();

            Toast.makeText(SimpleLoaderActivity.this, "拷贝成功！", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
