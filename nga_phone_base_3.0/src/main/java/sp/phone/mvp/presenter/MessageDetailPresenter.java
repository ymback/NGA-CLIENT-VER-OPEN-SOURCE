package sp.phone.mvp.presenter;

import gov.anzong.androidnga.R;
import sp.phone.http.bean.MessageDetailInfo;
import sp.phone.ui.fragment.MessageDetailFragment;
import sp.phone.http.OnHttpCallBack;
import sp.phone.mvp.model.MessageDetailModel;
import sp.phone.mvp.contract.MessageDetailContract;

/**
 * Created by Justwen on 2017/10/11.
 */

public class MessageDetailPresenter extends BasePresenter<MessageDetailFragment,MessageDetailModel> implements MessageDetailContract.IMessagePresenter {


    private OnHttpCallBack<MessageDetailInfo> mCallBack = new OnHttpCallBack<MessageDetailInfo>() {
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
        public void onSuccess(MessageDetailInfo data) {
            if (!isAttached()) {
                return;
            }
            mBaseView.setRefreshing(false);
            mBaseView.hideLoadingView();
            mBaseView.setData(data);
        }
    };

    @Override
    protected MessageDetailModel onCreateModel() {
        return new MessageDetailModel();
    }

    @Override
    public void loadPage(int page, int mid) {
        mBaseView.setRefreshing(true);
        mBaseModel.loadPage(page, mid, mCallBack);

    }
}
