package com.example.morgan.threejscontroller.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.example.morgan.threejscontroller.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morgan on 24/01/15.
 */
public class DrawSurfaceView extends SurfaceView {

    public static final String TAG = "ControlSurfaceView";
    private static final int POINT_SIZE = 50;
    private final SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String ip;
    private Context mContext;
    private GestureAnalyser analyser = new GestureAnalyser();

    public DrawSurfaceView(Context context, String pIp) {
        super(context);
        mContext=context;
        ip=pIp+":8787";
        surfaceHolder = getHolder();
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL);
        surfaceHolder.addCallback(new SurfaceCallback());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        analyser.analyse(event);
        drawGesture(event);
        if(((MainActivity)mContext).getCommander().isConnected()){
            ((MainActivity)mContext).getCommander().sendState(analyser.getState());
        };
        return true;
    }

    private boolean drawGesture(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        List<Point> points = new ArrayList<>();

        for(int i = 0; i < event.getPointerCount() ; i++){
            points.add(new Point(
                    (int)MotionEventCompat.getX(event, i),
                    (int)MotionEventCompat.getY(event, i)
            ));
        }

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG, "Action was DOWN");
                drawCircles(points);
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(TAG,"Action was MOVE");
                drawCircles(points);
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                drawText();
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(TAG,"Action was CANCEL");
                drawText();
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(TAG,"Movement outside bounds ");
                drawText();
                return true;
            default :
                Log.d(TAG,"Call to super : code=" + action);
                return super.onTouchEvent(event);
        }

    }

    public void drawText(){
        if (surfaceHolder.getSurface().isValid()) {

            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);

            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

            boolean connected = ((MainActivity )mContext).getCommander().isConnected();

            if(!connected){
                Rect r = new Rect();
                String message = ip;
                paint.getTextBounds(message, 0, message.length(), r);
                yPos += (Math.abs(r.height()))/2;
                textPaint.setTextSize(60);
                canvas.drawText(message, xPos, yPos, textPaint);
                yPos-= 120;
                textPaint.setTextSize(20);
                canvas.drawText("Connect the three.js app to this ip", xPos, yPos, textPaint);
            }else{
                textPaint.setTextSize(60);
                canvas.drawText("Ready to receive gestures", xPos, yPos, textPaint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawCircles(List<Point> pointList){
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            for(Point point:pointList){
                canvas.drawCircle(point.x,point. y, POINT_SIZE, paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    public class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
            Log.i(TAG,"Surface changed");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // you need to start your drawing thread here
            Log.i(TAG, "Surface created");
            drawText();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // and here you need to stop it
            Log.i(TAG,"Surface destroyed");
        }
    }

}