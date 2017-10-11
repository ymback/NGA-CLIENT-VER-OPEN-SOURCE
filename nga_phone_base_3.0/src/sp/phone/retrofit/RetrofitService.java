package sp.phone.retrofit;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Justwen on 2017/10/10.
 */

public interface RetrofitService {

    /**
     *  http://bbs.nga.cn/nuke.php?__lib=message&__act=message&act=list&page=1&lite=js
     */
    @GET("nuke.php")
    Observable<String> getMessageListInfo(@QueryMap Map<String,String> map);
}
