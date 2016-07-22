package com.kingdee.min.curveview.model;

import java.io.Serializable;

/**
 * Created by min on 16-7-20.
 */
public class Point implements Serializable {
    private float mPointX;
    private float mPointY;

    public Point(float mPointX, float mPointY) {
        this.mPointX = mPointX;
        this.mPointY = mPointY;
    }

    public Point() {
    }

    public float getmPointX() {
        return mPointX;
    }

    public void setmPointX(float mPointX) {
        this.mPointX = mPointX;
    }

    public float getmPointY() {
        return mPointY;
    }

    public void setmPointY(float mPointY) {
        this.mPointY = mPointY;
    }

    @Override
    public String toString() {
        return "Point{" +
                "mPointX=" + mPointX +
                ", mPointY=" + mPointY +
                '}';
    }
}
