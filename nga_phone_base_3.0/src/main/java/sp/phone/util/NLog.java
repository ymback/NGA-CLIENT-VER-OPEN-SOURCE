package sp.phone.util;

import android.util.Log;

import gov.anzong.androidnga.BuildConfig;

/**
 * 带开关的Log
 * Created by elrond on 2017/9/4.
 */

public class NLog {

    private static boolean sDebugMode = BuildConfig.DEBUG;
    private static boolean sShowLine = true;

    public static final String TAG = "NGA";

    public static void setDebug(boolean debug) {
        sDebugMode = debug;
    }

    public static int v(String tag, String msg) {
        if (sDebugMode) {
            return Log.v(tag, msg + getCurLineForJump(4));
        } else {
            return 0;
        }
    }

    public static int d(String tag, String msg) {
        if (sDebugMode) {
            return Log.d(tag, msg + getCurLineForJump(4));
        } else {
            return 0;
        }
    }

    private static String getCurLineForJump(int precount) {
        if (!sShowLine) {
            return "";
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && precount >= 0 && precount < stackTrace.length) {
            return "\n==> at " + stackTrace[precount];
        }
        return "";
    }

    public static int i(String tag, String msg) {
        if (sDebugMode) {
            return Log.i(tag, msg + getCurLineForJump(4));
        } else {
            return 0;
        }
    }

    public static int w(String tag, String msg) {
        if (sDebugMode) {
            return Log.w(tag, msg + getCurLineForJump(4));
        } else {
            return 0;
        }
    }

    public static int e(String tag, String msg) {
        return Log.e(tag, msg + getCurLineForJump(4));
    }

    public static int e(String msg) {
        return Log.e(TAG, msg + getCurLineForJump(4));
    }


    public static int e(String tag, String msg, Throwable throwable) {
        if (sDebugMode) {
            return Log.e(tag, msg, throwable);
        } else {
            return 0;
        }
    }

    public static String getStackTraceString(Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }


    // 可以打印行数
    public static void d(String msg) {
        if (sDebugMode) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            if (elements.length < 3) {
                Log.e(TAG, "Stack to shallow");
            } else {
                try {
                    String fullClassName = elements[3].getClassName();
                    String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
                    String methodName = elements[3].getMethodName();
                    int lineNumber = elements[3].getLineNumber();
                    Log.d(TAG, className + "." + methodName + "():"
                            + lineNumber + " " + msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
