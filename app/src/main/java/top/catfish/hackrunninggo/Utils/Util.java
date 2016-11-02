package top.catfish.hackrunninggo.Utils;

/*
 * Created by Catfish on 2016/10/20.
 */

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
}
