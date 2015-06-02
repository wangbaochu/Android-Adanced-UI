package com.open.ui.advancelistview.view;

import android.view.View;

public abstract class BaseItemViewFactory {
    
    public abstract View createItemView(BaseItemModel model);
    
    public abstract void updateItemView(View itemView, BaseItemModel model);
    
}
