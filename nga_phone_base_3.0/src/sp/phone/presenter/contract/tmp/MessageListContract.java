package sp.phone.presenter.contract.tmp;

import android.support.annotation.StringRes;

import sp.phone.bean.MessageListInfo;

/**
 * Created by Justwen on 2017/10/9.
 */

public interface MessageListContract {

    interface View extends BaseContract.View {

        void hideProgressBar();

        void setData(MessageListInfo listInfo);

        void setRefreshing(boolean refreshing);
    }

    interface Presenter extends BaseContract.Presenter<View> {

        void loadPage(int page);

        void loadNextPage(int page);

        void showMessage(@StringRes int id);

        void showMessage(String text);
    }

}
