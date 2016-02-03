/**
 *
 *
 */
package com.open.ui.refinedpageloading.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class PageListingAdapter<T> extends BaseAdapter implements OnItemClickListener {

    protected PageListingBrowser<T> mBrowser;
    private ListView listView;
    protected final LayoutInflater layoutInflater;
    protected final int rowResId;
    private PageListingSubscriber mPageListingSubscriber;
    
    public PageListingAdapter(final Context context, final int rowResId, final PageListingSubscriber subscriber) {
        this.layoutInflater = LayoutInflater.from(context);
        this.rowResId = rowResId;
        this.mPageListingSubscriber = subscriber;
    }

    public ListView getListView() {
        return listView;
    }
    
    public void setListView(ListView listView) {
        this.listView = listView;
        listView.setAdapter(this);
        listView.setOnItemClickListener(this);
    }
    
    public PageListingBrowser<T> getBrowser() {
        return mBrowser;
    }

    public void setBrowser(PageListingBrowser<T> browser) {
        this.mBrowser = browser;
        browser.setmPageListingSubscriber(mPageListingSubscriber);
        notifyDataSetChanged();
    }

    /**
     * Implement this function in the subclass to handle clicking one item in the list.
     * @param object
     */
    public abstract void goToClickedItem (AdapterView<?> parent, View view, final T object);

    //--------------------------------------------------------------------------
    // OnItemClickListener
    //--------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = position - (null == listView ? 0 : listView.getHeaderViewsCount());
        if (index < 0 || index >= mBrowser.getReceivedCount()) {
            return;
        }

        mBrowser.setCurrentIndex(index);
        T obj = mBrowser.getCurrentObject();
        goToClickedItem(parent, view, obj);
    }

    //--------------------------------------------------------------------------
    // Adapter Interface Implementations
    //--------------------------------------------------------------------------
    @Override
    public int getCount() {
        if (null == mBrowser) {
            return 0;
        }
        return mBrowser.getReceivedCount();
    }
    
    @Override
    public long getItemId(int position) {
        // this is sufficient for most lists of objects
        return position;
    }

    @Override
    public Object getItem(int position) {
        // this is sufficient for most lists of objects
        return position;
    }
    
}
