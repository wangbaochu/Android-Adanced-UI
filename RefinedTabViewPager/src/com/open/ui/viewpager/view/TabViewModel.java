package com.open.ui.viewpager.view;

/**
 * 封装TabView用到的数据模型,可以继承扩展
 */
public class TabViewModel {
	
	/** tab ID**/
	protected int mId;
	
	/** tab 标题 **/
	protected String mTitle;
		
	/** tab 对应页面Item **/
	protected TabView mViewItem;
	
	/** tab 对应页面Item是否已经调用过onCreate 默认初始化**/
	protected boolean mCreated = false;
	
	/** tab 对应页面Item是否已经调用过onPageFirstShow 默认初始化**/
	protected boolean mPageFirstShowed = false;
	
	/**
	 * 无参数构造，主要用来外面使用setXXX接口进行初始化的情况
	 */
	public TabViewModel(){
	}
	
	/**
	 * 构造方法 外部构造TabViewItem
	 * @param title
	 * @param item
	 */
	public TabViewModel(String title, TabView viewItem){
		this.mTitle = title;
		this.mViewItem = viewItem;
	}
		
	public String getTitle(){
		return mTitle;
	}
	
	public void setTitle(String title){
		this.mTitle = title;
	}
	
	public TabView getViewItem(){
		return mViewItem;
	}
	
	public void setViewItem(TabView viewItem){
		this.mViewItem = viewItem;
	}
	
	public boolean isViewItemCreated(){
		return mCreated;
	}
	
	public void setViewItemCreated(boolean created){
		this.mCreated = created;
	}
	
	public boolean isViewItemPageFirstShowed(){
		return mPageFirstShowed;
	}
	
	public void setViewItemPageFirstShowed(boolean pageFirstShowed){
		this.mPageFirstShowed = pageFirstShowed;
	}
	
	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}
	
}