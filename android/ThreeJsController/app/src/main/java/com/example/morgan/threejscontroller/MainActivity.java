package com.example.morgan.threejscontroller;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.morgan.threejscontroller.server.CommandServer;
import com.example.morgan.threejscontroller.surface.DrawSurfaceView;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";
    private static final CommandServer cs = CommandServer.launch();
    private static DrawSurfaceView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawView = new DrawSurfaceView(this, getIpAddr());
        setContentView(drawView);
        cs.setOutput(drawView);
    }

    public CommandServer getCommander(){
        return cs;
    }

    public String getIpAddr() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        String ipString = String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        return ipString;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
