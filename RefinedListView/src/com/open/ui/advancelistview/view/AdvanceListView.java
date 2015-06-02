package com.open.ui.advancelistview.view;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AdvanceListView extends ListView implements OnScrollListener {

    private Context mContext;
    protected AdvanceListViewAdapter mALiListAdapter;
    private ListViewImageLoader mListViewImageLoader;
    private OnScrollListener mOutOnScrollListenerList;
    private OnLastItemListener mIOnListLastItemListener;
    private boolean mResetLastVisible;

    public AdvanceListView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public AdvanceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    /**
     * Initialize some attributes of list view.
     */
    protected void init(Context context) {
        setDividerHeight(0);
        setCacheColorHint(0x00000000);
        setSelector(android.R.color.transparent);
        super.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mALiListAdapter = (AdvanceListViewAdapter) adapter;
        mListViewImageLoader = new ListViewImageLoader(mContext);
        mALiListAdapter.setListViewImageLoader(this, mListViewImageLoader);

        super.setAdapter(adapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case SCROLL_STATE_IDLE:
            // judge if the list view scroll to the bottom. 
            Log.i("getLastVisiblePosition", "getLastVisiblePosition getLastVisiblePosition " + getLastVisiblePosition()
                    + " getCount "+getCount());
            if (getLastVisiblePosition() == (getCount() - 1)) {  
                if(mResetLastVisible){
                    if(mIOnListLastItemListener != null){
                        Log.i("ALiListView", "ALiListView  onScrollStateChanged last item" );
                        mIOnListLastItemListener.onLastItemVisible();
                    }
                    mResetLastVisible = false;
                }
            }else{
                mResetLastVisible = true;
            }

            loadCurrentScreenItemIcon();

            break;
        case SCROLL_STATE_TOUCH_SCROLL:
        case SCROLL_STATE_FLING:
            if(mALiListAdapter != null){
                mALiListAdapter.setIsScorllOnec(true);
            }
            break;
        }
        if (mOutOnScrollListenerList != null) {
            mOutOnScrollListenerList.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (mOutOnScrollListenerList != null) {
            mOutOnScrollListenerList.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }

        if (getLastVisiblePosition() != (getCount() - 1)) {  
            mResetLastVisible = true;
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        if (listener != this) {
            mOutOnScrollListenerList = listener;
        } else {
            super.setOnScrollListener(listener);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        try {
            Log.i("onDetachedFromWindow", "onDetachedFromWindow onDestroy();");
            onDestroy();
            super.onDetachedFromWindow();
        } catch (Exception e) {
        }
    }

    /**
     * set the listener to observe if the list view scroll to last the item on the bottom.
     */
    public void setIOnListLastItemListener(OnLastItemListener onListLastItemListener){
        mIOnListLastItemListener = onListLastItemListener;
    }

    /**
     * This is a high performance function, it only update the current visible item on screen.
     */
    private void loadCurrentScreenItemIcon() {
        if (mListViewImageLoader != null && mALiListAdapter != null) {

            int firstPos = getFirstVisiblePosition() - getHeaderViewsCount();
            int lastPos = getLastVisiblePosition() - getHeaderViewsCount();
            if (firstPos < 0) {
                firstPos = 0;
            }

            for (int i = firstPos; i <= lastPos; i++) {
                if (i > mALiListAdapter.getItemlistData().size() - 1) {
                    return;
                }
                BaseItemModel model = mALiListAdapter.getItemlistData().get(i);
                ItemImageModel imageModel = model.getImageModel();
                if (imageModel != null) {
                    SoftReference<Drawable> ref = imageModel.getSoftReferenceIcon();
                    if (ref == null || ref.get() == null) {
                        mListViewImageLoader.addTask(model);
                    }
                }
            }
        }
    }
    
    /**
     * Destroy all the resource of list view
     */
    private void onDestroy() {
        if (mListViewImageLoader != null) {
            mListViewImageLoader.onDestroy();
            mListViewImageLoader = null;
        }
    }

}
