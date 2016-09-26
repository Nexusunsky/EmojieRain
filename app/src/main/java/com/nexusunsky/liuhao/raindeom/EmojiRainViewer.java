package com.nexusunsky.liuhao.raindeom;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author LiuHao
 * @time 16/9/26 上午9:25
 */
public class EmojiRainViewer {

    private int screenWidth;
    private int screenHeight;

    private ViewGroup rainRoot;//承载EmojieRain的根视图
    private Activity mActivity;

    private ITriggerCondition mCondition;//触发调节
    private List<ImageView> views;
    private List<ObjectAnimator> mAnimators;
    private int mViewCount = 0;

    interface ITriggerCondition {
        /**
         * @param objectAnimator 封装的动画对象
         * @return true 返回true,则执行当前动画,反之不执行。
         */
        boolean triggerCondition(List<ObjectAnimator> objectAnimator);
    }

    public EmojiRainViewer(Activity activity, ViewGroup rainRootView) {
        mActivity = activity;
        rainRoot = rainRootView;
        measureScreen();
    }

    /**
     * Emojie的设置
     *
     * @param count      Emojie个数
     * @param resId      Emojie图片ID
     * @param fallenTiem Emojie下落时间
     * @param condition  触发EmojieRain条件回调
     */
    public void preparEmojiRain(int count, int resId, int fallenTiem, ITriggerCondition condition) {
        mCondition = condition;
        initCache();
        if (mViewCount != count - 1) {
            for (int i = 0; i < count; i++) {
                ImageView emoji = new ImageView(mActivity);
                emoji.setImageResource(resId);
                ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(90, 90);
                views.add(emoji);
                rainRoot.addView(emoji, 1, layoutParams);
                mViewCount = i;
            }
        }
        createAnimator(fallenTiem, count);
        if (mCondition.triggerCondition(mAnimators)) {
            for (ObjectAnimator animator : mAnimators) {
                if (!animator.isRunning()) {
                    animator.start();
                }
            }
        }
    }

    /**
     * 资源释放,必须在Destory时执行。
     */
    public void releaseResource() {
        clearCache();
        if (mActivity != null) {
            mActivity = null;
        }
        if (rainRoot != null) {
            rainRoot = null;
        }
        if (mCondition != null) {
            mCondition = null;
        }
    }

    private void createAnimator(int fallenTiem, int emojicount) {
        for (int i = 0; i < emojicount; i++) {
            ImageView view = views.get(i);
            view.setY(-(new Random().nextInt(440) + 77));
            view.setX(new Random().nextInt(screenWidth - dip2px(mActivity, 77) - 27) + 27);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                    view, "translationY",
                    view.getY(), (float) (screenHeight)).setDuration(new Random().nextInt(2000) + fallenTiem);
            mAnimators.add(objectAnimator);
        }
    }

    private void initCache() {
        if (views == null) {
            views = new ArrayList<>();
        }
        if (mAnimators == null) {
            mAnimators = new ArrayList<>();
        }
        mAnimators.clear();
    }

    private void clearCache() {
        if (views != null && mAnimators != null) {
            for (int i = 0; i < views.size(); i++) {
                ObjectAnimator animator = mAnimators.get(i);
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                }
                ImageView view = views.get(i);
                rainRoot.removeView(view);
            }
        }
        views = null;
        mAnimators = null;
    }

    /**
     * 获取屏幕的宽,高。
     */
    private void measureScreen() {
        DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    /**
     * 工具类完成dp到px的转换
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}