/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.wuhenzhizao.image;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Sets;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wuhenzhizao.utils.TelephoneUtils;

import okhttp3.OkHttpClient;

/**
 * 图片配置
 */
class GImageConfig {
    /***
     * 最大可运行内存
     */
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    /**
     * 最大文件缓存限制
     */
    private static final int MAX_DISK_CACHE_SIZE = 40 * ByteConstants.MB;

    /**
     * 最大内存限制
     */
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;

    /**
     * 缓存目录
     */
    private static final String IMAGE_PIPELINE_CACHE_DIR = "Android/data/com.mx.os/FrescoCache";

    /**
     * 小缓存目录
     */
    private static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "Android/data/com.mx.os/FrescoSmallCache";

    /**
     * 是否显示图片
     */
    public static final boolean DISPLAY_IMAGE = true;

    /**
     * Pipeline配置
     */
    private static ImagePipelineConfig sOkHttpImagePipelineConfig;

    /**
     * 非wifi环境是否显示图片
     */
    private static boolean undisplayOutOffWifiNetWork = false;

    /**
     * Creates config using OkHttp as network backed.
     */
    public static ImagePipelineConfig getOkHttpImagePipelineConfig(Context context, OkHttpClient okHttpClient) {
        if (sOkHttpImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient);
            configureCaches(context, configBuilder);
            configureLoggingListeners(configBuilder);
            sOkHttpImagePipelineConfig = configBuilder.build();
        }
        return sOkHttpImagePipelineConfig;
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(final Context context, ImagePipelineConfig.Builder configBuilder) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                GImageConfig.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                GImageConfig.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size
        configBuilder.setBitmapMemoryCacheParamsSupplier(
                new Supplier<MemoryCacheParams>() {
                    public MemoryCacheParams get() {
                        return bitmapCacheParams;
                    }
                })
                .setDecodeFileDescriptorEnabled(true)
//                .setBitmapsConfig(Bitmap.Config.ARGB_8888) //TODO: if need high picture quality, remove comment here
//                .setResizeAndRotateEnabledForNetwork(true)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig());
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            configBuilder.setMainDiskCacheConfig(
                    DiskCacheConfig.newBuilder(context)
                            .setBaseDirectoryPath(Environment.getExternalStorageDirectory())
                            .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                            .setMaxCacheSize(GImageConfig.MAX_DISK_CACHE_SIZE)
                            .build())
                    .setSmallImageDiskCacheConfig(
                            DiskCacheConfig.newBuilder(context)
                                    .setBaseDirectoryPath(Environment.getExternalStorageDirectory())
                                    .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)
                                    .setMaxCacheSize(GImageConfig.MAX_DISK_CACHE_SIZE / 2)
                                    .build());
        } else {
            configBuilder.setMainDiskCacheConfig(
                    DiskCacheConfig.newBuilder(context)
                            .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                            .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                            .setMaxCacheSize(GImageConfig.MAX_DISK_CACHE_SIZE)
                            .build())
                    .setSmallImageDiskCacheConfig(
                            DiskCacheConfig.newBuilder(context)
                                    .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                                    .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)
                                    .setMaxCacheSize(GImageConfig.MAX_DISK_CACHE_SIZE / 2)
                                    .build());
        }
    }

    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        configBuilder.setRequestListeners(Sets.newHashSet((RequestListener) new RequestLoggingListener()));
    }

    private static ImageRequestBuilder setImageRequestBuilder(ImageRequestBuilder builder, Priority priority, ResizeOptions options, ImageRequest.RequestLevel level, boolean persistentCache) {
        builder.setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
//                .setAutoRotateEnabled(true)                      // 支持自动旋转
//                .setLocalThumbnailPreviewsEnabled(true)          // 设置使用本地预览图
                .setProgressiveRenderingEnabled(true);             // 支持渐进式图片
        if (persistentCache) {
            builder.setImageType(ImageRequest.ImageType.SMALL);
        }
        if (priority != null) {
            builder.setRequestPriority(priority);                  // 设置优先级
        }
        if (options != null) {
            builder.setResizeOptions(options);
        }
        return builder;
    }

    private static ImageRequest createImageRequestWidthUrl(String url, Priority priority, ResizeOptions options, ImageRequest.RequestLevel level, boolean persistentCache) {
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        builder = setImageRequestBuilder(builder, priority, options, level, persistentCache);
        return builder.build();
    }

    private static ImageRequest createImageRequestWidthLocalResource(int resId) {
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithResourceId(resId);
        builder = setImageRequestBuilder(builder, null, null, ImageRequest.RequestLevel.FULL_FETCH, false);
        return builder.build();
    }

    /**
     * 获取PipelineDraweeControllerBuilder实例，实现对加载图片的控制和定制
     *
     * @param callerContext
     * @param view
     * @param listener
     * @return
     */
    private static PipelineDraweeControllerBuilder getControllerBuilder(Context callerContext, final SimpleDraweeView view, final GImageLoaderListener listener) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setRetainImageOnFailure(false)
                .setOldController(view.getController())
                .setAutoPlayAnimations(true)
                .setCallerContext(callerContext)
                .setTapToRetryEnabled(false);
        if (listener != null) {
            builder.setControllerListener(new ControllerListener<ImageInfo>() {
                @Override
                public void onSubmit(String id, Object callerContext) {
                    listener.onSubmit(view, id, callerContext);
                }

                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    listener.onFinalImageSet(view, id, imageInfo);
                }

                @Override
                public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                    listener.onIntermediateImageSet(view, id, imageInfo);
                }

                @Override
                public void onIntermediateImageFailed(String id, Throwable throwable) {
                    listener.onIntermediateImageFailed(view, id, throwable);
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    listener.onFailure(view, id, throwable);
                }

                @Override
                public void onRelease(String id) {
                    listener.onRelease(view, id);
                }
            });
        }
        return builder;
    }

    /**
     * 通过资源Id加载图片
     *
     * @param callerContext
     * @param view
     * @param resId
     */
    static void load(Context callerContext, SimpleDraweeView view, int resId) {
        PipelineDraweeControllerBuilder controllerBuilder = getControllerBuilder(callerContext, view, null);
        ImageRequest request = createImageRequestWidthLocalResource(resId);
        controllerBuilder.setImageRequest(request);
        view.setController(controllerBuilder.build());
    }

    /**
     * 加载图片方法，
     * 支持的类型：
     * 远程图片	http://, https://	HttpURLConnection 或者参考 使用其他网络加载方案
     * 本地文件	file://	FileInputStream
     * Content provider	content://	ContentResolver
     * asset目录下的资源	asset://	AssetManager
     * res目录下的资源	res://	Resources.openRawResource
     *
     * @param callerContext
     * @param view
     * @param url
     * @param thumbnailUrl
     * @param priority
     * @param options
     * @param persistentCache
     * @param onlyLoadCache
     */
    static void load(Context callerContext, SimpleDraweeView view, String url, String thumbnailUrl, Priority priority, ResizeOptions options, ImageRequest.RequestLevel level, boolean persistentCache, boolean onlyLoadCache, GImageLoaderListener listener) {
        // 禁用网络图片的情况下,只加载本地缓存图片
        if (!onlyLoadCache && undisplayOutOffWifiNetWork && !TelephoneUtils.isWifiEnable(callerContext)) {
            if (url == null) {
                url = "";
            }
            Uri uri = Uri.parse(url);
            boolean isInMemoryCache = Fresco.getImagePipeline().isInBitmapMemoryCache(uri);
            DataSource<Boolean> dataSource = Fresco.getImagePipeline().isInDiskCache(uri);
            boolean isInDiskCache = (dataSource != null && dataSource.hasResult() && dataSource.getResult());
            if (!isInMemoryCache && !isInDiskCache) return;
        }

        ImageRequest thumbnailRequest = null;
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            thumbnailRequest = createImageRequestWidthUrl(thumbnailUrl, priority, options, level, persistentCache);
        }
        ImageRequest request = null;
        if (!TextUtils.isEmpty(url)) {
            request = createImageRequestWidthUrl(url, priority, options, level, persistentCache);
        }

        PipelineDraweeControllerBuilder controllerBuilder = getControllerBuilder(callerContext, view, listener);
        if (thumbnailRequest != null) {
            controllerBuilder.setLowResImageRequest(thumbnailRequest);
        }
        if (request != null) {
            controllerBuilder.setImageRequest(request);
        }

        view.setController(controllerBuilder.build());
    }

    static void setUndisplayOutOffWifiNetWork(boolean show) {
        undisplayOutOffWifiNetWork = show;
    }
}
