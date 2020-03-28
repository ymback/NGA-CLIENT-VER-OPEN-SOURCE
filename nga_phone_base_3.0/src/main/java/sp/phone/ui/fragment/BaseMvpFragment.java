package sp.phone.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import sp.phone.mvp.presenter.BasePresenter;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BaseMvpFragment<T extends BasePresenter> extends BaseRxFragment {

    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = onCreatePresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
            getLifecycle().addObserver(mPresenter);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mPresenter != null) {
            mPresenter.onViewCreated();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract T onCreatePresenter();
}
