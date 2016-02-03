package com.open.ui.refinedpageloading.test;

import android.app.Application;
import android.os.Handler;

public class MainApplication extends Application {

    private static MainApplication mThis;
    
    public static final MainApplication getInstance() {
        return mThis;
    }
    
    public void onCreate() {
        super.onCreate();
        mThis = this;
    }
    
    /** must be instantiated in the main thread */
    private final Handler handler = new Handler();

    /**
     * Execute runnable.run() asynchronously on the main UI event dispatching thread.
     * @param runnable runnable object put into this application's event queue.
     */
    public void invokeLater(final Runnable runnable) {
        handler.post(runnable);
    }
    /**
     * Execute runnable.run() asynchronously on the main UI event dispatching thread,
     * after a delay of delayMillis milliseconds.
     *
     * @param runnable runnable object put into this application's event queue.
     * @param delayMillis runnable will be run after a delay of delayMillis milliseconds
     */
    public void invokeLater(final Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }

}
