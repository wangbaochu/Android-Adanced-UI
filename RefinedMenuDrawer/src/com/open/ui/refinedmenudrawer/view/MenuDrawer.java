package com.open.ui.refinedmenudrawer.view;

import java.util.HashSet;
import java.util.Set;

import com.open.ui.refinedmenudrawer.R;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MenuDrawer<T> {
    
    private static final int DRAWER_GRAVITY = Gravity.LEFT;
    private MenuDrawerLayout mDrawerLayouView;
    private ListView mMenuList;
    private MenuDrawerListener mListener;
    private MenuItemAdapter<T> mMenuAdapter = null;
    
    public void setDrawerMenuAdapter(MenuItemAdapter<T> adapter) {
        mMenuAdapter = adapter;
        buildMenu(adapter);
    }
    
    public MenuDrawer(Context context) {
        this.mDrawerLayouView = (MenuDrawerLayout) LayoutInflater.from(context).inflate(R.layout.menu_drawer, null);
        mDrawerLayouView.setDrawerShadow(R.drawable.menu_drawer_shadow, DRAWER_GRAVITY);
        mDrawerLayouView.setDrawerListener(new DrawerListener());
        mMenuList = (ListView) mDrawerLayouView.findViewById(R.id.menu_drawer_list);
    }
    
    /**
     * Closes the drawer
     */
    public void close() {
        if (isUnlocked()) {
            mDrawerLayouView.closeDrawer(DRAWER_GRAVITY);
        }
    }
    
    /**
     * Opens the drawer if closed. Closes the drawer if opened
     */
    public void toggle() {
        if (isUnlocked()) {
            if (mDrawerLayouView.isDrawerOpen(DRAWER_GRAVITY)) {
                mDrawerLayouView.closeDrawer(DRAWER_GRAVITY);
            } else {
                mDrawerLayouView.openDrawer(DRAWER_GRAVITY);
            }
        }
    }
    
    /**
     * Opens the drawer
     */
    public void open() {
        if (isUnlocked()) {
            mDrawerLayouView.openDrawer(DRAWER_GRAVITY);
        }
    }
    
    /**
     * Locks the drawer.
     * @param opened - locks the drawer opened if true, locks the drawer closed otherwise
     */
    public void lock(boolean opened) {
        if (opened) {
            mDrawerLayouView.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, DRAWER_GRAVITY);
        } else {
            mDrawerLayouView.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, DRAWER_GRAVITY);
        }
    }
    
    /**
     * Locks the drawer.
     */
    public void lock() {
        lock(mDrawerLayouView.isDrawerOpen(DRAWER_GRAVITY));
    }
    
    /**
     * Unlocks the drawer.
     */
    public void unlock() {
        mDrawerLayouView.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, DRAWER_GRAVITY);
    }
    
    /**
     * @return true if drawer is open, false otherwise
     */
    public boolean isOpen() {
        return mDrawerLayouView.isDrawerOpen(DRAWER_GRAVITY);
    }
    
    /**
     * @return true if drawer is closed, false otherwise
     */
    public boolean isClosed() {
        return !isOpen();
    }
    
    /**
     * @return true if drawer is visible, false otherwise
     */
    public boolean isVisible() {
        return mDrawerLayouView.isDrawerVisible(DRAWER_GRAVITY);
    }
    
    /**
     * @return true if drawer is unlocked, false otherwise
     */
    public boolean isUnlocked() {
        return DrawerLayout.LOCK_MODE_UNLOCKED == mDrawerLayouView.getDrawerLockMode(DRAWER_GRAVITY);
    }
    
    /**
     * Collapses the expanded group and moves the drawer back to the starting position
     */
    public void resetPosition() {
        mMenuList.setSelection(0);
    }
    
    public void refresh() {
        if (mMenuAdapter != null) {
            mMenuAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * Should be called to destroy this Menu drawer
     */
    public void destroy() {
        // this will unregister the ListView from the adapter's data set observer
        mMenuList.setAdapter(null);
    }
    
    /**
     * Adds the Menu Drawer to the view.
     * 
     * @param contentView
     * @return a View with the Menu drawer applied
     */
    public View applyMenuDrawer(View contentView) {
        View oldContent = mDrawerLayouView.findViewById(R.id.content_frame);
        int index = mDrawerLayouView.indexOfChild(oldContent);
        mDrawerLayouView.removeView(oldContent);
        mDrawerLayouView.addView(contentView, index);
        
        return mDrawerLayouView;
    }
    
    /**
     * Adds the menu items to the menu.
     */
    private void buildMenu(MenuItemAdapter<T> adapter) { 
        mMenuList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
            }
        });
        mMenuList.setDescendantFocusability(ListView.FOCUS_BEFORE_DESCENDANTS);
        mMenuList.setAdapter(adapter);
    }
    
    /**
     * Listener for DrawerLayout
     */
    private class DrawerListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            if (mListener != null) {
                mListener.onDrawerSlide(MenuDrawer.this, slideOffset);
            }
        }

        @Override
        public void onDrawerClosed(View view) {
            if (mListener != null) {
                mListener.onDrawerClosed(MenuDrawer.this);
            }
        }

        @Override
        public void onDrawerOpened(View view) {
            if (mListener != null) {
                mListener.onDrawerOpened(MenuDrawer.this);
            }
        }

        @Override
        public void onDrawerStateChanged(int state) {
            switch (state) {
                case DrawerLayout.STATE_SETTLING :
                    // fall-through.
                    // Need to close keyboard during settling to handle "..." menu opening the menu
                case DrawerLayout.STATE_DRAGGING :
                    break;
                default :
                    // no-op
            }
        }
    }
    
    /**
     * Adds a listener for the MenuDrawer's actions
     * @param listener
     */
    public void setListener(MenuDrawerListener listener) {
        mListener = listener;
    }
    
    /**
     * Remove a listener
     * @param listener
     */
    public void removeListener() {
        mListener = null;
    }
}
