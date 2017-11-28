package sp.phone.mvp.model;

import android.content.Context;

import sp.phone.forumoperation.ArticleListParam;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;

/**
 * 加载帖子内容
 * Created by Yang Yihang on 2017/7/10.
 */

public class ArticleListModel extends BaseModel implements ArticleListContract.Model {

    private static final String TAG = ArticleListModel.class.getSimpleName();

    public void loadPage(Context context, ArticleListParam action, OnThreadPageLoadFinishedListener listener) {
        int page = action.page;
        int tid = action.tid;
        int pid = action.pid;
        int authorId = action.authorId;

        NLog.d(TAG, "loadPage" + page);

        String url = getUrl(action);

        JsonThreadLoadTask task = new JsonThreadLoadTask(context, listener);
        task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
    }

    private String getUrl(ArticleListParam param) {
        int page = param.page;
        int tid = param.tid;
        int pid = param.pid;
        int authorId = param.authorId;
        String url = HttpUtil.Server + "/read.php?" + "&page=" + page + "&lite=js&noprefix&v2";
        if (tid != 0) {
            url = url + "&tid=" + tid;
        }
        if (pid != 0) {
            url = url + "&pid=" + pid;
        }

        if (authorId != 0) {
            url = url + "&authorid=" + authorId;
        }

        return url;

    }
}
