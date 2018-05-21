package sp.phone.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import sp.phone.common.UserManagerImpl;
import sp.phone.retrofit.converter.JsonStringConvertFactory;

/**
 * Created by Justwen on 2017/10/10.
 */

public class RetrofitHelper {

    private Retrofit mRetrofit;

    private static final String URL_NGA_BASE = "http://bbs.nga.cn/";

    private static final String URL_NGA_BASE_CC = "https://bbs.ngacn.cc/";

    private RetrofitHelper() {

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

        mRetrofit = new Retrofit.Builder()
                .baseUrl(URL_NGA_BASE)
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
