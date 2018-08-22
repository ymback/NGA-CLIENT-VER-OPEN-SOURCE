package sp.phone.task;

import android.content.Context;

import java.lang.reflect.Method;

import gov.anzong.androidnga.BuildConfig;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.NLog;
import sp.phone.util.PluginUtils;

/**
 * Created by Justwen on 2018/8/12.
 */
public class DeviceStatisticsTask {

    //每次更新版本后会执行一次
    public static void execute() {
        Observable.create(new ObservableOnSubscribe<ClassLoader>() {
            @Override
            public void subscribe(ObservableEmitter<ClassLoader> e) throws Exception {
                PluginUtils.extractPlugin();
                ClassLoader cl = PluginUtils.createClassLoader("statistics.apk");
                e.onNext(cl);
                e.onComplete();
            }
        }).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<ClassLoader>() {
                    @Override
                    public void onNext(ClassLoader cl) {
                        try {
                            Class clz = cl.loadClass("gov.anzong.androidnga.statistics.bmob.BmobWrapper");
                            Object obj = clz.getDeclaredConstructor(Context.class).newInstance(ApplicationContextHolder.getContext());
                            Method method = clz.getMethod("startDeviceStatisticsTask", int.class);
                            method.invoke(obj, BuildConfig.VERSION_CODE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        NLog.e(throwable.getMessage());
                        super.onError(throwable);
                    }
                });
    }
}
