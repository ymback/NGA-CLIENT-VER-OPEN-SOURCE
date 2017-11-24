package sp.phone.presenter;

import sp.phone.model.entity.ThreadPageInfo;
import sp.phone.model.entity.TopicListInfo;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.model.TopicListModel;
import sp.phone.presenter.contract.tmp.TopicListContract;

/**
 * Created by Justwen on 2017/6/3.
 */

public class TopicListPresenter implements TopicListContract.Presenter {

    private TopicListContract.View mTopicView;

    private TopicListContract.Model mTopicModel;

    private OnHttpCallBack<TopicListInfo> mCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mTopicView.setRefreshing(false);
                mTopicView.showToast(text);
                mTopicView.hideLoadingView();
            }
        }

        @Override
        public void onSuccess(TopicListInfo data) {
            if (!isAttached()) {
                return;
            }
            mTopicView.clearData();
            mTopicView.scrollTo(0);
            setData(data);
            mTopicView.hideLoadingView();
        }
    };

    private OnHttpCallBack<TopicListInfo> mNextPageCallBack = new OnHttpCallBack<TopicListInfo>() {
        @Override
        public void onError(String text) {
            if (isAttached()) {
                mTopicView.setRefreshing(false);
                mTopicView.setNextPageEnabled(false);
                mTopicView.showToast(text);
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
        mTopicView.setRefreshing(false);
        mTopicView.setData(result);
    }


    public TopicListPresenter() {
        mTopicModel = new TopicListModel();
    }

    @Override
    public void removeTopic(ThreadPageInfo info, final int position) {
        mTopicModel.removeTopic(info, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                if (isAttached()) {
                    mTopicView.showToast(text);
                }
            }

            @Override
            public void onSuccess(String data) {
                if (isAttached()) {
                    mTopicView.showToast(data);
                    mTopicView.removeTopic(position);
                }
            }
        });
    }

    @Override
    public void loadPage(int page, TopicListParam requestInfo) {
        mTopicView.setRefreshing(true);
        mTopicModel.loadTopicList(page, requestInfo, mCallBack);
    }

    @Override
    public void loadNextPage(int page, TopicListParam requestInfo) {
        mTopicView.setRefreshing(true);
        mTopicModel.loadTopicList(page, requestInfo, mNextPageCallBack);
    }



    @Override
    public void attachView(TopicListContract.View view) {
        mTopicView = view;
    }

    @Override
    public void detachView() {
        mTopicView = null;
    }

    @Override
    public boolean isAttached() {
        return mTopicView != null;
    }
}
