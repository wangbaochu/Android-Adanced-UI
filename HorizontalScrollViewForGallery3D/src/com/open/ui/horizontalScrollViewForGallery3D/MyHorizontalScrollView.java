package com.open.ui.horizontalScrollViewForGallery3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mytesting.R;
import com.nineoldandroids.view.ViewHelper;

public class MyHorizontalScrollView extends HorizontalScrollView {

    private Context mContext;

    /** The item data to display on screen */
    private List<Integer> mDatas; 

    /** The item parent layout */
    private LinearLayout mContainer;

    /** The screent center of HorizontalScrollView */
    private int mScrollViewCenter;

    /** The width of item */
    private int mChildWidth;

    /** The index of the initial centered displayed item */
    private int mInitialDisplayedItemIndex;

    /** Record the initial scrollX of the HorizontalScrollView */
    private int mInitialScrollX;

    /** A flag indicate which direction the HorizontalScrollView moved */
    private boolean mIsScrollRight = false;

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mDatas = new ArrayList<Integer>(Arrays.asList(
                R.drawable.a, R.drawable.b, R.drawable.c, 
                R.drawable.d, R.drawable.e, R.drawable.f,
                R.drawable.g, R.drawable.h, R.drawable.i));
    }

    /**
     * Initialize the display item data
     */
    public void initDatas() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int count = mDatas.size();
        mContainer = (LinearLayout) getChildAt(0);
        for (int i = 0; i < count; i++) {
            ImageView convertView = (ImageView) inflater.inflate(R.layout.item_layout, null, false);
            convertView.setImageResource(mDatas.get(i));
            mContainer.addView(convertView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mChildWidth = mContainer.getChildAt(0).getMeasuredWidth();
            mScrollViewCenter = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();

            //Find which item is most closed to the screen center 
            int itemCountOnScreen = Math.round((float)getWidth() / mChildWidth);
            int centerToScroll = 0;
            int centerDistance = Integer.MAX_VALUE;
            for (int j = 0; j < itemCountOnScreen; j++) {
                View child = mContainer.getChildAt(j);
                int childCenter = getCenterOfChildView(child);
                int delta = Math.abs(childCenter - mScrollViewCenter);
                if (delta < centerDistance) {
                    centerDistance = delta;
                    centerToScroll = childCenter - mScrollViewCenter;
                    mInitialDisplayedItemIndex = j;
                }
            }

            //Initial the location of the HorizontalScrollView
            this.scrollTo(centerToScroll, 0);
            mInitialScrollX = centerToScroll > 0 ? centerToScroll : 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_UP:
            //Force to move to the center of an item.
            boundToMoveToCenteredItem();
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mIsScrollRight = l > oldl ? true : false;
        startTransform();
    }

    /**
     * Transform the child view
     */
    private void startTransform() {
        int count = mContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mContainer.getChildAt(i);
            int childCenter = getCenterOfChildView(child);
            int delta = Math.abs(childCenter - mScrollViewCenter);
            float scaleFactor = 1.0f;
            if (delta > mChildWidth) {
                scaleFactor = 0.3f;
            } else {
                scaleFactor = 0.3f + 0.7f * ((float)(mChildWidth - delta)/mChildWidth);
            }

            /**
             * Here we use android third party property animation sdk: nineoldandroids-2.4.0.jar
             * This library also includes support for animating rotation, translation, alpha, 
             * and scale on platforms prior to Android 3.0(API-11)
             * Reference to:
             * http://nineoldandroids.com/
             * https://github.com/JakeWharton/NineOldAndroids
             */
            ViewHelper.setPivotX(child, child.getWidth() / 2);
            ViewHelper.setPivotY(child, child.getHeight() / 2);
            ViewHelper.setScaleX(child, scaleFactor);
            ViewHelper.setScaleY(child, scaleFactor);
            //ViewHelper.setAlpha(child, alpha);
        }
    }

    /**
     * Get the center of child view
     */
    private int getCenterOfChildView(View view) {
        int left = view.getLeft();
        int scrollX = this.getScrollX() ;
        return left - scrollX + view.getWidth() / 2;
    }

    /**
     * Move to the center of an item according to the direction and distance.
     */
    private void boundToMoveToCenteredItem() {
        int centerToScroll = 0;
        int centerDistance = Integer.MAX_VALUE;
        int scrollX = this.getScrollX() - mInitialScrollX;
        int current = mInitialDisplayedItemIndex + scrollX / mChildWidth;
        int newCenterItemIndex = mInitialDisplayedItemIndex;

        //Find which item is most closed to the screen center
        for (int i = current - 1; i <= current + 1; i++) {
            if (i >= 0 && i < mContainer.getChildCount()) {
                View child = mContainer.getChildAt(i);
                int childCenter = getCenterOfChildView(child);
                int delta = Math.abs(childCenter - mScrollViewCenter);
                if (delta < centerDistance) {
                    centerDistance = delta;
                    centerToScroll = childCenter - mScrollViewCenter;
                    newCenterItemIndex = i;
                }
            }
        }

        if (centerToScroll < 0) {
            //The item that most closed to mScrollViewCenter is located at left of mScrollViewCenter
            if (mIsScrollRight) {
                if (newCenterItemIndex + 1 < mContainer.getChildCount() - 1) {
                    //Move to the next item(index = newCenterItemIndex + 1)
                    this.smoothScrollBy(mChildWidth + centerToScroll, 0);
                } else {
                    //Move to the next item(index = newCenterItemIndex)
                    this.smoothScrollBy(centerToScroll, 0);
                }
            } else {
                //Move to the next item(index = newCenterItemIndex)
                this.smoothScrollBy(centerToScroll, 0);
            }
        } else {
            //The item that most closed to mScrollViewCenter is located at right of mScrollViewCenter
            if (mIsScrollRight) {
                //Move to the next item(index = newCenterItemIndex)
                this.smoothScrollBy(centerToScroll, 0);
            } else {
                if (newCenterItemIndex - 1 > 0) {
                    //Move to the previous item(index = newCenterItemIndex - 1)
                    this.smoothScrollBy(centerToScroll - mChildWidth, 0);
                } else {
                    //Move to the next item(index = newCenterItemIndex)
                    this.smoothScrollBy(centerToScroll, 0);
                }
            }
        }
    }
}
