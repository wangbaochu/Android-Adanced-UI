package com.open.ui.expandablelistview.test;

import com.open.ui.expandablelistview.R;
import com.open.ui.expandablelistview.ui.ExpandDataManager;
import com.open.ui.expandablelistview.ui.ExpandDataManager.DataObserver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements DataObserver, OnItemClickListener {

    private TestDataManager mDataManager;
    private TestAdapter mTestAdapter;
    private ListView mListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mDataManager = new TestDataManager();
        mDataManager.initData(this);
        mDataManager.registerObserver(this);
        mDataManager.setLevelCount(3);//The total levels is three
        mTestAdapter = new TestAdapter(this, mDataManager);
       
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mTestAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onChanged(ExpandDataManager manager) {
        if (mTestAdapter != null) {
            mTestAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        if (mTestAdapter == null) {
            return;
        }

        if (parent instanceof ListView) {
            position -= ((ListView) parent).getHeaderViewsCount();
        }
        if (position < 0) {
            return;
        }

        final TestExpandedData object = (TestExpandedData) mDataManager.getItem(position);
        if (object.getChildrenList() != null) {
            mDataManager.setExpandOrUnExpand(object);
        }
    }
}
