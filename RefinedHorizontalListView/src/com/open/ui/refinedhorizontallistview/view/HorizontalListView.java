package com.open.ui.refinedhorizontallistview.view;

/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

/*
 * The original implementation is in https://github.com/vieux/Android-Horizontal-ListView and the one I modified from is in
 * https://github.com/vieux/Android-Horizontal-ListView/blob/6bdd23963e50fab44d1f5711ddee33c4a256e5bf/HorizontalListView.java
 * 
 * The original implementation does not support trackball movement in the listview and it does not well support adding data in the adapter on the fly. 
 */
import java.util.LinkedList;
import java.util.Queue;

import com.open.ui.refinedhorizontallistview.R;
import com.open.ui.refinedhorizontallistview.test.TestAdapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

public class HorizontalListView extends AdapterView<ListAdapter> implements OnFocusChangeListener {
    
    // the offset that the last focus item to the left or right edge of the screen
    private static final int PREVIEW_OFFSET = 20;
    
    protected ListAdapter mAdapter;
    private int mLeftViewPosition = -1; // The position on the viewport's left
    private int mRightViewPosition = 0; // The position on the viewport's right
    protected int mCurrentX;            // The screenview's position on x axis
    protected int mNextX;               // The next to be screenview's position on x axis, used in layout
    private int mLastLeft = -1;         // Left position of HorizontalListView, relative to parent when onLayout last time
    private int mLastRight = -1;        // Right position of HorizontalListView, relative to parent when onLayout last time
    private int mMaxX = Integer.MAX_VALUE;
    private int mDisplayOffset = 0;     // The display offset of t he leftest item on the viewport
    protected Scroller mScroller;
    private GestureDetector mGesture;
    private Queue<View> mRemovedViewQueue = new LinkedList<View>();
    private OnItemSelectedListener mOnItemSelected;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    private boolean mDataChanged = false;
    private int mCurrentFocusPosition = 0;
    private Drawable mHighlightBackground;
    private Drawable mNormalBackground;

    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        //set default item background
        mNormalBackground = getResources().getDrawable(R.color.transparent);
        mHighlightBackground = null;
        
        initView();
    }
    
    private synchronized void initView() {
        mLeftViewPosition = -1;
        mRightViewPosition = 0;
        mDisplayOffset = 0;
        mLastLeft = -1;
        mLastRight = -1;
        mCurrentX = 0;
        mNextX = 0;
        mCurrentFocusPosition = 0;
        mMaxX = Integer.MAX_VALUE;
        mRemovedViewQueue.clear();
        mScroller = new Scroller(getContext());
        mGesture = new GestureDetector(getContext(), mOnGesture);
        this.setOnFocusChangeListener(this);
        
    }
    
    @Override
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelected = listener;
    }
    
    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        mOnItemClicked = listener;
    }
    
    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mOnItemLongClicked = listener;
    }

    /**
     * The item of this customized HorizontalListView doesn't support android:background="@color/common_selector"
     * in touch mode, but only support in non-touch mode (e.g. using tack boll).
     * The background resource must be passed in to set at runtime.
     */
    public void setHighlightBackgroundResource(int resId) {
        mHighlightBackground = getResources().getDrawable(resId);
    }
    
    public void setNormalBackgroundResource(int resId) {
        mNormalBackground = getResources().getDrawable(resId);
    }
    
    public void setHighlightBackgroundResource(Drawable drawable) {
        mHighlightBackground = drawable;
    }
    
    public void setNormalBackgroundResource(Drawable drawable) {
        if (drawable != null) {
            mNormalBackground = drawable;
        }
    }
    
    // When we are in layout process, don't accept key event.
    // Otherwise, there is some small issue, like if keep pressing right key, the focus may be mess up
    private boolean mCanReceiveKeyEvent = true;
    
    private DataSetObserver mDataObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            synchronized(HorizontalListView.this){
                mDataChanged = true;
            }
            invalidate();
            requestLayout();
            mCanReceiveKeyEvent = false;
        }

        @Override
        public void onInvalidated() {
            reset();
            invalidate();
            requestLayout();
            mCanReceiveKeyEvent = false;
        }
        
    };

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        if (adapter == null) {
            return;
        }
        if (!(adapter instanceof TestAdapter)){
            throw new IllegalStateException("You should set a HorizontalListViewAdapter instance to this listview");
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataObserver);
        reset();
    }
    
    private synchronized void reset(){
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public void setSelection(int position) {
        //no-op
    }
    
    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
        addViewInLayout(child, viewPos, params, true);
    }
    

    private int getViewWidth(int pos) {
        View v = mAdapter.getView(pos, mRemovedViewQueue.poll(), this);
        
        v.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
        int w = v.getMeasuredWidth();
        mRemovedViewQueue.offer(v);
        return w;
    }
    
    /**
     * Return the delta between the right edge position of the rightmost child and the right edge of screen.
     * Must be non-negative
     * @param rightEdge
     * @param rightViewPos
     * @return
     */
    private int checkRight(int rightEdge, int rightViewPos) {
        while (rightEdge < getWidth() && rightViewPos < mAdapter.getCount()) {
            rightEdge += getViewWidth(rightViewPos);
            rightViewPos++;
        }
        
        if (rightEdge < getWidth()) {
            return (getWidth() - rightEdge);
        }
        return 0;
    }

    /**
     * Fix https://jira1.amazon.com/browse/ANDROID-1676.
     * 
     * Return the delta (in pixel) between the current screen's position and 
     * next screen's position in x axis, take into account the corner case described
     * in the issue above when calculating the delta.
     *   
     * @param left Left position, relative to parent
     * @param right Right position, relative to parent
     * @return
     */
    private int checkedDelta(int left, int right) {
        int width = right - left;
        int dx = 0;
        if (mLastLeft == -1) {
            mLastLeft = left;
            mLastRight = right;    
        }
        
        if (mLastRight - mLastLeft == width) { 
            // Screen Orientation is not changed
            dx = mCurrentX - mNextX;
        } else {
            
            
            // Wanted to check the leftDelta, but no need any more.
            // dx might be longer than the list max left, we handle it in fillListLeft.
            dx = checkRight(getRightEdge(), mRightViewPosition);
            
            mLastLeft = left;
            mLastRight = right;
        }
        
        return dx;
    }

    /**
     * Get the right edge position of the rightmost child, relative to its parent
     * @return
     */
    private int getRightEdge() {
        int rightEdge = mDisplayOffset;
        View child = getChildAt(getChildCount()-1);
        if (child != null) {
            rightEdge = child.getRight();
        }
        return rightEdge;
    }
    
    @Override
    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(mAdapter == null){
            return;
        }
        
        // whether the list view already has focus, if yes, then we can request focus later for the specific item, otherwise, don't request focus
        boolean shouldRequireFocus = (getFocusedChild() != null);
        
        if(mDataChanged){
            int oldCurrentX = mCurrentX;
            mRightViewPosition = mLeftViewPosition + 1;
            mMaxX = Integer.MAX_VALUE;
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        if(mScroller.computeScrollOffset()){
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
        }
        
        if(mNextX <= 0){
            mNextX = 0;
            mScroller.forceFinished(true);
        }
        
        if(mNextX >= mMaxX) {
            mNextX = mMaxX;
            mScroller.forceFinished(true);
        }
        
        int dx = checkedDelta(left, right);
        
        removeNonVisibleItems(dx);
        fillList(dx);
        positionItems(dx);
        
        mCurrentX = mNextX;
        
        View viewShouldTakeFocus = getChildAt(mCurrentFocusPosition - (mLeftViewPosition + 1));
        if (viewShouldTakeFocus != null && !viewShouldTakeFocus.isInTouchMode() && shouldRequireFocus) {
            viewShouldTakeFocus.requestFocus();
        }
            
        // release the flag to accept key event
        mCanReceiveKeyEvent = true;
        
        if(!mScroller.isFinished()){
            post(new Runnable(){
                @Override
                public void run() {
                    requestLayout();
                }
            });
            
        }
    }
    
    
    
    private void fillList(final int dx) {
        int edge = mDisplayOffset;
        View child = getChildAt(getChildCount()-1);
        if(child != null) {
            edge = child.getRight();
        }
        fillListRight(edge, dx);
        
        edge = 0;
        child = getChildAt(0);
        if(child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);
        
        
    }
    
    private void fillListRight(int rightEdge, final int dx) {
        while(rightEdge + dx < getWidth() && mRightViewPosition < mAdapter.getCount()) {
            View child = mAdapter.getView(mRightViewPosition, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, -1);
            rightEdge += child.getMeasuredWidth();
            mRightViewPosition++;
        }
        
        if (mRightViewPosition == mAdapter.getCount() && (rightEdge + dx < getWidth())) {
            mMaxX = mCurrentX + rightEdge - getWidth();
        }
        
        if (mMaxX < 0) {
            mMaxX = 0;
        }
    }
    
    private void fillListLeft(int leftEdge, final int dx) {
        while(leftEdge + dx > 0 && mLeftViewPosition >= 0) {
            View child = mAdapter.getView(mLeftViewPosition, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, 0);
            leftEdge -= child.getMeasuredWidth();
            mLeftViewPosition--;
            mDisplayOffset -= child.getMeasuredWidth();
        }
        
        // Make the first item show at 0
        // mDisplayOffset will increase by dx in positionItems.
        if (leftEdge + dx > 0 && mLeftViewPosition == -1) {
            mDisplayOffset = -dx;
        }
    }
    
    private void removeNonVisibleItems(final int dx) {
        View child = getChildAt(0);
        while(child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += child.getMeasuredWidth();
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mLeftViewPosition++;
            child = getChildAt(0);
        }
        
        child = getChildAt(getChildCount()-1);
        while(child != null && child.getLeft() + dx >= getWidth()) {
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mRightViewPosition--;
            child = getChildAt(getChildCount()-1);
            // once we remove some items on the right, we need to reset max, 
            // otherwise it will use previous one, this will be wrong when landscape/portait switch.
            mMaxX = Integer.MAX_VALUE; 
        }
    }
    
    private void positionItems(final int dx) {
        if(getChildCount() > 0){
            mDisplayOffset += dx;
            int left = mDisplayOffset;
            for(int i=0;i<getChildCount();i++){
                View child = getChildAt(i);
                int childWidth = child.getMeasuredWidth();
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
                left += childWidth;
            }
        }
    }
    
    public synchronized void scrollTo(int x) {
        mScroller.startScroll(mNextX, 0, x - mNextX, 0);
        requestLayout();
    }
    
    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
        synchronized(HorizontalListView.this){
            // maxX and minX should be large enough, otherwise when scrolling, the velocity will be really slow when approaching the left/right end
            // This especially happened in ICS 4.x device.
            mScroller.fling(mNextX, 0, (int)-velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
        requestLayout();
        
        return true;
    }

    protected boolean onDown(MotionEvent e) {
        mScroller.forceFinished(true);
        return true;
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            setHighlightItemToNormalBackground();
        }
        return mGesture.onTouchEvent(ev);
    }
    
    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return HorizontalListView.this.onDown(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            setFocusedItemToHightLightBackground(e);
            //Stop parents to intercept the event to scroll vertically, if user continue to scroll without up.
            HorizontalListView.this.getParent().requestDisallowInterceptTouchEvent(true);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            setFocusedItemToHightLightBackground(e);
            HorizontalListView.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setHighlightItemToNormalBackground();
                }
            }, 300);
            
            return false;
        }
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            setHighlightItemToNormalBackground();
            return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            setHighlightItemToNormalBackground();
            
            synchronized(HorizontalListView.this){
                mNextX += (int)distanceX;
            }
            // start horizontal scroll, stop parents to intercept the event to scroll vertically
            HorizontalListView.this.getParent().requestDisallowInterceptTouchEvent(true);
            requestLayout();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            boolean handled = false;
            for(int i=0;i<getChildCount();i++){
                View child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    if(mOnItemClicked != null){
                        mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewPosition + 1 + i, mAdapter.getItemId( mLeftViewPosition + 1 + i ));
                        handled = true;
                        break;
                    }
                    if(mOnItemSelected != null){
                        mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewPosition + 1 + i, mAdapter.getItemId( mLeftViewPosition + 1 + i ));
                        handled = true;
                        break;
                    }
                    
                    handled = child.performClick();
                    break;
                }
                
            }
            return handled;
        }
        
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setHighlightItemToNormalBackground();
            return false;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    if (mOnItemLongClicked != null) {
                        mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewPosition + 1 + i, mAdapter.getItemId(mLeftViewPosition + 1 + i));
                    }
                    break;
                }

            }
        }
    };

    private void setFocusedItemToHightLightBackground(MotionEvent e) {
        if (mHighlightBackground != null) {
            int i = 0;
            for(; i < getChildCount(); i++){
                View child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    child.setBackgroundDrawable(mHighlightBackground);
                    break;
                }
            }
        }
    }
    
    private void setHighlightItemToNormalBackground() {
        if (mHighlightBackground != null) {
            int i = 0;
            for(; i < getChildCount(); i++){
                View child = getChildAt(i);
                if (mHighlightBackground.equals(child.getBackground())) {
                    child.setBackgroundDrawable(mNormalBackground);
                    break;
                }
            }
        }
    }
    
    private boolean isEventWithinView(MotionEvent e, View child) {
        Rect viewRect = new Rect();
        int[] childPosition = new int[2];
        child.getLocationOnScreen(childPosition);
        int left = childPosition[0];
        int right = left + child.getWidth();
        int top = childPosition[1];
        int bottom = top + child.getHeight();
        viewRect.set(left, top, right, bottom);
        return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
    }
    
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!mCanReceiveKeyEvent) {
            // We are in the process of layout, don't accept key event any more
            return true;
        }

        /*
         * Mainly handle press left or right key when focus is in the listview
         */
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if ((mCurrentFocusPosition + 1) < getAdapter().getCount()) {
                // can move to the next item on the right
                mCurrentFocusPosition++;
            } else {
                // no more item on the right, just absorb the event
                return true;
            }
            
            if (mCurrentFocusPosition >= mRightViewPosition - 1) {
                // the to be focus item is out of the right edge
                int rightEdge = getChildAt(this.getChildCount() - 1).getRight();
                int parentWidth = ((View)getParent()).getWidth();
                int totalCount = ((TestAdapter)this.getAdapter()).getCount();
                
                if (rightEdge - parentWidth == 0) {
                    // the most right item is just on the right edge of our listview
                    View nextViewToShow = getAdapter().getView(mCurrentFocusPosition, mRemovedViewQueue.poll(), this);
                    nextViewToShow.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
                    mNextX += nextViewToShow.getMeasuredWidth();
                }
                
                // If it is the last one of all the item, don't need show preview offset
                int delta = rightEdge - parentWidth + (mCurrentFocusPosition == totalCount - 1 ? 0 : PREVIEW_OFFSET);
                mNextX += delta;
                requestLayout();
                return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            if ((mCurrentFocusPosition - 1) >= 0) {
                // can move to the next item on the left
                mCurrentFocusPosition--;
            } else {
                // no more item on the left, just absorb the event
                return true;
            }
            
            if (mCurrentFocusPosition == mLeftViewPosition + 1) {
                // the to be focus position is the left most position on the screen
                int left = this.getChildAt(0).getLeft();
                // if it is the first item of all the items, don't need show preview offset
                int delta = left - (mCurrentFocusPosition == 0 ? 0 : PREVIEW_OFFSET);
                mNextX += delta;
                requestLayout();
                return true;
            }
        }
        
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            /*
             * Logic to handle when the focus comes in the listview. Also can consume the focus to currentFocusPosition item generated in onLayout()
             */
            if (mCurrentFocusPosition >= (mLeftViewPosition + 1) && mCurrentFocusPosition <= (mRightViewPosition - 1)) {
                // last time's current focus position is still in the visible range of the listview
                int toBeFocusedItemIndex = mCurrentFocusPosition - (mLeftViewPosition + 1);
                
                View toBeFocusedView = getChildAt(toBeFocusedItemIndex);

                int left = toBeFocusedView.getLeft();
                int right = toBeFocusedView.getRight();

                int w = ((View) toBeFocusedView.getParent()).getWidth();

                if (left > 0 && right < w) {
                    // the item is inside the visible range
                    toBeFocusedView.requestFocus();
                } else {
                    if (left <= 0) {
                        // the left edge of the item to be focused is out of (or just at ) the visible range on the left
                        int delta = 0;
                        if (mCurrentFocusPosition > 0) {
                            // not the first one in the list, need some preview offset
                            delta = left - PREVIEW_OFFSET;
                        } else {
                            // it is the first item in the list, just move to its left-edge, always 0
                            delta = left;
                        }
                        mNextX += delta;
                        requestLayout();
                    } else if (right >= w) {
                        // the right edge of the item to be focused is out of (or just at) the visible range on the right
                        int delta = 0;
                        if (mCurrentFocusPosition != ((this.getAdapter()).getCount() - 1)) {
                            // not the last item in the list, need some preview offset
                            delta = right - w + PREVIEW_OFFSET;
                        } else {
                            // last item in the list, just move to its right-edge
                            delta = right - w;
                        }
                        mNextX += delta;
                        requestLayout();
                    }
                  }
            } else {
                // last time's current focus position is not in the visible range of the listview, just find the first match one to focus to
                for (int i = 0; i < this.getChildCount(); i++) {
                    View view = this.getChildAt(i);
                    int left = view.getLeft();
                    if (left >= 0) {
                        if (v != view) {
                            view.requestFocus();
                        }
                        mCurrentFocusPosition = (mLeftViewPosition + 1) + i;
                        break;
                    }
                }
            }
        }
    }
    
}
