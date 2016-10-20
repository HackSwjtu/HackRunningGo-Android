package top.catfish.hackrunninggo;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import top.catfish.hackrunninggo.broadcast.ExitAppReceiver;

/*
 * Created by Catfish on 2016/10/20.
 */

public class BaseAppCompatActivity extends AppCompatActivity {
    private ExitAppReceiver exitReceiver = new ExitAppReceiver();
    private static final String EXIT_APP_ACTION = "top.catfish.receiver.exit_app";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerExitReceiver();
    }

    private void registerExitReceiver() {

        IntentFilter exitFilter = new IntentFilter();
        exitFilter.addAction(EXIT_APP_ACTION);
        registerReceiver(exitReceiver, exitFilter);
    }

    private void unRegisterExitReceiver() {

        unregisterReceiver(exitReceiver);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unRegisterExitReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void exitApp(){
        Intent intent = new Intent();
        intent.setAction(EXIT_APP_ACTION);
        sendBroadcast(intent);
    }
}
