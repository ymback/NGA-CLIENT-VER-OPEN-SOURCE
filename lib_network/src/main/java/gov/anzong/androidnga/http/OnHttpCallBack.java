package gov.anzong.androidnga.http;


public interface OnHttpCallBack<T> {

    default void onError(String text) {

    }

    default void onSuccess(T data) {

    }
}
