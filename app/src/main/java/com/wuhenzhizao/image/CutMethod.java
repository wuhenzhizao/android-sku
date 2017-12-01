package com.wuhenzhizao.image;

/**
 * Created by wuxiaowei on 16/9/2.
 */
public enum CutMethod {
    LEFT_TOP("cLT"), RIGHT_TOP("cRT"), LEFT_BOTTOM("cLB"), RIGHT_BOTTOM("cRB"), CENTER("c");
    private final String value;

    CutMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
