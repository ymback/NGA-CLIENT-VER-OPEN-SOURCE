package sp.phone.mvp.model;

import com.trello.rxlifecycle2.android.FragmentEvent;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.UserManagerImpl;
import sp.phone.http.OnHttpCallBack;
import sp.phone.http.bean.ThreadData;
import sp.phone.http.retrofit.RetrofitHelper;
import sp.phone.http.retrofit.RetrofitService;
import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.mvp.model.convert.ArticleConvertFactory;
import sp.phone.mvp.model.convert.ErrorConvertFactory;
import sp.phone.param.ArticleListParam;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.NLog;

/**
 * 加载帖子内容
 * Created by Justwen on 2017/7/10.
 */

public class ArticleListModel extends BaseModel implements ArticleListContract.Model {

    private static final String TAG = ArticleListModel.class.getSimpleName();

    private RetrofitService mService;

    public ArticleListModel() {
        mService = (RetrofitService) RetrofitHelper.getInstance().getService(RetrofitService.class);
    }

    private String getUrl(ArticleListParam param) {
        int page = param.page;
        int tid = param.tid;
        int pid = param.pid;
        int authorId = param.authorId;
        String url = getAvailableDomain() + "/read.php?" + "&page=" + page + "&lite=js&noprefix&v2";
        if (tid != 0) {
            url = url + "&tid=" + tid;
        }
        if (pid != 0) {
            url = url + "&pid=" + pid;
        }

        if (authorId != 0) {
            url = url + "&authorid=" + authorId;
        }

        return url;

    }

    @Override
    public void loadPage(ArticleListParam param, final OnHttpCallBack<ThreadData> callBack) {
        String url = getUrl(param);
        mService.get(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .compose(getLifecycleProvider().<String>bindUntilEvent(FragmentEvent.DETACH))
                .map(new Function<String, ThreadData>() {
                    @Override
                    public ThreadData apply(@NonNull String s) throws Exception {
                        long time = System.currentTimeMillis();
                        ThreadData data = ArticleConvertFactory.getArticleInfo(s);
                        NLog.e(TAG, "time = " + (System.currentTimeMillis() - time));
                        if (data == null) {
                            throw new Exception(ErrorConvertFactory.getErrorMessage(s));
                        } else {
                            return data;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getLifecycleProvider().<ThreadData>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<ThreadData>() {

                    @Override
                    public void onNext(@NonNull ThreadData threadData) {
                        callBack.onSuccess(threadData);
                        UserManagerImpl.getInstance().putAvatarUrl(threadData);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(ErrorConvertFactory.getErrorMessage(throwable));
                    }
                });
    }

    @Override
    public void cachePage(ArticleListParam param, String rawData) {
        ThreadUtils.postOnSubThread(() -> {
            try {
                String path = ContextUtils.getContext().getFilesDir().getAbsolutePath() + "/cache/" + param.tid;
                File describeFile = new File(path, param.tid + ".json");
                FileUtils.write(describeFile, param.topicInfo);
                if (!describeFile.exists()) {
                    FileUtils.write(describeFile, param.topicInfo);
                }
                File rawDataFile = new File(path, param.page + ".json");
                FileUtils.write(rawDataFile, rawData);
                ToastUtils.success("缓存成功！");
            } catch (IOException e) {
                ToastUtils.error("缓存失败！");
                e.printStackTrace();
            }
        });
    }

}
