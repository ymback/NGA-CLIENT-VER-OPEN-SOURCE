package com.justwen.androidnga.cloud.lean;

import android.content.Context;
import android.os.Build;

import com.justwen.androidnga.cloud.BuildConfig;
import com.justwen.androidnga.cloud.R;
import com.justwen.androidnga.cloud.base.ICloudDataBase;
import com.justwen.androidnga.cloud.base.VersionBean;

import java.util.List;

import cn.leancloud.AVLogger;
import cn.leancloud.AVOSCloud;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LeanDataBase implements ICloudDataBase {

    @Override
    public void init(Context context) {
        AVOSCloud.initializeSecurely(context, context.getString(R.string.lean_cloud_app_id), null);
        if (BuildConfig.DEBUG) {
            AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
        }
    }

    private void uploadAndroidVersionInfo(final VersionBean versionBean) {
        AVQuery<AVObject> query = new AVQuery<>("androidVersion");
        query.whereEqualTo("versionName", versionBean.versionName);
        query.whereEqualTo("androidVersion", Build.VERSION.SDK_INT);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(List<AVObject> objects) {
                if (objects != null && !objects.isEmpty()) {
                    AVObject obj = objects.get(0);
                    obj.increment("count");
                    obj.saveInBackground().subscribe(new EmptyObserver<>());
                } else {
                    AVObject obj = new AVObject("androidVersion");
                    obj.put("versionName", versionBean.versionName);
                    obj.put("androidVersion", Build.VERSION.SDK_INT);
                    obj.put("count", 1);
                    obj.saveInBackground().subscribe(new EmptyObserver<>());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void uploadVersionInfo(final VersionBean versionBean) {
        AVQuery<AVObject> query = new AVQuery<>("version");
        query.whereEqualTo("versionName", versionBean.versionName);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(List<AVObject> objects) {
                if (objects != null && !objects.isEmpty()) {
                    AVObject obj = objects.get(0);
                    obj.increment("count");
                    obj.saveInBackground().subscribe(new EmptyObserver<>());
                } else {
                    AVObject obj = new AVObject("version");
                    obj.put("versionName", versionBean.versionName);
                    obj.put("count", 1);
                    obj.saveInBackground().subscribe(new EmptyObserver<>());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        uploadAndroidVersionInfo(versionBean);
    }


}
