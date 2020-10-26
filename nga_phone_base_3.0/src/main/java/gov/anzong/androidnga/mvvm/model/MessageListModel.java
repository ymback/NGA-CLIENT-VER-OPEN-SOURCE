package gov.anzong.androidnga.mvvm.model;

import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.http.bean.MessageListInfo;
import sp.phone.http.retrofit.RetrofitHelper;
import sp.phone.http.retrofit.RetrofitService;
import sp.phone.mvp.model.convert.MessageConvertFactory;

/**
 * Created by Justwen on 2017/10/10.
 */

public class MessageListModel {

    private RetrofitService mService;

    private Map<String,String> mParamMap = new HashMap<>();

    /**
     *  http://bbs.nga.cn/nuke.php?__lib=message&__act=message&act=list&page=1&lite=js
     */
    public MessageListModel() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
        mParamMap.put("__lib","message");
        mParamMap.put("__act","message");
        mParamMap.put("act","list");
        mParamMap.put("lite","js");
    }

    public void loadPage(int page, final OnHttpCallBack<MessageListInfo> callBack) {
        mParamMap.put("page",String.valueOf(page));
        mService.get(mParamMap)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, MessageListInfo>() {
                    @Override
                    public MessageListInfo apply(@NonNull String s) throws Exception {
                        MessageConvertFactory factory = new MessageConvertFactory();
                        MessageListInfo result = factory.getMessageListInfo(s);
                        if (result == null) {
                            throw new Exception(factory.getErrorMsg());
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageListInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@NonNull MessageListInfo messageListInfo) {
                        callBack.onSuccess(messageListInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
