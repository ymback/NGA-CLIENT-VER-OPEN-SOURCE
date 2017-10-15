package sp.phone.retrofit;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Justwen on 2017/10/10.
 */

public interface RetrofitService {

    @GET("nuke.php")
    Observable<String> getInfo(@QueryMap Map<String,String> map);

}
