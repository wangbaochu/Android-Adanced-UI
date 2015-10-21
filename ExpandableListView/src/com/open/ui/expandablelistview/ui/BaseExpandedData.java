package com.open.ui.expandablelistview.ui;

import java.util.List;

import com.open.ui.expandablelistview.ui.ExpandDataManager.DataProcessor;

/**
 * 可展开数据结构
 */
public interface BaseExpandedData {

    /**
     * 当前是否已经展开
     */
    public boolean isExanded();

    /**
     * 设置是否展开
     */
    public void setExanded(boolean expanded);

    /**
     * 子元素列表
     */
    public List<BaseExpandedData> getChildrenList();

    /**
     * 增加子元素
     */
    public void addChild(BaseExpandedData child);

    /**
     * 增加子元素列表
     */
    public void addChildren(List<BaseExpandedData> children);

    /**
     * 删除子元素
     */
    public void removeChild(BaseExpandedData child);

    /**
     * 删除子元素列表
     */
    public void removeChildren(List<BaseExpandedData> child);

    /**
     * 获取父元素
     * @return
     */
    public BaseExpandedData getParent();

    /**
     * 设置父元素
     * @return
     */
    void setParent(BaseExpandedData parent);

    /**
     * 获取指定位置子元素
     * @param index
     * @return
     */
    public BaseExpandedData getChildAt(int index);
    /**
     * 获取子元素个数
     * @return
     */
    public int getChildrenCount();

    /**
     * 是否没有子元素
     * @return
     */
    public boolean isEmpty();

    /**
     * 遍历数据
     * @param d
     */
    public void list(DataProcessor d);
}
