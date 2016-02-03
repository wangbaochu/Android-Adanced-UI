package com.open.ui.refinedhorizontallistview.test;

import java.util.ArrayList;
import java.util.List;

import com.open.ui.refinedhorizontallistview.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestAdapter extends BaseAdapter {
    
    private LayoutInflater mLayoutInflater;
    private List<String> mShownItems = new ArrayList<String>();
    
    public TestAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        
        mShownItems.add("Item1");
        mShownItems.add("Item2");
        mShownItems.add("Item3");
        mShownItems.add("Item4");
        mShownItems.add("Item5");
        mShownItems.add("Item6");
        mShownItems.add("Item7");
        mShownItems.add("Item8");
        mShownItems.add("Item9");
        mShownItems.add("Item10");
        mShownItems.add("Item11");
        mShownItems.add("Item12");
    }

    @Override
    public int getCount() {
        return mShownItems.size();
    }
    
    @Override
    public Object getItem(int position) {
        return mShownItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_view, parent, false);
        }
        
        TextView title = (TextView) convertView.findViewById(R.id.item_title);
        title.setText(mShownItems.get(position));
        convertView.setTag(mShownItems.get(position));
        return convertView;
    }
    
}
