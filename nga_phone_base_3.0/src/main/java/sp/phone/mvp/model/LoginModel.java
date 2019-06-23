package sp.phone.mvp.model;

import android.util.Base64;

import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import sp.phone.param.LoginParam;
import sp.phone.http.OnHttpCallBack;
import sp.phone.mvp.contract.LoginContract;
import sp.phone.http.retrofit.RetrofitHelper;
import sp.phone.http.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.ActivityUtils;

/**
 * Created by Justwen on 2017/6/16.
 */

public class LoginModel extends BaseModel implements LoginContract.Model {

    private static final String BOUNDARY = "----WebKitFormBoundaryhvwhz34oaoXZBQ6I";

    @Override
    public void loadAuthCode(OnHttpCallBack<LoginParam> callBack) {
        String cookie = "_" + Math.random();
        String url = "https://bbs.ngacn.cc/login_check_code.php?id=" + cookie + "/";
        RetrofitService service = RetrofitHelper.getAuthCodeService();
        service.getAuthCodeImage(url).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        byte[] data = readInputStream(responseBody.byteStream());
                        return Base64.encodeToString(data, Base64.DEFAULT);
                    }
                })
                .compose(getLifecycleProvider().<String>bindUntilEvent(FragmentEvent.DETACH))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        LoginParam loginParam = new LoginParam();
                        loginParam.setAuthCodeCookie(cookie);
                        loginParam.setDataUrl("data:image/png;base64," + s);
                        callBack.onSuccess(loginParam);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                        super.onError(throwable);
                    }
                });
    }

    @Override
    public void login(LoginParam loginParam, OnHttpCallBack<String> callBack) {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("name", loginParam.getUserName());
        fieldMap.put("type", "name");
        fieldMap.put("password", loginParam.getPassword());
        fieldMap.put("rid", loginParam.getAuthCodeCookie());
        fieldMap.put("captcha", loginParam.getAuthCode().toUpperCase());
        fieldMap.put("__lib", "login");
        fieldMap.put("__act", "login");
        fieldMap.put("__output", "1");
        fieldMap.put("__inchst", "UTF-8");
        fieldMap.put("raw", "3");
        fieldMap.put("qrkey", "");

        RetrofitService service = RetrofitHelper.getDefault();
        service.login(buildMultipartBody(fieldMap))
                .subscribeOn(Schedulers.io())
                .compose(getLifecycleProvider().<String>bindUntilEvent(FragmentEvent.DETACH))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
//                        JSONObject obj = JSON.parseObject(s);
//                        if (obj.containsKey("error")) {
//                            String error = obj.getJSONObject("error").getString("0");
//                            callBack.onError(error);
//                        }
                        ActivityUtils.showToast(s);
                        super.onNext(s);
                    }
                });

    }


    private FormBody buildFormBody(Map<String, String> fieldMap) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.add(key, value);
        }
        return builder.build();
    }

    private MultipartBody buildMultipartBody(Map<String, String> fieldMap) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addFormDataPart(key, value);
        }
        return builder.build();
    }


    private byte[] readInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = is.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        is.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
