package com.open.ui.advancelistview.view;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommonThreadPoolExecutor extends ThreadPoolExecutor {

    /** The least number of threads in the thread pool */
    private static final int DEFAULT_CORE_POOL_SIZE = 5;
    
    /** The most number of threads in the thread pool */
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 20;
    
    /** The max idle time (seconds) to keep the thread pool */
    private static final int DEFAULT_KEEP_ALIVETIME = 3;

    private static CommonThreadPoolExecutor sThreadPoolExecutor;
    private CommonThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public static CommonThreadPoolExecutor getInstance() {
        if (sThreadPoolExecutor == null) {
            sThreadPoolExecutor = new CommonThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE,
                    DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVETIME,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            
            sThreadPoolExecutor.allowCoreThreadTimeOut(true);
        }

        return sThreadPoolExecutor;
    }
    
}
