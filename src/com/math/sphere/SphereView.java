package com.math.sphere;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;

public class SphereView  extends View
{
    double radius;
    double originX;
    double originY;
    static final double radiusCpst = 30;
    boolean isCoordInfoSet = false;

    ArrayList<TextPoint> points = new ArrayList<TextPoint>();
    static int pointCount ;

    static final int alphaMax = 255;
    static final int alphaMin = 100;
    static final int textSizeMax = 35;
    static final int textSizeMin = 15;
    static final double maxSpeed = 3.0;
    static final int refreshInterval = 20;
    static final double minErr = 0.01;
    static final double deltaT = refreshInterval / 1000.0;

    double rotSpeed = 0;
    double rotAxisX;
    double rotAxisY;
    double rotAxisZ = 0;
    double t = 0;

    boolean fingerDown = true;
    Handler handler = new Handler();
    Runnable speedDecay = new Runnable() {
        @Override
        public void run() {
            //invalidate();
            t += deltaT;
            double newRotSpd = maxSpeed * Math.pow(Math.E, -t);
            if(newRotSpd - 0 > minErr)
            {
                rotSpeed = newRotSpd;
                handler.postDelayed(speedDecay, refreshInterval);
            }
            else
            {
                rotSpeed = 0;
                t = 0;
                //handler.removeCallbacks(speedDecay);
            }
        }
    };

    Runnable refresher = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.postDelayed(refresher, refreshInterval);
        }
    };

    public SphereView(Context context)
    {
        super(context);
    }
    public SphereView(Context context, AttributeSet set)
    {
        super(context, set);
    }


    private void initPoints()
    {
        pointCount = 6;
        //TextPoint point = new TextPoint(0.0, 0.0, radius, "Hell0");
        points.add(new TextPoint(0.0, 0.0, radius, "Hell0"));
        points.add(new TextPoint(0.0, 0.0, -radius, "Bye"));
        points.add(new TextPoint(-radius, 0.0, 0.0, "Jimi"));
        points.add(new TextPoint(radius, 0.0, 0.0, "Larry"));
        points.add(new TextPoint(0.0, -radius, 0.0, "Brin"));
        points.add(new TextPoint(0.0, radius, 0.0, "Jane"));

        handler.post(refresher);
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE)
        {
            if(! fingerDown)
            {
                fingerDown = true;
                handler.removeCallbacks(speedDecay);
            }
            double x = event.getX() - originX;
            double y = event.getY() - originY;
            double len = Math.sqrt(x*x + y*y);
            double maxLen = Math.sqrt(originX * originX + originY * originY);
            rotAxisX = y / len;
            rotAxisY = (-x) / len;
            rotSpeed = maxSpeed * (len / maxLen);
            //invalidate();
        }
        else if(event.getActionMasked() == MotionEvent.ACTION_UP)
        {
            fingerDown = false;
            if(rotSpeed != 0)
            {
                t = Math.log(maxSpeed) - Math.log(rotSpeed);
                handler.post(speedDecay);
            }
        }


        return true;
    }


    @Override
    public void onDraw(Canvas canvas)
    {
        if(! isCoordInfoSet)
        {
            radius = canvas.getWidth() / 2 - radiusCpst;
            originX = canvas.getWidth() / 2;
            originY = canvas.getHeight() / 2;
            initPoints();
            isCoordInfoSet = true;
        }

        if(rotSpeed != 0)
        {
            double theta = deltaT * rotSpeed;
            double C = Math.cos(theta);
            double S = Math.sin(theta);
            double A = 1 - C;

            for(Iterator it = points.iterator(); it.hasNext();)
            {
                TextPoint pt = (TextPoint)it.next();
                double x = pt.x;
                double y = pt.y;
                double z = pt.z;
                pt.x = (A * rotAxisX * rotAxisX + C) * x + (A * rotAxisX * rotAxisY - S * rotAxisZ) * y + (A * rotAxisX * rotAxisZ + S * rotAxisY) * z;
                pt.y = (A * rotAxisX * rotAxisY + S * rotAxisZ) * x + (A * rotAxisY * rotAxisY + C) * y + (A * rotAxisY * rotAxisZ - S * rotAxisX) * z;
                pt.z = (A * rotAxisX * rotAxisZ - S * rotAxisY) * x + (A * rotAxisY * rotAxisZ + S * rotAxisX) * y + (A * rotAxisZ * rotAxisZ + C) * z;
            }
        }

        Paint p=new Paint();
        p.setTextAlign(Paint.Align.CENTER);
        p.setAntiAlias(true);
        canvas.drawColor(Color.WHITE);
        p.setColor(Color.BLACK);

        for(Iterator it = points.iterator(); it.hasNext();)
        {
            TextPoint pt = (TextPoint)it.next();
            p.setAlpha( (int)(alphaMin + (pt.z + radius) / (2 * radius) * (alphaMax - alphaMin)) );
            p.setTextSize((float) (textSizeMin + (pt.z + radius) / (2 * radius) * (textSizeMax - textSizeMin)));
            canvas.drawText(pt.text, (float)(pt.x + originX), (float)(pt.y + originY), p);
        }

    }

}
