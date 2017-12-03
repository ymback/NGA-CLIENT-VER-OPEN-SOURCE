package sp.phone.mvp.contract;

import sp.phone.bean.MessageListInfo;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/10/9.
 */

public interface MessageListContract {

    interface IMessageView  {

        void hideLoadingView();

        boolean isRefreshing();

        void setData(MessageListInfo listInfo);

        void setRefreshing(boolean refreshing);
    }

    interface IMessagePresenter  {

        void loadPage(int page);

    }

    interface IMessageModel  {

        void loadPage(int page, OnHttpCallBack<MessageListInfo> callBack);
    }

}
