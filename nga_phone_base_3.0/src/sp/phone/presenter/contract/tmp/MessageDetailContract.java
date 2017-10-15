package sp.phone.presenter.contract.tmp;

import android.support.annotation.StringRes;

import sp.phone.bean.MessageDetailInfo;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/10/11.
 */

public interface MessageDetailContract {

    interface IMessageView extends BaseContract.View {

        void hideProgressBar();

        void setData(MessageDetailInfo listInfo);

        void setRefreshing(boolean refreshing);

        void clearData();
    }

    interface IMessagePresenter extends BaseContract.Presenter<IMessageView> {

        void loadPage(int page,int mid);

        void loadNextPage(int page,int mid);

        void showMessage(@StringRes int resId);

        void showMessage(String text);
    }

    interface IMessageModel extends BaseContract.Model {

        void loadPage(int page, int mid, OnHttpCallBack<MessageDetailInfo> callBack);
    }

}
