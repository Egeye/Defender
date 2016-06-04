package com.egeye.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Octavio on 2016/2/9.
 * 应用程序信息的业务bean
 */
public class AppInfo {
    private Drawable icon;
    private String name;
    private String packageName;
    private boolean inRom;
    private boolean userApp;
    //流量统计
    private int uid;

    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", inRom=" + inRom +
                ", userApp=" + userApp +
                ", uid=" + uid +
                '}';
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

}
