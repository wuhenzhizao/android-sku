package com.wuhenzhizao.image;

import android.content.Context;

import com.wuhenzhizao.sku.utils.ScreenUtils;

/**
 * Created by wuxiaowei on 16/7/5.
 * wiki地址: http://redmine.gomeo2omx.cn/projects/server/wiki/缩略图服务使用说明
 * 缩略图 URL 格式：http://i-test.meixincdn.com/源图ID.转图参数.后缀名
 * 其中转图参数部分格式为 [宽像素数][x高像素数][c剪取位置][z][q质量]。
 */
public class GImageUrlUtils {

//    private static String QUALITY = "q90";//图片质量默认90
//    private static String ZOOM = "z";//缩放

    public enum Extension {
        PNG(".png"), JPG(".jpg"), GIF(".gif");

        private final String value;

        Extension(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * @param context     上下文
     * @param oldUrl      原图地址
     * @param size        新图在屏幕中的比例
     * @param aspectRatio 新图的宽高比
     * @return 处理以后的图片路径
     * <p/>
     * 例子:
     * 原路径:https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK.jpg
     * 生成路径:https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK.jpg.540x216.jpg
     * 原路径无扩展名: https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK
     * 生成路径: https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK.540x216.jpg
     */
    public static String getUrl(Context context, String oldUrl, ImageWidth size, AspectRatio aspectRatio) {

        return getUrl(context, oldUrl, size, aspectRatio, null);
    }

    /**
     * @param context     上下文
     * @param oldUrl      原图地址
     * @param size        新图在屏幕中的比例
     * @param aspectRatio 新图的宽高比
     * @param extension   原图路径没有扩展名的时候，需要的扩展名
     * @return 处理以后的图片路径
     * <p/>
     * 例子:
     * 原路径:https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK.jpg
     * 生成路径:https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK.jpg.540x216.jpg
     * 原路径无扩展名: https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK
     * 生成路径: https://i-pre.meixincdn.com/v1/img/T15aATBCZT1RXrhCrK.540x216.gif
     */
    public static String getUrl(Context context, final String oldUrl, ImageWidth size, AspectRatio aspectRatio, Extension extension) {
        //没有url直接返回null
        if (oldUrl == null) {
            return null;
        }
        int[] screenSize = ScreenUtils.getScreenPixelSize(context);
        if (screenSize == null || screenSize.length == 0) {
            return oldUrl;
        }

        int screenWidth = screenSize[0];
        int newImageWidth = getStandardWidth((int) (screenWidth * size.getValue()));
        String height = "";
        if (aspectRatio != AspectRatio.UNSPECIFIED) {
            height = "x" + (int) (newImageWidth / aspectRatio.getValue());
        }

        String newExtension = getExtension(oldUrl);
        if ("".equals(newExtension)) {
            if (extension == null) {
                newExtension = Extension.JPG.getValue();
            } else {
                newExtension = extension.getValue();
            }
        }

        return String.format("%s.%s%s%s", oldUrl, newImageWidth, height, newExtension);
    }

    /**
     * 规范宽
     *
     * @param width
     * @return
     */
    private static int getStandardWidth(int width) {
        if (width <= 0) {
            return 60;
        } else if (width > 0 && width < 60) {
            return 60;
        } else if (width > 60 && width < 90) {
            return 90;
        } else if (width > 90 && width < 120) {
            return 120;
        } else if (width > 120 && width < 180) {
            return 180;
        } else if (width > 180 && width < 240) {
            return 240;
        } else if (width > 240 && width < 360) {
            return 360;
        } else if (width > 360 && width < 480) {
            return 480;
        } else if (width > 480 && width < 540) {
            return 540;
        } else if (width > 540 && width < 720) {
            return 720;
        } else if (width > 720 && width < 1080) {
            return 1080;
        } else if (width > 1080) {
            return 1080;
        }
        return width;
    }

    /**
     * 通过原url获取扩展名
     *
     * @param oldUrl
     * @return
     */
    private static String getExtension(String oldUrl) {
        String end;
        //根据图片后缀处理图片
        if (oldUrl.toLowerCase().endsWith(".png")) {
            end = ".png";
        } else if (oldUrl.toLowerCase().endsWith(".jpg")) {
            end = ".jpg";
        } else if (oldUrl.toLowerCase().endsWith(".gif")) {
            end = ".gif";
        } else {
            end = "";
        }
        return end;
    }


}
