package sp.phone.model;


import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.io.IOException;

import gov.anzong.androidnga.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import sp.phone.bean.TopicPostBean;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.TopicPostAction;
import sp.phone.presenter.contract.TopicPostContract;
import sp.phone.task.FileUploadTask;
import sp.phone.task.TopicPostTask;
import sp.phone.utils.NLog;

/**
 * Created by Yang Yihang on 2017/6/10.
 */

public class TopicPostModel implements TopicPostContract.Model {

    private static final String HOST = Utils.getNGAHost() + "post.php?";

    private static final String TAG = TopicPostModel.class.getSimpleName();

    private TopicPostContract.Presenter mPresenter;

    private OkHttpClient mOkHttpClient;

    public TopicPostModel(TopicPostContract.Presenter presenter) {
        mOkHttpClient = new OkHttpClient();
        mPresenter = presenter;
    }

    @Override
    public void preparePost() {

        final TopicPostAction act = mPresenter.getTopicPostAction();

        StringBuilder builder = new StringBuilder(HOST);
        builder.append("fid=").append(act.getFid_()).append("&lite=js");
        if (act.getAction() != null) {
            builder.append("&action=").append(act.getAction());
        }

        if (act.getPid() != null) {
            builder.append("&pid=").append(act.getPid());
        }

        if (act.getTid() != null) {
            builder.append("&tid=").append(act.getTid());
        }

        Request request = new Request.Builder()
                .url(builder.toString())
                .header("Cookie", PhoneConfiguration.getInstance().getCookie())
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                ResponseBody body = response.body();
                if ("ok".equalsIgnoreCase(response.message()) && body != null) {
                    String result = body.string();
                    int index = result.indexOf("=");
                    if (index < 0) {
                        NLog.e(TAG, "prepare post info failed !!");
                        return;
                    }
                    result = result.substring(index + 1);
                    TopicPostBean bean = null;
                    try {
                        bean = JSON.parseObject(result, TopicPostBean.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (bean != null) {
                        act.setAuth(bean.getData().getAuth());
                    } else {
                        NLog.e(TAG, "prepare post info failed !!");
                    }
                } else {
                    NLog.e(TAG, "prepare post info failed !!");
                }
            }
        });

    }

    @Override
    public void post() {
        TopicPostAction act = mPresenter.getTopicPostAction();
        new TopicPostTask(mPresenter.getContext(), (TopicPostTask.CallBack) mPresenter).execute(act.toString());
    }

    @Override
    public void uploadFile(Uri uri) {
        TopicPostAction act = mPresenter.getTopicPostAction();
        FileUploadTask fileUploadTask = new FileUploadTask(mPresenter.getContext(), (FileUploadTask.onFileUploaded) mPresenter, uri, act.getAuth());
        fileUploadTask.executeOnExecutor(FileUploadTask.THREAD_POOL_EXECUTOR);
    }

}
