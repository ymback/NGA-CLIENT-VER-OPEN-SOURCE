package sp.phone.fragment.material;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import sp.phone.fragment.BaseFragment;
import sp.phone.presenter.contract.tmp.BaseContract;

public abstract class BaseMvpFragment extends BaseFragment {

    private BaseContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
            mPresenter.detachView();
        }
        super.onDetach();
    }

    public void setPresenter(BaseContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public BaseContract.Presenter getPresenter() {
        return mPresenter;
    }

}
