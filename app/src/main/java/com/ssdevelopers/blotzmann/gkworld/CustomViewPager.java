package com.ssdevelopers.blotzmann.gkworld;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {

    private Boolean disable = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !disable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !disable && super.onInterceptTouchEvent(ev);
    }

    public void disableScroll(Boolean disable){
        //When disable = true not work the scroll and when disble = false work the scroll
        this.disable = disable;
    }
}
