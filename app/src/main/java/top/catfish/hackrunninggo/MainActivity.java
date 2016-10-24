package top.catfish.hackrunninggo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
    AlertDialog dialog = null;
    LinearLayoutManager mLayoutManager = null;
    RouteAdapter routeAdapter = null;
    TextView routeNameTextView = null;
    PathPainter painter = null;
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
        routeNameTextView = (TextView)this.findViewById(R.id.location_name_text);
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
        painter = new PathPainter(mBaiduMap, MainActivity.this);

        Button btn = (Button) findViewById(R.id.startBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Start!",Toast.LENGTH_SHORT);
            }
        });

        getData();
        //Dialog
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_view, (ViewGroup)findViewById(R.id.popup_window_layout));
        RecyclerView mView = (RecyclerView)layout.findViewById(R.id.pupup_recyclerView);
        mView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mView.setLayoutManager(mLayoutManager);
        routeAdapter = new RouteAdapter(MainActivity.this,lists);
        routeAdapter.setOnItemClickListener(new RouteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                //Log.i("view type",String.valueOf(view.getId()));


                int pos = Integer.valueOf((String)view.getTag());
                Snackbar.make(MainActivity.this.getCurrentFocus(), "Count:"+mLayoutManager.getItemCount()+" pos:"+view.getTag(), Snackbar.LENGTH_SHORT).show();
                View tView = mLayoutManager.findViewByPosition(pos);
                for(int i=0;i<mLayoutManager.getChildCount();i++){
                    RouteAdapter.ViewHolder tViewHolder = new RouteAdapter.ViewHolder(mLayoutManager.getChildAt(i));
                    tViewHolder.image.setVisibility(View.GONE);
                }
                RouteAdapter.ViewHolder viewHolder = new RouteAdapter.ViewHolder(tView);
                if(pos == routeAdapter.selectPos){
                    routeAdapter.selectPos = -1;
                    mBaiduMap.clear();
                    routeNameTextView.setText("");
                    setTextView(R.id.distanceText, "0M");
                    setTextView(R.id.durationText, "0min");
                    setTextView(R.id.speedText, "0KM/H");
                }else{
                    viewHolder.image.setVisibility(View.VISIBLE);
                    routeAdapter.selectPos = pos;
                    Route route = lists.get(pos);
                    painter.drawPath(route);
                    fitMapStatus(route);
                    routeNameTextView.setText(route.getName());
                }
                dialog.dismiss();

            }
        });
        mView.setAdapter(routeAdapter);
        AlertDialog.Builder  builder= new AlertDialog.Builder(MainActivity.this);
        builder.setView(layout);
        dialog = builder.create();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_path) {
            dialog.show();
            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
            android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
            Point a = new Point();
            d.getSize(a);
            p.height = (int) (a.x * 0.7);   //高度设置为屏幕的0.3
            p.width = (int) (a.y * 0.5);    //宽度设置为屏幕的0.5
            p.gravity = Gravity.CENTER_HORIZONTAL;
            dialog.getWindow().setAttributes(p);     //设置生效

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
    public void fitMapStatus(Route route){
        List<LatLng> list = route.getList();
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
        InputStream is = getResources().openRawResource(R.raw.route);
        try {
            InputStreamReader isr = new InputStreamReader(is,"utf8");
            BufferedReader br = new BufferedReader(isr);
            int count = Integer.parseInt(br.readLine());
            for(int i=0;i<count;i++){
                int n = Integer.valueOf(br.readLine());
                String name  = br.readLine();
                List<LatLng> list = new ArrayList<>();
                for(int j=0;j<n;j++) {
                    String data = br.readLine();
                    String[] datas = data.split(" ");
                    LatLng latLng = new LatLng(Double.valueOf(datas[1]), Double.valueOf(datas[0]));
                    list.add(latLng);
                }
                Route route = new Route(name,list);
                lists.add(route);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

