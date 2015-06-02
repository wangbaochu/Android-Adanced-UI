package com.open.ui.advancelistview.test;

import java.lang.ref.SoftReference;

import com.open.ui.advancelistview.view.BaseItemModel;
import com.open.ui.advancelistview.view.ItemImageModel;
import com.open.ui.refinedlistview.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestItemView extends LinearLayout {

    private ImageView mItemImage = null;
    private TextView mItemTitle = null;

    public TestItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mItemImage = (ImageView) findViewById(R.id.app_icon);
        mItemTitle = (TextView) findViewById(R.id.app_title);
    }

    public void updateView(BaseItemModel model) {
        if (model != null && (model instanceof TestItemMode)) {
            TestItemMode testMode = (TestItemMode) model;
            if (mItemTitle != null && testMode.getItemTitle() != null) {
                mItemTitle.setText(testMode.getItemTitle());
            }

            ItemImageModel imageModel = testMode.getImageModel();
            if (imageModel != null) {
                SoftReference<Drawable> ref = imageModel.getSoftReferenceIcon();
                if(ref != null && ref.get() != null) {
                    mItemImage.setImageDrawable(ref.get());
                } else if (imageModel.getDefaultIcon() != null) {
                    mItemImage.setImageDrawable(imageModel.getDefaultIcon());
                }  else if (imageModel.getDrawableIcon() != null) {
                    mItemImage.setImageDrawable(imageModel.getDrawableIcon());
                } else if (imageModel.getBitmapIcon() != null) {
                    mItemImage.setImageBitmap(imageModel.getBitmapIcon());
                }
            }
        }
    }
}
