package com.open.ui.refinedpullrefresh.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.ListView;

import com.open.ui.refinedpullrefresh.R;
import com.open.ui.refinedpullrefresh.library.PullToRefreshBase;
import com.open.ui.refinedpullrefresh.library.PullToRefreshBase.Mode;
import com.open.ui.refinedpullrefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.open.ui.refinedpullrefresh.library.PullToRefreshListView;

public class MainActivity extends Activity {
    
    private PullToRefreshListView mTestPullRefreshListView;
    private TestListAdapter mTestListAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        initListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Initialize the pull refresh list view
     */
    private void initListView() {
        mTestPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mTestPullRefreshListView.setMode(Mode.PULL_FROM_START);//or set app:ptrMode="pullFromStart" in xml layout
        mTestPullRefreshListView.getRefreshableView().setDividerHeight(0);
        mTestPullRefreshListView.getRefreshableView().setCacheColorHint(0x00000000);
        mTestPullRefreshListView.getRefreshableView().setSelector(android.R.color.transparent);
        //whether show the indicator to inform user that the list view can be pull to auto refresh.
        mTestPullRefreshListView.setShowIndicator(true);
        mTestListAdapter = new TestListAdapter(this);
        mTestPullRefreshListView.setAdapter(mTestListAdapter);
        
        mTestPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //Called if setMode(Mode.PULL_FROM_START)
                refreshToLoadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //Called if setMode(Mode.PULL_FROM_START)
                //refreshToLoadData();
            }

        });
        
        
        loadListData();
    }
    
    private void loadListData() {
        new Thread() {
            @Override
            public void run() {
                final List<String> itemModelList = new ArrayList<String>();
                for (int i = 0; i < 20; i++) {
                    itemModelList.add(String.format("Item %d", i));
                }
                
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTestListAdapter.setListData(itemModelList);
                    }
                });
            }
        }.start();
    }
    
    /***
     * Loading data when pull to refresh
     */
    private void refreshToLoadData() {
        new Thread() {
            @Override
            public void run() {
                //To do your loading, here we just sleep 2 seconds for testing.
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTestPullRefreshListView.onRefreshComplete();
                    }
                });
            }
        }.start();
    }
}
