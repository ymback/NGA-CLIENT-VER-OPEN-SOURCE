package sp.phone.task;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.ApiConstants;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.convert.ForumNotificationFactory;
import sp.phone.mvp.model.entity.NotificationInfo;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;

public class ForumNotificationTask {

    private RetrofitService mService;

    private LifecycleProvider<FragmentEvent> mLifecycleProvider;

    public ForumNotificationTask(LifecycleProvider<FragmentEvent> lifecycleProvider) {
        mLifecycleProvider = lifecycleProvider;
        mService = RetrofitHelper.getInstance().getService();
    }


    // 只返回最近被喷的信息
    public void queryRecentReply(OnHttpCallBack<List<RecentReplyInfo>> callBack) {
        mService.get(ApiConstants.NGA_NOTIFICATION)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, List<RecentReplyInfo>>() {
                    @Override
                    public List<RecentReplyInfo> apply(String s) throws Exception {
                        return ForumNotificationFactory.buildRecentReplyList(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<List<RecentReplyInfo>>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<List<RecentReplyInfo>>() {
                    @Override
                    public void onNext(List<RecentReplyInfo> s) {
                        if (callBack != null) {
                            callBack.onSuccess(s);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });

    }

    // 返回最近被喷和短信的信息
    public void queryNotification(OnHttpCallBack<List<NotificationInfo>> callBack) {

        mService.get(ApiConstants.NGA_NOTIFICATION)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, List<NotificationInfo>>() {
                    @Override
                    public List<NotificationInfo> apply(String s) throws Exception {
                        return ForumNotificationFactory.buildNotificationList(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<NotificationInfo>>() {
                    @Override
                    public void onNext(List<NotificationInfo> s) {
                        if (callBack != null) {
                            callBack.onSuccess(s);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });

    }

}
