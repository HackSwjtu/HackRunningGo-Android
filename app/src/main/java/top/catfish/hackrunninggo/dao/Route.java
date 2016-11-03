package top.catfish.hackrunninggo.dao;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/*
 * Created by Catfish on 2016/10/24.
 */

public class Route {
    private String name;
    private JSONArray allLocJsonArray;
    private List<LatLng> list;
    private JSONArray fivePointJsonArray;

    public Route(String name) {
        this.name = name;
    }

    public void setAllLocJsonArray(JSONArray jsonArray) {
        this.allLocJsonArray = jsonArray;
        selectFivePoints();
    }

    public void selectFivePoints() {
        try {
            fivePointJsonArray = new JSONArray();
            this.list = new ArrayList<>();
            Long now = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String nowString = String.valueOf(now);
            String dateString = format.format(now);
            int len = allLocJsonArray.length();
            Random random = new Random();
            List<Integer> indexArray = new ArrayList<>(5);
            HashSet<Integer> set = new HashSet<>();
            while (true) {
                int num = random.nextInt(len);
                if (set.add(num)) {
                    indexArray.add(num);
                    if (set.size() >= 5 ||(set.size()>=len))
                        break;
                }
            }
            for (int i = 0; i < allLocJsonArray.length(); i++) {
                allLocJsonArray.getJSONObject(i).put("flag", nowString);
                allLocJsonArray.getJSONObject(i).put("gainTime",dateString);
            }
            for (int i = 0; i < indexArray.size(); i++) {
                JSONObject jsonObject = allLocJsonArray.getJSONObject(indexArray.get(i));
                JSONObject pointObject = new JSONObject();
                pointObject.put("flag",nowString);
                pointObject.put("isPass",true);
                LatLng latlng = new LatLng(Double.valueOf(jsonObject.getString("lat")),Double.valueOf(jsonObject.getString("lng")));
                this.list.add(latlng);
                pointObject.put("lat",String.valueOf(latlng.latitude));
                pointObject.put("lng",String.valueOf(latlng.longitude));
                if(i==indexArray.size()-1){
                    pointObject.put("isFixed","1");
                }else{
                    pointObject.put("isFixed","0");
                }
                fivePointJsonArray.put(pointObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LatLng> getList() {
        return list;
    }

    public LatLng getLatlngAt(int i) {
        return list.get(i);
    }

    public String getName() {
        return name;
    }
}
