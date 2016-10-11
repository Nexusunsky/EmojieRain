package com.nexusunsky.liuhao.raindeom;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 表情雨类
 *
 * @author LiuHao
 * @time 16/9/26 上午9:25
 */
public class EmojieRainViewer {
    public Map<String, Integer> keyWord_emojie_Pair;

    //Map表情雨的关键字与对应表情
    {
        keyWord_emojie_Pair = new HashMap<>();
        Context appContext = AppContext.getInstance().getApplicationContext();
        keyWord_emojie_Pair.put(appContext.getResources().getString(R.string.hw7_emojierain_happynewyear)
                , R.drawable.activity_chat_emojierain_lantern);
        keyWord_emojie_Pair.put(appContext.getResources().getString(R.string.hw7_emojierain_like)
                , R.drawable.activity_chat_emojierain_like);
        keyWord_emojie_Pair.put(appContext.getResources().getString(R.string.hw7_emojierain_goodluck)
                , R.drawable.activity_chat_emojierain_coin);
        keyWord_emojie_Pair.put(appContext.getResources().getString(R.string.hw7_emojierain_redpacket)
                , R.drawable.activity_chat_emojierain_redpacket);
        keyWord_emojie_Pair.put(appContext.getResources().getString(R.string.hw7_emojierain_pinan)
                , R.drawable.activity_chat_emojierain_apple);
        keyWord_emojie_Pair.put(appContext.getResources().getString(R.string.hw7_emojierain_healthwalk)
                , R.drawable.activity_chat_emojierain_feet);
        keyWord_emojie_Pair.put(appContext.getResources().getString(R.string.hw7_emojierain_happybirthday)
                , R.drawable.activity_chat_emojierain_cake);
    }

    private int screenWidth;
    private int screenHeight;

    private ViewGroup rainRoot;//承载EmojieRain的根视图
    private Activity context;

    private ITriggerCondition mCondition;//触发条件
    private List<ImageView> views;
    private Map<ObjectAnimator, ObjectAnimator> mAnimators;
    private int mViewCount = 0;
    private int mCount;
    private int mResId;
    private int mFallenTime;

    public interface ITriggerCondition {
        /**
         * @param objectAnimator 封装的动画对象
         * @return true 返回true,则执行当前动画,反之不执行。
         */
        boolean triggerCondition(Map<ObjectAnimator, ObjectAnimator> objectAnimator);
    }

    public EmojieRainViewer(Activity activity, ViewGroup rainRootView) {
        context = activity;
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
        if (mCount != count || mResId != resId || mFallenTime != fallenTiem || mCondition != condition) {
            //Emojie个数,Emojie图片ID,Emojie下落时间,触发EmojieRain条件回调之一变化时,重置设置
            clearCache();
        }
        mCount = count;
        mResId = resId;
        mFallenTime = fallenTiem;
        mCondition = condition;
        initCache();
        if (mViewCount < mCount) {//防止重复触发表情雨,rainRoot重复添加emoji
            for (int i = 0; i < mCount; i++) {
                ImageView emoji = new ImageView(context);
                emoji.setImageResource(mResId);
                ViewGroup.LayoutParams layoutParams = new RelativeLayout
                        .LayoutParams(dip2px(context, 33), dip2px(context, 33));
                views.add(emoji);
                rainRoot.addView(emoji, 1, layoutParams);
                mViewCount = i;
            }
        }
        createAnimator(mFallenTime, mCount);
        if (mCondition.triggerCondition(mAnimators)) {
            Set<ObjectAnimator> objectAnimators = mAnimators.keySet();
            for (ObjectAnimator yanimator : objectAnimators) {
                ObjectAnimator xanimator = mAnimators.get(yanimator);
                if (!yanimator.isRunning() && !xanimator.isRunning()) {
                    AnimatorSet set = new AnimatorSet();
                    set.play(yanimator).with(xanimator);
                    set.start();
                }
            }
        }
    }

    /**
     * 资源释放,必须在Destory时执行。
     */
    public void releaseResource() {
        clearCache();
        if (context != null) {
            context = null;
        }
        if (rainRoot != null) {
            rainRoot = null;
        }
        if (mCondition != null) {
            mCondition = null;
        }
    }

    private void createAnimator(int fallenTime, int emojicount) {
        for (int i = 0; i < emojicount; i++) {
            ImageView view = views.get(i);
            view.setY(-new Random().nextInt(dip2px(context, 2727)) - dip2px(context, 17));
            ObjectAnimator yAnimator = ObjectAnimator.ofFloat(view, "translationY",
                    view.getY(), (float) (screenHeight)).setDuration(new Random().nextInt(fallenTime) + 5000);
            yAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
            if (0 == (i & 1)) {//奇,偶分离
                ObjectAnimator xAnimator = ObjectAnimator.ofFloat(view, "translationX",
                        screenWidth / 2 + new Random().nextInt(screenWidth) / 2,
                        11 * screenWidth / 28 - new Random().nextInt(screenWidth) / 2)
                        .setDuration(new Random().nextInt(fallenTime) + 5000);
                xAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
                mAnimators.put(yAnimator, xAnimator);
            } else {
                ObjectAnimator xAnimator = ObjectAnimator.ofFloat(view, "translationX",
                        screenWidth / 2 - new Random().nextInt(screenWidth) / 2,
                        11 * screenWidth / 28 + new Random().nextInt(screenWidth) / 2)
                        .setDuration(new Random().nextInt(fallenTime) + 5000);
                xAnimator.setInterpolator(new DecelerateInterpolator(1.5f));
                mAnimators.put(yAnimator, xAnimator);
            }
        }
    }

    private void initCache() {
        if (views == null) {
            views = new ArrayList<>();
        }
        if (mAnimators == null) {
            mAnimators = new HashMap<>();
        }
        mAnimators.clear();
    }

    private void clearCache() {
        if (views != null && views.size() > 0 && mAnimators != null && mAnimators.size() > 0) {
            Set<ObjectAnimator> objectAnimators = mAnimators.keySet();
            for (ObjectAnimator xanimator : objectAnimators) {
                if (null != xanimator && xanimator.isRunning()) {
                    ObjectAnimator yanimator = mAnimators.get(xanimator);
                    ImageView view = (ImageView) xanimator.getTarget();
                    rainRoot.removeView(view);
                    xanimator.cancel();
                    yanimator.cancel();
                }
            }
        }
        views = null;
        mViewCount = 0;
        mAnimators = null;
    }

    /**
     * 获取屏幕的宽,高。
     */
    private void measureScreen() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
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

    /**
     * 动画减速器
     */
    public class DecelerateInterpolator implements Interpolator {
        public DecelerateInterpolator() {
        }

        /**
         * @param factor 动画的快慢度。将factor值设置为1.0f时将产生一条从上向下的y=x^2抛物线。
         *               增加factor到1.0f以上将使渐入的效果增强（也就是说，开头更快，结尾更慢）
         */
        public DecelerateInterpolator(float factor) {
            mFactor = factor;
        }

        public DecelerateInterpolator(Context context, AttributeSet attrs) {
            TypedArray a =
                    context.obtainStyledAttributes(attrs, R.styleable.EmojileRainViewer);

            mFactor = a.getFloat(R.styleable.EmojileRainViewer_accelerateInterpolator, 1.0f);

            a.recycle();
        }

        @Override
        public float getInterpolation(float input) {
            float result;
            if (mFactor == 1.0f) {
                result = (1.0f - ((1.0f - input) * (1.0f - input)));
            } else {
                result = (float) (1.0f - Math.pow((1.0f - input), 2 * mFactor));
            }
            return result;
        }

        private float mFactor = 1.0f;
    }
}