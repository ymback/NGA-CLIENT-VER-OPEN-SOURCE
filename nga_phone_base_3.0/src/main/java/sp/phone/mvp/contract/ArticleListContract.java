package sp.phone.mvp.contract;

import android.content.Intent;
import android.os.Bundle;

import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/11/22.
 */

public interface ArticleListContract {

    interface Presenter {

        void loadPage(ArticleListParam param);

        void banThisSB(ThreadRowInfo row);

        void postComment(ArticleListParam param, ThreadRowInfo row);

        void postSupportTask(int tid, int pid);

        void postOpposeTask(int tid, int pid);

        void quote(ArticleListParam param, ThreadRowInfo row);
    }

    interface View {

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

        void hideLoadingView();

        void setData(ThreadData data);

        void startPostActivity(Intent intent);

        void showPostCommentDialog(String prefix, Bundle bundle);

    }

    interface Model {

        void loadPage(ArticleListParam param, OnHttpCallBack<ThreadData> callBack);
    }
}
