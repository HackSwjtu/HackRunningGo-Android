package top.catfish.hackrunninggo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
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
import android.widget.ImageView;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

import top.catfish.hackrunninggo.Utils.PathPainter;
import top.catfish.hackrunninggo.Utils.SerializableMap;
import top.catfish.hackrunninggo.Utils.Util;
import top.catfish.hackrunninggo.adapter.RouteAdapter;
import top.catfish.hackrunninggo.dao.Route;
import top.catfish.hackrunninggo.dao.User;
import top.catfish.hackrunninggo.manager.ImageManager;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    String username = null;
    String password = null;
    String deviceID = null;
    List<Route> lists = null;
    AlertDialog dialog = null;
    LinearLayoutManager mLayoutManager = null;
    RouteAdapter routeAdapter = null;
    PathPainter painter = null;
    TextView usernameTextView,nameTextView,departTextView;
    ImageView iconImageView;
    boolean isLogin;
    Map<String,String> userData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        SerializableMap smap = (SerializableMap) bundle.getSerializable("data");
        userData = smap.getMap();
        isLogin = true;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.spLoginData, Context.MODE_PRIVATE);

        //BaiduMap
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        username = sharedPreferences.getString(Util.spLoginUsername,
                "Name");
        password = sharedPreferences.getString(Util.spLoginPassword,"Pass");
        deviceID = sharedPreferences.getString(Util.spDeviceID,"None");
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
        View headerView = navigationView.getHeaderView(0);
        //header widgets
        usernameTextView = (TextView)headerView.findViewById(R.id.username_text);
        usernameTextView.setText(username);
        nameTextView  = (TextView)headerView.findViewById(R.id.name_text);
        nameTextView.setText(userData.get("name"));
        departTextView = (TextView)headerView.findViewById(R.id.depart_text);
        departTextView.setText(userData.get("depart"));
        iconImageView = (ImageView)headerView.findViewById(R.id.icon_image);
        ImageManager imageManager = new ImageManager(MainActivity.this);
        Log.i("userData",userData.get("uid"));
        Log.i("userData",userData.get("icon"));
        UpdateProfileUIAction updateProfileUITask = new UpdateProfileUIAction(userData.get("uid"),userData.get("icon"),imageManager);
        updateProfileUITask.execute((Void)null);
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
                mBaiduMap.clear();
                setTextView(R.id.location_name_text,"");
                setTextView(R.id.distanceText, "0M");
                setTextView(R.id.durationText, "0min");
                setTextView(R.id.speedText, "0KM/H");
                double lat = 30.770331, lon = 103.992012;
                LatLng p = new LatLng(lat, lon);
                MapStatus mMapStatus = new MapStatus.Builder().target(p).zoom(17)
                        .build();
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                        .newMapStatus(mMapStatus);
                mBaiduMap.animateMapStatus(mMapStatusUpdate);
                Snackbar.make(getWindow().getDecorView(), "Clear!", Snackbar.LENGTH_SHORT).show();
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
                int pos = Integer.valueOf((String)view.getTag());

                View tView = mLayoutManager.findViewByPosition(pos);
                for(int i=0;i<mLayoutManager.getChildCount();i++){
                    RouteAdapter.ViewHolder tViewHolder = new RouteAdapter.ViewHolder(mLayoutManager.getChildAt(i));
                    tViewHolder.image.setVisibility(View.GONE);
                }
                RouteAdapter.ViewHolder viewHolder = new RouteAdapter.ViewHolder(tView);
                String msg;
                if(pos == routeAdapter.selectPos){
                    routeAdapter.selectPos = -1;
                    mBaiduMap.clear();
                    //setTextView(R.id.location_name_text,"");
                    setTextView(R.id.distanceText, "0M");
                    setTextView(R.id.durationText, "0min");
                    setTextView(R.id.speedText, "0KM/H");
                    Route route = lists.get(pos);
                    msg = "Deselect "+route.getName();
                }else{
                    viewHolder.image.setVisibility(View.VISIBLE);
                    routeAdapter.selectPos = pos;
                    Route route = lists.get(pos);
                    painter.drawPath(route);
                    fitMapStatus(route);
                    //setTextView(R.id.location_name_text,route.getName());
                    msg = "Select "+route.getName();
                }
                Snackbar.make(getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
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
            LogoutAction logoutActionTask = new LogoutAction(username,password,deviceID);
            logoutActionTask.execute((Void)null);
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
        isLogin = false;
        LogoutAction logoutActionTask = new LogoutAction(username,password,deviceID);
        logoutActionTask.execute((Void)null);
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
    @Override
    protected void onStop() {
        isLogin = false;
        LogoutAction logoutActionTask = new LogoutAction(username,password,deviceID);
        logoutActionTask.execute((Void)null);
        super.onStop();
    }
    @Override
    protected void onRestart() {
        LoginAction loginActionTask = new LoginAction(username,password,deviceID);
        loginActionTask.execute((Void)null);
        super.onRestart();
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
    public class LogoutAction extends AsyncTask<Void,Void,Map<String,String>>{
        private String mUsername;
        private String mPassword;
        private String mDeviceID;
        public LogoutAction(String mUsername,String mPassword,String mDeviceID){
            this.mUsername = mUsername;
            this.mPassword = mPassword;
            this.mDeviceID = mDeviceID;
        }
        @Override
        protected Map<String, String> doInBackground(Void... params) {
            User user = new User(mUsername,mPassword,mDeviceID);
            return user.logout();
        }
        @Override
        protected void onPostExecute(Map<String,String> result) {

        }
    }
    public class LoginAction extends AsyncTask<Void,Void,Map<String,String>>{
        private String mUsername;
        private String mPassword;
        private String mDeviceID;
        public LoginAction(String mUsername,String mPassword,String mDeviceID){
            this.mUsername = mUsername;
            this.mPassword = mPassword;
            this.mDeviceID = mDeviceID;
        }
        @Override
        protected Map<String, String> doInBackground(Void... params) {
            User user = new User(mUsername,mPassword,mDeviceID);
            return user.login(null);
        }
        @Override
        protected void onPostExecute(Map<String,String> result) {
            if(result.get("state").compareTo(Util.stateSuccess)==0) {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences(Util.spLoginData, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Util.spLoginUsername, mUsername);
                editor.putString(Util.spLoginPassword, mPassword);
                editor.putLong(Util.spLoginStamp, new Date().getTime());
                editor.putString(Util.spDeviceID, deviceID);
                editor.putString(Util.spUID, result.get("uid"));
                boolean flag = editor.commit();
            }
        }
    }
    public class UpdateProfileUIAction extends AsyncTask<Void,Void,Bitmap>{
        private String uid;
        private String iconUrl;
        private ImageManager imageManager;
        public UpdateProfileUIAction(String uid,String iconUrl,ImageManager imageManager){
            this.uid = uid;
            this.iconUrl = iconUrl;
            this.imageManager = imageManager;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {

            return  imageManager.getImage(Util.MD532(userData.get("uid")),userData.get("icon"));
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("ImageManager",String.valueOf(bitmap.getByteCount()));
            Log.i("ImageManager",String.valueOf(bitmap.getAllocationByteCount()));
            Log.i("ImageManager",String.valueOf(bitmap.getHeight()+" - "+bitmap.getWidth()));
            if(null != bitmap)
                iconImageView.setImageBitmap(bitmap);
        }
    }
}

