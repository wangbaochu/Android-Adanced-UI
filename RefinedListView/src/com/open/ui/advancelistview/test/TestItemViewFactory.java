package com.open.ui.advancelistview.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.open.ui.advancelistview.view.BaseItemModel;
import com.open.ui.advancelistview.view.BaseItemViewFactory;
import com.open.ui.refinedlistview.R;

public class TestItemViewFactory extends BaseItemViewFactory {
    
    private Context mContext;
    
    public TestItemViewFactory(Context context) {
        mContext = context;
    }

    @Override
    public View createItemView(BaseItemModel model) {
        View itemView = null;
        if (model != null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, null);
        }
        return itemView;
    }

    @Override
    public void updateItemView(View itemView, BaseItemModel model) {
        ((TestItemView) itemView).updateView(model);
    }

}
