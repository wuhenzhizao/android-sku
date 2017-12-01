package com.wuhenzhizao.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by liufei on 2017/11/30.
 */

public class NumberUtils {

    public static String formatNumber(double value) {
        return String.format("%.2f", value);
    }

    public static String formatNumber(Object value, String pattern) {
        if (value != null) {
            return new DecimalFormat(pattern).format(value);
        }
        return "";
    }

    public static int formatNumberReturnInteger(Object value, String pattern) {
        if (value != null) {
            return Integer.valueOf(formatNumber(value, pattern));
        }
        return 0;
    }

    public static double formatNumberReturnDouble(Object value, String pattern) {
        if (value != null) {
            DecimalFormat format = new DecimalFormat(pattern);
            format.setRoundingMode(RoundingMode.HALF_UP);
            return Double.valueOf(format.format(value));
        }
        return 0.0;
    }
}
