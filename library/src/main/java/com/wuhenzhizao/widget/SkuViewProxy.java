package com.wuhenzhizao.widget;

import com.wuhenzhizao.bean.SkuAttribute;
import com.wuhenzhizao.bean.Sku;

/**
 * Created by wuhenzhizao on 2017/8/2.
 */

public class SkuViewProxy {
    private OnSkuListener listener;

    public OnSkuListener getListener() {
        return listener;
    }

    public void setListener(OnSkuListener listener) {
        this.listener = listener;
    }


}
