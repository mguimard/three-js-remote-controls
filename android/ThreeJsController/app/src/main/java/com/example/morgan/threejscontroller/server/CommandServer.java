package com.example.morgan.threejscontroller.server;

import android.util.Log;

import com.example.morgan.threejscontroller.surface.DrawSurfaceView;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandServer extends WebSocketServer {

    public static final String TAG = "CommandServer";
    private WebSocket remoteApp;
    private DrawSurfaceView output;

    public static CommandServer launch() {
        WebSocketImpl.DEBUG = true;
        int port = 8887;
        try {
            CommandServer s = new CommandServer( port );
            s.start();
            Log.i(TAG, "ChatServer started on port: " + s.getPort());
            return s;
        } catch (UnknownHostException e) {

        }
        return null;
    }


    public CommandServer(int port) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public boolean isConnected(){
        return remoteApp != null;
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        Log.i(TAG,"New connection");
        if(remoteApp != null){
            Log.i(TAG,"Already registered, closing");
            conn.close();
            return;
        }
        remoteApp = conn;
        this.output.drawText();
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        Log.i(TAG,"Closed connection");
        if(conn == remoteApp){
            remoteApp = null;
            this.output.drawText();
        }
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        Log.i(TAG,"Message");
        Log.i(TAG,message);
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        Log.i(TAG,"Error");
        ex.printStackTrace();
    }

    public void send(String key,String value){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, value);
            remoteApp.send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendState(HashMap<String, Object> state) {
        try {
            JSONObject jsonObject = new JSONObject();
            Iterator it = state.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                jsonObject.put((String) pairs.getKey(),pairs.getValue());
            }
            remoteApp.send(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swipeLeft() {
        send("swipe","left");
    }

    public void swipeRight() {
        send("swipe","right");
    }

    public void swipeUp() {
        send("swipe","up");
    }

    public void swipeDown() {
        send("swipe","down");
    }

    public void zoomIn() {
        send("zoom","in");
    }

    public void zoomOut() {
        send("zoom","out");
    }

    public void setOutput(DrawSurfaceView output) {
        this.output = output;
    }

    public DrawSurfaceView getOutput() {
        return output;
    }
}