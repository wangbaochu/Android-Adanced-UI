package com.open.ui.viewpager.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TabTitleScrollBar extends View implements TabViewPager.OnPageChangeListener {

    private int mCurrentPage;
    private float mPositionOffset;
    private int mScrollState;
    private TabViewPager mViewPager;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TabTitleScrollBar(Context context) {
        super(context);
    }

    public TabTitleScrollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSelectedColor(Color.parseColor("#ff0d86ff"));
    }

    public TabTitleScrollBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSelectedColor(Color.parseColor("#ff0d86ff"));
    }

    public void setSelectedColor(int selectedColor) {
        mPaint.setColor(selectedColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mViewPager == null) {
            return;
        }
        final int count = mViewPager.getAdapter().getCount();
        if (count == 0) {
            return;
        }

        if (mCurrentPage >= count) {
            mCurrentPage = count -1;
            return;
        }

        final int paddingLeft = getPaddingLeft();
        final float pageWidth = (getWidth() - paddingLeft - getPaddingRight()) / (1f * count);
        final float left = paddingLeft + pageWidth * (mCurrentPage + mPositionOffset) + 20;
        final float right = left + pageWidth - 40;
        final float top = getPaddingTop();
        final float bottom = getHeight() - getPaddingBottom();
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    public void setViewPager(TabViewPager viewPager) {
        if (mViewPager == viewPager) {
            return;
        }
        if (mViewPager != null) {
            //Clear us from the old pager.
            mViewPager.setOnPageChangeListener(null);
        }
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPositionOffset = positionOffset;
        invalidate();
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {

        if (mScrollState == TabViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position;
            mPositionOffset = 0;
            invalidate();
        }
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrollDeltaX(float deltax) {
        if (mListener != null) {
            mListener.onPageScrollDeltaX(deltax);
        }
    }

    private TabViewPager.OnPageChangeListener mListener;

    public void setOnPageChangeListener(TabViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }
}
