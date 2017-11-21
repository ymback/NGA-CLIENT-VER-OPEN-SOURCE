package sp.phone.retrofit;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Justwen on 2017/10/10.
 */

public interface RetrofitService {

    @GET("nuke.php")
    Observable<String> getInfo(@QueryMap Map<String,String> map);

    @GET
    Observable<String> get(@Url String url);

    @FormUrlEncoded
    @POST("nuke.php")
    Observable<String> post(@FieldMap Map<String,String> map);

}
