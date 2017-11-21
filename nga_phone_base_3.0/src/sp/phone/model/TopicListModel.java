package sp.phone.model;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import sp.phone.listener.OnHttpCallBack;
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
}
