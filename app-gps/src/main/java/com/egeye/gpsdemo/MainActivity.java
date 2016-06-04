package com.egeye.gpsdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

public class MainActivity extends Activity {

    //定位服务
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //给位置提供器设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //注册监听位置服务
        String provider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(provider, 0, 0, myLocationListener);//gps 一般1分钟更新一次即 60000,距离50米

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //取消监听
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(myLocationListener);
    }


    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            String longitude = "经度：" + location.getLongitude();
            String latitude = "纬度：" + location.getLatitude();
            String accuracy = "精确度：" + location.getAccuracy();

            TextView textView = new TextView(MainActivity.this);
            textView.setText(longitude + "\n" + latitude + "\n" + accuracy);

            setContentView(textView);

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
