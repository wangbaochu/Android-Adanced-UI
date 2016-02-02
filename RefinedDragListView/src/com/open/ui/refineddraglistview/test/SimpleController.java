package com.open.ui.refineddraglistview.test;

import com.open.ui.refineddraglistview.view.DragSortController;
import com.open.ui.refineddraglistview.view.DragSortListView;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by baochu on 15/11/17.
 */
public class SimpleController extends DragSortController {
    
    private SimpleAdapter mAdapter;
    private DragSortListView mDslv;

    public SimpleController(DragSortListView dslv, SimpleAdapter adapter) {
        super(dslv, 0, DragSortController.ON_LONG_PRESS, FLING_REMOVE);
        setRemoveEnabled(true);
        mDslv = dslv;
        mAdapter = adapter;
    }

    @Override
    public int startDragPosition(MotionEvent ev) {
        int res = super.dragHandleHitPosition(ev);
        int width = mDslv.getWidth();
        if ((int) ev.getX() < width) {
            return res;
        } else {
            return DragSortController.MISS;
        }
    }

    @Override
    public View onCreateFloatView(int position) {
        View v = mAdapter.getView(position, null, mDslv);
        return v;
    }
    
    @Override
    public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
        //int first = mDslv.getFirstVisiblePosition();
    }

    @Override
    public void onDestroyFloatView(View floatView) {
        //do nothing; block super from crashing
    }
}
