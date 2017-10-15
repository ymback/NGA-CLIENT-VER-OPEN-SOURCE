package sp.phone.presenter;

import android.support.annotation.StringRes;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.model.MessageDetailModel;
import sp.phone.presenter.contract.tmp.MessageDetailContract;

/**
 * Created by Justwen on 2017/10/11.
 */

public class MessageDetailPresenter implements MessageDetailContract.IMessagePresenter {


    private MessageDetailContract.IMessageModel mMessageModel;

    private MessageDetailContract.IMessageView mMessageView;


    private OnHttpCallBack<MessageDetailInfo> mCallBack = new OnHttpCallBack<MessageDetailInfo>() {
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
        public void onSuccess(MessageDetailInfo data) {
            if (!isAttached()) {
                return;
            }
            mMessageView.setRefreshing(false);
            mMessageView.hideProgressBar();
            mMessageView.setData(data);
        }
    };

    public MessageDetailPresenter() {
        mMessageModel = new MessageDetailModel();
    }

    @Override
    public void attachView(MessageDetailContract.IMessageView view) {

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
    public void loadPage(int page,int mid) {
        mMessageModel.loadPage(page,mid,mCallBack);

    }

    @Override
    public void loadNextPage(int page,int mid) {
        mMessageView.setRefreshing(true);
        mMessageModel.loadPage(page,mid,mCallBack);
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
        mMessageView.showToast(text);
    }
}
