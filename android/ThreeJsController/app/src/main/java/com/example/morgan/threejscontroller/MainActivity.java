package com.example.morgan.threejscontroller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private static CommandServer cs = CommandServer.launch();
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        App myApp = (App) this.getApplicationContext();
        myApp.mainActivity = this;

        setContentView(R.layout.activity_main);

        DrawSurfaceView drawView = (DrawSurfaceView) findViewById(R.id.draw_view);
        drawView.drawText();
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
        mMenu = menu;
        setButtonsState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if( id == R.id.reset_button){
            cs.send("reset", true);
            return true;
        }
        if( id == R.id.next_button){
            cs.send("next", true);
            return true;
        }
        if( id == R.id.previous_button){
            cs.send("previous", true);
            return true;
        }
        if(id == R.id.wifi_button){
            turnOnWifi();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    public void turnOnWifi(){
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void onConnectionEvent() {
        DrawSurfaceView drawView = (DrawSurfaceView) findViewById(R.id.draw_view);
        drawView.drawText();
        setButtonsState();
    }

    private void setButtonsState() {
        if(mMenu != null){
            boolean csConnected = cs.isConnected();
            boolean wifiConnected = isWifiConnected();
            mMenu.findItem(R.id.reset_button).setVisible(csConnected);
            mMenu.findItem(R.id.previous_button).setVisible(csConnected);
            mMenu.findItem(R.id.next_button).setVisible(csConnected);
            mMenu.findItem(R.id.wifi_button).setVisible(!wifiConnected);
        }
    }

}
