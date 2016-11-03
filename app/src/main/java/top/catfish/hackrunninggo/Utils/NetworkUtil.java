package top.catfish.hackrunninggo.Utils;

import android.net.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Catfish on 2016/10/26.
 */

public class NetworkUtil {
    public final static int HTTP_ERROR = 0;
    public final static int HTTP_OK = 1;
    public static Map<String,String> sendGet(String urlstr,Map<String,String> header){
        HttpURLConnection connection;
        Map<String,String> result = new HashMap<>();
        result.put("state",String.valueOf(HTTP_ERROR));
        try {
            URL url = new URL(urlstr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            for(Map.Entry<String,String> entry : header.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                while((readLine = br.readLine())!=null){
                    sb.append(readLine+'\n');
                }
                br.close();
                result.put("result",sb.toString());
                result.put("state",String.valueOf(HTTP_OK));
            }
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }
        return result;
    }
    public static Map<String,String> sendPost(String urlstr,Map<String,String> header,String body) {
        HttpURLConnection connection;
        Map<String,String> result = new HashMap<>();
        result.put("state",String.valueOf(HTTP_ERROR));
        try {
            URL url = new URL(urlstr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            for(Map.Entry<String,String> entry : header.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if(body != null) {
                byte[] requestBytes = body.getBytes("UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(requestBytes);
                os.close();
            }

            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                while((readLine = br.readLine())!=null){
                    sb.append(readLine+'\n');
                }
                br.close();
                result.put("result",sb.toString());
                result.put("state",String.valueOf(HTTP_OK));
            }
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }
        return result;
    }
}
