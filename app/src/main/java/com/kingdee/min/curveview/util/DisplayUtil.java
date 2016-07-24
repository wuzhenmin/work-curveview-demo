package com.kingdee.min.curveview.util;

import android.content.res.Resources;

/**
 * Created by hasee on 2016/7/23.
 */
public class DisplayUtil {
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5f);
    }

    /**
     * 得到密度0.75,1.0,1.5
     *
     * @return
     */
    public static float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }
}
