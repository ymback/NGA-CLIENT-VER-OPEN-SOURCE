package sp.phone.presenter;

import android.support.annotation.StringRes;

import sp.phone.bean.MessageListInfo;
import sp.phone.interfaces.OnMessageListLoadFinishedListener;
import sp.phone.presenter.contract.tmp.MessageListContract;
import sp.phone.task.JsonMessageListLoadTask;
import sp.phone.utils.HttpUtil;

/**
 * Created by Justwen on 2017/10/9.
 */

public class MessageListPresenter implements MessageListContract.Presenter,OnMessageListLoadFinishedListener {

    private MessageListContract.View mView;

    @Override
    public void attachView(MessageListContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public boolean isAttached() {
        return mView != null;
    }

    @Override
    public void loadPage(int page) {
        JsonMessageListLoadTask task = new JsonMessageListLoadTask(mView.getContext(), this);
        task.execute(getUrl(page));
    }

    @Override
    public void loadNextPage(int page) {
        mView.setRefreshing(true);
        JsonMessageListLoadTask task = new JsonMessageListLoadTask(mView.getContext(), this);
        task.execute(getUrl(page));
    }

    private String getUrl(int page) {
        String jsonUri = HttpUtil.Server + "/nuke.php?__lib=message&__act=message&act=list&";
        jsonUri += "page=" + page + "&lite=js&noprefix";
        return jsonUri;
    }

    @Override
    public void showMessage(@StringRes int id) {
        if (!isAttached()) {
            return;
        }
        mView.showToast(id);
    }

    @Override
    public void showMessage(String text) {
        if (!isAttached()) {
            return;
        }
        mView.showToast(text);
    }

    @Override
    public void jsonfinishLoad(MessageListInfo result) {
        if (!isAttached()) {
            return;
        }
        mView.setRefreshing(false);
        mView.hideProgressBar();
        mView.setData(result);
    }
}
