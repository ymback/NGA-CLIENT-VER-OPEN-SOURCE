package sp.phone.lab.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.annotations.NonNull;
import sp.phone.fragment.BaseFragment;
import sp.phone.lab.rxjava.BaseSubscriber;
import sp.phone.lab.rxjava.RxBus;
import sp.phone.lab.rxjava.RxEvent;
import sp.phone.lab.rxjava.RxLifecycleProvider;

/**
 * Created by Justwen on 2017/11/27.
 */

public class BaseRxFragment extends BaseFragment {

    private RxLifecycleProvider mRxLifecycleProvider;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycleProvider().onNext(FragmentEvent.CREATE);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLifecycleProvider().onNext(FragmentEvent.CREATE_VIEW);
    }

    public void onStart() {
        super.onStart();
        getLifecycleProvider().onNext(FragmentEvent.START);
    }

    public void onResume() {
        super.onResume();
        getLifecycleProvider().onNext(FragmentEvent.RESUME);
    }

    public void onPause() {
        getLifecycleProvider().onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    public void onStop() {
        getLifecycleProvider().onNext(FragmentEvent.STOP);
        super.onStop();
    }

    public void onDestroyView() {
        getLifecycleProvider().onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    public void onDestroy() {
        getLifecycleProvider().onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    public void onDetach() {
        getLifecycleProvider().onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    protected void registerRxBus() {
        RxBus.getInstance().register(RxEvent.class)
                .subscribe(new BaseSubscriber<RxEvent>() {
                    @Override
                    public void onNext(@NonNull RxEvent rxEvent) {
                        accept(rxEvent);
                    }
                });

    }

    protected void accept(@NonNull RxEvent rxEvent) {

    }

    public RxLifecycleProvider getLifecycleProvider() {
        if (mRxLifecycleProvider == null) {
            mRxLifecycleProvider = new RxLifecycleProvider();
        }
        return mRxLifecycleProvider;
    }

}
