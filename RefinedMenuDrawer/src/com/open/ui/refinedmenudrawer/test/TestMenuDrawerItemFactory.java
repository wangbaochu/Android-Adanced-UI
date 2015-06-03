package com.open.ui.refinedmenudrawer.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.open.ui.refinedmenudrawer.R;
import com.open.ui.refinedmenudrawer.test.TestItemView;
import com.open.ui.refinedmenudrawer.view.MenuDrawerItemFactory;

public class TestMenuDrawerItemFactory implements MenuDrawerItemFactory<TestItemData> {
    
    private Context mContext = null;
    
    public TestMenuDrawerItemFactory(Context context) {
        mContext = context;
    }

    @Override
    public View createItemView(TestItemData itemData) {
        if (itemData != null) {
            return LayoutInflater.from(mContext).inflate(R.layout.menu_item, null);
        }
        return null;
    }

    @Override
    public void updateItemView(View itemView, TestItemData itemData) {
        if (itemView instanceof TestItemView) {
            ((TestItemView)itemView).updateView(itemData);
        }
    }

}
