package com.wuhenzhizao;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * @Description: 购物车添加动画
 * @Date:15/7/1 下午12:12
 * @author:liufei
 */
public class AddToCartAnimation extends AnimationSet {

    public AddToCartAnimation(int[] startLocation, int[] topCartLocation) {
        super(false);

        ScaleAnimation expandAnimation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        expandAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        expandAnimation.setDuration(350);

        ScaleAnimation collapseAnimation = new ScaleAnimation(1.0f, 0.2f, 1.0f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        collapseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        collapseAnimation.setDuration(700);
        collapseAnimation.setStartOffset(350);

        // 计算位移
        int endX = topCartLocation[0] - startLocation[0];// 动画位移的X坐标
        int endY = topCartLocation[1] - startLocation[1];// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setDuration(700);
        translateAnimationX.setStartOffset(350);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, 0, endY);
        translateAnimationY.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationY.setDuration(700);
        translateAnimationY.setStartOffset(350);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.8f);
        translateAnimationY.setDuration(700);
        translateAnimationY.setStartOffset(350);

        setFillAfter(false);
        addAnimation(expandAnimation);
        addAnimation(collapseAnimation);
        addAnimation(translateAnimationX);
        addAnimation(translateAnimationY);
        addAnimation(alphaAnimation);
    }
}
