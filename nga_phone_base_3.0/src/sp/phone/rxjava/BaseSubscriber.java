package sp.phone.rxjava;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Justwen on 2017/11/25.
 */
// 兼容 rxjava 1 和 rxjava 2
public abstract class BaseSubscriber<T> implements Observer<T>, FlowableSubscriber<T> {

    @Override
    public void onNext(@NonNull T t) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(@NonNull Throwable throwable) {

    }

    @Override
    public void onSubscribe(@NonNull Disposable disposable) {

    }

    @Override
    public void onSubscribe(@NonNull Subscription subscription) {
        subscription.request(Integer.MAX_VALUE);
    }
}
