package sp.phone.fragment.material;


import android.os.Bundle;
import android.support.annotation.Nullable;

import sp.phone.fragment.BaseFragment;
import sp.phone.presenter.contract.tmp.BaseContract;

public abstract class BaseMvpFragment extends BaseFragment {

    private BaseContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter.attachView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        mPresenter.attachView(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.detachView();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mPresenter = null;
        super.onDestroy();
    }

    public void setPresenter(BaseContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public BaseContract.Presenter getPresenter() {
        return mPresenter;
    }

}
