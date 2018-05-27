package sp.phone.mvp.model;


import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import gov.anzong.androidnga.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import sp.phone.bean.TopicPostBean;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.forumoperation.PostParam;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.task.TopicPostTask;
import sp.phone.util.FileUtils;
import sp.phone.util.HttpUtil;
import sp.phone.util.ImageUtils;
import sp.phone.util.NLog;

/**
 * Created by Justwen on 2017/6/10.
 */

public class TopicPostModel extends BaseModel implements TopicPostContract.Model {

    private static final String HOST = Utils.getNGAHost() + "post.php?";

    private static final String BASE_URL_ATTACHMENT_SERVER = "http://" + HttpUtil.NGA_ATTACHMENT_HOST + ":8080/attach.php?";

    private RetrofitService mRetrofitService;

    public TopicPostModel() {
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
    public void post(PostParam postParam, TopicPostTask.CallBack callBack) {
        new TopicPostTask(ApplicationContextHolder.getContext(), callBack).execute(postParam.toString());
    }

    @Override
    public void uploadFile(Uri uri, PostParam postParam, OnHttpCallBack<String> callBack) {
        Observable.just(uri)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<Uri, MultipartBody>() {
                    @Override
                    public MultipartBody apply(Uri uri) throws Exception {
                        File file = FileUtils.createFile(uri);
                        byte[] img;
                        String fileName = file.getName();
                        if (file.length() >= 1024 * 1024) {
                            img = ImageUtils.fitImageToUpload(new FileInputStream(file), new FileInputStream(file));
                        } else {
                            img = FileUtils.getBytes(file);

                        }
                        return buildMultipartBody(fileName, img, postParam);
                    }
                })
                .subscribe(new BaseSubscriber<MultipartBody>() {
                    @Override
                    public void onNext(MultipartBody body) {
                        uploadFile(body, postParam, callBack);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });

    }

    private void uploadFile(MultipartBody multipartBody, PostParam postParam, OnHttpCallBack<String> callBack) {
        RetrofitService service = RetrofitHelper.getInstance().getService();
        service.uploadFile(BASE_URL_ATTACHMENT_SERVER, multipartBody)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(getLifecycleProvider().<ResponseBody>bindUntilEvent(FragmentEvent.DETACH))
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        return responseBody.string();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        s = s.replace("window.script_muti_get_var_store=", "");
                        JSONObject object = JSON.parseObject(s).getJSONObject("data");
                        postParam.appendAttachments_(object.getString("attachments"));
                        postParam.appendAttachments_check_(object.getString("attachments_check"));
                        callBack.onSuccess(object.getString("url"));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });
    }

    private MultipartBody buildMultipartBody(String fileName, byte[] bytes, PostParam postParam) throws UnsupportedEncodingException {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(MediaType.parse("multipart/form-data"))
                .addPart(MultipartBody.Part.createFormData("attachment_file1", fileName, RequestBody.create(MediaType.parse("image/jpeg"), bytes)))
                .addFormDataPart("attachment_file1_url_utf8_name", new String(fileName.getBytes(), "UTF-8"))
                .addFormDataPart("fid", String.valueOf(postParam.getFid()))
                .addFormDataPart("auth", postParam.getAuth())
                .addFormDataPart("func", "upload")
                .addFormDataPart("v2", "1")
                .addFormDataPart("lite", "js")
                .addFormDataPart("attachment_file1_auto_size", "1")
                .addFormDataPart("attachment_file1_watermark", "")
                .addFormDataPart("attachment_file1_dscp", "")
                .addFormDataPart("attachment_file1_img", "1")
                .addFormDataPart("origin_domain", "bbs.ngacn.cc");
        return builder.build();
    }


}
