package sp.phone.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.PreferenceKey;
import sp.phone.common.UserManagerImpl;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.ApplicationContextHolder;
import sp.phone.utils.NLog;

/**
 * Created by Justwen on 2018/1/21.
 */

public class SignLoadTask {

    private RetrofitService mService;

    private static final String TAG_SUCCESS = "签到成功";

    private static final String TAG_ALREADY_SIGN = "已经签到了";

    public SignLoadTask() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
    }

    public void execute() {

        if (!shouldLoadSignTask()) {
            return;
        }

        String url = "http://nga.178.com/nuke.php?__lib=check_in&__output=8&lite=js&noprefix&__act=check_in&action=add";
        mService.get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        NLog.d(s);
                        return parseResult(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(@NonNull String s) {
                        ActivityUtils.showToast(s);
                    }
                });
    }

    private String parseResult(String js) {
        try {
            js = js.substring(js.indexOf("=") + 1);
            JSONObject object = JSON.parseObject(js);
            String time = object.getString("time");
            String msg = "";
            if (object.containsKey("data")) {
                object = object.getJSONObject("data");
                String result = object.getString("0");
                object = object.getJSONObject("1");
                String uid = object.getString("uid");
                String continued = object.getString("continued");
                String sum = object.getString("sum");
                String lastTime = object.getString("last_time");
                msg = result +
                        "\n" +
                        String.format("连续签到%s天", continued) +
                        "\n" +
                        String.format("累计签到%s天", sum);


            } else if (object.containsKey("error")) {
                object = object.getJSONObject("error");
                msg = object.getString("0");
            }
            if (msg.contains(TAG_SUCCESS) || msg.contains(TAG_ALREADY_SIGN)) {
                SharedPreferences sp = ApplicationContextHolder.getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
                sp.edit().putLong(PreferenceKey.KEY_SIGN_DATE, System.currentTimeMillis()).apply();
            }
            return msg;
        } catch (Exception e) {
            return "刮墙失败！";
        }

    }

    private boolean shouldLoadSignTask() {
        SharedPreferences sp = ApplicationContextHolder.getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        long time = sp.getLong(PreferenceKey.KEY_SIGN_DATE, 0);
        return UserManagerImpl.getInstance().hasValidUser()
                && sp.getBoolean(PreferenceKey.KEY_AUTO_SIGN, false)
                && (time == 0 || !DateUtils.isToday(time));
    }

}
