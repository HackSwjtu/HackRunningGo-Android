package top.catfish.hackrunninggo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import top.catfish.hackrunninggo.Utils.PathPainter;
import top.catfish.hackrunninggo.Utils.Util;
import top.catfish.hackrunninggo.adapter.RouteAdapter;
import top.catfish.hackrunninggo.dao.Route;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    String username = null;
    List<Route> lists = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.spLoginData, Context.MODE_PRIVATE);

        //BaiduMap
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        username = sharedPreferences.getString(Util.spLoginUsername,
                "Name");
        TextView usernameTextView = (TextView)findViewById(R.id.username_text);
        usernameTextView.setText(username);
        //SlideBar Init
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //BaiduMap init
        mMapView = (MapView) findViewById(R.id.bMapView);
        mBaiduMap = mMapView.getMap();
        double lat = 30.770331, lon = 103.992012;
        LatLng p = new LatLng(lat, lon);
        MapStatus mMapStatus = new MapStatus.Builder().target(p).zoom(17)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);


        Button btn = (Button) findViewById(R.id.startBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<LatLng> list = new ArrayList<>();
                list.add(new LatLng(30.771025, 103.985729));
                list.add(new LatLng(30.768689, 103.987363));
                list.add(new LatLng(30.772452, 103.988140));
                list.add(new LatLng(30.768565, 103.989981));
                list.add(new LatLng(30.765339, 103.990071));
                fitMapStatus(list);
                PathPainter pathPainter = new PathPainter(mBaiduMap, MainActivity.this);
                pathPainter.drawPath(list);
                //Toast.makeText(getApplicationContext(), "Search!", Toast.LENGTH_LONG).show();

            }
        });

        getData();
        Log.e("list",lists.toString());
    }
    public BaiduMap getBaiduMap() {
        return this.mBaiduMap;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_path) {

            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_view, (ViewGroup)findViewById(R.id.popup_window_layout));
            RecyclerView mView = (RecyclerView)layout.findViewById(R.id.pupup_recyclerView);
            mView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            mView.setLayoutManager(mLayoutManager);
            mView.setAdapter(new RouteAdapter(MainActivity.this,lists));
            AlertDialog.Builder  builder= new AlertDialog.Builder(MainActivity.this);
            builder.setView(layout);
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.spLoginData, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(Util.spLoginStamp);
            boolean flag = editor.commit();
            Intent intent = new Intent();
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_exit) {
            exitApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    public void setTextView(int id, String msg) {
        TextView tv = (TextView) this.findViewById(id);
        tv.setText(msg);
    }
    public void fitMapStatus(List<LatLng> list){
        double avgLat=0,avgLon=0,maxDis = Double.MIN_VALUE;
        for(int i=0;i<list.size();i++){
            avgLat += list.get(i).latitude;
            avgLon += list.get(i).longitude;
        }
        avgLat/=list.size();
        avgLon/=list.size();
        for(int i=0;i<list.size();i++){
            for(int j=i+1;j<list.size();j++){
                double dis = getDistance(list.get(i),list.get(j));
                if(dis>maxDis)
                    maxDis = dis;
            }
        }
        int disData[]={2000000,1000000,500000,200000,100000,50000,25000,20000,10000,5000,2000,1000,500,200,100,50,20,10};
        int status = 3;
        while(disData[status]>maxDis){
            status++;
            if(status>15) {
                status = 15;
                break;
            }
        }
        LatLng p = new LatLng(avgLat,avgLon);
        MapStatus mMapStatus = new MapStatus.Builder().target(p).zoom(status+5)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
        Log.e("distance",String.valueOf(maxDis));
    }
    private double getDistance(LatLng p1,LatLng p2) {
        double radLat1 = Math.toRadians (p1.latitude);
        double radLat2 = Math.toRadians (p2.latitude);
        double a = radLat1 - radLat2;
        double b = Math.toRadians (p1.longitude) - Math.toRadians (p2.longitude);
        double s = 2 * Math. asin(Math .sqrt(
                Math.pow (Math. sin(a / 2), 2) + Math.cos (radLat1) * Math.cos (radLat2) * Math.pow (Math. sin(b / 2), 2))) ;
        s = s * 6378137.0 ;// 取WGS84标准参考椭球中的地球长半径(单位:m)
        s = Math. round(s * 10000) / 10000 ;
        return s ;
    }


    public void getData() {
        lists = new ArrayList<>();
        for(int i=0;i<30;i++){
            Route route = new Route("Hello "+i);
            lists.add(route);
        }
    }
}

