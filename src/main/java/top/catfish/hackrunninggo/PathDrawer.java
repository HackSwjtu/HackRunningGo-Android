package top.catfish.hackrunninggo;

import android.graphics.Point;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class PathDrawer {
    private BaiduMap map;

    public PathDrawer(BaiduMap map) {
        this.map = map;
    }

    public void drawPath(List<LatLng> list) {
        LatLng first = list.get(0), last = list.get(list.size() - 1);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.bd_markers);
        OverlayOptions option1 = new MarkerOptions().icon(bitmap)
                .position(first);
        map.addOverlay(option1);
        for (int i = 1; i < list.size() - 1; i++) {
            OverlayOptions option = new MarkerOptions().icon(bitmap)
                    .position(list.get(i));
            map.addOverlay(option);
        }
        OverlayOptions option2 = new MarkerOptions().icon(bitmap).position(last);
        map.addOverlay(option2);

    }

}
