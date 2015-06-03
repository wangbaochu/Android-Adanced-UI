package com.open.ui.refinedmenudrawer.view;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Custom list adapter for the menu's ListView
 */
public class MenuItemAdapter<T> extends BaseAdapter {
    
    private List<T> mDrawerItems = new ArrayList<T>();;
    private List<T> mDrawerCopyItems = null;
    private MenuDrawerItemFactory<T> mMenuItemItemFactory = null;
    
    public MenuItemAdapter(MenuDrawerItemFactory<T> factory) {
        mMenuItemItemFactory = factory;
    }

    public List<T> getItemlistData() {
        return mDrawerCopyItems;
    }

    public void setItemlistData(List<T> itemModelList) {
        if (itemModelList != null) {
            mDrawerCopyItems = itemModelList;
            notifyDataSetChanged();
        } else {
            mDrawerCopyItems = new ArrayList<T>();
        }
    }
    
    @Override
    public void notifyDataSetChanged() {
        mDrawerItems.clear();
        mDrawerItems.addAll(mDrawerCopyItems);
        super.notifyDataSetChanged();
    }
    
    /**
     * Clear items from the adapter
     */
    public void clearItemData() {
        mDrawerItems.clear();
    }

    /**
     * Returns the number of visible items
     */
    @Override
    public int getCount() {
        return mDrawerItems.size();
    }

    @Override
    public T getItem(int position) {
        return mDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T model = mDrawerItems.get(position);
        if (convertView == null) {
            convertView = mMenuItemItemFactory.createItemView(model);
        }
        mMenuItemItemFactory.updateItemView(convertView, model);
        
        return convertView;
    }

}
