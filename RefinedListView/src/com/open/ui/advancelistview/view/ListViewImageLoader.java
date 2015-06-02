package com.open.ui.advancelistview.view;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * This class is used to load the image of list item asynchronously.  
 * 1. First use mImageFetcher to load image
 * 2. Second if mImageFetcher = null, use the mDrawableIcon
 * 3. Third if mDrawableIcon = null, use the mBitmapIcon
 * 4. Forth if mBitmapIcon = null, use the mDefaultIcon
 * 
 * When load the image completely, use imageModel.setSoftReferenceIcon() to cache the Drawable.
 * Next time use the cache Drawable directly, but if it is null, start to load image again.
 */
public class ListViewImageLoader {

    private final List<BaseItemModel> mTaskList = new ArrayList<BaseItemModel>();
    /** Record the task thread to load image. Each task will loop to load image till the mTaskList is empty */
    private final ConcurrentHashMap<Runnable,Integer> mTaskThreads = new ConcurrentHashMap<Runnable,Integer>();
    private AdvanceListView mALiListView;
    private AdvanceListViewAdapter mALiListAdapter;
    private Context mContext;

    private class TaskRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
                BaseItemModel task = pickTask();
                if (task == null) {
                    break;
                }
                onTaskRun(task);
            }
            mTaskThreads.remove(this);
        }
    }
    
    public ListViewImageLoader(Context context) {
        mContext = context;
    }
    
    /**
     * Set the parent list view for this image loader
     */
    public void setALiList(AdvanceListView listView, AdvanceListViewAdapter listAdapter) {
        mALiListView = listView;
        mALiListAdapter = listAdapter;
    }

    /**
     * Add the new task and invoke the task queue to be executed.
     */
    public boolean addTask(BaseItemModel task) {
        synchronized (mTaskList) {
            if (!mTaskList.contains(task)) {
                int firstPos = mALiListView.getFirstVisiblePosition() - mALiListView.getHeaderViewsCount();
                if (mALiListAdapter.findPosition(task) >= firstPos) {
                    //high priority task should be added to the top of the queue.
                    mTaskList.add(0, task);
                } else {
                    mTaskList.add(task);
                }
                
                //Trigger the task runner to start loop running.
                notifyTaskRunner();
                
                return true;
            }
        }
        return false;
    }

    /**
     * Task execution function, deal with the detail of each the task.
     */
    public void onTaskRun(BaseItemModel task){
        ItemImageModel imageModel = task.getImageModel();
        Drawable drawable = null;
        if(imageModel.getImageFetcher() != null){
            drawable = imageModel.getImageFetcher().fetchImage();
        } else {
            if(imageModel.getDrawableIcon() != null){
                drawable = imageModel.getDrawableIcon();
            } else if (imageModel.getBitmapIcon() != null){
                drawable = bitmapToDrawable(imageModel.getBitmapIcon());
            }
        }

        if (drawable != null) {
            SoftReference<Drawable> drawableReference = new SoftReference<Drawable>(drawable);
            imageModel.setSoftReferenceIcon(drawableReference);
            if (mALiListView != null) {
                mALiListAdapter.notifyPart(mALiListView, task);
            }
        }
    }

    /**
     * Clear the pending task
     */
    public void clearTasks() {
        synchronized (mTaskList) {
            mTaskList.clear();
        }
    }
    
    /**
     * Destroy the task runner
     */
    public void onDestroy() {
        try {
            recycleBitmap();
            clearTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recycle Bitmap resource
     */
    private void recycleBitmap() {
        for (int i = 0; i < mTaskList.size(); i++) {
            SoftReference<Drawable> ref = mTaskList.get(i).getImageModel().getSoftReferenceIcon();
            if (ref != null) {
                Drawable drawable = ref.get();
                if(drawable instanceof BitmapDrawable){
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
            }
        }
    }

    /**
     * Invoke up TaskRunner thread
     */
    private void notifyTaskRunner() {
        if (mTaskThreads.size() < 3) {
            //Allows at most three runner task to execute 
            TaskRunner runner = new TaskRunner();
            mTaskThreads.put(runner, 1);
            
            try {
                CommonThreadPoolExecutor.getInstance().submit(runner);
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Pick up the task to run
     */
    private BaseItemModel pickTask() {
        BaseItemModel task = null;
        synchronized (mTaskList) {
            int size = mTaskList.size();
            if (size > 0) {
                task = mTaskList.remove(0);
            }
        }
        return task;
    }
    
    /**
     * Create the bitmap to drawable
     */
    private Drawable bitmapToDrawable(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            Bitmap tempBitmap = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
            BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), tempBitmap);
            return bd;
        }
        BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), bitmap);
        return bd;
    }
}
