package sp.phone.rxjava;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class RxUtils {

    private RxUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Disposable clicks(View view, View.OnClickListener listener) {
        //避免双击
        return RxView.clicks(view)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> listener.onClick(view));
    }

    public static void post(Object obj) {
        RxBus.getInstance().post(obj);
    }

    public static void postDelay(int delay, BaseSubscriber<Long> subscriber) {
        Observable.timer(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
