package com.open.ui.viewpager.view;

import java.util.List;

import android.view.View;

/**
 * Base class providing the adapter to populate pages inside of
 * a {@link TabViewPager}.  You will most likely want to use a more
 * specific implementation of this, such as
 * {@link android.support.v2.app.FragmentPagerAdapter} or
 * {@link android.support.v2.app.FragmentStatePagerAdapter}.
 */
public class TabViewPagerAdapter {

    public static final int POSITION_UNCHANGED = -1;
    public static final int POSITION_NONE = -2;
    
    private DataSetObserver mObserver;
    private List<TabViewModel> mTabModelList;

    /**
     * Used to watch for changes within the adapter.
     */
    interface DataSetObserver {
        public void onDataSetChanged();
    }
    
    public TabViewPagerAdapter(List<TabViewModel> tabModelList) {
        mTabModelList = tabModelList;
    }

    public int getCount() {
        if(mTabModelList == null){
            return 0;
        }
        return mTabModelList.size();
    }

    public Object instantiateItem(View container, int position) {
        if(mTabModelList == null){
            return null;
        }
        TabViewModel model = mTabModelList.get(position);
        TabView viewItem = model.getViewItem();
        if (!model.isViewItemCreated()){
            model.setViewItemCreated(true);
            viewItem.onCreate();
        }
        View itemView = viewItem.getView(); // 先创建后getView，避免外部空初始化

        /*  不能使用arg1 ViewPager默认只初始化两个页面 mDefaultTabIndex-1 以及 mDefaultTabIndex， 
         *     若外部设置的默认页面mDefaultTabIndex > 1,则会导致addView操作出现IndexOutOfBoundsException
         */
        if(itemView.getParent() == null){
            ((TabViewPager) container).addView(itemView, 0); 
        }

        return itemView;
    }

    public boolean isViewFromObject(View view, Object object){
        return view == (object);
    }

    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    /**
     * This method should be called by the application if the data backing this adapter has changed
     * and associated views should update.
     */
    public void notifyDataSetChanged() {
        if (mObserver != null) {
            mObserver.onDataSetChanged();
        }
    }

    public void setDataSetObserver(DataSetObserver observer) {
        mObserver = observer;
    }

}
