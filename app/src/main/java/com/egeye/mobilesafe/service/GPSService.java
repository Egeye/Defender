package com.egeye.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import java.io.InputStream;

/**
 * Created by Octavio on 2016/2/4.
 */
public class GPSService extends Service {

    //定位服务
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;


    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*
         *获取所有可以用的位置提供器
        List<String> provider = locationManager.getAllProviders();
        for (String l : provider) {
            System.out.println(l);
        }
        //模拟器下有这两个：passive gps
        */

        myLocationListener = new MyLocationListener();


        //给位置提供器设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //注册监听位置服务
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, myLocationListener);//gps 一般1分钟更新一次即 60000,距离50米

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(myLocationListener);
        myLocationListener = null;
    }


    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            String longitude = "longitudeJ:" + location.getLongitude() + "\n";
            String latitude = "latitudeW:" + location.getLatitude() + "\n";
            String accuracy = "accuracy:" + location.getAccuracy();

//            String newlongitude = null;
//            String newlatitude = null;
            //修正坐标偏移
            /*
            try {
                InputStream is = getAssets().open("axisoffset.dat");//返回一个输入流
                ModifyOffset offset = ModifyOffset.getInstance(is);
                PointDouble pointDouble = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));

                newlongitude = "longitudeJ:" + offset.X + "\n";
                newlatitude = "latitudeW:" + offset.Y + "\n";

            } catch (Exception e) {
                e.printStackTrace();
            }
            */

            //发短信给安全号码,避免频繁发短信
            SharedPreferences sp = getSharedPreferences("MDConfig", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            String lastLocation = longitude + latitude + accuracy;

            editor.putString("prevent_location", lastLocation);
            editor.commit();


            //ditu.google.cn
            //String altitude = "高度(海拔)："+location.getAltitude();

        }

        //当手机设置允许获取地理位置中，无论设置开启还是关闭都会回调这个方法
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        //某一个位置提供者可使用就回调它
        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
