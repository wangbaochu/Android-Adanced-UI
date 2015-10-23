package com.open.ui.gallery3d;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    private Integer[] mImageIds;
    private ImageView[] mImages;
    private int mImageViewWidth;
    private int mImageHeight;

    /**
     * The iamge view adapter
     * @param c
     * @param ImageIds
     * @param width in pixel
     * @param height in pixel
     */
    public ImageAdapter(Context c, Integer[] ImageIds, int width, int height) {
        mImageViewWidth = width;
        mImageHeight = height;
        mContext = c;
        mImageIds = ImageIds;
        mImages = new ImageView[mImageIds.length];
    }

    public boolean createReflectedImages() {
        final int reflectionGap = 4;
        int index = 0;
        for (int imageId : mImageIds) {
            Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                    height/2, width, height/2, matrix, false);
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                    (height + height / 2), Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmapWithReflection);
            canvas.drawBitmap(originalImage, 0, 0, null);
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
            
            LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                    bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
            Paint paint = new Paint();
            paint.setAntiAlias(false);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()+ reflectionGap, paint);

            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmapWithReflection);
            imageView.setLayoutParams(new GalleryFlow.LayoutParams(mImageViewWidth, mImageHeight));
            imageView.setScaleType(ScaleType.FIT_XY);
            mImages[index++] = imageView;
        }
        return true;
    }
    
    @SuppressWarnings("unused")
    private Resources getResources() {
        return null;
    }
    public int getCount() {
        return mImageIds.length;
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        return mImages[position];
    }
}


