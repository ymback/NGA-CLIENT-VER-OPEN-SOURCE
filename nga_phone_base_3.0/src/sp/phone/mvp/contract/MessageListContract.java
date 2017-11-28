package sp.phone.mvp.contract;

import sp.phone.bean.MessageListInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.presenter.contract.tmp.*;
import sp.phone.presenter.contract.tmp.BaseContract;

/**
 * Created by Justwen on 2017/10/9.
 */

public interface MessageListContract {

    interface IMessageView extends sp.phone.presenter.contract.tmp.BaseContract.View {

        void hideLoadingView();

        boolean isRefreshing();

        void setData(MessageListInfo listInfo);

        void setRefreshing(boolean refreshing);
    }

    interface IMessagePresenter extends sp.phone.presenter.contract.tmp.BaseContract.Presenter<IMessageView> {

        void loadPage(int page);

    }

    interface IMessageModel extends BaseContract.Model {

        void loadPage(int page, OnHttpCallBack<MessageListInfo> callBack);
    }

}
