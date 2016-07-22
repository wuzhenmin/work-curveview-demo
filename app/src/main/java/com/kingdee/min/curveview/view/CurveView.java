package com.kingdee.min.curveview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kingdee.min.curveview.model.Point;


/**
 * Created by min on 16-7-20.
 */
public class CurveView extends View {
    private Paint mPaint;
    private Path mPath;
    private Path mMeasurePath;
    private Path[] mPaths;
    private float[] mPointX = {50, 250, 450, 650, 850};
    private float[] mPointY = {400, 655, 405, 621, 365};
    private float[] mInterPoint;
    private final float SENSE_AREA = 20;
    private final double ACCURACY = 0.01;
    private Point startP;
    private Point endP;
    private Point preContrPoint;
    private Point futureContrPoint;
    private float lineSY, lineEY;
    private float currentXPosition, currentYPosition;
    private OnMoveActionListener mMove = null;
    private OnUpActionListener mUp = null;

    public CurveView(Context context) {
        this(context, null);
        init();
    }

    public CurveView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();
    }


    private void init() {

        mPaint = new Paint();
        mPath = new Path();
        mMeasurePath = new Path();
        mPaths = new Path[4];
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        startP = new Point();
        endP = new Point();
        preContrPoint = new Point();
        futureContrPoint = new Point();
        currentXPosition = mPointX[1];
        currentYPosition = mPointY[1];
        lineSY = 50;
        lineEY = 900;
        mInterPoint = new float[2];
    }


    @Override
    protected void onDraw(Canvas canvas) {

        drawCurve(canvas);
        drawVerLine(canvas);

        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Log.e("TOUCHX===========", "" + currentXPosition);
                if (Math.abs(event.getX() - currentXPosition) <= SENSE_AREA) {
                    currentXPosition = event.getX();
                    if (currentXPosition > mPointX[4]) {
                        currentXPosition = mPointX[4];
                        currentYPosition = mPointY[4];
                    } else if (currentXPosition < mPointX[0]) {
                        currentXPosition = mPointX[0];
                        currentYPosition = mPointY[0];
                    } else {
                        mInterPoint = getPOfInter(currentXPosition);
                        currentYPosition = mInterPoint[1];
                    }
                    if (mMove != null) {
                        mMove.OnMove(currentXPosition, currentYPosition);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(event.getX() - currentXPosition) <= SENSE_AREA) {
                    if (getNearestX(event.getX()) != currentXPosition) {
                        if (mUp != null) {
                            currentXPosition = getNearestX(event.getX());
                            mUp.OnUp(currentXPosition, currentYPosition);
                        }
                        invalidate();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public float getNearestX(float x) {
        float xLeft, xRight;
        for (int i = 0; i < mPointX.length - 1; i++) {
            xLeft = mPointX[i];
            xRight = mPointX[i + 1];
            if (x > xLeft && x < xRight) {
                if ((x - xLeft) - (xRight - x) > 0.01) {
                    currentYPosition = mPointY[i + 1];
                    return xRight;
                } else {
                    currentYPosition = mPointY[i];
                    return xLeft;
                }
            }
        }
        return currentXPosition;
    }


    public interface OnMoveActionListener {
        public void OnMove(float x, float y);
    }

    public interface OnUpActionListener {
        public void OnUp(float x, float y);
    }

    public void setOnUpActionListener(OnUpActionListener up) {
        mUp = up;
    }

    public void setOnMoveActionListener(OnMoveActionListener move) {
        mMove = move;
    }

    private void drawVerLine(Canvas canvas) {
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(currentXPosition, lineSY, currentXPosition, lineEY, mPaint);
    }

    public float[] getPOfInter(float x) {
        PathMeasure pathMeasure = new PathMeasure();
        float point[] = new float[2];
        float distance;
        float halfX;
        for (int i = 0; i < mPointX.length - 1; i++) {
            if (x > mPointX[i] && x < mPointX[i + 1]) {
                startP.setmPointX(mPointX[i]);
                startP.setmPointY(mPointY[i]);
                endP.setmPointX(mPointX[i + 1]);
                endP.setmPointY(mPointY[i + 1]);
                break;
            }
        }

        for (; ; ) {
            halfX = (startP.getmPointX() + endP.getmPointX()) / 2f;
            preContrPoint.setmPointX(halfX);
            preContrPoint.setmPointY(startP.getmPointY());
            futureContrPoint.setmPointX(halfX);
            futureContrPoint.setmPointY(endP.getmPointY());
            mPath.rewind();
            mPath.moveTo(startP.getmPointX(), startP.getmPointY());
            mPath.cubicTo(preContrPoint.getmPointX(), preContrPoint.getmPointY(),
                    futureContrPoint.getmPointX(), futureContrPoint.getmPointY(),
                    endP.getmPointX(), endP.getmPointY());
            pathMeasure.setPath(mPath, false);
            pathMeasure.getPosTan(pathMeasure.getLength() / 2, point, null);
            distance = point[0] - x;
            if (distance >= -ACCURACY && distance <= ACCURACY) {
                return point;
            } else {
                if (distance > 0 && distance > ACCURACY) {
                    endP.setmPointX(point[0]);
                    endP.setmPointY(point[1]);
                } else if (distance < 0 && distance < -ACCURACY) {
                    startP.setmPointX(point[0]);
                    startP.setmPointY(point[1]);
                }
                continue;
            }
        }
    }

    private void drawCurve(Canvas canvas) {
        mPath.rewind();

        mPath.moveTo(mPointX[0], mPointY[0]);
        float midValue;

        for (int i = 0; i < mPointX.length - 1; i++) {
            startP.setmPointX(mPointX[i]);
            startP.setmPointY(mPointY[i]);
            endP.setmPointX(mPointX[i + 1]);
            endP.setmPointY(mPointY[i + 1]);
            midValue = (mPointX[i] + mPointX[i + 1]) / 2f;
            preContrPoint.setmPointX(midValue);
            preContrPoint.setmPointY(mPointY[i]);
            futureContrPoint.setmPointX(midValue);
            futureContrPoint.setmPointY(endP.getmPointY());
            mPath.moveTo(startP.getmPointX(), startP.getmPointY());
            mPath.cubicTo(preContrPoint.getmPointX(), preContrPoint.getmPointY(),
                    futureContrPoint.getmPointX(), futureContrPoint.getmPointY(),
                    endP.getmPointX(), endP.getmPointY());

        }
        mPaint.setColor(Color.BLACK);
        canvas.drawPath(mPath, mPaint);
    }

}
