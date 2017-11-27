package sp.phone.lab.mvp.presenter;

import sp.phone.forumoperation.TopicListParam;
import sp.phone.fragment.material.TopicListFragment;
import sp.phone.lab.mvp.contract.TopicListContract;
import sp.phone.lab.mvp.model.TopicListModel;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.model.entity.ThreadPageInfo;
import sp.phone.model.entity.TopicListInfo;

/**
 * Created by Justwen on 2017/6/3.
 */

public class TopicListPresenter extends BasePresenter<TopicListFragment, TopicListModel> implements TopicListContract.Presenter {

    private OnHttpCallBack<TopicListInfo> mCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.showToast(text);
                mBaseView.hideLoadingView();
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            mBaseView.clearData();
            mBaseView.scrollTo(0);
            setData(data);
            mBaseView.hideLoadingView();
        }
    };

    private OnHttpCallBack<TopicListInfo> mNextPageCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mBaseView.setRefreshing(false);
                mBaseView.setNextPageEnabled(false);
                mBaseView.showToast(text);
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            setData(data);
        }
    };

    private void setData(TopicListInfo result) {
        mBaseView.setRefreshing(false);
        mBaseView.setData(result);
    }

    @Override
    protected TopicListModel onCreateModel() {
        return new TopicListModel();
    }

    @Override
    public void removeTopic(ThreadPageInfo info, final int position) {
        mBaseModel.removeTopic(info, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                if (isAttached()) {
                    mBaseView.showToast(text);
                }
            }

            @Override
            public void onSuccess(String data) {
                if (isAttached()) {
                    mBaseView.showToast(data);
                    mBaseView.removeTopic(position);
                }
            }
        });
    }

    @Override
    public void loadPage(int page, TopicListParam requestInfo) {
        mBaseView.setRefreshing(true);
        mBaseModel.loadTopicList(page, requestInfo, mCallBack);
    }

    @Override
    public void loadNextPage(int page, TopicListParam requestInfo) {
        mBaseView.setRefreshing(true);
        mBaseModel.loadTopicList(page, requestInfo, mNextPageCallBack);
    }

}
