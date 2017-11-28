package sp.phone.mvp.contract;

import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * Created by Justwen on 2017/11/25.
 */

public interface BaseContract {

    interface Presenter<T> {

        void attachView(T view);

        void detach();

        boolean isAttached();
    }

    interface Model {

        void detach();

        void setLifecycleProvider(LifecycleProvider provider);
    }

    interface View {

        void showToast(String text);

        LifecycleProvider getLifecycleProvider();
    }

}
