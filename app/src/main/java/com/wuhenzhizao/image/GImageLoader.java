package com.wuhenzhizao.image;

import android.app.Application;
import android.content.Context;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;

import okhttp3.OkHttpClient;

/**
 * 图片加载封装类
 * <p>
 * Created by wuhenzhizao on 15/12/31.
 */
public class GImageLoader {
    /**
     * Frasco初始化
     *
     * @param context
     */
    public static void init(Application context, OkHttpClient okHttpClient) {
        Fresco.initialize(context, GImageConfig.getOkHttpImagePipelineConfig(context, okHttpClient));
    }

    /**
     * 通过图片资源ID，显示图片
     *
     * @param callerContext
     * @param view
     * @param resId         ：app内部图片ID
     */
    public static void displayRes(Context callerContext, SimpleDraweeView view, int resId) {
        GImageConfig.load(callerContext, view, resId);
    }

    /**
     * 显示Url图片, 原图
     *
     * @param callerContext
     * @param view
     * @param url           : 图片地址
     *                      支持的类型：
     *                      远程图片	http://, https://	HttpURLConnection 或者参考 使用其他网络加载方案
     *                      本地文件	file://	FileInputStream
     *                      Content provider	content://	ContentResolver
     *                      asset目录下的资源	asset://	AssetManager
     *                      res目录下的资源	res://	Resources.openRawResource
     */
    public static void displayUrl(Context callerContext, SimpleDraweeView view, String url) {
        display(callerContext, view, url, null, null, null);
    }

    /**
     * 加载本地图片，并进行压缩展示
     *
     * @param callerContext
     * @param view
     * @param url
     * @param options
     */
    public static void displayLocalUrl(Context callerContext, SimpleDraweeView view, String url, ResizeOptions options) {
        display(callerContext, view, url, null, null, options);
    }

    /**
     * 显示Url图片，使用切图服务
     * 应用场景：获取服务端指定大小图片
     *
     * @param callerContext
     * @param view
     * @param url
     * @param width
     * @param ratio
     */
    public static void displayResizeUrl(Context callerContext, SimpleDraweeView view, String url, ImageWidth width, AspectRatio ratio) {
        display(callerContext, view, url, null, null, null, width, ratio, ImageRequest.RequestLevel.FULL_FETCH, null);
    }

    /**
     * 显示图片
     *
     * @param callerContext ：绑定的对象，可以是Actiivty或Fragment
     * @param view          : 显示图片的控件
     * @param url           ：图片地址
     * @param thumbnailUrl  ：低分辨率图片地址
     * @param priority      ：优先级，可以设置是否优先加载, LOW, MEDIUM, HIGH
     */
    @Deprecated
    public static void display(Context callerContext, SimpleDraweeView view, String url, String thumbnailUrl, Priority priority, ResizeOptions options) {
        GImageConfig.load(callerContext, view, url, thumbnailUrl, priority, options, ImageRequest.RequestLevel.FULL_FETCH, false, false, null);
    }

    /**
     * 显示图片总方法
     *
     * @param callerContext
     * @param view
     * @param url
     * @param thumbnailUrl
     * @param priority
     * @param options
     * @param width
     * @param ratio
     */
    public static void display(
            Context callerContext,
            SimpleDraweeView view,
            String url,
            String thumbnailUrl,
            Priority priority,
            ResizeOptions options,
            ImageWidth width,
            AspectRatio ratio,
            ImageRequest.RequestLevel level,
            GImageLoaderListener listener) {
        url = GImageUrlUtils.getUrl(callerContext, url, width, ratio);
        GImageConfig.load(callerContext, view, url, thumbnailUrl, priority, options, level, false, false, listener);
    }

    /**
     * 清理内存缓存，在内存不足时手动调用
     */
    public static void clearMemoryCache() {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    /**
     * 清理硬盘缓存
     */
    public static void clearDiskCache() {
        ImagePipelineFactory factory = Fresco.getImagePipelineFactory();
        factory.getImagePipeline().clearDiskCaches();
    }

    /**
     * 获取缓存大小
     *
     * @return
     */
    public static double getDiskCacheSize() {
        return Fresco.getImagePipelineFactory().getMainFileCache().getSize();
    }

    /**
     * 获取fresco本地缓存图片
     *
     * @param url
     */
    public static File getDiskCache(String url) {
        File targetFile = null;
        if (url != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(url));
            if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                targetFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
                targetFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return targetFile;
    }

    /**
     * 释放图片缓存资源
     */
    public static void releaseResources() {
        Fresco.shutDown();
    }

    /**
     * 是否在移动网络下显示图片
     *
     * @param showImageInMobileNetwork
     */
    public static void setShowImageInMobileNetwork(boolean showImageInMobileNetwork) {
        GImageConfig.setUndisplayOutOffWifiNetWork(showImageInMobileNetwork);
    }
}
