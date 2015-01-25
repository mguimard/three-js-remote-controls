package com.example.morgan.threejscontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by morgan on 25/01/15.
 */
public class NetworkMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity mainActivity = ((App) context.getApplicationContext()).mainActivity;
        mainActivity.onConnectionEvent();
    }
}
