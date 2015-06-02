package com.open.ui.advancelistview.test;

import java.util.ArrayList;
import java.util.List;

import com.open.ui.advancelistview.view.AdvanceListView;
import com.open.ui.advancelistview.view.AdvanceListViewAdapter;
import com.open.ui.advancelistview.view.BaseItemModel;
import com.open.ui.advancelistview.view.ItemImageModel;
import com.open.ui.advancelistview.view.ItemImageModel.ImageFetcher;
import com.open.ui.refinedlistview.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class MainActivity extends Activity {
    
    private Handler mHandler = null;
    private ProgressDialog mProgressDialog = null;
    private AdvanceListView mListView = null;
    private AdvanceListViewAdapter mAdapter = null;
    private List<BaseItemModel> mItemModelList = new ArrayList<BaseItemModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        
        mListView = (AdvanceListView) findViewById(R.id.list_view);
        mAdapter = new AdvanceListViewAdapter(this, mItemModelList, new TestItemViewFactory(this));
        mListView.setAdapter(mAdapter);
        loadItemData();
    }
    
    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
    
    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    
    private void loadItemData() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this); 
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
        }
        
        new Thread() {
            @Override
            public void run() {
                List<AppInfo> appInfoList = AppInfo.loadAllInstalledAppInfo(MainActivity.this, true, true, true);
                Drawable defautIcon = MainActivity.this.getResources().getDrawable(R.drawable.defaut_icon);
                
                for (int i = 0; i < appInfoList.size(); i++) {
                    final AppInfo appInfo = appInfoList.get(i);
                    if (appInfo != null) {
                        ItemImageModel imageModel = new ItemImageModel(defautIcon, new ImageFetcher() {
                            @Override
                            public Drawable fetchImage() {
                                PackageManager packageManager = MainActivity.this.getPackageManager();
                                return AppInfo.loadIcon(packageManager, appInfo.mApplicationInfo);
                            }

                        });
                        
                        TestItemMode itemModel = new TestItemMode();
                        itemModel.setItemImageModel(imageModel);
                        itemModel.setItemTitle(appInfo.mLable);
                        mItemModelList.add(itemModel);
                    }
                }
                
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        dismissProgressDialog();
                    }
                });
            }
        }.start();
    }

}
