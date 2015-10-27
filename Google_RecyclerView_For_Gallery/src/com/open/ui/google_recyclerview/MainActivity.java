package com.open.ui.google_recyclerview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.open.ui.google_recyclerview.GalleryAdapter.OnItemClickLitener;
import com.open.ui.google_recyclerview.MyRecyclerView.OnItemScrollChangeListener;

public class MainActivity extends Activity {

    private MyRecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<Integer> mDatas;
    private ImageView mBigImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatas = new ArrayList<Integer>(Arrays.asList(R.drawable.a,
                R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e,
                R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i,
                R.drawable.j, R.drawable.k, R.drawable.l));

        mRecyclerView = (MyRecyclerView) findViewById(R.id.id_recyclerview_horizontal);
        mRecyclerView.setBackgroundColor(Color.GRAY);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new GalleryAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new MyItemAnimator());

        mBigImageView = (ImageView) findViewById(R.id.id_content);
        mRecyclerView.setOnItemScrollChangeListener(new OnItemScrollChangeListener() {
            @Override
            public void onChange(View view, int position) {
                mBigImageView.setImageResource(mDatas.get(position));
            };
        });

        mAdapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                mBigImageView.setImageResource(mDatas.get(position));
            }
        });
    }
}
