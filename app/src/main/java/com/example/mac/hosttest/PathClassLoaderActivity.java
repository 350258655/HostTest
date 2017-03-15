package com.example.mac.hosttest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.PathClassLoader;

public class PathClassLoaderActivity extends AppCompatActivity {

    private ImageView imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_class_loader);

        imagepath = (ImageView) findViewById(R.id.image_path);
        Button btnstartpathclass = (Button) findViewById(R.id.btn_start_pathclass);


        btnstartpathclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //查找内存的插件,假如插件是有多个的，就用一个集合包住
                PluginBean plugBean = findAllPlugBean();

                try {
                    //去加载已经安装的APK
                    dynamicLoadApk(plugBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    /**
     * 加载已经安装的APK
     *
     * @param plugBean
     */
    private void dynamicLoadApk(PluginBean plugBean) throws Exception {
        //获取插件包名
        String pluginPackageName = plugBean.getPackageName();
        //获取插件的上下文,通过插件的上下文可以得到插件的Resource
        Context pluginContext = this.createPackageContext(pluginPackageName, CONTEXT_IGNORE_SECURITY | CONTEXT_INCLUDE_CODE);
        //创建PathClassLoader实例
        PathClassLoader pathClassLoader = new PathClassLoader(pluginContext.getPackageResourcePath(), ClassLoader.getSystemClassLoader());
        //参数：1、类的全名，2、是否初始化类，3、加载时使用的类加载器
        Class clazz = Class.forName(pluginPackageName + ".R$mipmap", true, pathClassLoader);
        //我们得到R类中的内部类mipmap，通过它得到对应的图片id，进而给我们使用
        Field field = clazz.getDeclaredField("one");
        //获取图片对应的ID
        int resourceId = field.getInt(R.mipmap.class);
        //根据插件的Context创建一个Drawable对象
        Drawable drawable = pluginContext.getResources().getDrawable(resourceId);

        if (drawable != null) {
            imagepath.setImageDrawable(drawable);
            Toast.makeText(PathClassLoaderActivity.this, "成功加载了插件APK", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 查找手机内存中的所有插件
     *
     * @return
     */
    private PluginBean findAllPlugBean() {
        PluginBean pluginBean = new PluginBean();
        PackageManager pm = getPackageManager();

        //通过包管理器查找所有已经安装的APK文件
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (PackageInfo info : packageInfos) {
            //获取当前扫描的apk包名
            String pgkName = info.packageName;
            //获取当前扫描的apk的shareUserId
            String shareUserId = info.sharedUserId;
            //判断这个apk是否是我们应用程序的插件
            if (shareUserId != null && shareUserId.equals("com.shake.test") && !pgkName.equals(this.getPackageName())) {
                //得到插件apk的名称
                String pluginName = pm.getApplicationLabel(info.applicationInfo).toString();
                pluginBean.setPackageName(pgkName);
                pluginBean.setPluginName(pluginName);
            }

        }
        return pluginBean;
    }


}
