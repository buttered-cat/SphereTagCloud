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
    static final double radiusCpst = 60;
    boolean isCoordInfoSet = false;

    ArrayList<TextPoint> points = new ArrayList<>();
    static int pointCount ;

    Paint p=new Paint();

    static final int alphaMax = 255;
    static final int alphaMin = 80;
    static final int textSizeMax = 30;
    static final int textSizeMin = 15;
    static final double maxSpeed = 3.0;
    static final int refreshInterval = 100;
    static final double minErr = 0.05;
    static final double degreesMinErr = minErr;
    static final double deltaT = refreshInterval / 1000.0;
    static final int ringCount = 4;
//    static final int ptsPairCount = ringCount;
    static final int ptsPerRing = ringCount * 2;

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
        /*pointCount = 6;
        //TextPoint point = new TextPoint(0.0, 0.0, radius, "Hell0");
        points.add(new TextPoint(0.0, 0.0, radius, "Hell0"));
        points.add(new TextPoint(0.0, 0.0, -radius, "Bye"));
        points.add(new TextPoint(-radius, 0.0, 0.0, "Jimi"));
        points.add(new TextPoint(radius, 0.0, 0.0, "Larry"));
        points.add(new TextPoint(0.0, -radius, 0.0, "Brin"));
        points.add(new TextPoint(0.0, radius, 0.0, "Jane"));*/

        ArrayList<String> tags = new ArrayList<>();

        String input = new String("Chance word which common everyday living " +
                "The radio reports speaking tomorrow weather There sixty percent chance." +
                " You might There small chance that shall live hundred years. " +
                "Scientists also word chance seismologist interested question " +
                "What chance that there will earthquake certain size Southern California next year" +
                "definition requires several comments First speak probability " +
                "something happening only occurrence possible outcome some repeatable observation");

        /*String input = new String("腹胀 富余 富裕 附属 附小 该书 该署 概述 概算 干草 干柴 " +
                "干活 干货 干粮 干了 干松 干土 甘露 甘美 甘孜 甘作 赶集 赶紧 刚到 刚刚 刚走 钢板 " +
                "凤冠 凤凰 凤梨 否定 否决 否认 否则 敷设 敷衍 肤浅 肤色 孵化 幅度 幅面 符合 服从 " +
                "服毒 服过 服满 服气 服色 服食 服式 服侍 服饰 服输 服贴 服务 服药 服役 服用 服装 " +
                "服罪 浮标 浮尘 浮沉 浮出 浮雕 浮吊 浮动 浮夸 浮力 浮漂 浮起 浮浅 浮桥 浮筒 浮土 " +
                "浮现 浮想 浮游 浮肿 浮子 福安 福地 福分 福气 福特 福相 福星 福音 福州 福祉 抚爱 " +
                "抚摸 抚摩 抚顺 抚慰 抚恤 抚养 腑脏 府第 府内 府上 腐败 腐臭 腐化 腐烂 腐乳 腐尸");*/

        String[] res = input.split("\\s+", ringCount * (/*ptsPairCount*/ptsPerRing - 1) * 2 + 2 + 1);
        for(int count = 0;count < res.length - 1; ++count)
        {
            tags.add(res[count]);
        }

        try {
            if(180.0 % ringCount != 0.0/* || 180.0 % ptsPairCount != 0.0*/)
                throw new Exception();
        }
        catch(Exception e)
        {
            points.add(new TextPoint(radius, 0, 0, "invalid ringCount"));
            return;
        }

        try {
            if(tags.size() < ringCount * (/*ptsPairCount*/ptsPerRing - 1) * 2 + 2)
                throw new Exception();
        }
        catch(Exception e)
        {
            points.add(new TextPoint(radius, 0, 0, "too few tags!"));
            return;
        }

        Iterator it = tags.iterator();
        /*double degrees;
        double k;*/

        points.add(new TextPoint(0, 0, radius, (String)it.next()));
        points.add(new TextPoint(0, 0, -radius, (String)it.next()));

        for(int curRing = 0; curRing < ringCount; ++curRing)
        {
            double axisX = Math.cos(Math.PI / ringCount * curRing);
            double axisY = Math.sin(Math.PI / ringCount * curRing);
            double temp = axisX;
            axisX = -axisY;
            axisY = temp;

            TextPoint formerPt = new TextPoint(0, 0, radius, "");

            for(int curPt = 1; curPt < ptsPerRing && it.hasNext(); ++curPt)
            {
                if(360 / ptsPerRing * curPt > 180 - degreesMinErr && 360 / ptsPerRing * curPt < 180 + degreesMinErr)
                {
                    formerPt = transform(formerPt, 2 * Math.PI / ptsPerRing, axisX, axisY, 0, true);
                    continue;
                }
                else
                {
                    formerPt = transform(formerPt, 2 * Math.PI / ptsPerRing, axisX, axisY, 0, true);
                    formerPt.text = (String)it.next();
                    points.add(formerPt);
                }

            }
        }


//        for(int curRing = 0; curRing < ringCount/* && it.hasNext()*/; ++curRing)
//        {
//            if(curRing * (180.0 / ringCount) != 90)
//            {
//                k = Math.tan(curRing * (180.0 / ringCount));
//                for (int curPt = 1; curPt < ptsPairCount/* && it.hasNext()*/; ++curPt)
//                {
//                    degrees = curPt * (180.0 / ptsPairCount);
//                    double y = radius * Math.sin(Math.toRadians(degrees)) / Math.sqrt(1.0 + k*k);
//                    points.add(new TextPoint(radius * Math.cos(Math.toRadians(degrees)), y, k * y, (String)it.next()));
//                    degrees += 180.0;
//                    y = radius * Math.sin(Math.toRadians(degrees)) / Math.sqrt(1.0 + k*k);
//                    points.add(new TextPoint(radius * Math.cos(Math.toRadians(degrees)), y, k * y, (String)it.next()));
//                }
//            }
//            else
//            {
//                for (int curPt = 1; curPt < ptsPairCount/* && it.hasNext()*/; ++curPt)
//                {
//                    degrees = curPt * (180.0 / ptsPairCount);
//                    points.add(new TextPoint(radius * Math.cos(Math.toRadians(degrees)), 0, radius * Math.sin(Math.toRadians(degrees)), (String)it.next()));
//                    degrees += 180.0;
//                    points.add(new TextPoint(radius * Math.cos(Math.toRadians(degrees)), 0, radius * Math.sin(Math.toRadians(degrees)), (String)it.next()));
//                }
//            }
//        }

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
            /*double C = Math.cos(theta);
            double S = Math.sin(theta);
            double A = 1 - C;*/

            for(Iterator it = points.iterator(); it.hasNext();)
            {
                /*TextPoint pt = (TextPoint)it.next();
                double x = pt.x;
                double y = pt.y;
                double z = pt.z;
                pt.x = (A * rotAxisX * rotAxisX + C) * x + (A * rotAxisX * rotAxisY - S * rotAxisZ) * y + (A * rotAxisX * rotAxisZ + S * rotAxisY) * z;
                pt.y = (A * rotAxisX * rotAxisY + S * rotAxisZ) * x + (A * rotAxisY * rotAxisY + C) * y + (A * rotAxisY * rotAxisZ - S * rotAxisX) * z;
                pt.z = (A * rotAxisX * rotAxisZ - S * rotAxisY) * x + (A * rotAxisY * rotAxisZ + S * rotAxisX) * y + (A * rotAxisZ * rotAxisZ + C) * z;
                */
                transform((TextPoint)it.next(), theta, rotAxisX, rotAxisY, rotAxisZ, false);
            }
        }

        p.setTextAlign(Paint.Align.CENTER);
        p.setAntiAlias(true);
        canvas.drawColor(Color.BLACK);
        p.setColor(Color.WHITE);

        for(Iterator it = points.iterator(); it.hasNext();)
        {
            TextPoint pt = (TextPoint)it.next();
            p.setAlpha( (int)(alphaMin + (pt.z + radius) / (2 * radius) * (alphaMax - alphaMin)) );
            p.setTextSize((float) (textSizeMin + (pt.z + radius) / (2 * radius) * (textSizeMax - textSizeMin)));
            canvas.drawText(pt.text, (float)(pt.x + originX), (float)(pt.y + originY), p);
        }

    }

    TextPoint transform(TextPoint pt, double rTheta, double rotAxisX, double rotAxisY, double rotAxisZ, boolean createPoint)
    {
        if(Math.sqrt(rotAxisX * rotAxisX + rotAxisY * rotAxisY + rotAxisZ * rotAxisZ) != 1)
        {
            double len = Math.sqrt(rotAxisX * rotAxisX + rotAxisY * rotAxisY + rotAxisZ * rotAxisZ);
            rotAxisX = rotAxisX / len;
            rotAxisY = rotAxisY / len;
            rotAxisZ = rotAxisZ / len;
        }
        double C = Math.cos(rTheta);
        double S = Math.sin(rTheta);
        double A = 1 - C;

        double x = pt.x;
        double y = pt.y;
        double z = pt.z;

        if(createPoint)
        {
            TextPoint res = new TextPoint();
            res.x = (A * rotAxisX * rotAxisX + C) * x + (A * rotAxisX * rotAxisY - S * rotAxisZ) * y + (A * rotAxisX * rotAxisZ + S * rotAxisY) * z;
            res.y = (A * rotAxisX * rotAxisY + S * rotAxisZ) * x + (A * rotAxisY * rotAxisY + C) * y + (A * rotAxisY * rotAxisZ - S * rotAxisX) * z;
            res.z = (A * rotAxisX * rotAxisZ - S * rotAxisY) * x + (A * rotAxisY * rotAxisZ + S * rotAxisX) * y + (A * rotAxisZ * rotAxisZ + C) * z;
            return res;
        }
        else
        {
            pt.x = (A * rotAxisX * rotAxisX + C) * x + (A * rotAxisX * rotAxisY - S * rotAxisZ) * y + (A * rotAxisX * rotAxisZ + S * rotAxisY) * z;
            pt.y = (A * rotAxisX * rotAxisY + S * rotAxisZ) * x + (A * rotAxisY * rotAxisY + C) * y + (A * rotAxisY * rotAxisZ - S * rotAxisX) * z;
            pt.z = (A * rotAxisX * rotAxisZ - S * rotAxisY) * x + (A * rotAxisY * rotAxisZ + S * rotAxisX) * y + (A * rotAxisZ * rotAxisZ + C) * z;
            return pt;
        }
    }

}
