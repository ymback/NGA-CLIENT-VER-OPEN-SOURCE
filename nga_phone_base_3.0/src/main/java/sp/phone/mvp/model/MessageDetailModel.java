package sp.phone.mvp.model;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.MessageDetailContract;
import sp.phone.mvp.model.convert.MessageConvertFactory;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.convert.MessageConvertFactory;
import sp.phone.mvp.contract.MessageDetailContract;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;

/**
 * Created by Justwen on 2017/10/11.
 */

public class MessageDetailModel extends BaseModel implements MessageDetailContract.IMessageModel {

    private RetrofitService mService;

    private Map<String,String> mParamMap = new HashMap<>();

    /**
     *  http://bbs.nga.cn/nuke.php?__lib=message&__act=message&act=read&page=1&mid=1&lite=js
     */
    public MessageDetailModel() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
        mParamMap.put("__lib","message");
        mParamMap.put("__act","message");
        mParamMap.put("act","read");
        mParamMap.put("lite","js");
    }

    @Override
    public void loadPage(final int page, int mid, final OnHttpCallBack<MessageDetailInfo> callBack) {
        mParamMap.put("page",String.valueOf(page));
        mParamMap.put("mid",String.valueOf(mid));
        mService.get(mParamMap)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, MessageDetailInfo>() {
                    @Override
                    public MessageDetailInfo apply(@NonNull String s) throws Exception {
                        MessageConvertFactory factory = new MessageConvertFactory();
                        MessageDetailInfo result = factory.getMessageDetailInfo(s,page);
                        if (result == null) {
                            throw new Exception(factory.getErrorMsg());
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessageDetailInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@NonNull MessageDetailInfo messageListInfo) {
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
