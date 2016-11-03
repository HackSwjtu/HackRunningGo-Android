package top.catfish.hackrunninggo.Utils;

/*
 * Created by Catfish on 2016/10/20.
 */

import com.baidu.mapapi.model.LatLng;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    public static final String spLoginData = "spLoginData";
    public static final String spLoginUsername = "spLoginUsername";
    public static final String spLoginPassword = "spLoginPassword";
    public static final String spLoginStamp = "spLoginStamp";
    public static final String spDeviceID = "spDeviceID";
    public static final String spUID = "spUID";
    public static final String stateSuccess = "stateSuccess";
    public static final String stateError = "stateError";
    public static String MD532(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result.toUpperCase();
    }
    public static double  getDistance(LatLng p1, LatLng p2) {
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
    public static String formatJsonString(String input){
        String output;
        output = input.replaceAll("\n", "").replaceAll("\\\"[\\t\\s]*\\[", "[")
                .replaceAll("\\][\\t\\s]*\\\"", "]").replaceAll("\\\"[\\t\\s]*\\{", "{")
                .replaceAll("\\}[\\t\\s]*\\\"", "}").replaceAll("\\\\", "").replaceAll("\\][\\t\\s]*\\\"", "]");
        return output;
    }
}
