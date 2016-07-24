package com.kingdee.min.curveview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kingdee.min.curveview.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by min on 16-7-20.
 */
public class CurveView extends View {
    private Paint curvePaint;
    private Paint pointPaint;
    private Paint linePaint;
    private Path curvePath;
    private List<PointF> pointFs = new ArrayList<PointF>();
    private float[] mPointX = {150, 750, 1350, 1950, 2550};
    private float[] mPointY = {1200, 1900, 1220, 1980, 1320};
    private final float SENSE_AREA = 20;
    private final float ACCURACY = 0.1f;
    private PointF startP;
    private PointF endP;
    private PointF preContrPoint;
    private PointF futureContrPoint;
    private float lineSY, lineEY;
    private float currentXPosition, currentYPosition;
    private OnMoveActionListener mMove = null;
    private OnUpActionListener mUp = null;
    private ObjectAnimator lineAnimator;

    public CurveView(Context context) {
        this(context, null);
        init();
    }

    public CurveView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();
    }


    private void init() {

        PointF pointF;
        for (int i = 0; i < mPointX.length; i++) {
            float hig = getHeight();
            pointF = new PointF(DisplayUtil.px2dp(mPointX[i]), DisplayUtil.px2dp(mPointY[i]));
            pointFs.add(pointF);
        }
        curvePaint = new Paint();
        pointPaint = new Paint();
        linePaint = new Paint();
        curvePath = new Path();
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setColor(Color.BLACK);
        pointPaint.setColor(Color.RED);
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(5);
        curvePaint.setStrokeWidth(5);
        startP = new PointF();
        endP = new PointF();
        preContrPoint = new PointF();
        futureContrPoint = new PointF();
        lineAnimator = new ObjectAnimator();

        currentXPosition = pointFs.get(0).x;
        currentYPosition = pointFs.get(0).y;
        lineSY = 50;
        lineEY = 900;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        drawCurve(canvas);
        drawVerLine(canvas);
        drawPoint(canvas);

    }

    private void drawPoint(Canvas canvas) {

        canvas.drawCircle(currentXPosition, currentYPosition, 5, pointPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Log.e("TOUCHX===========", "" + currentXPosition);
                if (Math.abs(event.getX() - currentXPosition) <= SENSE_AREA) {
                    currentXPosition = event.getX();
                    if (currentXPosition > pointFs.get(4).x) {
                        currentXPosition = pointFs.get(4).x;
                        currentYPosition = pointFs.get(4).y;
                    } else if (currentXPosition < pointFs.get(0).x) {
                        currentXPosition = pointFs.get(0).x;
                        currentYPosition = pointFs.get(0).y;
                    } else {
                        getPOfInter(currentXPosition);
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
                    }
                    invalidate();
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
            xLeft = pointFs.get(i).x;
            xRight = pointFs.get(i + 1).x;
            if (x > xLeft && x < xRight) {
                if ((x - xLeft) - (xRight - x) > ACCURACY) {
                    currentYPosition = pointFs.get(i + 1).y;
                    return xRight;
                } else {
                    currentYPosition = pointFs.get(i).y;
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
        canvas.drawLine(currentXPosition, lineSY, currentXPosition, lineEY, linePaint);
    }

    public void getPOfInter(float eventX) {
        PathMeasure pathMeasure = new PathMeasure();
        float point[] = new float[2];
        float start = 0;
        float mid = 0.5f;
        float end = 1;
        pathMeasure.setPath(curvePath, false);
        float distance = pathMeasure.getLength();
        do {
            pathMeasure.getPosTan(distance * mid, point, null);
            if (eventX > point[0]) {
                start = mid;
            } else if (eventX < point[0]) {
                end = mid;
            } else {
                break;
            }
            mid = (start + end) / 2f;
            Log.e("dis-start-mid-end-X-P", distance + "," + start + "," + mid + "," + end + "," + eventX + "," + point[0]);
        } while (Math.abs(eventX - point[0]) > ACCURACY);
        currentXPosition = point[0];
        currentYPosition = point[1];
    }

    private void drawCurve(Canvas canvas) {
        curvePath.rewind();
        float midValue;
        for (int i = 0; i < mPointX.length - 1; i++) {
            startP.set(pointFs.get(i));
            endP.set(pointFs.get(i + 1));
            midValue = (startP.x + endP.x) / 2f;
            preContrPoint.set(midValue, startP.x);
            futureContrPoint.set(midValue, endP.y);
            curvePath.moveTo(startP.x, startP.y);
            curvePath.cubicTo(preContrPoint.x, preContrPoint.y,
                    futureContrPoint.x, futureContrPoint.y,
                    endP.x, endP.y);
        }
        canvas.drawPath(curvePath, curvePaint);
    }

}
