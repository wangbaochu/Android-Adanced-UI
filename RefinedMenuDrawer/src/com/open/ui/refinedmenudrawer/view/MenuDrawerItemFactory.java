package com.open.ui.refinedmenudrawer.view;

import android.view.View;

public interface MenuDrawerItemFactory<T> {

    public abstract View createItemView(T itemData);
    
    public abstract void updateItemView(View itemView, T itemData);
}