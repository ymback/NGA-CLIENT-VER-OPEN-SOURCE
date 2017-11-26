package sp.phone.lab.mvp.contract;

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
    }

    interface View {

        void showToast(String text);
    }

}
