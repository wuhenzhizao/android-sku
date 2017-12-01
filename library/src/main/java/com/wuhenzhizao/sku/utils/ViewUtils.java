package com.wuhenzhizao.sku.utils;

import android.os.Build;
import android.view.View;

import java.util.UUID;

/**
 * Created by wuhenzhizao on 2017/9/20.
 */

public class ViewUtils {

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            return UUID.randomUUID().hashCode();
        }
    }
}
