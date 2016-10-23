package top.catfish.hackrunninggo.dao;

import java.util.List;

/*
 * Created by Catfish on 2016/10/24.
 */

public class Route {
    private String name;
    //private List<?> list;
    public Route(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
