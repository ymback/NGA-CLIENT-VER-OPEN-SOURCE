package sp.phone.presenter.contract;

import android.content.Context;

/**
 * Created by Yang Yihang on 2017/5/28.
 */

public interface BaseContract {

    interface Presenter {

        Context getContext();

    }

    interface View<T> {

        void setPresenter(T presenter);

        void showToast(int resId);

        void showToast(String toast);

        Context getContext();

        void finish();
    }

    interface Model{

    }
}
