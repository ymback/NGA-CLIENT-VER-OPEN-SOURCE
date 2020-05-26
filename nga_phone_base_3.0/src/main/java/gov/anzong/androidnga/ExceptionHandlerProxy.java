package gov.anzong.androidnga;

import android.os.DeadSystemException;
import android.os.Process;

import androidx.annotation.NonNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeoutException;

import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;


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

        if (ex instanceof RuntimeException
                && ex.getMessage() != null
                && ex.getMessage().contains("Using WebView from more than one process at once with the same data directory is not supported")) {
            int index = PreferenceUtils.getData(PreferenceKey.KEY_WEBVIEW_DATA_INDEX, 0);
            index++;
            PreferenceUtils.edit().putInt(PreferenceKey.KEY_WEBVIEW_DATA_INDEX, index).commit();
        }

        if (mOrigExceptionHandler == null || ex instanceof DeadSystemException) {
            Process.killProcess(Process.myPid());
        } else {
            mOrigExceptionHandler.uncaughtException(thread, ex);
        }
    }
}
