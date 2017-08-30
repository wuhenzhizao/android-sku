package com.wuhenzhizao.widget;

import com.wuhenzhizao.bean.Sku;
import com.wuhenzhizao.bean.SkuAttribute;

/**
 * Created by wuhenzhizao on 2017/8/30.
 */
public interface OnSkuListener {
    /**
     * 属性取消选中
     *
     * @param unselectAttribute
     */
    void onUnselect(SkuAttribute unselectAttribute);

    /**
     * 属性选中
     *
     * @param selectAttribute
     */
    void onSelect(SkuAttribute selectAttribute);

    /**
     * sku选中
     *
     * @param sku
     */
    void onSkuSelected(Sku sku);
}