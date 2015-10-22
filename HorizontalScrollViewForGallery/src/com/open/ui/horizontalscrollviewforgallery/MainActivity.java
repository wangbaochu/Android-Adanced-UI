package com.open.ui.horizontalscrollviewforgallery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.open.ui.horizontalscrollviewforgallery.HorizontalScrollViewForGallery.CurrentImageChangeListener;
import com.open.ui.horizontalscrollviewforgallery.HorizontalScrollViewForGallery.OnItemClickListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

/**
 * Gallery自从API-LEVEL-16就被谷歌废弃了，Google推荐使用ViewPager和HorizontalScrollView来实现Gallery的效果。
 * 的确HorizontalScrollView可以实现Gallery的效果，但是HorizontalScrollView存在一个很大的问题，
 * 如果你仅是用来展示少量的图片，应该是没问题的，但是如果我希望HorizontalScrollView可以像ViewPager一样，绑定数据集（动态改变图片），
 * 显示大量图片时，就会很容易发生OOM。
 * 
 * 本工程就是对HorizontalScrollView进行扩展，自定义HorizontalScrollView实现我们上面提到的效果
 *
 */
public class MainActivity extends Activity {

    private HorizontalScrollViewForGallery mHorizontalScrollView;
    private HorizontalScrollViewForGalleryAdapter mAdapter;
    private ImageView mImg;
    private List<Integer> mDatas = new ArrayList<Integer>(Arrays.asList(
            R.drawable.a, R.drawable.b, R.drawable.c, 
            R.drawable.a, R.drawable.b, R.drawable.c,
            R.drawable.a, R.drawable.b, R.drawable.c));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        mImg = (ImageView) findViewById(R.id.id_content);
        mHorizontalScrollView = (HorizontalScrollViewForGallery) findViewById(R.id.id_horizontalScrollView);
        mAdapter = new HorizontalScrollViewForGalleryAdapter(this, mDatas);
        
        //添加滚动回调
        mHorizontalScrollView.setCurrentImageChangeListener(new CurrentImageChangeListener() {
            @Override
            public void onCurrentImgChanged(int position, View viewIndicator) {
                mImg.setImageResource(mDatas.get(position));
                viewIndicator.setBackgroundColor(Color.parseColor("#AA024DA4"));
            }
        });
        
        //添加点击回调
        mHorizontalScrollView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mImg.setImageResource(mDatas.get(position));
                view.setBackgroundColor(Color.parseColor("#AA024DA4"));
            }
        });
        
        //设置适配器
        mHorizontalScrollView.initDatas(mAdapter);
    }
}
