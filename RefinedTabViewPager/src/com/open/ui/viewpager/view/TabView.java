package com.open.ui.viewpager.view;

import android.view.KeyEvent;
import android.view.View;

/**
 * tab里面的view包装接口
 */
public abstract class TabView {
	
	/**
	 * 生命周期方法 Tab View 创建初始化回调
	 */
	public void onCreate(){
	}
	
	/**
	 * 生命周期方法 Tab View 从未选中切换为选中状态后回调显示
	 */
	public void onResume(){
	}

	/**
	 * 生命周期方法 Tab View 从选中状态切换为未选中状态回调
	 */
	public void onPause(){
	}
	
	/**
	 * 生命周期方法 Tab View 被销毁时回调
	 */
	public void onDestroy(){
	}
	
	/**
	 * 获取具体的View视图, QTabBaseView中已经保证在onCreate()后调用,防止未构造
	 */
	public abstract View getView();
	
	public boolean onBackPressed(){
		return false;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		return false;
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event){
		return false;
	}

	/**
	 * 如果item需要在除初始化之外的时间进行排布， 那么需要把TabActivity的getViewUpdater传进来
	 */
	private TabActivity.ViewUpdater mViewUpdater;
	/**
	 * 设置ViewUpdater
	 */
	public void setViewUpdater(TabActivity.ViewUpdater viewUpdater){
		mViewUpdater = viewUpdater;
	}
	/**
	 * 进行view的重新排布
	 */
	public void updateView(){
		View view = getView();
		if (mViewUpdater != null && view != null)
			mViewUpdater.updateView(view);
	}
	
}
