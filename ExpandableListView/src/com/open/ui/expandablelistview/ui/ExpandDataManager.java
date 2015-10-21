package com.open.ui.expandablelistview.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 可展开数据操作类
 */
public class ExpandDataManager {

    public interface DataObserver {
        public void onChanged(ExpandDataManager manager);
    }

    /**
     * 遍历数据处理器
     */
    public interface DataProcessor {
        public void deal(BaseExpandedData d);
    }

    /**
     * 根元素结点
     */
    private RootData mRootData = new RootData();

    private WeakHashMap<DataObserver, Object> mDataObservers = new WeakHashMap<DataObserver, Object>();
    private List<DataObserver> mTmpObserverList = new ArrayList<DataObserver>();

    public void notifyDataSetChanged() {
        mTmpObserverList.clear();
        mTmpObserverList.addAll(mDataObservers.keySet());
        Iterator<DataObserver> iterator = mTmpObserverList.iterator();
        while(iterator.hasNext()) {
            DataObserver observer = iterator.next();
            observer.onChanged(this);
        }
    }

    /**
     * 注册数据变化监听器
     * @param observer
     */
    public void registerObserver(DataObserver observer) {
        if (observer != null) {
            mDataObservers.put(observer, null);
        }
    }

    /**
     * 取消注册数据变化监听器
     * @param observer
     */
    public void unregisterObserver(DataObserver observer) {
        if (observer != null) {
            mDataObservers.remove(observer);
        }
    }

    /**
     * 遍历数据
     * @param deallr
     */
    public void list(DataProcessor deallr) {
        mRootData.list(deallr);
    }

    /**
     * 获取总数
     *
     * @return
     */
    public int getCount() {
        return getChildrenCount(mRootData);
    }

    public List<BaseExpandedData> getList() {
        return mRootData.getChildrenList();
    }

    /**
     * 获取对应位置对象
     * @param position
     * @return
     */
    public BaseExpandedData getItem(int position) {
        return getChildByPosition(mRootData, position);
    }

    /**
     * 获取层级数
     * @return
     */
    public int getLevelCount() {
        return mRootData.getLevelCount();
    }

    /**
     * 根据位置获取层级
     * @param position
     * @return 0=一级；1=二级；2=三级
     */
    public int getLevelByPosition(int position) {
        return getChildLevelByPosition(mRootData, position);
    }

    /**
     * 某一位置是否展开
     *
     * @param position
     * @return
     */
    public boolean isExpanded(int position) {
        BaseExpandedData o = getChildByPosition(mRootData, position);
        if (o == null) {
            return false;
        }

        return o.isExanded();
    }

    /**
     * 设置某一位置是否展开
     *
     * @param position
     * @param expanded
     * @return
     */
    public void setExpand(int position, boolean expanded) {
        BaseExpandedData o = getChildByPosition(mRootData, position);
        if (o == null) {
            return;
        }

        setExpand(o, expanded);

    }

    public void setExpand(BaseExpandedData data, boolean expanded) {
        if (data != null && data.getChildrenList() != null) {
            if (data.isExanded() != expanded) {
                data.setExanded(expanded);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 设置某一位置是否展开,如果当前是展开则合上，如果当前合上则展开
     *
     * @param position
     */
    public void setExpandOrUnExpand(int position) {
        BaseExpandedData o = getChildByPosition(mRootData, position);
        if (o == null) {
            return;
        }
        setExpandOrUnExpand(o);

    }

    public void setExpandOrUnExpand(BaseExpandedData data) {
        if (data != null && data.getChildrenList() != null) {
            data.setExanded(!data.isExanded());
            notifyDataSetChanged();
        }
    }

    /**
     * 根据位置删除对应元素
     * @param position
     */
    public void removeChild(int position) {
        BaseExpandedData removeChild = removeChild(mRootData, position);
        if (removeChild != null) {
            notifyDataSetChanged();
        }
    }

    /**
     * 根据位置删除对应元素
     * @param positions
     */
    public void removeChildren(int... positions) {
        if (positions == null) {
            return;
        }
        BaseExpandedData removeChild = null;
        for (int p : positions) {
            removeChild = removeChild(mRootData, p);
        }
        if (removeChild != null) {
            notifyDataSetChanged();
        }
    }

    /**
     * 设置 层级数目
     *
     * @param count
     */
    public void setLevelCount(int count) {
        mRootData.setLevelCount(Math.max(0, count));
    }

    /**
     * 增加子节点
     */
    public void addChild(BaseExpandedData o) {
        addChild(mRootData, o);
    }

    /**
     * 增加子节点
     */
    public void addChildren(List<BaseExpandedData> l) {
        addChildren(mRootData, l);
    }

    /**
     * 增加子节点
     */
    public void addChild(BaseExpandedData parent, BaseExpandedData o) {
        if (o != null && parent != null) {
            parent.addChild(o);
            notifyDataSetChanged();
        }
    }

    /**
     * 增加子节点
     */
    public void addChildren(BaseExpandedData parent, List<BaseExpandedData> l) {
        if (parent != null && l != null && !l.isEmpty()) {
            parent.addChildren(l);
            notifyDataSetChanged();
        }
    }

    /**
     * 根据位置，获取该位置元素在某一级上的 父元素
     *
     * @param level
     * @param position
     * @return
     */
    public BaseExpandedData getAncestorByPosition(int level, int position) {
        return getLevelAncestorByPosition(mRootData, level, position);
    }

    /**
     * 根元素节点类，只做内部使用
     */
    private static class RootData extends ExpandedData {


        private int mLevelCount;

        @Override
        public List<BaseExpandedData> getChildrenList() {
            return super.getChildrenList();
        }

        @Override
        public boolean isExanded() {
            return true;
        }

        public int getLevelCount() {
            return mLevelCount;
        }

        public void setLevelCount(int levelCount) {
            mLevelCount = levelCount;
        }

        @Override
        public void setExanded(boolean expanded) {
        }
    }

    /**
     * 递归得到每一级的子元素数量,不管是否展开都返回实际数量
     *
     * @param data
     * @return
     */
    public static final int getChildrenCountIgnoreExpand(BaseExpandedData data) {
        if (data == null) {
            return 0;
        }
        List<BaseExpandedData> list = data.getChildrenList();
        int size = (list != null ? list.size() : 0);
        if (size > 0) {
            for (BaseExpandedData o : list) {
                size += getChildrenCount(o);
            }
        }
        return size;
    }

    /**
     * 递归得到每一级的子元素数量,如果当前没有展开返回0
     *
     * @param data
     * @return
     */
    private static final int getChildrenCount(BaseExpandedData data) {
        if (data == null || !data.isExanded()) {
            return 0;
        }
        List<BaseExpandedData> list = data.getChildrenList();
        int size = (list != null ? list.size() : 0);
        if (size > 0) {
            for (BaseExpandedData o : list) {
                size += getChildrenCount(o);
            }
        }
        return size;
    }

    /**
     * 根据位置得到对应位置对象
     * A
     *
     * @param data
     * @param position
     * @return
     */
    private static final BaseExpandedData getChildByPosition(BaseExpandedData data, int position) {
        if (data == null) {
            return null;
        }

        List<BaseExpandedData> list = data.getChildrenList();
        if (list == null) {
            return null;
        }

        if (data.isExanded()) {
            for (BaseExpandedData o : list) {
                if (0 == position) {
                    return o;
                }
                position--;

                int c = getChildrenCount(o);
                if (c > position) {
                    return getChildByPosition(o, position);
                }

                position -= c;
            }
        }
        return null;
    }

    /**
     * 根据位置返回,所在层级
     *
     * @param data
     * @param position
     * @return
     */
    private static final int getChildLevelByPosition(BaseExpandedData data, int position) {
        if (data == null) {
            return -1;
        }
        BaseExpandedData findChild = getChildByPosition(data, position);
        if (findChild == null) {
            return -1;
        }
        int i = -1;
        BaseExpandedData parent = findChild.getParent();
        while (parent != null) {
            parent = parent.getParent();
            i++;
        }
        
        return i;
    }

    /**
     * 根据位置，获取该位置元素在某一级上的 父元素
     *
     * @param data
     * @param level
     * @param position
     * @return
     */
    private static final BaseExpandedData getLevelAncestorByPosition(BaseExpandedData data, int level, int position) {
        if (data == null) {
            return null;
        }
        BaseExpandedData findChild = getChildByPosition(data, position);
        if (findChild == null) {
            return null;
        }

        int myLevel = -1;
        BaseExpandedData parent = findChild.getParent();
        while (parent != null) {
            parent = parent.getParent();
            myLevel++;
        }

        if (myLevel == -1 || myLevel < level) {
            return null;
        }

        if (myLevel == level) {
            return findChild;
        }

        int levelDelta = myLevel - level;
        parent = findChild.getParent();
        while (parent != null && (--levelDelta) > 0) {
            parent = parent.getParent();
        }
        return parent;
    }

    /**
     * 移除对应位置原素
     *
     * @param data
     * @param position
     * @return 返回父元素
     */
    private static BaseExpandedData removeChild(BaseExpandedData data, int position) {
        BaseExpandedData findChild = getChildByPosition(data, position);
        if (findChild == null) {
            return null;
        }
        BaseExpandedData removeChild = findChild;
        BaseExpandedData parent = findChild.getParent();
        while (parent != null) {
            parent.removeChild(removeChild);
            removeChild = parent;

            List<BaseExpandedData> list = parent.getChildrenList();

            if (list == null || list.isEmpty()) {
                parent = parent.getParent();
            } else {
                break;
            }
        }
        return findChild;
    }
}
