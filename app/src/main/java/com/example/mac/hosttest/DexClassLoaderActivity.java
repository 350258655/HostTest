package com.example.mac.hosttest;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class DexClassLoaderActivity extends AppCompatActivity {

    /**
     * 插件apk的存放目录
     */
    private String apkDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "plugin";


    /**
     * 插件apk的名称
     */
    private String apkName = "plugin.apk";

    ImageView imagedex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dex_class_loader);
        imagedex = (ImageView) findViewById(R.id.image_dex);
        Button btnstartdexclass = (Button) findViewById(R.id.btn_start_dexclass);
        Button btnstartcopy = (Button) findViewById(R.id.btn_start_copy);


        btnstartcopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一步先把apk拷贝到sd卡中
                copyApkFile(apkName);
                Toast.makeText(DexClassLoaderActivity.this, "拷贝成功", Toast.LENGTH_SHORT).show();
            }
        });

        btnstartdexclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //获取未安装的apk的信息，其实只要获取未安装的应用的包名就好
                    String apkPackageName = getUninstallApkInfo(apkDir + File.separator + apkName);
                    //加载apk内部资源
                    dynamicLoadApk(apkPackageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 加载apk内部资源
     *
     *
     * @param apkPackageName
     */
    private void dynamicLoadApk(String apkPackageName) throws Exception {
        //在应用安装目录下创建一个名为app_dex文件夹目录,如果已经存在则不创建
        // /data/data/com.example.dynamicloadapk/app_dex
        File ooptimizedDirectoryFile = getDir("dex", Context.MODE_PRIVATE);

        Log.i("TAG", "插件包名是什么: "+apkPackageName);
        Log.i("TAG", "这个路径是什么: "+ooptimizedDirectoryFile.toString());

        //参数：1、包含dex的apk文件或jar文件的路径，2、apk、jar解压缩生成dex存储的目录，
        // 3、本地library库目录，一般为null，4、父ClassLoader
        DexClassLoader loader = new DexClassLoader(apkDir + File.separator + apkName,
                ooptimizedDirectoryFile.getPath(), null, ClassLoader.getSystemClassLoader());

        //通过使用apk自己的类加载器，反射出R类中相应的内部类进而获取我们需要的资源id
        Class<?> clazz = loader.loadClass(apkPackageName + ".R$mipmap");
        //得到名为one的这张图片字段
        Field field = clazz.getDeclaredField("one");
        //得到图片id
        int resId = field.getInt(R.id.class);
        Log.i("TAG", "得到图片的ID是什么: "+resId);

        //获取插件的Resource对象
        Resources resource = getPluginResource();

        //构造Drawable对象
        Drawable drawable = resource.getDrawable(resId);

        imagedex.setImageDrawable(drawable);

    }

    /**
     * 得到对应插件的Resource对象
     *
     * @return
     */
    private Resources getPluginResource() {

        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            //反射调用方法addAssetPath(String path)
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            //第二个参数是apk的路径：Environment.getExternalStorageDirectory().getPath()+File.separator+"plugin"+File.separator+"apkplugin.apk"
            //将未安装的Apk文件的添加进AssetManager中，第二个参数为apk文件的路径带apk名
            addAssetPath.invoke(assetManager, apkDir + File.separator + apkName);

            Resources superRes = this.getResources();

            Resources mResource = new Resources(assetManager, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());

            return mResource;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 获取未安装apk的包名
     *
     * @param archiveFilePath
     * @return
     */
    private String getUninstallApkInfo(String archiveFilePath) {

        String apkPackageName= null;
        //获取PackageManager
        PackageManager pm = this.getPackageManager();
        //获取PackageInfo
        PackageInfo packageInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);

        if (packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            apkPackageName= appInfo.packageName;
        }
        return apkPackageName;
    }

    /**
     * 拷贝apk文件至SD卡plugin目录下
     *
     * @param apkName
     */
    private void copyApkFile(String apkName) {

        File file = new File(apkDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        File apk = new File(apkDir + File.separator + apkName);

        try {
            if (apk.exists()) {
                return;
            }
            FileOutputStream fos = new FileOutputStream(apk);
            InputStream is = getResources().getAssets().open(apkName);
            BufferedInputStream bis = new BufferedInputStream(is);

            int len = -1;
            byte[] by = new byte[1024];
            while ((len = bis.read(by)) != -1) {
                fos.write(by, 0, len);
                fos.flush();
            }

            fos.close();
            is.close();
            bis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
