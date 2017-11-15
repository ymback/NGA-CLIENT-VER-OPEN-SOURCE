package sp.phone.presenter.contract.tmp;

import sp.phone.bean.MessageDetailInfo;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/10/11.
 */

public interface MessageDetailContract {

    interface IMessageView extends BaseContract.View {

        void hideLoadingView();

        void setData(MessageDetailInfo listInfo);

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

    }

    interface IMessagePresenter extends BaseContract.Presenter<IMessageView> {

        void loadPage(int page,int mid);
    }

    interface IMessageModel extends BaseContract.Model {

        void loadPage(int page, int mid, OnHttpCallBack<MessageDetailInfo> callBack);
    }

}
