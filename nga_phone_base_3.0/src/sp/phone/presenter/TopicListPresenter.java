package sp.phone.presenter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.bean.TopicListInfo;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.model.TopicListModel;
import sp.phone.presenter.contract.tmp.TopicListContract;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtils;

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
            } else if (data.get__SEARCHNORESULT()) {
                mTopicView.showToast("结果已搜索完毕");
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
    public void removeTopic(String tidArray, final int position) {
        mTopicModel.removeTopic(tidArray, new OnHttpCallBack<String>() {
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
        mTopicModel.loadTopicList(getUrl(page, requestInfo), mCallBack);
    }

    @Override
    public void loadNextPage(int page, TopicListParam requestInfo) {
        mTopicView.setRefreshing(true);
        mTopicModel.loadTopicList(getUrl(page, requestInfo), mNextPageCallBack);
    }

    private String getUrl(int page, TopicListParam requestInfo) {
        StringBuilder jsonUri = new StringBuilder(HttpUtil.Server + "/thread.php?");
        if (0 != requestInfo.authorId) {
            jsonUri.append("authorid=").append(requestInfo.authorId).append("&");
        }
        if (requestInfo.searchPost != 0) {
            jsonUri.append("searchpost=").append(requestInfo.searchPost).append("&");
        }
        if (requestInfo.favor != 0) {
            jsonUri.append("favor=").append(requestInfo.favor).append("&");
        }
        if (requestInfo.content != 0) {
            jsonUri.append("content=").append(requestInfo.content).append("&");
        }

        if (!StringUtils.isEmpty(requestInfo.author)) {
            try {
                if (requestInfo.author.endsWith("&searchpost=1")) {
                    jsonUri.append("author=").append(URLEncoder.encode(
                            requestInfo.author.substring(0, requestInfo.author.length() - 13),
                            "GBK")).append("&searchpost=1&");
                } else {
                    jsonUri.append("author=").append(URLEncoder.encode(requestInfo.author, "GBK")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            if (0 != requestInfo.fid) {
                jsonUri.append("fid=").append(requestInfo.fid).append("&");
            }
            if (!StringUtils.isEmpty(requestInfo.key)) {
                jsonUri.append("key=").append(StringUtils.encodeUrl(requestInfo.key, "UTF-8")).append("&");
            }
            if (!StringUtils.isEmpty(requestInfo.fidGroup)) {
                jsonUri.append("fidgroup=").append(requestInfo.fidGroup).append("&");
            }
        }
        jsonUri.append("page=").append(page).append("&lite=js&noprefix");
        if (requestInfo.category == 1) {
            jsonUri.append("&recommend=1&order_by=postdatedesc&user=1");
        }
        return jsonUri.toString();
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
