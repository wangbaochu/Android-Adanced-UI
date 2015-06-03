package com.open.ui.refinedmenudrawer.test;

import com.open.ui.refinedmenudrawer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestItemView extends LinearLayout {
    
    private TextView mItemTitle = null;

    public TestItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mItemTitle = (TextView) findViewById(R.id.menu_item_title);
    }
    
    public void updateView(TestItemData itemData) {
        if (mItemTitle != null && itemData != null) {
            mItemTitle.setText(itemData.getItemTitle());
        }
    }
}
