package com.open.ui.expandablelistview.ui;

import java.util.ArrayList;
import java.util.List;

import com.open.ui.expandablelistview.ui.ExpandDataManager.DataProcessor;

/**
 * 可展开数据，基本类
 */
public class ExpandedData implements BaseExpandedData {
    
    private boolean mExpanded;
    private BaseExpandedData mParent;
    private List<BaseExpandedData> mList = new ArrayList<BaseExpandedData>();

    @Override
    public boolean isExanded() {
        return mExpanded;
    }

    @Override
    public void setExanded(boolean expanded) {
        mExpanded = expanded;
    }

    @Override
    public List<BaseExpandedData> getChildrenList() {
        return mList;
    }

    @Override
    public BaseExpandedData getParent() {
        return mParent;
    }

    @Override
    public void setParent(BaseExpandedData parent) {
        mParent = parent;
    }

    @Override
    public void addChild(BaseExpandedData child) {
        if (child != null) {
            child.setParent(this);
            mList.add(child);
        }
    }

    @Override
    public void addChildren(List<BaseExpandedData> children) {
        if (children != null) {
            for (BaseExpandedData c : children) {
                if (c != null) {
                    c.setParent(this);
                }
            }
            mList.addAll(children);
        }
    }

    @Override
    public void removeChild(BaseExpandedData child) {
        if (child != null) {
            child.setParent(null);
            mList.remove(child);
        }
    }

    @Override
    public void removeChildren(List<BaseExpandedData> children) {
        if (children != null) {
            for (BaseExpandedData c : children) {
                if (c != null) {
                    c.setParent(null);
                }
            }
            mList.removeAll(children);
        }
    }

    @Override
    public BaseExpandedData getChildAt(int index) {
        if (mList == null || index < 0 || index >= mList.size()) {
            return null;
        }
        return mList.get(index);
    }

    @Override
    public int getChildrenCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public void list(DataProcessor deallr) {
        if (deallr == null) {
            return;
        }
        deallr.deal(this);

        if (mList == null || mList.isEmpty()) {
            return;
        }

        for (BaseExpandedData data : mList) {
            if (data != null) {
                data.list(deallr);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return mList == null || mList.isEmpty();
    }
}
