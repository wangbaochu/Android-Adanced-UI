package com.open.ui.viewpager.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.open.ui.refinedviewpager.R;
import com.open.ui.viewpager.view.TabView;

public class TestTabView extends TabView {

    private Context mContext;
    private ViewGroup mMainView;

    public TestTabView(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        if (mMainView == null) {
            mMainView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.tab_page1, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }
    
    @Override
    public View getView() {
        return mMainView;
    }
}
