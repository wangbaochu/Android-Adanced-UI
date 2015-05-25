package com.open.ui.viewpager.view;

import java.util.List;

import com.open.ui.refinedviewpager.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * tab页面类型的activity
 */
public abstract class TabActivity extends Activity {

    private static final String TAG = "TabActivity";

    /** Activity整个页面视图:Activity Title + TabViewContainer **/
    protected TabPage mPageView;

    /** TabViewContainer页面: TabTitle + TabViewPager **/
    private RelativeLayout mTabViewContainer;

    /** TabViewPager页面:左右滑动控制TabView **/
    private TabViewPager mTabViewPager;

    /** TabView的Title部分**/
    private LinearLayout mTitleView;
    private TabTitleScrollBar mTabTitleScrollBar;
    private View mTabTitleBottomBar;

    /** 读取数据过渡的视图 **/
    private View mLoadingView;

    /** TabView的 数据模型列表 **/
    private List<TabViewModel> mTabModelList;

    /** 默认选中的Tab index **/
    private int mDefaultTabIndex;

    /** 当前选中的具体页面Item项 **/
    private TabView mCurrentViewItem;

    private int mCurrentIndex;

    private ViewUpdater mViewUpdater;

    private boolean mEnable = true;

    private boolean mParentPageFirstShowed;

    private boolean mIsFirstOnRusume = true;
    protected Context mContext;

    public TabPage getPageView() {
        return mPageView;
    }

    public View getTitleView(){
        return mTitleView;
    }

    public ViewUpdater getViewUpdater(){
        if (mViewUpdater == null) {
            mViewUpdater = new ViewUpdater();
        }
        return mViewUpdater;
    }

    /**
     * 该页面第一次绘制视图在屏幕上的时机回调
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (!mParentPageFirstShowed && mCurrentViewItem != null && mTabModelList != null){ // 父亲第一次回调主动刷新当前子tab
            mParentPageFirstShowed = true;
            mTabModelList.get(mCurrentIndex).setViewItemPageFirstShowed(true);
            mCurrentViewItem.onCreate();
        }
    }

    protected void initTabPage(TabPage parent) {
        if (mTabViewContainer == null) {
            mTabViewContainer = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.layout_tab_view, null);
            mTitleView = (LinearLayout)mTabViewContainer.findViewById(R.id.tab_title);
            mTabTitleScrollBar = (TabTitleScrollBar)mTabViewContainer.findViewById(R.id.tab_title_scroll_bar);
            mTabTitleBottomBar = (View)mTabViewContainer.findViewById(R.id.tab_title_bottom_line);

            List<TabViewModel> modelList = createTabViewModel();
            if (modelList == null){
                Log.w(TAG, "mCTabModelList is null");
                return;
            }

            resetTabModelData(modelList);

            mTabTitleScrollBar.setViewPager(mTabViewPager);

            if(mLoadingView != null){
                mTabViewContainer.removeView(mTitleView);
                mTabViewContainer.removeView(mTabTitleScrollBar);
                mTabViewContainer.removeView(mTabTitleBottomBar);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mTabViewContainer.addView(mLoadingView, params);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, mTabTitleBottomBar.getId());
                mTabViewContainer.addView(mTabViewPager, params);
            }
        }

        if (parent != null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
            parent.addView(mTabViewContainer, layoutParams);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            setContentView(mTabViewContainer, params);
        }

        onResumeInit();
    }

    @Override
    public void onResume(){
        super.onResume();

        if (!mIsFirstOnRusume) {
            onResumeInit();
        } else {
            mIsFirstOnRusume = false;
        }

        if (mCurrentViewItem != null && mTabModelList != null) {
            mCurrentViewItem.onResume();
        } else {
            setCurrentViewItem(mDefaultTabIndex);
        }
    }

    @Override
    public void onPause(){
        if (mCurrentViewItem != null){
            mCurrentViewItem.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(mTabModelList != null){
            for (TabViewModel model: mTabModelList){
                model.getViewItem().onDestroy();
            }
        }

        super.onDestroy();
    }

    //================================================================================================================================
    protected void onResumeInit() {
        if (mCurrentViewItem != null && mTabModelList != null){ // 父亲第一次回调主动刷新当前子tab
            mCurrentViewItem.onResume();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mCurrentViewItem != null && mCurrentViewItem.onBackPressed()){
                return;
            }
            super.onBackPressed();
        } catch (Exception e){
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mCurrentViewItem != null){
            if(mCurrentViewItem.onKeyDown(keyCode, event)){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(mCurrentViewItem != null){
            if(mCurrentViewItem.onKeyUp(keyCode, event)){
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 设置当前View
     * @param current
     */
    public void setCurrentViewItem(int current){
        TabViewModel newModel = mTabModelList.get(current);
        if (mCurrentViewItem == null){ // 当前页面Item为null 第一次进入TabView
            if (mDefaultTabIndex > -1 && mDefaultTabIndex < mTitleView.getChildCount()){
                RelativeLayout defaultTab = (RelativeLayout)mTitleView.getChildAt(mDefaultTabIndex);	
                ((TextView)defaultTab.findViewById(R.id.tab_title_text)).setTextColor(getResources().getColor(R.color.tab_activity_tabbar_text_selected));
                defaultTab.setClickable(false);				
            }
            mCurrentViewItem = newModel.getViewItem();
            if (!newModel.isViewItemCreated()){
                newModel.setViewItemCreated(true);
                mCurrentViewItem.onCreate();
            }
            mCurrentViewItem.onResume();
            mTabViewPager.setCurrentItem(current);

        }else {	
            TabView newViewItem = newModel.getViewItem();
            if (mCurrentViewItem == newViewItem){ 	// 当前viewItem 即为新ViewItem 则直接返回
                return;
            }

            if (newViewItem != null){
                mCurrentViewItem.onPause();			// 先调用前一个页面的onPause

                if (!newModel.isViewItemCreated()){
                    newModel.setViewItemCreated(true);
                    newViewItem.onCreate();
                }

                newViewItem.onResume();
                mCurrentViewItem = newViewItem;
            }			
        }

        Log.i("QBaseTabPage", "QBaseTabPage onPageFirstShow setCurrentViewItem current "+ current + " mParentPageFirstShowed: "+ mParentPageFirstShowed + " mCurrentViewItem: " + mCurrentViewItem);
        if(mParentPageFirstShowed){ // 父亲完成第一次回调后再因为其他原因重设子tab则需要主动刷新子tab，如果父亲还没有回调则等待父亲回调
            if(!newModel.isViewItemPageFirstShowed()){
                newModel.setViewItemPageFirstShowed(true);
                mCurrentViewItem.onCreate();
                mCurrentViewItem.onResume();
            }else{
                mCurrentViewItem.onResume();
            }
        }

    }

    /**
     * 获取TabView所需的数据
     * 抽象接口，子类实现
     * @return
     */
    public abstract List<TabViewModel> createTabViewModel();

    /**
     * 设置默认的tab 建议在onCreate()中调用
     * @param index 默认tab的index
     */
    public void setDefaultTabIndex(int index) {
        mDefaultTabIndex = index;
    }

    /**
     * 页面切换回调
     * 子类重写实现
     * @param index 滑动结束后当前tab index 从0 开始
     */
    protected void onTabSelected(int index){
    }

    /**
     * 通知数据改变
     * 暂时只是刷新title 部分
     */
    public void notifyTabModelDataChanged(){
        if (mTabModelList != null){
            int size = mTabModelList.size();
            for (int index = 0; index < size; index++){
                ((TextView)(mTitleView.getChildAt(index)).findViewById(R.id.tab_title_text)).setText(mTabModelList.get(index).getTitle());
            }
        }
    }

    /**
     * 重置tab model list 
     * 例如在数据初始化完成后才能决定tab的个数
     * 调用时机比较敏感，一定要在mTabView初始化完成后才可以
     * 子类继承，暂时不提供外部使用，
     * @param modelList
     */
    protected void resetTabModelData(List<TabViewModel> modelList){
        Log.i(TAG, "resetTabModelData s");
        if(modelList == null || modelList.size() ==0){
            return;
        }
        mTabModelList = modelList;
        mTitleView.removeAllViews();
        int size = mTabModelList.size();
        for (int index = 0; index < size; index++){
            RelativeLayout tabTitle = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.layout_tab_title, null);
            tabTitle.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
            ((TextView)tabTitle.findViewById(R.id.tab_title_text)).setText(mTabModelList.get(index).getTitle());
            tabTitle.setId(index);
            tabTitle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEnable)
                        mTabViewPager.setCurrentItem(v.getId());
                }
            });

            mTitleView.addView(tabTitle, index);

        }
        if (mTabViewPager == null){
            mTabViewPager = new TabViewPager(this);
        }
        mTabViewPager.removeAllViews();
        //自动插桩会报java.lang.IllegalAccessError异常，暂时屏蔽。
        mTabTitleScrollBar.setOnPageChangeListener(new TabViewPager.OnPageChangeListener() {

            /**当前TAB索引**/
            private int mCurrent;

            @Override
            public void onPageSelected(int position) {
                Log.i("ViewPager", "onPageSelected "+position);
                SwitchTabTitle(mCurrent, position);  // 修改页面标题
                mCurrent = position;
                mCurrentIndex = mCurrent;
                setCurrentViewItem(mCurrent);		 // 切换页面
                onTabSelected(mCurrent); 		 // 完成页面切换通知回调
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 从1到2滑动，在1滑动前调用
                Log.i("ViewPager", "onPageScrolled "+position+" "+positionOffset+" "+positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                PageScrollStateChanged(state);
                Log.i("ViewPager", "onPageScrollStateChanged "+state);
            }

            @Override
            public void onPageScrollDeltaX(float deltax) {
            }
        });   
        mTabViewPager.setAdapter(new TabViewPagerAdapter(mTabModelList));    // 设置监听器
        Log.i(TAG, "resetTabModelData e");
    }

    /**
     * 自动插桩会替换这个空方法。jinnining2013-09-03
     */
    public void PageScrollStateChanged(int arg0){
    }

    /**
     * 代码主动设置当前页面
     * 
     * @param index 页面索引，从0开始计算
     */
    public void setCurrentView(int index){
        mTabViewPager.setCurrentItem(index);
    }

    /**
     * 禁止ViewPager响应触摸手势以防止滑屏
     * @param disable
     * 			true 为禁止ViewPager滑屏
     */
    public void disableInterceptTouchEvent(boolean disable){
        Log.i(TAG, "disableInterceptTouchEvent: " + disable);
        mTabViewPager.disableInterceptTouchEvent(disable);
    }

    /**
     * 是否允许滚动
     * 
     * @param bEnable 是否滚动
     */
    public void setEnableScroll(boolean bEnable){
        mEnable = bEnable;
        mTabViewPager.setEnabled(bEnable);
    }

    /**
     * 页面滑动时刷新tab title 不处理具体页面
     * @param previous
     * 		  滑动开始前当前的tab index
     * @param current
     * 		  滑动结束时当前的tab index
     */
    private void SwitchTabTitle(int previous, int current) {
        RelativeLayout preLayout = (RelativeLayout)mTitleView.getChildAt(previous);
        TextView preTextView = (TextView)preLayout.findViewById(R.id.tab_title_text);

        RelativeLayout curLayout = (RelativeLayout)mTitleView.getChildAt(current);
        TextView curTextView = (TextView)curLayout.findViewById(R.id.tab_title_text);

        preTextView.setTextColor(getResources().getColor(R.color.common_text_color_grey));
        curTextView.setTextColor(getResources().getColor(R.color.tab_activity_tabbar_text_selected));

        preLayout.setClickable(true);
        curLayout.setClickable(false);

    }

    public class ViewUpdater {
        public void updateView(View view) {
            mTabViewPager.updataChildPage(view);
        }
    }

    public View getViewPager(){
        return mTabViewPager;
    }

    public int getCurTabIndex() {
        return mCurrentIndex;
    }

    public TabView getCurTabItem() {
        return mCurrentViewItem;
    }
}
