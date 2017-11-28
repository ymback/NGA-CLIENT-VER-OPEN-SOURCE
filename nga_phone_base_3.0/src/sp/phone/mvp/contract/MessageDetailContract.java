package sp.phone.mvp.contract;

import sp.phone.bean.MessageDetailInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.presenter.contract.tmp.*;
import sp.phone.presenter.contract.tmp.BaseContract;

/**
 * Created by Justwen on 2017/10/11.
 */

public interface MessageDetailContract {

    interface IMessageView extends sp.phone.presenter.contract.tmp.BaseContract.View {

        void hideLoadingView();

        void setData(MessageDetailInfo listInfo);

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

    }

    interface IMessagePresenter extends sp.phone.presenter.contract.tmp.BaseContract.Presenter<IMessageView> {

        void loadPage(int page,int mid);
    }

    interface IMessageModel extends BaseContract.Model {

        void loadPage(int page, int mid, OnHttpCallBack<MessageDetailInfo> callBack);
    }

}
