package org.trinity.util.thread;

import android.support.annotation.NonNull;

import org.trinity.wallet.ConfigList;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorUtil {
    /**
     * How many CPU cores do we have.
     */
    private static final int CPU_CORE_ACCOUNT = Runtime.getRuntime().availableProcessors();
    /**
     * Core pool account.
     */
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_CORE_ACCOUNT - 1, 4));
    /**
     * Spike pool account.
     */
    private static final int MAX_POOL_SIZE = CPU_CORE_ACCOUNT * 2 + 1;
    /**
     * Thread furlough time.
     */
    private static final int LEISURE_TIME = ConfigList.BLOCK_SYNC_TIME + 1;

    public static ThreadPoolExecutor getHighIOModeDynamicThreadPool() {
        // A blocked queue saves the pool tasks.
        BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();

        // The factory of thread.
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadID = new AtomicInteger(75001);

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "AsyncTasksThread@" + threadID.getAndIncrement());
            }
        };

        // Recovery strategy fully loaded.
        RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();

        // The thread pool.
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                LEISURE_TIME,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                rejectHandler
        );
    }
}
