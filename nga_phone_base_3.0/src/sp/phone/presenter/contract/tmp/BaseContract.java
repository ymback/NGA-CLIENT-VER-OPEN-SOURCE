package sp.phone.presenter.contract.tmp;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Created by Justwen on 2017/5/28.
 */

public interface BaseContract {

    interface Presenter<T> {

        void attachView(T view);

        void detachView();

        boolean isAttached();

    }

    interface View {

        void showToast(@StringRes int resId);

        void showToast(String text);

        Context getContext();

    }

    interface Model{

    }
}
