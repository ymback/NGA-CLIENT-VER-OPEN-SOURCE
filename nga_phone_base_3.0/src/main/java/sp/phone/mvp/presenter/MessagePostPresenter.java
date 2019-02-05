package sp.phone.mvp.presenter;

import gov.anzong.androidnga.R;
import sp.phone.forumoperation.MessagePostParam;
import sp.phone.fragment.MessagePostFragment;
import sp.phone.mvp.contract.MessagePostContract;
import sp.phone.mvp.model.MessagePostModel;
import sp.phone.task.MessagePostTask;
import sp.phone.util.ActivityUtils;

/**
 * Created by Justwen on 2017/5/28.
 */

public class MessagePostPresenter extends BasePresenter<MessagePostFragment, MessagePostModel> implements MessagePostContract.Presenter, MessagePostTask.CallBack {

    private final static Object COMMIT_LOCK = new Object();

    private boolean mLoading;

    private MessagePostParam mPostParam;

    @Override
    public void commit(String title, String to, String body) {
        synchronized (COMMIT_LOCK) {
            if (mLoading) {
                mBaseView.showToast(R.string.avoidWindfury);
                return;
            }
            mLoading = true;
        }
        mPostParam.setRecipient(to);
        mPostParam.setPostSubject(title);
        mPostParam.setPostContent(body);
        mBaseModel.postMessage(mPostParam, this);
    }

    @Override
    public void onViewCreated() {
        if (isNewMessage()) {
            mBaseView.setRecipient(mPostParam.getRecipient());
        }
        super.onViewCreated();
    }

    @Override
    public void setPostParam(MessagePostParam param) {
        mPostParam = param;
    }

    @Override
    public void onMessagePostFinished(boolean result, String resultInfo) {
        if (resultInfo != null && mBaseView != null) {
            mBaseView.showToast(resultInfo);
        }
        ActivityUtils.getInstance().dismiss();
        if (result && mBaseView != null) {
            if (!isNewMessage()) {
                mBaseView.finish(123);
            } else {
                mBaseView.finish(321);
            }
        }
        synchronized (COMMIT_LOCK) {
            mLoading = false;
        }
    }

    @Override
    public void onResume() {
        if (!isNewMessage()) {
            mBaseView.hideRecipientEditor();
        }
    }

    private boolean isNewMessage() {
        return mPostParam.getAction().equals("new");
    }

    @Override
    protected MessagePostModel onCreateModel() {
        return new MessagePostModel();
    }
}
