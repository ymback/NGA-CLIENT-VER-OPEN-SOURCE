package sp.phone.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import sp.phone.mvp.contract.tmp.BaseContract;

public abstract class BaseMvpFragment<T extends BaseContract.Presenter> extends BaseFragment {

    private T mPresenter;

    public static class PresenterLoader<T extends BaseContract.Presenter> extends Loader<T> {

        private T mPresenter;

        private Class<T> mTClass;

        /**
         * Stores away the application context associated with context.
         * Since Loaders can be used across multiple activities it's dangerous to
         * store the context directly; always use {@link #getContext()} to retrieve
         * the Loader's Context, don't use the constructor argument directly.
         * The Context returned by {@link #getContext} is safe to use across
         * Activity instances.
         *
         * @param context used to retrieve the application context.
         */
        public PresenterLoader(Context context,Class<T> tClass) {
            super(context);
            mTClass = tClass;
        }

        @Override
        protected void onStartLoading() {
            if (mPresenter != null) {
                deliverResult(mPresenter);
                return;
            }
            forceLoad();
            super.onStartLoading();
        }

        @Override
        protected void onReset() {
            mPresenter = null;
            super.onReset();
        }

        @Override
        protected void onForceLoad() {

            try {
                mPresenter = mTClass.newInstance();
                deliverResult(mPresenter);
            } catch (java.lang.InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<T>() {
            @Override
            public Loader<T> onCreateLoader(int id, Bundle args) {
                return new PresenterLoader<>(getContext(),getPresenterClass());
            }

            @Override
            public void onLoadFinished(Loader<T> loader, T data) {
                mPresenter = data;
            }

            @Override
            public void onLoaderReset(Loader<T> loader) {

            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    protected T getPresenter() {
        return mPresenter;
    }

    protected abstract Class<T> getPresenterClass();
}
