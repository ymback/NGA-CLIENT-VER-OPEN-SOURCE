package gov.anzong.androidnga;

import android.os.Process;

import androidx.annotation.NonNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeoutException;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类
 * 来接管程序,并记录 发送错误报告.
 */
public class ExceptionHandlerProxy implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler mOrigExceptionHandler;

    public ExceptionHandlerProxy(UncaughtExceptionHandler defaultHandler) {
        mOrigExceptionHandler = defaultHandler;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        if (ex instanceof TimeoutException && thread.getName().equals("FinalizerWatchdogDaemon")
                || ex instanceof IllegalStateException && thread.getName().equals("GoogleApiHandler")) {
            return;
        }
        if (mOrigExceptionHandler != null) {
            mOrigExceptionHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }
}
