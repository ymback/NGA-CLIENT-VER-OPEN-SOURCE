package sp.phone.presenter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.bean.TopicListInfo;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.presenter.contract.tmp.TopicListContract;
import sp.phone.task.DeleteBookmarkTask;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtils;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public class TopicListPresenter implements TopicListContract.Presenter {

    private TopicListContract.View mTopicView;

    private OnTopListLoadFinishedListener mLoadFinishedListener = new OnTopListLoadFinishedListener() {
        @Override
        public void jsonFinishLoad(TopicListInfo result) {
            if (!isAttached()) {
                return;
            } else if (result.get__SEARCHNORESULT()) {
                mTopicView.showToast("结果已搜索完毕");
                return;
            }
            mTopicView.clearData();
            mTopicView.scrollTo(0);
            setData(result);
        }

        @Override
        public void onListLoadFailed() {
            if (!isAttached()) {
                return;
            }
            mTopicView.setRefreshing(false);
        }
    };

    private OnTopListLoadFinishedListener mNextPageLoadFinishedListener = new OnTopListLoadFinishedListener() {
        @Override
        public void jsonFinishLoad(TopicListInfo result) {
            if (!isAttached()) {
                return;
            }
            setData(result);
        }

        @Override
        public void onListLoadFailed() {
            if (!isAttached()) {
                return;
            }
            mTopicView.setRefreshing(false);
            mTopicView.setNextPageEnabled(false);
        }
    };

    private void setData(TopicListInfo result) {
        mTopicView.setRefreshing(false);
        mTopicView.setData(result);
    }


    public TopicListPresenter() {

    }

    @Override
    public void removeBookmark(String tidId, int position) {
        DeleteBookmarkTask task = new DeleteBookmarkTask(
                mTopicView.getContext(), mTopicView.getTopicListView(), position);
        task.execute(tidId);
    }

    @Override
    public void loadPage(int page, TopicListParam requestInfo) {
        mTopicView.setRefreshing(true);
        JsonTopicListLoadTask task = new JsonTopicListLoadTask(
                mTopicView.getContext(), mLoadFinishedListener);
        task.executeOnExecutor(JsonTopicListLoadTask.THREAD_POOL_EXECUTOR,
                getUrl(page, requestInfo));
    }

    @Override
    public void loadNextPage(int page, TopicListParam requestInfo) {
        mTopicView.setRefreshing(true);
        JsonTopicListLoadTask task = new JsonTopicListLoadTask(
                mTopicView.getContext(), mNextPageLoadFinishedListener);
        task.executeOnExecutor(JsonTopicListLoadTask.THREAD_POOL_EXECUTOR,
                getUrl(page, requestInfo));
    }

    private String getUrl(int page, TopicListParam requestInfo) {
        String jsonUri = HttpUtil.Server + "/thread.php?";
        if (0 != requestInfo.authorId) {
            jsonUri += "authorid=" + requestInfo.authorId + "&";
        }
        if (requestInfo.searchPost != 0) {
            jsonUri += "searchpost=" + requestInfo.searchPost + "&";
        }
        if (requestInfo.favor != 0) {
            jsonUri += "favor=" + requestInfo.favor + "&";
        }
        if (requestInfo.content != 0) {
            jsonUri += "content=" + requestInfo.content + "&";
        }

        if (!StringUtils.isEmpty(requestInfo.author)) {
            try {
                if (requestInfo.author.endsWith("&searchpost=1")) {
                    jsonUri += "author="
                            + URLEncoder.encode(
                            requestInfo.author.substring(0, requestInfo.author.length() - 13),
                            "GBK") + "&searchpost=1&";
                } else {
                    jsonUri += "author="
                            + URLEncoder.encode(requestInfo.author, "GBK") + "&";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            if (0 != requestInfo.fid) {
                jsonUri += "fid=" + requestInfo.fid + "&";
            }
            if (!StringUtils.isEmpty(requestInfo.key)) {
                jsonUri += "key=" + StringUtils.encodeUrl(requestInfo.key, "UTF-8") + "&";
            }
            if (!StringUtils.isEmpty(requestInfo.fidGroup)) {
                jsonUri += "fidgroup=" + requestInfo.fidGroup + "&";
            }
        }
        jsonUri += "page=" + page + "&lite=js&noprefix";
        if (requestInfo.category == 1) {
            jsonUri += "&recommend=1&order_by=postdatedesc&user=1";
        }
        return jsonUri;
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
