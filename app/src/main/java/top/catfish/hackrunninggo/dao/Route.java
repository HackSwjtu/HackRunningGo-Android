package top.catfish.hackrunninggo.dao;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/*
 * Created by Catfish on 2016/10/24.
 */

public class Route {
    private String name;
    private List<LatLng> list;
    public Route(String name,List<LatLng> list){
        this.name = name;
        this.list = list;
    }
    public List<LatLng> getList(){
        return list;
    }
    public LatLng getLatlngAt(int i){
        return list.get(i);
    }
    public String getName(){
        return name;
    }
}
