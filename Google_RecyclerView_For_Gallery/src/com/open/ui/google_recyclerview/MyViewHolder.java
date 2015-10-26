package com.open.ui.google_recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.open.ui.google_recyclerview.GalleryAdapter.OnItemClickLitener;

public class MyViewHolder extends RecyclerView.ViewHolder {

    private OnItemClickLitener mListener;

    public MyViewHolder(View itemView, OnItemClickLitener litener) {
        super(itemView);
        mListener = litener;
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view, getPosition());
            }
        });
    }
    
    ImageView mImage;
}

