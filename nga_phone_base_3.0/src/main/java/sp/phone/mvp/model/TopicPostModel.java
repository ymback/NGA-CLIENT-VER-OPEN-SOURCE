package sp.phone.mvp.model;


import android.net.Uri;

import com.alibaba.fastjson.JSON;

import gov.anzong.androidnga.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import sp.phone.bean.TopicPostBean;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.forumoperation.PostParam;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.task.FileUploadTask;
import sp.phone.task.TopicPostTask;
import sp.phone.util.NLog;

/**
 * Created by Justwen on 2017/6/10.
 */

public class TopicPostModel extends BaseModel implements TopicPostContract.Model {

    private static final String HOST = Utils.getNGAHost() + "post.php?";

    private TopicPostContract.Presenter mPresenter;

    private RetrofitService mRetrofitService;

    public TopicPostModel(TopicPostContract.Presenter presenter) {
        mPresenter = presenter;
        mRetrofitService = RetrofitHelper.getInstance().getService();
    }

    @Override
    public void getPostInfo(PostParam postParam, OnHttpCallBack<PostParam> callBack) {
        StringBuilder builder = new StringBuilder(HOST);
        builder.append("fid=")
                .append(postParam.getFid())
                .append("&lite=js");
        if (postParam.getAction() != null) {
            builder.append("&action=").append(postParam.getAction());
        }

        if (postParam.getPid() != null) {
            builder.append("&pid=").append(postParam.getPid());
        }

        if (postParam.getTid() != null) {
            builder.append("&tid=").append(postParam.getTid());
        }

        mRetrofitService.post(builder.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, PostParam>() {
                    @Override
                    public PostParam apply(String s) throws Exception {
                        NLog.d(s);
                        s = s.replace("window.script_muti_get_var_store=", "");
                        TopicPostBean bean = JSON.parseObject(s, TopicPostBean.class);
                        postParam.setAuth(bean.getData().getAuth());
                        return postParam;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<PostParam>() {

                    @Override
                    public void onNext(PostParam postParam) {
                        callBack.onSuccess(postParam);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError("获取附件验证码失败，将无法上传附件！！");
                    }
                });
    }

    @Override
    public void post() {
        PostParam act = mPresenter.getTopicPostAction();
        new TopicPostTask(ApplicationContextHolder.getContext(), (TopicPostTask.CallBack) mPresenter).execute(act.toString());
    }

    @Override
    public void uploadFile(Uri uri) {
        PostParam act = mPresenter.getTopicPostAction();
        FileUploadTask fileUploadTask = new FileUploadTask(ApplicationContextHolder.getContext(), (FileUploadTask.onFileUploaded) mPresenter, uri, act.getAuth());
        fileUploadTask.executeOnExecutor(FileUploadTask.THREAD_POOL_EXECUTOR);
    }

}
