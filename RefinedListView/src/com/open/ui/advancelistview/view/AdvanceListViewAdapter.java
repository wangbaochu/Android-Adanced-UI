package com.open.ui.advancelistview.view;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class AdvanceListViewAdapter extends BaseAdapter {

    protected Context mContext;
    /** The item data for list-view display */
    protected List<BaseItemModel> mDataModelList;

    /** Used to backup data. When the data is changed but not called NotifyDataSetChanged(), 
     * it will not impact the original page display.
     */
    protected List<BaseItemModel> mCopyDataModelList;
    private ListViewImageLoader mListViewImageLoader;
    private BaseItemViewFactory mBaseItemModelFactory;
    private boolean mIsScorllOnce;

    public AdvanceListViewAdapter(Context context, List<BaseItemModel> itemModelList, BaseItemViewFactory factory) {
        mContext = context;
        setItemlistData(itemModelList);
        mBaseItemModelFactory = factory;
    }

    @Override
    public int getCount() {
        if (mDataModelList != null) {
            return mDataModelList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (position > -1 && mDataModelList != null && position < mDataModelList.size()) {
            return mDataModelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseItemModel model = mDataModelList.get(position);
        if (convertView == null) {
            convertView = mBaseItemModelFactory.createItemView(model);
        }

        mBaseItemModelFactory.updateItemView(convertView, model);

        if (!mIsScorllOnce && mListViewImageLoader != null) {
            ItemImageModel imageModel = model.getImageModel();
            if (imageModel != null) {
                SoftReference<Drawable> ref = imageModel.getSoftReferenceIcon();
                if (ref == null || ref.get() == null) {
                    mListViewImageLoader.addTask(model);
                }
            }
        }

        return convertView;
    }

    public List<BaseItemModel> getItemlistData() {
        return mCopyDataModelList;
    }

    public void setItemlistData(List<BaseItemModel> itemModelList) {
        if (itemModelList != null) {
            mCopyDataModelList = itemModelList;
            notifyDataSetChanged();
        } else {
            mCopyDataModelList = new ArrayList<BaseItemModel>();
        }
    }

    /**
     * Replace one model of list，used to update some item of list.
     * @param newItemModel
     * @param oldItemModel
     * @return the replaced item position，return -1 if failed.
     */
    public int replaceItemModel(BaseItemModel newItemModel, BaseItemModel oldItemModel) {
        int index = findPositionInTempData(oldItemModel);
        if (index >= 0) {
            mCopyDataModelList.set(index, newItemModel);
        }
        return index;
    }

    /**
     * Refresh the whole screen. Used when add and remove item.
     * This is time consuming, if only want to refresh some items, please use notifyPart().
     */
    @Override
    public void notifyDataSetChanged() {
        if (mDataModelList == null) {
            mDataModelList = new ArrayList<BaseItemModel>();
        }
        mDataModelList.clear();
        mDataModelList.addAll(mCopyDataModelList);
        super.notifyDataSetChanged();
    }

    /**
     * Refresh part of the screen. Only refresh some items on the screen.
     * If you want the refresh the whole screen, please use notifyDataSetChanged()
     */
    public void notifyPart(final ListView listView, final List<BaseItemModel> itemModelList) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < itemModelList.size(); i++) {
                    doNotifyPart(listView, itemModelList.get(i));
                }
            }
        });
    }

    /**
     * Refresh part of the screen. Only refresh one item on the screen.
     * If you want the refresh the whole screen, please use notifyDataSetChanged()
     */
    public void notifyPart(final ListView listView, final BaseItemModel model) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                doNotifyPart(listView, model);
            }
        });
    }

    /**
     * Find out the item data of this list view
     */
    public int findPosition(BaseItemModel model) {
        if (mDataModelList != null) {
            return mDataModelList.indexOf(model);
        }
        return -1;
    }

    public void setListViewImageLoader(AdvanceListView listView, ListViewImageLoader listViewImageLoader) {
        this.mListViewImageLoader = listViewImageLoader;
        this.mListViewImageLoader.setALiList(listView, this);
    }

    public void setIsScorllOnec(boolean isScorllOnce) {
        mIsScorllOnce = isScorllOnce;
    }

    /**
     * Refresh part of the screen. Only refresh one item on the screen.
     */
    private void doNotifyPart(ListView listView, BaseItemModel model) {
        try {
            View itemView = findVisibleItemByModel(listView, model);
            if (itemView != null) {
                mBaseItemModelFactory.updateItemView(itemView, model);
                if (mListViewImageLoader != null) {
                    ItemImageModel imageModel = model.getImageModel();
                    if (imageModel != null) {
                        SoftReference<Drawable> ref = imageModel.getSoftReferenceIcon();
                        if (ref == null || ref.get() == null) {
                            mListViewImageLoader.addTask(model);
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the visible item on screen by mode, return null if invisible.
     */
    private View findVisibleItemByModel(ListView listView, BaseItemModel model) {
        int fvp = listView.getFirstVisiblePosition();
        int mp = findPosition(model);
        if (mp < 0) {
            return null;
        }
        int headerViewsCount = listView.getHeaderViewsCount();
        View v = listView.getChildAt(mp + headerViewsCount - fvp);
        return v;
    }

    private int findPositionInTempData(BaseItemModel model) {
        if (mCopyDataModelList != null) {
            return mCopyDataModelList.indexOf(model);
        }
        return -1;
    }
}
