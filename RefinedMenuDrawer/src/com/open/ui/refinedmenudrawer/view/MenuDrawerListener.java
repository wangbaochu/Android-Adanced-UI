package com.open.ui.refinedmenudrawer.view;

/**
 * Listener interface for MenuDrawer actions.
 */
public interface MenuDrawerListener {

    public void onDrawerSlide(MenuDrawer drawer, float slideOffset);
    
    public void onDrawerOpened(MenuDrawer drawer);
    
    public void onDrawerClosed(MenuDrawer drawer);
}
