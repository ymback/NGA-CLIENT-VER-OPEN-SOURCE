package sp.phone.task;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.ApiConstants;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.convert.ForumNotificationFactory;
import sp.phone.mvp.model.entity.NotificationInfo;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.utils.NLog;

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
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mLifecycleProvider.<String>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        if (callBack != null) {
                            callBack.onSuccess(ForumNotificationFactory.buildRecentReplyList(s));
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        callBack.onSuccess(ForumNotificationFactory.buildNotificationList(s));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        NLog.e("query notification error : text");
                    }
                });
    }

}
