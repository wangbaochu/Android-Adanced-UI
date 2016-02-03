package com.open.ui.refinedpageloading.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.open.ui.refinedpageloading.R;
import com.open.ui.refinedpageloading.view.PageListingBrowser;
import com.open.ui.refinedpageloading.view.PageListingSubscriber;

public class MainActivity extends Activity {

    /** How many item will be displayed per one page */
    private static final int PAGE_SIZE = 4;
    /** The number of item at the bottom of the list that will trigger the next page to be fetched */
    private static final int TRIGGER_SIZE = 1;
    private static final String TRY_AGAIN = "error, please try again.";
    private static final String PAGE_LISTING_END = "Page List is End!";
    
    private ListView mListView;
    private View mFooter;
    private ProgressBar mProgress;
    private TextView mStatus;
    private TestBrowser mBrowser;
    private TestAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mListView = (ListView) findViewById(R.id.items_list);
        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterDividersEnabled(false);
        mListView.setDividerHeight(0);
        mFooter = LayoutInflater.from(this).inflate(R.layout.page_listing_browse_footer, null);
        mProgress = (ProgressBar) mFooter.findViewById(R.id.page_listing_progress);
        mStatus = (TextView) mFooter.findViewById(R.id.page_listing_status);
        mListView.addFooterView(mFooter, null, false);

        mBrowser = new TestBrowser(this, "http://test.com", PAGE_SIZE, TRIGGER_SIZE);
        mAdapter = new TestAdapter(this, R.layout.item_view, mPageListingSubscriber);
        mAdapter.setBrowser(mBrowser);
        mBrowser.startFirstPageRequest(PageListingBrowser.UNKNOWN_COUNT);
        mListView.setItemsCanFocus(true);
        mAdapter.setListView(mListView);
        
        mStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mStatus.getText().equals(TRY_AGAIN)) {
                    mStatus.setText("Loading...");
                    mBrowser.replayPageRequest();
                    mProgress.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void refreshPage() {
        mAdapter.notifyDataSetChanged();
    }
    
    //***********************************************************************************************
    // PageListingSubscriber: all the page are loaded completely
    //***********************************************************************************************
    private PageListingSubscriber mPageListingSubscriber = new PageListingSubscriber() {
        
        @Override
        public void onPageComplete(boolean isPageListingEnd) {
            refreshPage();
            if (isPageListingEnd) {
                mStatus.setText(PAGE_LISTING_END);
                mProgress.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled() {
            // TODO Auto-generated method stub 
        } 
        
        @Override
        public void onError(String errorMsg) {
            if (mStatus != null) {
                mStatus.setText(TRY_AGAIN);
                mProgress.setVisibility(View.GONE);
            }
        }
    };
}
