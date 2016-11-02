package top.catfish.hackrunninggo.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/*
 * Created by Catfish on 2016/11/2.
 */

public class ImageManager {
    private LruCache<String,Bitmap> imageMemoryCache;
    private File dataFile;
    public ImageManager(Context context){
        imageMemoryCache = new LruCache<>(5*1024*1024);
        dataFile = context.getExternalFilesDir(null);
    }
    public Bitmap getImage(String filename,String url){
        String strs[] = url.split("\\.");
        filename += "."+strs[strs.length-1];
        Bitmap bitmap = getImageFromMemory(filename);
        if(null != bitmap) {
            Log.i("ImageManager","Memory");
            return bitmap;
        }
        bitmap = getImageFromDisk(filename);
        if(null !=bitmap){
            saveImageToMemory(filename,bitmap);
            Log.i("ImageManager","Disk");
            return bitmap;
        }
        bitmap = getImageFromNetwork(url);
        if(null != bitmap){
            saveImageToDisk(filename,bitmap);
            saveImageToMemory(filename,bitmap);
            Log.i("ImageManager","Network");
            return bitmap;
        }
        return null;
    }
    private Bitmap getImageFromMemory(String filename){
        return imageMemoryCache.get(filename);
    }
    private Bitmap getImageFromDisk(String filename){
        File file = new File(dataFile,filename);
        if(file.exists()){
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }
    private void saveImageToMemory(String filename,Bitmap bitmap){
        imageMemoryCache.put(filename,bitmap);
    }
    private void saveImageToDisk(String filename,Bitmap bitmap){
        File file = new File(dataFile,filename);
        try{
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            bos.flush();
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Bitmap getImageFromNetwork(String urlString){
        URL url;
        Bitmap bitmap = null;
        try{
            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
