package com.justwen.androidnga.cloud.bugly;

import androidx.annotation.NonNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeoutException;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类
 * 来接管程序,并记录 发送错误报告.
 */
public class BuglyExceptionHandlerProxy implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler mBuglyHandler;

    public BuglyExceptionHandlerProxy(UncaughtExceptionHandler defaultHandler) {
        mBuglyHandler = defaultHandler;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        if (ex instanceof TimeoutException && thread.getName().equals("FinalizerWatchdogDaemon")) {
            return;
        }
        mBuglyHandler.uncaughtException(thread, ex);
    }
}
