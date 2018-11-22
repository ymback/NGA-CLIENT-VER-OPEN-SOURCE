package sp.phone.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import sp.phone.bean.AdminForumsData;
import sp.phone.bean.ProfileData;
import sp.phone.bean.ReputationData;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PreferenceKey;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.ActivityUtils;
import sp.phone.util.NLog;
import sp.phone.util.StringUtils;

public class JsonProfileLoadTask {

    private static final String TAG = JsonProfileLoadTask.class.getSimpleName();

    private OnHttpCallBack<ProfileData> mCallback;

    private String mErrorMsg;

    private RetrofitService mService = RetrofitHelper.getInstance().getService();

    private Subscription mSubscription;

    public JsonProfileLoadTask(OnHttpCallBack<ProfileData> callback) {
        mCallback = callback;
    }

    public void execute(String url) {
        url = Utils.getNGAHost() + "nuke.php?__lib=ucp&__act=get&lite=js&noprefix&" + url;
        mService.get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(this::parseJsonPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<ProfileData>() {

                    @Override
                    public void onNext(@NonNull ProfileData profileData) {
                        ActivityUtils.getInstance().dismiss();
                        mCallback.onSuccess(profileData);
                        mSubscription = null;
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        ActivityUtils.getInstance().dismiss();
                        mCallback.onError(StringUtils.isEmpty(mErrorMsg) ? throwable.getMessage() : mErrorMsg);
                        mSubscription = null;
                    }

                    @Override
                    public void onSubscribe(@NonNull Subscription subscription) {
                        mSubscription = subscription;
                        super.onSubscribe(subscription);
                    }
                });
    }

    public void cancel() {
        if (mSubscription != null) {
            mSubscription.cancel();
        }
    }

    private ProfileData parseJsonPage(String js) {
        if (StringUtils.isEmpty(js)) {
            mErrorMsg = ApplicationContextHolder.getString(R.string.network_error);
            throw new IllegalStateException();
        }
        js = js.replaceAll("window.script_muti_get_var_store=", "");
        if (js.contains("/*error fill content")) {
            js = js.substring(0, js.indexOf("/*error fill content"))
                    .replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
        }
        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",")
                .replaceAll("/\\*\\$js\\$\\*/", "");
        JSONObject obj = JSON.parseObject(js);
        if (obj.containsKey("data")) {
            obj = obj.getJSONObject("data");
            try {
                ProfileData ret = new ProfileData();
                obj = obj.getJSONObject("0");
                buildBasicInfo(ret, obj);
                buildReputation(ret, obj.getJSONObject("reputation"));
                buildAdminForums(ret, obj.getJSONObject("adminForums"));
                return ret;
            } catch (Exception e) {
                NLog.e(TAG, "can not parse :\n" + js);
                mErrorMsg = "二哥玩坏了或者你需要重新登录";
                throw e;
            }
        }
        obj = obj.getJSONObject("error");
        mErrorMsg = obj == null ? "二哥玩坏了或者你需要重新登录" : obj.getString("0");
        throw new IllegalStateException();
    }

    private void buildAdminForums(ProfileData ret, JSONObject obj) {
        if (obj != null) {
            List<AdminForumsData> entryList = new ArrayList<>();
            for (String key : obj.keySet()) {
                AdminForumsData entry = new AdminForumsData(key, obj.getString(key));
                entryList.add(entry);
            }
            ret.setAdminForums(entryList);
        }
    }

    private void buildReputation(ProfileData ret, JSONObject obj) {
        if (obj != null) {
            List<ReputationData> entryList = new ArrayList<>();
            for (int i = 0; i < obj.size(); i++) {
                JSONObject subObj = obj.getJSONObject(String.valueOf(i));
                if (subObj != null) {
                    ReputationData entry = new ReputationData(subObj.getString("0"), subObj.getString("1"));
                    entryList.add(entry);
                }
            }
            ret.setReputationEntryList(entryList);
        }
    }

    private void buildBasicInfo(ProfileData ret, JSONObject obj) {
        String defaultValue = "N/A";

        String money = obj.getString("money");
        ret.setMoney(StringUtils.isEmpty(money) ? "0" : money);

        String frame = obj.getString("fame");
        ret.setFrame(StringUtils.isEmpty(frame) ? defaultValue : frame);

        String posts = obj.getString("posts");
        ret.setPostCount(StringUtils.isEmpty(posts) ? defaultValue : posts);

        String email = obj.getString("email");
        ret.setEmailAddress(StringUtils.isEmpty(email) ? defaultValue : email);

        String phone = obj.getString("phone");
        ret.setPhoneNumber(StringUtils.isEmpty(phone) ? defaultValue : phone);

        String userName = obj.getString("username");
        ret.setUserName(StringUtils.isEmpty(userName) ? defaultValue : userName);

        String uid = obj.getString("uid");
        ret.setUid(StringUtils.isEmpty(uid) ? defaultValue : uid);

        String group = obj.getString("group");
        ret.setMemberGroup(StringUtils.isEmpty(group) ? defaultValue : group);

        String verified = obj.getString("verified");
        if (!StringUtils.isEmpty(verified)) {
            int state = Integer.parseInt(verified);
            if (state == -1) {
                ret.setNuked(true);
            } else if (state < -1) {
                ret.setMuted(true);
            }
        }
        if (!ret.isNuked()) {
            JSONObject buffObj = obj.getJSONObject("buffs");
            if (buffObj != null && buffObj.containsKey("0")) {
                ret.setMutedTime(buffObj.getString("0"));
                ret.setMuted(true);
            } else if (!StringUtils.isEmpty(obj.getString("muteTime")) && !"0".equals(obj.getString("muteTime"))) {
                ret.setMuted(true);
                ret.setMutedTime("禁言至: " + StringUtils.timeStamp2Date1(obj.getString("muteTime")));
            }
        }
        String regdate = obj.getString("regdate");
        ret.setRegisterDate(StringUtils.isEmpty(regdate) ? defaultValue : StringUtils.timeStamp2Date1(regdate));

        String sign = obj.getString("sign");
        ret.setSign(StringUtils.isEmpty(sign) ? "无签名" : sign);

        ret.setAvatarUrl(obj.getString(PreferenceKey.PREFERENCE_AVATAR));
    }
}