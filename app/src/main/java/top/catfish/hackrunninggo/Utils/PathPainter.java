package top.catfish.hackrunninggo.Utils;

import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.List;

import top.catfish.hackrunninggo.MainActivity;
import top.catfish.hackrunninggo.R;
import top.catfish.hackrunninggo.dao.Route;

/*
 * Created by Catfish on 2016/10/19.
 */

public class PathPainter {
    private BaiduMap map;
    private MainActivity activity;
    private LatLng last;
    private int distance;
    private int duration;
    private List<LatLng> list;

    private List<BitmapDescriptor> bitmaps;
    private BitmapDescriptor bitmap_st, bitmap_en;

    public PathPainter(BaiduMap map, MainActivity activity) {
        this.map = map;
        this.activity = activity;
        distance = 0;
        duration = 0;
        initData();
    }

    public void initData() {
        bitmaps = new ArrayList<>();
        bitmap_st = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_st);
        bitmap_en = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_en);
        List<Integer> ress = new ArrayList<>();
        ress.add(R.drawable.icon_gcoding);
        ress.add(R.drawable.icon_marka);
        ress.add(R.drawable.icon_markb);
        ress.add(R.drawable.icon_markc);
        ress.add(R.drawable.icon_markd);
        ress.add(R.drawable.icon_marke);
        ress.add(R.drawable.icon_markf);
        ress.add(R.drawable.icon_markg);
        ress.add(R.drawable.icon_markh);
        ress.add(R.drawable.icon_marki);
        ress.add(R.drawable.icon_markj);
        for (int i = 0; i < ress.size(); i++) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(ress.get(i));
            bitmaps.add(bitmap);
        }
    }

    public void drawPath(Route route) {
        distance = 0;
        duration = 0;
        list = route.getList();
        map.clear();
        LatLng first = list.get(0);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker);
        OverlayOptions option1 = new MarkerOptions().icon(bitmap)
                .position(first);
        map.addOverlay(option1);
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                //Toast.makeText(activity, "Result " + count++, Toast.LENGTH_LONG).show();
                List<WalkingRouteLine> routeLines = walkingRouteResult.getRouteLines();
                if (routeLines == null)
                    return;
                last = null;
                for (int i = 0; i < routeLines.size(); i++) {
                    WalkingRouteLine routeLine = routeLines.get(i);
                    PathPainter.this.distance += routeLine.getDistance();
                    PathPainter.this.duration += routeLine.getDuration();
                    activity.setTextView(R.id.distanceText, String.valueOf(distance) + "M");
                    activity.setTextView(R.id.durationText, String.valueOf(duration / 60) + "min");
                    activity.setTextView(R.id.speedText, String.valueOf(Double.valueOf(distance * 1.0 / 1000 / (duration * 1.0 / 3600)).intValue()) + "KM/H");
                    Log.e("distance", String.valueOf("route" + i + ":" + routeLine.getDistance()));
                    List<WalkingRouteLine.WalkingStep> steps = routeLine.getAllStep();
                    for (int j = 0; j < steps.size(); j++) {
                        WalkingRouteLine.WalkingStep step = steps.get(j);
                        List<LatLng> points = step.getWayPoints();
                        List<LatLng> tPoints = new ArrayList<>();
                        OverlayOptions ooPolyline;
                        if (last != null) {
                            tPoints.add(last);
                            tPoints.add(points.get(0));
                            ooPolyline = new PolylineOptions().width(15).color(0xAAFF0000).points(tPoints).visible(true);
                            map.addOverlay(ooPolyline);
                        }
                        if (points.size() > 1) {
                            ooPolyline = new PolylineOptions().width(15).color(0xAAFF0000).points(points).visible(true);
                            map.addOverlay(ooPolyline);
                        }
                        last = points.get(points.size() - 1);
                    }
                }

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            }
        };

        OverlayOptions option;
        for (int i = 1; i < list.size(); i++) {
            if (i != list.size() - 1) {
                option = new MarkerOptions().icon(bitmaps.get(i))
                        .position(list.get(i));
                map.addOverlay(option);
            }
            PlanNode st = PlanNode.withLocation(list.get(i - 1));
            PlanNode en = PlanNode.withLocation(list.get(i));
            RoutePlanSearch search = RoutePlanSearch.newInstance();
            search.setOnGetRoutePlanResultListener(listener);
            search.walkingSearch(new WalkingRoutePlanOption().from(st).to(en));

        }
        option = new MarkerOptions().icon(bitmap_st)
                .position(list.get(0));
        map.addOverlay(option);
        option = new MarkerOptions().icon(bitmap_en)
                .position(list.get(list.size() - 1));
        map.addOverlay(option);

    }
}