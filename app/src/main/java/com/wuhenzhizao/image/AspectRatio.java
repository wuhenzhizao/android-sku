package com.wuhenzhizao.image;

/**
 * Created by wuxiaowei on 16/9/2.
 */
public enum AspectRatio {
    RATIO_1_1(1f / 1), RATIO_6_7(6f / 7), RATIO_15_8(15f / 8), UNSPECIFIED(0f);
    private final float value;

    AspectRatio(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
