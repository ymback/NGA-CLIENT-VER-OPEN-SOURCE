package sp.phone.model;

import android.content.Context;

import sp.phone.forumoperation.ArticleListParam;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;

/**
 * 加载帖子内容
 * Created by Yang Yihang on 2017/7/10.
 */

public class ArticleListTask {

    private static final String TAG = ArticleListTask.class.getSimpleName();

    public void loadPage(Context context, ArticleListParam action, OnThreadPageLoadFinishedListener listener) {
        int page = action.getPageFromUrl();
        int tid = action.getTid();
        int pid = action.getPid();
        int authorId = action.getAuthorId();

        NLog.d(TAG, "loadPage" + page);

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

        JsonThreadLoadTask task = new JsonThreadLoadTask(context, listener);
        task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
    }
}
