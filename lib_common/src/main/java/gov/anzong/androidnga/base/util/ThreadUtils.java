package gov.anzong.androidnga.base.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yangyihang
 */
public class ThreadUtils {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private static volatile ThreadPoolExecutor sThreadPoolExecutor;

    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static void postOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    public static void postOnSubThread(Runnable runnable) {
        if (sThreadPoolExecutor == null) {
            sThreadPoolExecutor = new ThreadPoolExecutor(0, 3, 60, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(), (ThreadFactory) Thread::new);
        }
        sThreadPoolExecutor.execute(runnable);
    }
}
