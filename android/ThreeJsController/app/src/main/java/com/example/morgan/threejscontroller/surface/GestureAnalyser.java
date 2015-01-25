package com.example.morgan.threejscontroller.surface;

import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

import java.util.HashMap;

/**
 * Created by morgan on 24/01/15.
 */
public class GestureAnalyser {

    public static final String TAG = "GestureAnalyser";
    public static final double EPSILON = 2.5;

    private MotionEvent oldEvent;
    private HashMap<String, Object> state = new HashMap<>();


    public GestureAnalyser() {
        initState();
    }

    private void initState(){
        oldEvent = null;
        state.put("rotateX",0);
        state.put("rotateY",0);
        state.put("panX",0);
        state.put("panY",0);
        state.put("zoom",0);
    }

    public HashMap<String, Object> getState() {
        return state;
    }

    public void analyse(MotionEvent pEvent){

        MotionEvent event = MotionEvent.obtain(pEvent);

        int action = MotionEventCompat.getActionMasked(event);

        if(action != MotionEvent.ACTION_MOVE){
            initState();
            return;
        }

        if(oldEvent != null){
            if(hasOneFinger(event)){
                checkForOneFingerGesture(oldEvent, event);
            }
            else if(hasTwoFingers(event)){
                checkForTwoFingersGesture(oldEvent, event);
            }
            else if(hasThreeFingers(event)){

            }
        }

        oldEvent = event;
    }

    private void checkForTwoFingersGesture(MotionEvent e1, MotionEvent e2) {

        Point oldFinger1 = new Point((int)MotionEventCompat.getX(e1, 0),(int)MotionEventCompat.getY(e1, 0));
        Point oldFinger2 = new Point((int)MotionEventCompat.getX(e1, 1),(int)MotionEventCompat.getY(e1, 1));
        Point newFinger1 = new Point((int)MotionEventCompat.getX(e2, 0),(int)MotionEventCompat.getY(e2, 0));
        Point newFinger2 = new Point((int)MotionEventCompat.getX(e2, 1),(int)MotionEventCompat.getY(e2, 1));

        double oldDistance = distance(oldFinger1,oldFinger2);
        double newDistance = distance(newFinger1,newFinger2);

        double distanceDiff = newDistance - oldDistance;

        Point A = new Point(newFinger1.x-oldFinger1.x,newFinger1.y-oldFinger1.y);
        Point B = new Point(newFinger2.x-oldFinger2.x,newFinger2.x-oldFinger2.x);
        // cos θ = (AxBx + AyBy + AzBz) / AB
        double phi = (A.x * B.x + A.y * B.y) / (distance(new Point(),A)*distance(new Point(), B));
        double angle = Math.acos(phi);

        if(angle > Math.PI/2){
            state.put("zoom",distanceDiff);
            state.put("panX",0);
            state.put("panY",0);
        }else{
            state.put("zoom",0);
            state.put("panX",newFinger1.x-oldFinger1.x);
            state.put("panY",newFinger1.y-oldFinger1.y);
        }
    }

    private void checkForOneFingerGesture(MotionEvent e1, MotionEvent e2) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();
        state.put("rotateX",diffX);
        state.put("rotateY",diffY);
    }

    private static boolean hasThreeFingers(MotionEvent motionEvent) {
        return motionEvent.getPointerCount() == 3;
    }

    private static boolean hasTwoFingers(MotionEvent motionEvent) {
        return motionEvent.getPointerCount() == 2;
    }

    private static boolean hasOneFinger(MotionEvent motionEvent) {
        return motionEvent.getPointerCount() == 1;
    }

    private double distance(Point a, Point b){
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

}
