package com.open.ui.advancelistview.view;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * The item image of list view.
 * The image loading sequence is as follows:
 * 1. First use mImageFetcher to load image
 * 2. Second if mImageFetcher = null, use the mDrawableIcon
 * 3. Third if mDrawableIcon = null, use the mBitmapIcon
 * 4. Forth if mBitmapIcon = null, use the mDefaultIcon
 * 
 * When load the image completely, use this weak reference to cache the Drawable.
 * Next time use the cache Drawable directly, if it is null, use mDefaultIcon if it exists,
 * and meanwhile start to load image again.
 */
public class ItemImageModel {

	/** 30*30 **/
	public static final int SMALL_ICON = 1;
	/** 50*50 **/
	public static final int MEDIUM_ICON = 2;
	/** 70*70 **/
	public static final int LARGER_ICON = 3;

	/** 
	 * When load the image completely, use this weak reference to cache the Drawable.
	 *  Next time use the cache Drawable directly, if it is null, start to load image again.
	 */
	private SoftReference<Drawable> mSoftReferenceIcon;
	private ImageFetcher mImageFetcher;
    private Drawable mDrawableIcon;
    private Bitmap mBitmapIcon;
    private Drawable mDefaultIcon;
	
	/** Implement this interface to provide your own way to load the image */
	public static interface ImageFetcher {
	    
	    /** Always implement this method to download image form remote server */
	    public Drawable fetchImage();
	}

	public ItemImageModel(Drawable defaultIcon, ImageFetcher imageFetcher) {
		mDefaultIcon = defaultIcon;
		mImageFetcher = imageFetcher;
	}

	public ItemImageModel(Drawable drawableIcon) {
		mDrawableIcon = drawableIcon;
	}

	public ItemImageModel(Bitmap bitmapIcon) {
		mBitmapIcon = bitmapIcon;
	}
	
	//--------------------------------------------------------------------------
	public Drawable getDrawableIcon() {
		return mDrawableIcon;
	}

	public void setDrawableIcon(Drawable normalDrawableIcon) {
		mDrawableIcon = normalDrawableIcon;
	}

	public Bitmap getBitmapIcon() {
		return mBitmapIcon;
	}

	public void setBitmapIcon(Bitmap normalBitmapIcon) {
		mBitmapIcon = normalBitmapIcon;
	}

	public SoftReference<Drawable> getSoftReferenceIcon() {
		return mSoftReferenceIcon;
	}

	public void setSoftReferenceIcon(SoftReference<Drawable> icon) {
		mSoftReferenceIcon = icon;
	}

	public ImageFetcher getImageFetcher() {
		return mImageFetcher;
	}

	public void setImageFetcher(ImageFetcher imageFetcher) {
		mImageFetcher = imageFetcher;
	}

	public Drawable getDefaultIcon() {
		return mDefaultIcon;
	}

	public void setDefaultIcon(Drawable defaultIcon) {
		mDefaultIcon = defaultIcon;
	}

}
