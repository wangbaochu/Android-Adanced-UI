package com.open.ui.expandablelistview.test;

import java.util.List;

import com.open.ui.expandablelistview.ui.BaseExpandedData;
import com.open.ui.expandablelistview.ui.ExpandedData;

public class TestExpandedData extends ExpandedData {
    
    public boolean mChecked;
    public long mSize;

    public long getSize() {
        List<BaseExpandedData> list = getChildrenList();
        if (list == null || list.isEmpty()) {
            return mSize;
        }
        long s = 0;
        for (Object o : list) {
            if (o instanceof TestExpandedData) {
                s += ((TestExpandedData) o).getSize();
            }
        }
        return s;
    }
    
    public boolean isAllChecked() {
        List<BaseExpandedData> list = getChildrenList();
        if (list == null || list.isEmpty()) {
            return mChecked;
        }
        boolean checked = true;
        if (list != null) {
            for (Object o : list) {
                if (o instanceof TestExpandedData) {
                    checked &= ((TestExpandedData) o).isAllChecked();
                    if (!checked) {
                        break;
                    }
                }
            }
        }
        return checked;
    }

    public boolean isUnChecked() {
        List<BaseExpandedData> list = getChildrenList();
        if (list == null || list.isEmpty()) {
            return !mChecked;
        }
        boolean unChecked = true;
        if (list != null) {
            for (Object o : list) {
                if (o instanceof TestExpandedData) {
                    unChecked &= ((TestExpandedData) o).isUnChecked();
                    if (!unChecked) {
                        break;
                    }
                }
            }
        }
        return unChecked;
    }

    public void setChecked(boolean checked) {
        List<BaseExpandedData> list = getChildrenList();
        if (list == null || list.isEmpty()) {
            if (mChecked == checked) {
                return;
            }
            mChecked = checked;
        } else {
            for (Object o : list) {
                ((TestExpandedData) o).setChecked(checked);
            }
        }
    }
}
