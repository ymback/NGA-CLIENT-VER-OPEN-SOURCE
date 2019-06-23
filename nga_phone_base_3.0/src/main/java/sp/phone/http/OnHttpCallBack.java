package sp.phone.http;

/**
 * Created by Justwen on 2017/10/10.
 */

public interface OnHttpCallBack<T> {

    void onError(String text);

    void onSuccess(T data);
}
