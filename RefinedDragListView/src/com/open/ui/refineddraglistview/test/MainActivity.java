package com.open.ui.refineddraglistview.test;

import com.open.ui.refineddraglistview.R;
import com.open.ui.refineddraglistview.view.DragSortListView;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    
    private DragSortListView mListView;
    private SimpleAdapter mListAdapter;
    private SimpleController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mListView = (DragSortListView)findViewById(R.id.drag_list_view);
        mListAdapter = new SimpleAdapter(this);
        mController = new SimpleController(mListView, mListAdapter);
        
        mListView.setFloatViewManager(mController);
        mListView.setOnTouchListener(mController);
        mListView.setDropListener(mListAdapter);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mListAdapter);
        mListView.setDragEnabled(true);
    }
    

}
