package sp.phone.mvp.model;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.apache.commons.io.IOUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import sp.phone.bean.TopicPostBean;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.forumoperation.ParamKey;
import sp.phone.forumoperation.PostParam;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.task.TopicPostTask;
import sp.phone.util.HttpUtil;
import sp.phone.util.ImageUtils;
import sp.phone.util.NLog;
import sp.phone.util.StringUtils;

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
                .append(postParam.getPostFid())
                .append("&lite=js");
        if (postParam.getPostAction() != null) {
            builder.append("&action=").append(postParam.getPostAction());
        }

        if (postParam.getPostPid() != null) {
            builder.append("&pid=").append(postParam.getPostPid());
        }

        if (postParam.getPostTid() != null) {
            builder.append("&tid=").append(postParam.getPostTid());
        }

        mRetrofitService.post(builder.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map((String s) -> {
                    NLog.d(s);
                    s = s.replace("window.script_muti_get_var_store=", "");
                    TopicPostBean bean = JSON.parseObject(s, TopicPostBean.class);
                    postParam.setAuthCode(bean.getData().getAuth());
                    return postParam;
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
    public void loadTopicCategory(PostParam postParam, final OnHttpCallBack<List<String>> callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("__lib", "topic_key");
        map.put("__act", "get");
        map.put(ParamKey.KEY_FID, String.valueOf(postParam.getPostFid()));
        map.put("__output", "8");
        mRetrofitService.get(map)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> {
                    JSONObject obj = JSON.parseObject(s).getJSONObject("data").getJSONObject("0");
                    List<String> ret = new ArrayList<>();
                    for (int index = 0; obj.containsKey(String.valueOf(index)); index++) {
                        ret.add(obj.getJSONObject(String.valueOf(index)).getString("0"));
                    }
                    return ret;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<String>>() {

                    @Override
                    public void onNext(@NonNull List<String> list) {
                        callBack.onSuccess(list);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        callBack.onError(throwable.getLocalizedMessage());
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
                        Context context = ApplicationContextHolder.getContext();
                        ContentResolver cr = context.getContentResolver();

                        ParcelFileDescriptor pfd = cr.openFileDescriptor(uri, "r");
                        String contentType = cr.getType(uri);
                        if (StringUtils.isEmpty(contentType)) {
                            throw new IllegalArgumentException(context.getString(R.string.invalid_img_selected));
                        }
                        String fileName = contentType.replace('/', '.');
                        long fileSize = pfd.getStatSize();
                        byte[] img;
                        if (fileSize >= 1024 * 1024) {
                            img = ImageUtils.fitImageToUpload(cr.openInputStream(uri), cr.openInputStream(uri));
                        } else {
                            img = IOUtils.toByteArray(cr.openInputStream(uri));
                        }

                        return buildMultipartBody(fileName, img, postParam);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
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
                        try {
                            s = s.replace("window.script_muti_get_var_store=", "");
                            JSONObject object = JSON.parseObject(s).getJSONObject("data");
                            postParam.appendAttachment(object.getString("attachments"), object.getString("attachments_check"));
                            callBack.onSuccess(object.getString("url"));
                        } catch (Exception e) {
                            NLog.e("exception occur while uploading file " + s);
                            callBack.onError("上传图片失败，请重试");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });
    }

    private MultipartBody buildMultipartBody(String fileName, byte[] bytes, PostParam postParam) throws UnsupportedEncodingException {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(Objects.requireNonNull(MediaType.parse("multipart/form-data")))
                .addPart(MultipartBody.Part.createFormData("attachment_file1", fileName, RequestBody.create(MediaType.parse("image/jpeg"), bytes)))
                .addFormDataPart("attachment_file1_url_utf8_name", new String(fileName.getBytes(), "UTF-8"))
                .addFormDataPart("fid", String.valueOf(postParam.getPostFid()))
                .addFormDataPart("auth", postParam.getAuthCode())
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
