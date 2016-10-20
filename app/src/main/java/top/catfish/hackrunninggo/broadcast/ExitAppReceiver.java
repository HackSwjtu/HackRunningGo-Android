package top.catfish.hackrunninggo.broadcast;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Created by Catfish on 2016/10/20.
 */

public class ExitAppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {

            if (context instanceof Activity) {
                ((Activity) context).finish();
            } else if (context instanceof Service) {
                ((Service) context).stopSelf();
            }
        }
    }
}
