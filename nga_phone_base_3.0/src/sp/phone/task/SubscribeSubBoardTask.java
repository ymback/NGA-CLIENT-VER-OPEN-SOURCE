package sp.phone.task;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;

/**
 * Created by Justwen on 2018/1/27.
 */

public class SubscribeSubBoardTask {

    private RetrofitService mService;


    private LifecycleProvider<FragmentEvent> mLifecycleProvider;

    public SubscribeSubBoardTask(LifecycleProvider<FragmentEvent> lifecycleProvider) {
        mLifecycleProvider = lifecycleProvider;
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
    }

    public void subscribe(int parentFid, int fid, final OnHttpCallBack<String> callBack) {
        mService.post(getUrl(parentFid, fid, true))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<String>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<String>() {

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }


                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("成功")) {
                            callBack.onSuccess("取消屏蔽成功，请刷新界面！");
                        } else {
                            callBack.onError(s);
                        }
                        super.onNext(s);
                    }
                });

    }

    public void unsubscribe(int parentFid, int fid, final OnHttpCallBack<String> callBack) {
        mService.post(getUrl(parentFid, fid, false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<String>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<String>() {

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("成功")) {
                            callBack.onSuccess("屏蔽子板块成功，请刷新界面！");
                        } else {
                            callBack.onError(s);
                        }
                    }
                });

    }

    private String getUrl(int parentFid, int fid, boolean isSubscribe) {
        int type = getType(fid);
        String action = getAction(fid, isSubscribe);
        String url;
        if (!isSubscribe) {
            url = String.format("http://bbs.ngacn.cc/nuke.php?__lib=user_option&__act=set&raw=3&type=%s&__output=8&fid=%s&%s=%s", type, parentFid, action, fid);
        } else {
            url = String.format("http://bbs.ngacn.cc/nuke.php?__lib=user_option&__act=set&raw=3&type=%s&__output=8&fid=%s&%s=%s", type, parentFid, action, fid);
        }
        return url;
    }

    // 有些板块的type是1 比如 “优惠信息 购物指南”
    private int getType(int fid) {
        if (fid == 12700430) {
            return 1;
        } else {
            return 0;
        }
    }

    //NGA 后台好变态啊，某个板块的操作居然是反的 比如 “优惠信息 购物指南”
    private String getAction(int fid, boolean isSubscribe) {
        if (fid == 12700430) {
            return isSubscribe ? "del" : "add";
        } else {
            return isSubscribe ? "add" : "del";
        }
    }
}
