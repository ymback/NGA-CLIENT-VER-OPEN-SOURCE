package sp.phone.mvp.contract.tmp;

import android.content.Context;

/**
 * Created by Yang Yihang on 2017/5/28.
 */

public interface BaseContract {

    interface Presenter<T> {

        Context getContext();

        void setView(T view);

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
