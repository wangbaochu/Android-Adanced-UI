package com.open.ui.refinedmenudrawer.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MenuDrawerLayout extends DrawerLayout {

    public MenuDrawerLayout(Context context) {
        super(context);
    }
    
    public MenuDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        boolean handled = false;
        try {
            handled = super.onInterceptTouchEvent(arg0);
        } catch (ArrayIndexOutOfBoundsException e) {
            handled = false;
        }
        return handled;
    }
}