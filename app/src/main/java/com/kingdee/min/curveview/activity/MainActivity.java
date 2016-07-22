package com.kingdee.min.curveview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kingdee.min.curveview.R;
import com.kingdee.min.curveview.view.CurveView;

public class MainActivity extends AppCompatActivity {

    TextView mTvPointX;
    TextView mTvPointY;
    CurveView mCurveView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initValues();
    }

    private void initValues() {
        mCurveView.setOnMoveActionListener(new CurveView.OnMoveActionListener() {
            @Override
            public void OnMove(float x, float y) {
                mTvPointX.setText(x + "");
                mTvPointY.setText(y + "");
            }
        });
        mCurveView.setOnUpActionListener(new CurveView.OnUpActionListener() {
            @Override
            public void OnUp(float x, float y) {
                mTvPointX.setText(x + "");
                mTvPointY.setText(y + "");
            }
        });
    }

    private void findViews() {

        mTvPointX = (TextView) findViewById(R.id.tv_x);
        mTvPointY = (TextView) findViewById(R.id.tv_y);
        mCurveView = (CurveView) findViewById(R.id.curve_view_id);
    }


}
