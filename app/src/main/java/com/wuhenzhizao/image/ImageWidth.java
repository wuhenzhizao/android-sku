package com.wuhenzhizao.image;

/**
 * Created by wuxiaowei on 16/9/2.
 */
public enum ImageWidth {
    IMAGE_WIDTH_1_8(1f / 8), IMAGE_WIDTH_1_4(1f / 4), IMAGE_WIDTH_1_2(1f / 2), IMAGE_WIDTH_1_3(1f / 3), IMAGE_WIDTH_2_3(2f / 3), IMAGE_WIDTH_3_4(3f / 4), IMAGE_WIDTH_1(1f);
    private final float value;

    ImageWidth(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
