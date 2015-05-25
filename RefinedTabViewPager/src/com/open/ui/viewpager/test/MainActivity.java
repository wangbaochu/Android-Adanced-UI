package com.open.ui.viewpager.test;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.open.ui.refinedviewpager.R;
import com.open.ui.viewpager.view.TabActivity;
import com.open.ui.viewpager.view.TabPage;
import com.open.ui.viewpager.view.TabViewModel;

public class MainActivity extends TabActivity {

    public int mTabIndex = 0;

    private TabPage mTabPage;
    private TestTabView mMyTestTabView1;
    private TestTabView mMyTestTabView2;
    private TestTabView mMyTestTabView3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTabPage = (TabPage) findViewById(R.id.tab_page);
        initTabPage(mTabPage);
        setCurrentView(0);
    }

    @Override
    public List<TabViewModel> createTabViewModel() {
        mMyTestTabView1 = new TestTabView(this);
        mMyTestTabView1.setViewUpdater(getViewUpdater());

        mMyTestTabView2 = new TestTabView(this);
        mMyTestTabView2.setViewUpdater(getViewUpdater());

        mMyTestTabView3 = new TestTabView(this);
        mMyTestTabView3.setViewUpdater(getViewUpdater());

        List<TabViewModel> list = new ArrayList<TabViewModel>();
        list.add(new TabViewModel("page1", mMyTestTabView1));
        list.add(new TabViewModel("page2", mMyTestTabView2));
        list.add(new TabViewModel("page3", mMyTestTabView3));

        return list;
    }
}
