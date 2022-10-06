package sp.phone.http.retrofit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.URLDecoder;
import java.util.Objects;

import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.StringUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.debug.Debugger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import sp.phone.common.UserManagerImpl;
import sp.phone.http.retrofit.converter.JsonStringConvertFactory;
import sp.phone.util.ForumUtils;
import sp.phone.util.NLog;

/**
 * Created by Justwen on 2017/10/10.
 */

public class RetrofitHelper {

    private Retrofit mRetrofit;

    private static final String URL_NGA_BASE_CC = "https://bbs.ngacn.cc/";

    private String mBaseUrl;

    private String mUserAgent;

    private RetrofitHelper() {
        SharedPreferences sp = ContextUtils.getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        mBaseUrl = ForumUtils.getAvailableDomain();
        mUserAgent = ForumUtils.getCurrentUserAgent();
        mRetrofit = createRetrofit();

        sp.registerOnSharedPreferenceChangeListener((sp1, key) -> {
            if (key.equals(PreferenceKey.KEY_NGA_DOMAIN)) {
                mBaseUrl = ForumUtils.getAvailableDomain();
                mRetrofit = createRetrofit();
            }
            if (key.equals(PreferenceKey.USER_AGENT_LIST)) {
                mUserAgent = ForumUtils.getCurrentUserAgent();
                mRetrofit = createRetrofit();
            }
        });
    }

    public void updateRetrofit(){
        mBaseUrl = ForumUtils.getAvailableDomain();
        mUserAgent = ForumUtils.getCurrentUserAgent();
        mRetrofit = createRetrofit();
    }

    public Retrofit createRetrofit() {
        return createRetrofit(mBaseUrl,mUserAgent, null);
    }

    public Retrofit createRetrofit(String baseUrl) {
        return createRetrofit(baseUrl, mUserAgent,null);
    }

    public Retrofit createRetrofit(OkHttpClient.Builder builder) {
        return createRetrofit(mBaseUrl, mUserAgent,builder);
    }

    public Retrofit createRetrofit(String baseUrl,String userAgent, OkHttpClient.Builder builder) {
        if (builder == null) {
            builder = createOkHttpClientBuilder(userAgent);
        }
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JsonStringConvertFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();
    }

    public OkHttpClient.Builder createOkHttpClientBuilder(String userAgent) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {
            Request original = chain.request();

            String cookie = original.header("Cookie");
            if (cookie == null) {
                cookie = UserManagerImpl.getInstance().getCookie();
            }
            Request request = original.newBuilder()
                    .header("Cookie", cookie)
                    .header("User-Agent", Objects.equals(userAgent, "自动") ?"Nga_Official/80023":userAgent)
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        builder.addInterceptor(chain -> {
            Request request = chain.request();
            try {
                if (request.method().equalsIgnoreCase("post")) {
                    String body = StringUtils.requestBody2String(request.body());
                    body = URLDecoder.decode(body, "utf-8");
                    if (body.contains("charset=gbk")) {
                        request = request.newBuilder().post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=GBK"), body)).build();
                    }
                }
            } catch (Exception e) {
                NLog.e(e.getMessage());
            }
            return chain.proceed(request);
        });
        builder.addInterceptor(chain -> {
            Request request = chain.request();
            Debugger.collectRequest(request);
            return chain.proceed(request);
        });
        return builder;
    }

    public static RetrofitHelper getInstance() {
        return SingleTonHolder.sInstance;
    }

    public Object getService(Class<?> service) {
        return mRetrofit.create(service);
    }

    public RetrofitService getService() {
        return mRetrofit.create(RetrofitService.class);
    }

    public static RetrofitService getAuthCodeService() {
        return new Retrofit.Builder()
                .baseUrl(URL_NGA_BASE_CC)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(RetrofitService.class);
    }

    public static RetrofitService getDefault() {
        return new Retrofit.Builder()
                .baseUrl(URL_NGA_BASE_CC)
                .addConverterFactory(JsonStringConvertFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(RetrofitService.class);
    }

    private static class SingleTonHolder {

        static final RetrofitHelper sInstance = new RetrofitHelper();
    }
}
