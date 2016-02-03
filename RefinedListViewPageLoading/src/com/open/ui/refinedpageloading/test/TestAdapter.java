package com.open.ui.refinedpageloading.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.open.ui.refinedpageloading.R;
import com.open.ui.refinedpageloading.view.PageListingAdapter;
import com.open.ui.refinedpageloading.view.PageListingSubscriber;

public class TestAdapter extends PageListingAdapter<String> {

    public TestAdapter(Context context, int rowResId, PageListingSubscriber subscriber) {
        super(context, rowResId, subscriber);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        mBrowser.setCurrentIndex(position);
        if (null == convertView) {
            convertView = this.layoutInflater.inflate(rowResId, null);
        }
        TextView itemTitle = (TextView) convertView.findViewById(R.id.item_title);
        itemTitle.setText(mBrowser.getObjectAtIndex(position));
        
        return convertView;
    }

    @Override
    public void goToClickedItem(AdapterView<?> parent, View view, String objectData) {
        // TODO This is actually the item click listener
    }

}
