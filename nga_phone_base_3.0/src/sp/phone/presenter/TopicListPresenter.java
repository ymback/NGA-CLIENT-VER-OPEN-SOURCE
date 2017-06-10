package sp.phone.presenter;

import android.content.Context;
import android.widget.AdapterView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gov.anzong.androidnga.activity.FlexibleTopicListActivity;
import sp.phone.bean.TopicListInfo;
import sp.phone.bean.TopicListRequestInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.presenter.contract.TopicListContract;
import sp.phone.task.DeleteBookmarkTask;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public class TopicListPresenter implements TopicListContract.Presenter{


    private TopicListContract.View mView;


    public TopicListPresenter(TopicListContract.View view) {
        view.setPresenter(this);
        mView = view;
    }

    @Override
    public void refresh() {
        TopicListRequestInfo requestInfo = mView.getTopicListRequestInfo();
        mView.setRefreshing(true);
        JsonTopicListLoadTask task = new JsonTopicListLoadTask(mView.getContext(), new OnTopListLoadFinishedListener() {
            @Override
            public void jsonfinishLoad(TopicListInfo result) {
                jsonFinishLoad(result);
            }
        });
        task.execute(getUrl(1,requestInfo));
    }

    @Override
    public void loadNextPage(OnTopListLoadFinishedListener callback) {
        JsonTopicListLoadTask task = new JsonTopicListLoadTask(mView.getContext(), callback);
        mView.setRefreshing(true);
        task.executeOnExecutor(JsonTopicListLoadTask.THREAD_POOL_EXECUTOR, getUrl(mView.getNextPage(), mView.getTopicListRequestInfo()));
    }

    @Override
    public void jsonFinishLoad(TopicListInfo result) {
        final TopicListRequestInfo requestInfo = mView.getTopicListRequestInfo();
        mView.setRefreshing(false);
        if (result == null) {
            return;
        }
        if (result.get__SEARCHNORESULT()) {
            mView.showToast("结果已搜索完毕");
            return;
        }
        int lines = 35;
        if (requestInfo.authorId != 0)
            lines = 20;
        int pageCount = result.get__ROWS() / lines;
        if (pageCount * lines < result.get__ROWS())
            pageCount++;

        if (requestInfo.searchPost != 0){ // can not get exact row counts
            int page = result.get__ROWS();
            pageCount = page;
            if (result.get__T__ROWS() == lines)
                pageCount++;
        }

        mView.setData(result);
    }

    @Override
    public void removeBookmark(String tidId,int position) {
        DeleteBookmarkTask task = new DeleteBookmarkTask(
                mView.getContext(), (AdapterView<?>) mView.getTopicListView(), position);
        task.execute(tidId);
    }

    public String getUrl(int page,TopicListRequestInfo requestInfo) {
        String jsonUri = HttpUtil.Server + "/thread.php?";
        if (0 != requestInfo.authorId)
            jsonUri += "authorid=" + requestInfo.authorId + "&";
        if (requestInfo.searchPost != 0)
            jsonUri += "searchpost=" + requestInfo.searchPost + "&";
        if (requestInfo.favor != 0)
            jsonUri += "favor=" + requestInfo.favor + "&";
        if (requestInfo.content != 0)
            jsonUri += "content=" + requestInfo.content + "&";

        if (!StringUtil.isEmpty(requestInfo.author)) {
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
            if (0 != requestInfo.fid)
                jsonUri += "fid=" + requestInfo.fid + "&";
            if (!StringUtil.isEmpty(requestInfo.key)) {
                jsonUri += "key=" + StringUtil.encodeUrl(requestInfo.key, "UTF-8") + "&";
            }
            if (!StringUtil.isEmpty(requestInfo.fidGroup)) {
                jsonUri += "fidgroup=" + requestInfo.fidGroup + "&";
            }
        }
        jsonUri += "page=" + page + "&lite=js&noprefix";
        switch (requestInfo.category) {
            case 2:
                jsonUri += "&recommend=1&order_by=postdatedesc&admin=1";
                break;
            case 1:
                jsonUri += "&recommend=1&order_by=postdatedesc&user=1";
                break;
            case 0:
            default:
        }

        return jsonUri;
    }

    @Override
    public Context getContext() {
        return null;
    }
}
