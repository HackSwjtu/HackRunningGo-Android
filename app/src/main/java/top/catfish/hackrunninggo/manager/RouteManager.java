package top.catfish.hackrunninggo.manager;

import android.content.Intent;
import android.util.Log;

import com.baidu.platform.comapi.map.E;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import top.catfish.hackrunninggo.Utils.NetworkUtil;
import top.catfish.hackrunninggo.Utils.Util;
import top.catfish.hackrunninggo.adapter.RouteAdapter;
import top.catfish.hackrunninggo.dao.Route;
import top.catfish.hackrunninggo.dao.User;

/*
 * Created by Catfish on 2016/11/3.
 */

public class RouteManager {
    public final static String routeUpdateURL = "http://gxapp.iydsj.com/api/v3/get/aboutrunning/list/80604/901/3";
    private User user;
    private List<Route> routes;

    public RouteManager(User user) {
        this.user = user;
        routes = new ArrayList<>();
    }

    private List<Integer> getRouteRoomIDs() {
        List<Integer> roomIDs = null;
        Map<String, String> header = user.getHeader();
        Map<String, String> response = NetworkUtil.sendGet(routeUpdateURL, header);
        if (response.get("state").compareTo(String.valueOf(NetworkUtil.HTTP_OK)) == 0) {
            try {
                roomIDs = new ArrayList<>();
                JSONTokener jsonTokener = new JSONTokener(response.get("result"));
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                if (Integer.parseInt(jsonObject.get("error").toString()) == 10000) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String roomID = json.getString("roomId");
                        if (roomID != null)
                            roomIDs.add(Integer.valueOf(roomID.trim()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return roomIDs;
    }

    public void updateRoutesData(RouteAdapter routeAdapter) {
        List<Integer> roomIDs = getRouteRoomIDs();
        Map<String, String> header = user.getHeader();
        Log.i("RoomSize", String.valueOf(roomIDs.size()));
        for (int i = 0; i < (roomIDs.size()>15 ? 15:roomIDs.size()) ; i++) {
            int roomID = roomIDs.get(i);
            String url = "http://gxapp.iydsj.com/api/v3/get/" + String.valueOf(roomID) + "/history/finished/record";
            Map<String, String> response = NetworkUtil.sendGet(url, header);
            if (response.get("state").compareTo(String.valueOf(NetworkUtil.HTTP_OK)) == 0) {
                try {
                    JSONTokener jsonTokener = new JSONTokener(response.get("result"));
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                    if (jsonObject.getInt("error") == 10000) {
                        JSONTokener jsonDataTokener = new JSONTokener(jsonObject.getJSONObject("data").toString());
                        JSONObject jsonData = (JSONObject) jsonDataTokener.nextValue();
                        String routeName = jsonData.getJSONObject("roomInfoModel").getString("locDesc");
                        JSONArray roomersLists = jsonData.getJSONArray("roomersModelList");
                        if (roomersLists.length() > 0) {
                            JSONObject roomerObject = roomersLists.getJSONObject(0);
                            String pointsJsonString = roomerObject.getString("points");
                            JSONTokener tempJsonTokener1 = new JSONTokener(pointsJsonString);
                            JSONObject pointsObject = (JSONObject) tempJsonTokener1.nextValue();
                            String allLocJsonString = pointsObject.getString("allLocJson");
                            JSONTokener tempJsonTokener2 = new JSONTokener(allLocJsonString);
                            JSONArray allLocJson = (JSONArray) tempJsonTokener2.nextValue();
                            Route route = new Route(routeName);
                            route.setAllLocJsonArray(allLocJson);
                            Log.i("RouteJson", allLocJson.toString());
                            routes.add(route);
                            if (null != routeAdapter)
                                routeAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JSONObject getRouteDataJsonObject() {
        return null;
    }

    public Route getRoute(int index) {
        return routes.get(index);
    }

    public List<Route> getRoutesList() {
        return routes;
    }
}
