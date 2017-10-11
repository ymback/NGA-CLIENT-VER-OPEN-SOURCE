package sp.phone.presenter;

import android.support.annotation.StringRes;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.model.MessageListModel;
import sp.phone.presenter.contract.tmp.MessageListContract;

/**
 * Created by Justwen on 2017/10/9.
 */

public class MessageListPresenter implements MessageListContract.IMessagePresenter {

    private MessageListContract.IMessageView mMessageView;

    private MessageListContract.IMessageModel mMessageModel;

    private OnHttpCallBack<MessageListInfo> mCallBack = new OnHttpCallBack<MessageListInfo>() {
        @Override
        public void onError(String text) {
            if (!isAttached()) {
                return;
            }
            mMessageView.setRefreshing(false);
            mMessageView.hideProgressBar();
            if (text.isEmpty()) {
                showMessage(R.string.error_network);
            } else {
                showMessage(text);
            }
        }

        @Override
        public void onSuccess(MessageListInfo data) {
            if (!isAttached()) {
                return;
            }
            mMessageView.setRefreshing(false);
            mMessageView.hideProgressBar();
            mMessageView.setData(data);
        }
    };

    public MessageListPresenter() {
        mMessageModel = new MessageListModel();
    }

    @Override
    public void attachView(MessageListContract.IMessageView view) {
        mMessageView = view;
    }

    @Override
    public void detachView() {
        mMessageView = null;
    }

    @Override
    public boolean isAttached() {
        return mMessageView != null;
    }

    @Override
    public void loadPage(int page) {
        mMessageModel.loadPage(page, mCallBack);
    }

    @Override
    public void loadNextPage(int page) {
        mMessageView.setRefreshing(true);
        mMessageModel.loadPage(page, mCallBack);
    }

    @Override
    public void showMessage(@StringRes int id) {
        if (!isAttached()) {
            return;
        }
        mMessageView.showToast(id);
    }

    @Override
    public void showMessage(String text) {
        if (!isAttached()) {
            return;
        }
        mMessageView.showToast(text);
    }
}
