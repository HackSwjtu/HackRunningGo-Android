package top.catfish.hackrunninggo;

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

/**
 * Created by Administrator on 2016/10/19.
 */

public class PathPainter {
    private BaiduMap map;
    private MainActivity activity;
    private LatLng last;
    private int distance;
    private int duration;

    public PathPainter(BaiduMap map, MainActivity activity) {
        this.map = map;
        this.activity = activity;
        distance = 0;
        duration = 0;
    }

    public void drawPath(List<LatLng> list) {
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
                    activity.setTextView(R.id.distanceText,String.valueOf(distance)+"M");
                    activity.setTextView(R.id.durationText,String.valueOf(duration/60)+"min");
                    activity.setTextView(R.id.speedText,String.valueOf(Double.valueOf(distance*1.0/1000/(duration*1.0/3600)).intValue())+"KM/H");
                    Log.e("distance",String.valueOf("route"+i+":"+routeLine.getDistance()));
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


        for (int i = 1; i < list.size(); i++) {
            OverlayOptions option = new MarkerOptions().icon(bitmap)
                    .position(list.get(i));
            PlanNode st = PlanNode.withLocation(list.get(i - 1));
            PlanNode en = PlanNode.withLocation(list.get(i));
            RoutePlanSearch search = RoutePlanSearch.newInstance();
            search.setOnGetRoutePlanResultListener(listener);
            search.walkingSearch(new WalkingRoutePlanOption().from(st).to(en));
            map.addOverlay(option);
            //search.destroy();
        }
    }
}