package com.xiaolian.amigo.ui.base.swipeback;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;


/**
 * @author zcd
 * @date 17/11/18
 * 可侧滑的activity
 */

public class SwipeBackActivity extends AppCompatActivity implements SwipeBackHelper.SlideBackManager {

    private static final String TAG = "SwipeBackActivity";


    protected   boolean isNeedSwipe = true  ;
    private SwipeBackHelper mSwipeBackHelper;


    protected void setNeedSwipe(boolean needSwipe) {
        isNeedSwipe = needSwipe;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mSwipeBackHelper == null && isNeedSwipe) {
            mSwipeBackHelper = new SwipeBackHelper(this);
        }
        if (mSwipeBackHelper != null && isNeedSwipe ) {
            return mSwipeBackHelper.processTouchEvent(ev) || super.dispatchTouchEvent(ev);
        }else{
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public Activity getSlideActivity() {
        return this;
    }

    @Override
    public boolean supportSlideBack() {
        return true;
    }

    @Override
    public boolean canBeSlideBack() {
        return true;
    }

    @Override
    public void finish() {
        if (mSwipeBackHelper != null) {
            mSwipeBackHelper.finishSwipeImmediately();
            mSwipeBackHelper = null;
        }
        super.finish();
    }
}

