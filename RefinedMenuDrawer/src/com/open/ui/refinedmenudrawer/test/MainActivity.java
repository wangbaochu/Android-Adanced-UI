package com.open.ui.refinedmenudrawer.test;

import java.util.ArrayList;
import java.util.List;

import com.open.ui.refinedmenudrawer.R;
import com.open.ui.refinedmenudrawer.view.MenuDrawer;
import com.open.ui.refinedmenudrawer.view.MenuItemAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.app.Activity;

public class MainActivity extends Activity {
    
    private MenuDrawer<TestItemData> mMenuDrawer = null;
    private MenuItemAdapter<TestItemData> mAdapter = null;
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Set the menu drawer to the content view
        View contentView = this.getLayoutInflater().inflate(R.layout.activity_main, null);
        mMenuDrawer = new MenuDrawer<TestItemData>(this);
        contentView = mMenuDrawer.applyMenuDrawer(contentView);
        mMenuDrawer.setListener(null);
        mAdapter = new MenuItemAdapter<TestItemData>(new TestMenuDrawerItemFactory(this));
        mMenuDrawer.setDrawerMenuAdapter(mAdapter);
        setContentView(contentView);
        
        mHandler = new Handler();
        initLoadMenuData();
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        mMenuDrawer.resetPosition();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        // Make sure to close the drawer when app is going into the background so that it is closed when
        // app is woken up from the background.
        mMenuDrawer.close();
    }
    
    @Override
    protected void onDestroy() {
        mMenuDrawer.destroy();
        super.onDestroy();
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            if (KeyEvent.KEYCODE_BACK == event.getKeyCode() && mMenuDrawer.isOpen()) {
                mMenuDrawer.close();
                return true;
            } else if (KeyEvent.KEYCODE_MENU == event.getKeyCode()) {
                mMenuDrawer.toggle();
                return true;
            }
        }
        
        return super.dispatchKeyEvent(event);
    }
    
    private void initLoadMenuData() {
        new Thread() {
            @Override
            public void run() {
                final List<TestItemData> itemModelList = new ArrayList<TestItemData>();
                for (int i = 0; i < 8; i++) {
                    TestItemData itemData = new TestItemData();
                    itemData.setItemTitle(String.format("Item %d", i));
                    itemModelList.add(itemData);
                }
                
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setItemlistData(itemModelList);
                    }
                });
            }
        }.start();
    }
}
