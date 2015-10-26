package com.open.ui.google_recyclerview;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GalleryAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private LayoutInflater mInflater;
    private List<Integer> mDatas;
    
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public GalleryAdapter(Context context, List<Integer> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_layout, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view, mOnItemClickLitener);
        viewHolder.mImage = (ImageView)view.findViewById(R.id.id_index_gallery_item_image);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int i) {
        viewHolder.mImage.setImageResource(mDatas.get(i));
    }
}
