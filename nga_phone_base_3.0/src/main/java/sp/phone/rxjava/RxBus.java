package sp.phone.rxjava;

import androidx.annotation.NonNull;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by Justwen on 2017/11/25.
 */

public class RxBus {

    private FlowableProcessor<Object> mBus;

    private static class SingleTonHolder {

        static final RxBus INSTANCE = new RxBus();
    }

    public static RxBus getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized();
    }

    public void post(@NonNull Object obj) {
        mBus.onNext(obj);
    }

    public <T> Flowable<T> register(Class<T> clz) {
        return mBus.ofType(clz);
    }

    public Flowable<Object> register() {
        return mBus;
    }

    public void unregisterAll() {
        //会将所有由mBus生成的Flowable都置completed状态后续的所有消息都收不到了
        mBus.onComplete();
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

}
