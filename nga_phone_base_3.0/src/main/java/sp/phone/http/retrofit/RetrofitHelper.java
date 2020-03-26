package sp.phone.http.retrofit;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import gov.anzong.androidnga.base.util.ContextUtils;;
import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.common.UserManagerImpl;
import sp.phone.http.retrofit.converter.JsonStringConvertFactory;
import sp.phone.util.ForumUtils;

/**
 * Created by Justwen on 2017/10/10.
 */

public class RetrofitHelper {

    private Retrofit mRetrofit;

    private static final String URL_NGA_BASE_CC = "https://bbs.ngacn.cc/";

    private RetrofitHelper() {
        SharedPreferences sp = ContextUtils.getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        String baseUrl = ForumUtils.getAvailableDomain();
        mRetrofit = createRetrofit(baseUrl);

        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                if (key.equals(PreferenceKey.KEY_NGA_DOMAIN)) {
                    String baseUrl = ForumUtils.getAvailableDomain();
                    mRetrofit = createRetrofit(baseUrl);
                }
            }
        });
    }

    private Retrofit createRetrofit(String baseUrl) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Cookie", UserManagerImpl.getInstance().getCookie())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JsonStringConvertFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();
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
