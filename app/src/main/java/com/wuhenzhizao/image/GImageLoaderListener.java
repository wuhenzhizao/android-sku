package com.wuhenzhizao.image;

import android.support.annotation.Nullable;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by wuhenzhizao on 2017/4/25.
 */

public abstract class GImageLoaderListener<INFO> {
    /**
     * Called before the image request is submitted.
     * <p> IMPORTANT: It is not safe to reuse the controller from within this callback!
     *
     * @param imageView
     * @param id            controller id
     * @param callerContext caller context
     */
    public void onSubmit(SimpleDraweeView imageView, String id, Object callerContext) {}

    /**
     * Called after the final image has been set.
     *
     * @param imageView
     * @param id        controller id
     * @param imageInfo image info
     * @param imageView
     */
    public abstract void onFinalImageSet(SimpleDraweeView imageView, String id, @Nullable INFO imageInfo);

    /**
     * Called after any intermediate image has been set.
     *
     * @param imageView
     * @param id        controller id
     * @param imageInfo image info
     */
    public void onIntermediateImageSet(SimpleDraweeView imageView, String id, @Nullable INFO imageInfo){}

    /**
     * Called after the fetch of the intermediate image failed.
     *
     * @param imageView
     * @param id        controller id
     * @param throwable failure cause
     */
    public void onIntermediateImageFailed(SimpleDraweeView imageView, String id, Throwable throwable){}

    /**
     * Called after the fetch of the final image failed.
     *
     * @param imageView
     * @param id        controller id
     * @param throwable failure cause
     */
    public void onFailure(SimpleDraweeView imageView, String id, Throwable throwable){}

    /**
     * Called after the controller released the fetched image.
     * <p> IMPORTANT: It is not safe to reuse the controller from within this callback!
     *
     * @param imageView
     * @param id controller id
     */
    public void onRelease(SimpleDraweeView imageView, String id){}
}
