package com.open.ui.advancelistview.test;

import com.open.ui.advancelistview.view.BaseItemModel;

public class TestItemMode extends BaseItemModel {

    private String mItemTitle = null;
    
    public void setItemTitle(String title) {
        mItemTitle = title;
    }
    
    public String getItemTitle() {
        return mItemTitle;
    }
            
}
