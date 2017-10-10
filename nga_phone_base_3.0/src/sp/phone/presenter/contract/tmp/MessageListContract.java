package sp.phone.presenter.contract.tmp;

import android.support.annotation.StringRes;

import sp.phone.bean.MessageListInfo;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/10/9.
 */

public interface MessageListContract {

    interface IMessageView extends BaseContract.View {

        void hideProgressBar();

        void setData(MessageListInfo listInfo);

        void setRefreshing(boolean refreshing);
    }

    interface IMessagePresenter extends BaseContract.Presenter<IMessageView> {

        void loadPage(int page);

        void loadNextPage(int page);

        void showMessage(@StringRes int id);

        void showMessage(String text);
    }

    interface IMessageModel extends BaseContract.Model {

        void loadPage(int page, OnHttpCallBack<MessageListInfo> callBack);
    }

}
