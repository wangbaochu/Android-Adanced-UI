package com.open.ui.expandablelistview.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 支持 多级展开对应adapter
 * <p/>
 * 每一级的列表view样式须保持一致
 * <p/>
 */
public class ExpandedAdapter extends BaseAdapter {

    private ExpandDataManager mExpandDataManager;

    public ExpandedAdapter(ExpandDataManager expandDataManager) {
        mExpandDataManager = expandDataManager;
    }

    @Override
    public int getCount() {
        return mExpandDataManager.getCount();
    }

    @Override
    public BaseExpandedData getItem(int position) {
        return mExpandDataManager.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return mExpandDataManager.getLevelCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mExpandDataManager.getLevelByPosition(position);
    }

    public ExpandDataManager getExpandDataManager() {
        return mExpandDataManager;
    }
}
