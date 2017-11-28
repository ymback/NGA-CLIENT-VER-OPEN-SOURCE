package sp.phone.mvp.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.mvp.contract.TopicListContract;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.convert.TopicConvertFactory;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.ResourceUtils;
import sp.phone.utils.StringUtils;

/**
 * Created by Justwen on 2017/11/21.
 */

public class TopicListModel extends BaseModel implements TopicListContract.Model {

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
            mFieldMap.put("__output", "8");
            mFieldMap.put("action", "del");
        }
    }

    @Override
    public void removeTopic(ThreadPageInfo info, final OnHttpCallBack<String> callBack) {
        initFieldMap();
        mFieldMap.put("page", String.valueOf(info.getPage()));
        mFieldMap.put("tidarray", String.valueOf(info.getTid()));
        mService.post(mFieldMap)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("操作成功")) {
                            callBack.onSuccess("操作成功！");
                        } else {
                            callBack.onError("操作失败!");
                        }
                    }
                });
    }

    @Override
    public void loadTopicList(final int page, TopicListParam param, final OnHttpCallBack<TopicListInfo> callBack) {
        String url = getUrl(page, param);
        mService.get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, TopicListInfo>() {
                    @Override
                    public TopicListInfo apply(@NonNull String js) throws Exception {
                        NLog.d(js);
                        TopicListInfo result = TopicConvertFactory.getTopicListInfo(js, page);
                        if (result == null) {
                            throw new Exception(TopicConvertFactory.getErrorMessage(js));
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TopicListInfo>() {

                    @Override
                    public void onNext(@NonNull TopicListInfo listInfo) {
                        callBack.onSuccess(listInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        String error;
                        if (throwable instanceof UnknownHostException) {
                            error = ResourceUtils.getString(R.string.network_error);
                        } else {
                            error = throwable.getMessage();
                        }
                        callBack.onError(error);
                    }

                });
    }

    private String getUrl(int page, TopicListParam requestInfo) {
        StringBuilder jsonUri = new StringBuilder(HttpUtil.Server + "/thread.php?");
        if (0 != requestInfo.authorId) {
            jsonUri.append("authorid=").append(requestInfo.authorId).append("&");
        }
        if (requestInfo.searchPost != 0) {
            jsonUri.append("searchpost=").append(requestInfo.searchPost).append("&");
        }
        if (requestInfo.favor != 0) {
            jsonUri.append("favor=").append(requestInfo.favor).append("&");
        }
        if (requestInfo.content != 0) {
            jsonUri.append("content=").append(requestInfo.content).append("&");
        }

        if (!StringUtils.isEmpty(requestInfo.author)) {
            try {
                if (requestInfo.author.endsWith("&searchpost=1")) {
                    jsonUri.append("author=").append(URLEncoder.encode(
                            requestInfo.author.substring(0, requestInfo.author.length() - 13),
                            "GBK")).append("&searchpost=1&");
                } else {
                    jsonUri.append("author=").append(URLEncoder.encode(requestInfo.author, "GBK")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            if (0 != requestInfo.fid) {
                jsonUri.append("fid=").append(requestInfo.fid).append("&");
            }
            if (!StringUtils.isEmpty(requestInfo.key)) {
                jsonUri.append("key=").append(StringUtils.encodeUrl(requestInfo.key, "UTF-8")).append("&");
            }
            if (!StringUtils.isEmpty(requestInfo.fidGroup)) {
                jsonUri.append("fidgroup=").append(requestInfo.fidGroup).append("&");
            }
        }
        jsonUri.append("page=").append(page).append("&lite=js&noprefix");
        if (requestInfo.recommend == 1) {
            jsonUri.append("&recommend=1&order_by=postdatedesc&user=1");
        }
        return jsonUri.toString();
    }
}
