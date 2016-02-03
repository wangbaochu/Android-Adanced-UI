package com.open.ui.refinedslidingdrawer.test;

import com.open.ui.refinedslidingdrawer.R;
import com.open.ui.refinedslidingdrawer.view.SlidingDrawer;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class TestSlidingDrawer extends SlidingDrawer {

    private static final int HANDLE_BACKGROUND = 0x00DDDDDD;
    private static final int HANDLE_OPEN_BACKGROUND_ALPHA = 0xFF;
    private static final int HANDLE_CLOSE_BACKGROUND_ALPHA = 0x80;

    private Activity mActivity;
    private TestListView mListView;
    private View mHeaderContent;
    private TestAdapter mAdapter;
    private View mHandle;
    private boolean mIsListViewScrollOnTop = false;

    /**
     * Setting header to gone doesn't work, must also set its content to gone.
     * This is a known issue about list view.
     */
    public void setListViewHeaderVisibility(int visibility) {
        mHeaderContent.setVisibility(visibility);
    }

    public View getListViewHeaderContent() {
        return mHeaderContent;
    }

    public TestSlidingDrawer(Context context, AttributeSet attrs){
        super(context, attrs);
        mActivity = (Activity) context;
    }

    @Override
    protected boolean isScrolledToTop() {
        return mIsListViewScrollOnTop;
    }

    /**
     * Called each time before start to animating/tracking 
     */
    @Override
    protected void prepareContent() {
        super.prepareContent();
    }

    @Override
    protected void onFinishInflate() {
        mHandle = findViewById(R.id.drawer_handle);

        mListView = (TestListView)findViewById(R.id.drawer_list_view);
        mListView.setDividerHeight(0);
        mListView.setItemsCanFocus(true);
        mAdapter = new TestAdapter(mActivity);
        mListView.setAdapter(mAdapter);

        // Must be called after mAdapter.setListView(), 
        // disable the list view to response the click event, 
        // but let the item response by its own.
        mListView.setOnItemClickListener(null);
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View firstVisibleItemView = mListView.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        Log.d("ListView", "##### 滚动到顶部 #####");
                        mIsListViewScrollOnTop = true;
                        return;
                    }
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = mListView.getChildAt(mListView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == mListView.getHeight()) {
                        Log.d("ListView", "##### 滚动到底部 ######");       
                    }
                }
                mIsListViewScrollOnTop = false;
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //do nothing
            }
            
        });

        //must be called at the end.
        super.onFinishInflate();
        updateHandleBackground(1.0f);
    }
    
    /**
     * Set the variable transparent background for handle. 
     * percent = 0.0f, the handle is on the top position, sliding drawer is expended.
     * percent = 1.0f, the handle is on the top position, sliding drawer is closed.
     */
    public void updateHandleBackground(float percent) {
        int backgroundColor = HANDLE_BACKGROUND;
        if (percent >= 0.0f && percent <= 1.0f) {
            int alpha = (int) (HANDLE_OPEN_BACKGROUND_ALPHA - (HANDLE_OPEN_BACKGROUND_ALPHA - HANDLE_CLOSE_BACKGROUND_ALPHA) * percent);
            backgroundColor = backgroundColor | (alpha << 24);
        }
        mHandle.setBackgroundColor(backgroundColor);
    }
}
