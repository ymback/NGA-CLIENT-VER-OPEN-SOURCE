package sp.phone.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.annotations.NonNull;
import sp.phone.fragment.BaseFragment;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.rxjava.RxLifecycleProvider;

/**
 * Created by Justwen on 2017/11/27.
 */

public class BaseRxFragment extends BaseFragment {

    private RxLifecycleProvider mRxLifecycleProvider = new RxLifecycleProvider();

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRxLifecycleProvider.onNext(FragmentEvent.CREATE);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRxLifecycleProvider.onNext(FragmentEvent.CREATE_VIEW);
    }

    public void onStart() {
        super.onStart();
        mRxLifecycleProvider.onNext(FragmentEvent.START);
    }

    public void onResume() {
        super.onResume();
        mRxLifecycleProvider.onNext(FragmentEvent.RESUME);
    }

    public void onPause() {
        mRxLifecycleProvider.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    public void onStop() {
        mRxLifecycleProvider.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    public void onDestroyView() {
        mRxLifecycleProvider.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    public void onDestroy() {
        mRxLifecycleProvider.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    public void onDetach() {
        mRxLifecycleProvider.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    protected void registerRxBus() {
        RxBus.getInstance().register(RxEvent.class)
                .compose(mRxLifecycleProvider.<RxEvent>bindUntilEvent(FragmentEvent.DETACH))
                .subscribe(new BaseSubscriber<RxEvent>() {
                    @Override
                    public void onNext(@NonNull RxEvent rxEvent) {
                        accept(rxEvent);
                    }
                });

    }

    protected void accept(@NonNull RxEvent rxEvent) {

    }

    public LifecycleProvider getLifecycleProvider() {
        return mRxLifecycleProvider;
    }

}
