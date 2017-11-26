package sp.phone.lab.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.annotations.NonNull;
import sp.phone.lab.mvp.presenter.BasePresenter;
import sp.phone.lab.rxjava.BaseSubscriber;
import sp.phone.lab.rxjava.RxBus;
import sp.phone.lab.rxjava.RxEvent;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BaseMvpFragment<T extends BasePresenter> extends BaseFragment {

    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = onCreatePresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
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

    protected abstract T onCreatePresenter();
}
