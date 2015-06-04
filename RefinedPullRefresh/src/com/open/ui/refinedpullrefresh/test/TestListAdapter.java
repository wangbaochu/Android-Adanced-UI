package com.open.ui.refinedpullrefresh.test;

import java.util.ArrayList;
import java.util.List;

import com.open.ui.refinedpullrefresh.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestListAdapter extends BaseAdapter {
    
    private Context mContext = null;
    public List<String> mListData = new ArrayList<String>();
    
    public TestListAdapter(Context context) {
        mContext = context;
    }
    
    public void setListData(List<String> data) {
        mListData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mListData != null) {
            return mListData.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mListData != null && position < mListData.size()) {
            return mListData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_view, null);
        }
        
        TextView title = (TextView) convertView.findViewById(R.id.item_title);
        if (title != null) {
            title.setText(mListData.get(position));
        }
        
        return convertView;
    }

}
