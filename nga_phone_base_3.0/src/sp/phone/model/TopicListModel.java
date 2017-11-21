package sp.phone.model;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.bean.TopicListInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.model.convert.TopicConvertFactory;
import sp.phone.presenter.contract.tmp.TopicListContract;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.utils.StringUtils;

/**
 * Created by Justwen on 2017/11/21.
 */

public class TopicListModel implements TopicListContract.Model {

    private RetrofitService mService;

    private Map<String, String> mFieldMap;

    public TopicListModel() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
    }

    private void initFieldMap() {
        if (mFieldMap == null) {
            mFieldMap = new HashMap<>();
            mFieldMap.put("__lib", "topic_favor");
            mFieldMap.put("__act", "topic_favor");
            mFieldMap.put("lite", "js");
            mFieldMap.put("action", "del");
        }
    }

    @Override
    public void removeTopic(String tidArray, final OnHttpCallBack<String> callBack) {
        tidArray = tidArray + '&';
        String page = StringUtils.getStringBetween(tidArray, 0, "page=", "&").result;
        tidArray = StringUtils.getStringBetween(tidArray, 0, "tidarray=", "&").result;
        initFieldMap();
        mFieldMap.put("page", page);
        mFieldMap.put("tidarray", tidArray);
        mService.post(mFieldMap)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (s.contains("操作成功")) {
                            callBack.onSuccess("操作成功！");
                        } else {
                            callBack.onError("操作失败!");
                        }
                    }
                });
    }

    @Override
    public void loadTopicList(final String url, final OnHttpCallBack<TopicListInfo> callBack) {
        mService.get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, TopicListInfo>() {
                    @Override
                    public TopicListInfo apply(@NonNull String s) throws Exception {
                        TopicConvertFactory factory = new TopicConvertFactory();
                        TopicListInfo result = factory.getTopicListInfo(s, url);
                        if (result == null) {
                            throw new Exception(factory.getErrorMsg());
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TopicListInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@NonNull TopicListInfo listInfo) {
                        callBack.onSuccess(listInfo);
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
