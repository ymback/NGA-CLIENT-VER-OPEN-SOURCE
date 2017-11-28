package sp.phone.mvp.presenter;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.MessageListModel;
import sp.phone.mvp.contract.MessageListContract;

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
            mMessageView.hideLoadingView();
            if (text.isEmpty()) {
                mMessageView.showToast(R.string.error_network);
            } else {
                mMessageView.showToast(text);
            }
        }

        @Override
        public void onSuccess(MessageListInfo data) {
            if (!isAttached()) {
                return;
            }
            mMessageView.setRefreshing(false);
            mMessageView.hideLoadingView();
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
        mMessageView.setRefreshing(true);
        mMessageModel.loadPage(page, mCallBack);
    }

}
