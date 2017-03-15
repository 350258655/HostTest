package com.example.mac.hosttest;

/**
 * Created by shake on 17-3-9.
 * 插件的实体类
 */
public class PluginBean {

    //插件的名称
    private String pluginName;

    //插件的包名
    private String packageName;

    public PluginBean() {
    }

    public PluginBean(String pluginName, String packageName) {
        this.pluginName = pluginName;
        this.packageName = packageName;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }




}
