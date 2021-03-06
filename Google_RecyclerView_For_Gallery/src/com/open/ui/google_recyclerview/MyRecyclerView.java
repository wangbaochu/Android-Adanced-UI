package com.open.ui.google_recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyRecyclerView extends RecyclerView implements OnScrollListener {
    
    public interface OnItemScrollChangeListener {
        void onChange(View view, int position);
    }

    /**
     * 记录当前第一个View
     */
    private View mCurrentView;

    private OnItemScrollChangeListener mItemScrollChangeListener;
    public void setOnItemScrollChangeListener(OnItemScrollChangeListener mItemScrollChangeListener) {
        this.mItemScrollChangeListener = mItemScrollChangeListener;
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mCurrentView = getChildAt(0);
        if (mItemScrollChangeListener != null) {
            mItemScrollChangeListener.onChange(mCurrentView, getChildPosition(mCurrentView));
        }
    }

    @Override
    public void onScrollStateChanged(int arg0) {
    }

    /**
     * 滚动时，判断当前第一个View是否发生变化，发生才回调
     */
    @Override
    public void onScrolled(int dx, int dy) {
        int scollX = getScollXDistance();
        Log.i("MyRecyclerView", "getScollXDistance = " + scollX);
        
        View newView = getChildAt(0);
        if (mItemScrollChangeListener != null) {
            if (newView != null && newView != mCurrentView) {
                mCurrentView = newView;
                mItemScrollChangeListener.onChange(mCurrentView, getChildPosition(mCurrentView));
            }
        }
    }
   
    /**
     * The default getScrollX() doesn't work, change to use this function.
     * @return the total scroll X distance
     */
   public int getScollXDistance() {
       LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
       int position = layoutManager.findFirstVisibleItemPosition();
       View firstVisiableChildView = layoutManager.findViewByPosition(position);
       int itemWidth = firstVisiableChildView.getWidth();
       return (position) * itemWidth - firstVisiableChildView.getLeft();
   }
}
