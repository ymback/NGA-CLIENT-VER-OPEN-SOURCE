package gov.anzong.androidnga;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Locale;

import sp.phone.utils.NLog;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类
 * 来接管程序,并记录 发送错误报告.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    /**
     * Debug Log tag
     */
    public static final String TAG = "CrashHandler";
    /** 是否开启日志输出,在Debug状态下开启,
     * 在Release状态下关闭以提示程序性能
     * */
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    private String courseName;
    private String VERSION_NAME;
    private String NAME;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象,
     * 获取系统默认的UncaughtException处理器,
     * 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
//        handleException(ex);
//        if (mDefaultHandler != null)
//            mDefaultHandler.uncaughtException(thread, ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            //Sleep一会后结束程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                NLog.e(TAG, "Error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    public void setCouseName(String name) {
        if (name != null)
            courseName = name;
    }

    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     * 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出错啦:" + msg, Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }

        }.start();
        //保存错误报告文件
        collectCrashDeviceInfo(mContext);
        String err = saveCrashInfoToFile(ex);
        StringBuffer sb = new StringBuffer();
        sb.append(NAME + "\n" + Build.VERSION.SDK_INT + "\n" + VERSION_NAME + "\n" + courseName + "\n");
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName().toLowerCase(Locale.CHINA) + ":\t" + field.get(null).toString());
                sb.append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append(err);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crash/";
        File f = new File(path);
        if (!f.exists())
            f.mkdirs();
        File file = new File(path + "crash" + System.currentTimeMillis() + ".txt");
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(sb.toString());
            fw.flush();
            fw.close();
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 保存错误信息到字符串
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        ex.printStackTrace();

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        return result;
    }

    private void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                NAME = pi.packageName == null ? "not set" : pi.packageName;
                VERSION_NAME = pi.versionName == null ? "not set" : pi.versionName;
            }
        } catch (NameNotFoundException e) {
            NLog.e(TAG, "Error while collect package info", e);
        }
    }
}
