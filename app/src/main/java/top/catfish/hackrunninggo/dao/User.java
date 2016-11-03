package top.catfish.hackrunninggo.dao;

/*
 * Created by Catfish on 2016/10/31.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.channels.NetworkChannel;
import java.util.HashMap;
import java.util.Map;

import top.catfish.hackrunninggo.Utils.NetworkUtil;
import top.catfish.hackrunninggo.Utils.Util;

public class User {
    public static int LOGIN  = 1;
    public static int LOGOUT = 2;
    public static int UPDATE = 3;
    public static String loginURL = "http://gxapp.iydsj.com/api/v3/login";
    public static String logoutURL = "http://gxapp.iydsj.com/api/v2/user/logout";
    public static String runDataUpdateURL = "";
    private String username,password,deviceID,uid = null;
    private NetworkUtil network;
    private int logState;
    public User(String username,String password,String deviceID){
        this.username = username;
        this.password = password;
        this.deviceID = deviceID;
        this.logState  = LOGOUT;
    }
    public Map<String,String> login(String uid){
        Map<String,String> result = new HashMap<>();
        result.put("state",Util.stateError);
        result.put("msg","网络错误");
        Map<String,String> header = getHeader();
        Map<String,String> response = NetworkUtil.sendPost(loginURL,header,null);
        if(response.get("state")==String.valueOf(NetworkUtil.HTTP_OK)) {
            try {
                JSONTokener jsonTokener = new JSONTokener(response.get("result"));
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                int errorCode = (int)jsonObject.get("error");
                if(errorCode == 10000){
                    //登录成功，记录相关信息
                    result.put("state",Util.stateSuccess);
                    JSONObject dataObject = (JSONObject)jsonObject.get("data");
                    result.put("msg",jsonObject.get("message").toString());
                    this.uid = String.valueOf((int)dataObject.get("uid"));
                    result.put("uid",this.uid);
                    result.put("unid",String.valueOf((int)dataObject.get("unid")));
                    result.put("name",dataObject.get("name").toString());
                    Log.i("JSON",dataObject.get("icon").toString());
                    if(dataObject.get("icon").toString() != "null");
                        result.put("icon",dataObject.get("icon").toString());
                    result.put("campusId",dataObject.get("campusId").toString());
                    result.put("depart",dataObject.get("depart").toString());
                }else{
                    //登陆失败，记录错误信息
                    result.put("msg",(String)jsonObject.get("message"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }
    public Map<String,String> logout(){
        Map<String,String> result = new HashMap<>();
        result.put("state",Util.stateError);
        result.put("msg","网络错误");
        Map<String,String> header = getHeader();
        Map<String,String> response = NetworkUtil.sendPost(logoutURL,header,null);
        if(response.get("state")==String.valueOf(NetworkUtil.HTTP_OK)) {
            try {
                JSONTokener jsonTokener = new JSONTokener(response.get("result"));
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                int errorCode = (int)jsonObject.get("error");
                if(errorCode == 10000){
                    //登出成功，记录相关信息
                    result.put("state",Util.stateSuccess);
                    result.put("msg",(String)jsonObject.get("message"));
                }else{
                    //登出失败，记录错误信息
                    result.put("msg",(String)jsonObject.get("message"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }
    public void getRouteData(){

    }
    public void updateData(){

    }
    public Map<String,String> getHeader(){
        Map<String,String> header = new HashMap<>();
        header.put("Accept","application/json");
        header.put("Content-Type","application/json");
        header.put("DeviceId",deviceID);
        header.put("source","000049");
        header.put("appVersion","1.2.0");
        header.put("osType","0");
        header.put("CustomDeviceId",Util.MD532(deviceID));
        if(null != this.uid)
            header.put("uid",this.uid);
        StringBuffer sb = new StringBuffer();
        sb.append(username);
        sb.append(":");
        sb.append(password);
        header.put("Authorization","Basic "+ new String(Base64.encodeBase64(sb.toString().getBytes())));
        header.put("User-Agent","User-Agent: Dalvik/2.1.0 (Linux; Android 6.0.1;");
        header.put("Host","gxapp.iydsj.com");
        header.put("Connection","Keep-Alice");
        return  header;
    }
}
