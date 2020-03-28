package sp.phone.mvp.presenter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import sp.phone.mvp.model.BaseModel;
import sp.phone.ui.fragment.BaseMvpFragment;

/**
 * @author Justwen
 * @date 2017/11/25
 */

public abstract class BasePresenter<T extends BaseMvpFragment, E extends BaseModel>
        implements LifecycleObserver {

    protected T mBaseView;

    protected E mBaseModel;

    @Deprecated
    public BasePresenter() {
        mBaseModel = onCreateModel();
    }

    public BasePresenter(T baseView) {
        mBaseModel = onCreateModel();
        attachView(baseView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public final void performCreate() {
        onCreate();
    }

    protected void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public final void performDestroy() {
        detachView();
        onDestroy();
    }

    protected void onDestroy() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public final void performResume() {
        onResume();
    }

    protected void onResume() {

    }

    public void attachView(T view) {
        mBaseView = view;
        if (mBaseModel != null) {
            mBaseModel.setLifecycleProvider(view.getLifecycleProvider());
        }
    }

    private void detachView() {
        mBaseView = null;
        if (mBaseModel != null) {
            mBaseModel.detach();
        }
    }

    protected boolean isAttached() {
        return mBaseView != null;
    }

    public void onViewCreated() {
    }


    protected abstract E onCreateModel();
}
