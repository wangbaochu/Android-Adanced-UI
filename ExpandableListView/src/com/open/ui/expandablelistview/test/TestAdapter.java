package com.open.ui.expandablelistview.test;

import com.open.ui.expandablelistview.R;
import com.open.ui.expandablelistview.ui.BaseExpandedData;
import com.open.ui.expandablelistview.ui.ExpandDataManager;
import com.open.ui.expandablelistview.ui.ExpandedAdapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestAdapter extends ExpandedAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TestAdapter(Context context, ExpandDataManager dataManager) {
        super(dataManager);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
            case 0:
                convertView = mLayoutInflater.inflate(R.layout.item_level0_layout, parent, false);
                break;
            case 1:
                convertView = mLayoutInflater.inflate(R.layout.item_level1_layout, parent, false);
                break;
            case 2:
                convertView = mLayoutInflater.inflate(R.layout.item_level2_layout, parent, false);
                break;
            default:
                break;
            }
        }

        TestExpandedData item = (TestExpandedData) getItem(position);
        updateItemView(convertView, item, type);
        return convertView;
    }

    /**
     * update the item view
     * @param convertView
     * @param item
     * @param type
     */
    public void updateItemView(View convertView, TestExpandedData item, int type) {
        if (convertView == null) {
            return;
        }

        TextView textView = (TextView) convertView.findViewById(R.id.item_title);
        
        switch (type) {
        case 0: 
            textView.setText(R.string.item_level_0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setPadding(0, 0, 0, 0);
            break;
        case 1: 
            textView.setText(R.string.item_level_1);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setPadding(30, 0, 0, 0);
            break;
        case 2:
            textView.setText(R.string.item_level_2);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            textView.setPadding(60, 0, 0, 0);
            break;
        default:
            break;
        }
    }

    public long getSize(TestExpandedData o) {
        if (o != null) {
            return o.getSize();
        }
        return 0;
    }

    public boolean isExpanded(TestExpandedData o) {
        if (o != null) {
            return o.isExanded();
        }
        return false;
    }

    public boolean isItemEmpty(TestExpandedData o) {
        if (o != null) {
            return o.isEmpty();
        }
        return true;
    }

    public int getChildrenCount(TestExpandedData o) {
        if (o != null) {
            return o.getChildrenCount();
        }
        return 0;
    }

    public boolean isFirstChild(TestExpandedData o) {
        if (o != null) {
            BaseExpandedData parent = o.getParent();
            if (parent != null) {
                return o == parent.getChildAt(0);
            }
        }
        return false;
    }

    public boolean isLastChild(TestExpandedData o) {
        if (o != null) {
            BaseExpandedData parent = o.getParent();
            if (parent != null) {
                return o == parent.getChildAt(parent.getChildrenCount() - 1);
            }
        }
        return false;
    }
    
    public boolean isAllChecked(TestExpandedData o) {
        if (o != null) {
            return o.isAllChecked();
        }
        return false;
    }

    public boolean isUnChecked(TestExpandedData o) {
        if (o != null) {
            return o.isUnChecked();
        }
        return false;
    }
}
