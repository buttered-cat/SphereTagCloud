package com.math.sphere;

import java.lang.String;

public class TextPoint {
    double x;
    double y;
    double z;
    String text;

    public TextPoint(double x, double y, double z, String text)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.text = text;
    }

    public TextPoint()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public void set(double x, double y, double z, String text)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.text = text;
    }
}
