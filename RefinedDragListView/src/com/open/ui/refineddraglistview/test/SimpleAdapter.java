package com.open.ui.refineddraglistview.test;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.open.ui.refineddraglistview.R;
import com.open.ui.refineddraglistview.view.DragSortListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baochu on 15/11/5.
 */
public class SimpleAdapter extends BaseAdapter implements DragSortListView.DropListener, 
                                                          DragSortListView.RemoveListener, AdapterView.OnItemClickListener {

    private LayoutInflater mLayoutInflater;
    private List<String> mDataList = new ArrayList<String>();

    public SimpleAdapter(Context context) {
        super();
        mLayoutInflater = LayoutInflater.from(context);
        loadData();
    }
   
    public void loadData() {
        for (int i = 0; i < 8; i++) {
            mDataList.add("ItemView-" + (i+1));
        }
        notifyDataSetChanged();
    }

    //############################## DropListener ##############################
    @Override
    public void drop(int from, int to) {
        if (from != to) {
            moveItemData(from, to);
            notifyDataSetChanged();
        }
    }

    //############################## RemoveListener ##############################
    @Override
    public void remove(int which) {
        mDataList.remove(which);
        notifyDataSetChanged();
    }
    
    //############################## BaseAdapter ##############################
    @Override
    public int getCount() {
        return mDataList.size();
    }
    
    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public Object getItem(int position) {
        if (position < mDataList.size()) {
            return mDataList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.item_view, parent, false);
        }
        
        TextView title = (TextView)convertView.findViewById(R.id.item_title);
        title.setText(mDataList.get(position));
        return convertView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        //TODO
    }

    private boolean moveItemData(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return true;
        }
        if (fromIndex >= mDataList.size() || toIndex >= mDataList.size()) {
            return false;
        }
        String entity = mDataList.remove(fromIndex);
        mDataList.add(toIndex, entity);
        return true;
    }
}
