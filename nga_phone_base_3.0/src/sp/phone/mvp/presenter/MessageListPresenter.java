package sp.phone.mvp.presenter;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.fragment.MessageListFragment;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.MessageListModel;
import sp.phone.mvp.contract.MessageListContract;

/**
 * Created by Justwen on 2017/10/9.
 */

public class MessageListPresenter extends BasePresenter<MessageListFragment,MessageListModel> implements MessageListContract.IMessagePresenter {

    private OnHttpCallBack<MessageListInfo> mCallBack = new OnHttpCallBack<MessageListInfo>() {
        @Override
        public void onError(String text) {
            if (!isAttached()) {
                return;
            }
            mBaseView.setRefreshing(false);
            mBaseView.hideLoadingView();
            if (text.isEmpty()) {
                mBaseView.showToast(R.string.error_network);
            } else {
                mBaseView.showToast(text);
            }
        }

        @Override
        public void onSuccess(MessageListInfo data) {
            if (!isAttached()) {
                return;
            }
            mBaseView.setRefreshing(false);
            mBaseView.hideLoadingView();
            mBaseView.setData(data);
        }
    };

    @Override
    public boolean isAttached() {
        return mBaseView != null;
    }

    @Override
    protected MessageListModel onCreateModel() {
        return new MessageListModel();
    }

    @Override
    public void loadPage(int page) {
        mBaseView.setRefreshing(true);
        mBaseModel.loadPage(page, mCallBack);
    }

}
