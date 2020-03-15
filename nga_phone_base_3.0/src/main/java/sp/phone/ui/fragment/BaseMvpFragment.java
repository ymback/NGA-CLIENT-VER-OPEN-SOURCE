package sp.phone.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import sp.phone.mvp.contract.BaseContract;
import sp.phone.mvp.presenter.BasePresenter;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BaseMvpFragment<T extends BasePresenter> extends BaseRxFragment implements BaseContract.View {

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
    public void onResume() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
            mPresenter.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDetach() {
        if (mPresenter != null) {
            mPresenter.detach();
        }
        super.onDetach();
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
