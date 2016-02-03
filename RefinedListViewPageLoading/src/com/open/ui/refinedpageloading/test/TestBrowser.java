package com.open.ui.refinedpageloading.test;

import java.util.ArrayList;
import java.util.List;

import com.open.ui.refinedpageloading.net.ErrorCode;
import com.open.ui.refinedpageloading.net.MService;
import com.open.ui.refinedpageloading.net.ServiceCallback;
import com.open.ui.refinedpageloading.view.PageListingBrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class TestBrowser extends PageListingBrowser<String> implements ServiceCallback {

    private static final boolean DEBUG_TEST = true;
    private Activity mActivity = null;
    private String mUrl = null;
    
    public TestBrowser(Activity activity, String url, int pageSize, int nextPageTriggerCount) {
        super(pageSize, nextPageTriggerCount);
        mActivity = activity;
        mUrl = url;
    }

    /**
     * You should pass the offset and pageSize to server side. So that server 
     * can know which beginning of data should sent back to you.
     */
    @Override
    protected MService startPageRequest(int pageNumber) {
        if (!TextUtils.isEmpty(mUrl)) {
            
            //The beginning index of data that server should send back.
            String offsetParam = "offset=" + pageNumber * pageSize;
            
            //The count of data that server should send back per request.
            String pageSizeParam = "count=" + pageSize;

            if (mUrl.contains("?")) {
                if (mUrl.contains("&")) {
                    mUrl = mUrl + "&" + offsetParam + "&" + pageSizeParam;
                } else if (mUrl.endsWith("?")) {
                    mUrl = mUrl + offsetParam + "&" + pageSizeParam;
                }
            } else {
                mUrl = mUrl + "?" + offsetParam + "&" + pageSizeParam;
            }

            MService service = new MService(mActivity, this);
            Intent data = new Intent(MService.ACTION_DO_GET);
            data.putExtra("url", mUrl);

            service.start(data);
            return service;
        } else {
            return null;
        }
    }
    
    /********************************************************************
     * implement ServiceCallback interface
     *******************************************************************/
    @Override
    public void onServiceComplete(Intent intent) {
        if (DEBUG_TEST) {
            //Only for debug testing
            MainApplication.getInstance().invokeLater(new Runnable() {
                @Override
                public void run() {
                    List<String> itemList = new ArrayList<String>();
                    itemList.add("item1");
                    itemList.add("item2");
                    itemList.add("item3");
                    itemList.add("item4");
                    itemList.add("item5");
                    itemList.add("item6");
                    itemList.add("item7");
                    itemList.add("item8");
                    itemList.add("item9");
                    itemList.add("item10");
                    itemList.add("item11");
                    itemList.add("item12");
                    itemList.add("item13");
                    itemList.add("item14");
                    itemList.add("item15");
                    itemList.add("item16");
                    itemList.add("item17");
                    itemList.add("item18");
                    itemList.add("item19");
                    itemList.add("item20");
                    completed(itemList, false);
                }
            }, 2000);
            
        } else {
            //Check the Http error
            Bundle bundle = intent.getExtras();
            int what = bundle.getInt("what", 200);
            if (what != 200) {
                switch (what) {
                case ErrorCode.SERVER_RETURN_ERROR:
                    String errorMsg = intent.getStringExtra("msg");
                    if (!TextUtils.isEmpty(errorMsg)) {
                        error(errorMsg);
                    } else {
                        error(ErrorCode.getMsg(what));
                    }
                default:
                    error(ErrorCode.getMsg(what));
                }
                return;
            }

            List<String> itemList = new ArrayList<String>();
            //Parse the response data
            Bundle dataBundle = bundle.getBundle("data");
            if (dataBundle != null) {
                for (int i = 0; i < dataBundle.size(); i++) {
                    //TODO itemList.add(data)
                }
            }

            boolean isPageListingEnd = (dataBundle == null || dataBundle.size() < pageSize);
            this.completed(itemList, isPageListingEnd);
        }
    }

    @Override
    public void onServiceCancelled() {
        this.onServiceCancelled();
    } 
}
