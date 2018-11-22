package sp.phone.mvp.model;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.bean.MessageListInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.MessageListContract;
import sp.phone.mvp.model.convert.MessageConvertFactory;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.bean.MessageListInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.MessageListContract;
import sp.phone.mvp.model.convert.MessageConvertFactory;
import sp.phone.retrofit.RetrofitService;
import sp.phone.retrofit.RetrofitHelper;

/**
 * Created by Justwen on 2017/10/10.
 */

public class MessageListModel extends BaseModel implements MessageListContract.IMessageModel {

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

    @Override
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
