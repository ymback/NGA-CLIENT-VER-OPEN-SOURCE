package sp.phone.lab.mvp.presenter;

import sp.phone.lab.mvp.contract.BaseContract;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BasePresenter<T,E extends BaseContract.Model> implements BaseContract.Presenter<T> {

    protected T mBaseView;

    protected E mBaseModel;

    public BasePresenter() {
        mBaseModel = onCreateModel();
    }

    @Override
    public void detach() {
        mBaseView = null;
        mBaseModel.detach();
    }

    @Override
    public void attachView(T view) {
        mBaseView = view;
    }

    @Override
    public boolean isAttached() {
        return mBaseView != null;
    }

    protected abstract E onCreateModel();
}
