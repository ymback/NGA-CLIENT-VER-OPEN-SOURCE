package sp.phone.mvp.presenter;

import android.content.Intent;
import android.os.Bundle;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.fragment.ArticleListFragment;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.mvp.model.ArticleListModel;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.rxjava.RxUtils;
import sp.phone.task.LikeTask;
import sp.phone.util.FunctionUtils;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2017/11/22.
 */

public class ArticleListPresenter extends BasePresenter<ArticleListFragment, ArticleListModel> implements ArticleListContract.Presenter {

    private LikeTask mLikeTask;

    @Override
    protected ArticleListModel onCreateModel() {
        return new ArticleListModel();
    }

    @Override
    public void loadPage(ArticleListParam param) {
        mBaseView.setRefreshing(true);
        mBaseModel.loadPage(param, new OnHttpCallBack<ThreadData>() {
            @Override
            public void onError(String text) {
                mBaseView.hideLoadingView();
                mBaseView.setRefreshing(false);
                mBaseView.showToast(text);
            }

            @Override
            public void onSuccess(ThreadData data) {
                mBaseView.setRefreshing(false);
                mBaseView.setData(data);
                RxUtils.postDelay(300, new BaseSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        if (mBaseView != null) {
                            mBaseView.hideLoadingView();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void banThisSB(ThreadRowInfo row) {
        if (row.getISANONYMOUS()) {
            mBaseView.showToast(R.string.cannot_add_to_blacklist_cause_anony);
        } else {
            UserManager um = UserManagerImpl.getInstance();
            if (row.get_isInBlackList()) {
                row.set_IsInBlackList(false);
                um.removeFromBlackList(String.valueOf(row.getAuthorid()));
                mBaseView.showToast(R.string.remove_from_blacklist_success);
            } else {
                row.set_IsInBlackList(true);
                um.addToBlackList(row.getAuthor(), String.valueOf(row.getAuthorid()));
                mBaseView.showToast(R.string.add_to_blacklist_success);
            }
        }
    }

    @Override
    public void postComment(ArticleListParam param, ThreadRowInfo row) {
        final String quoteRegex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
        final String replayRegex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
        StringBuilder postPrefix = new StringBuilder();
        String content = row.getContent()
                .replaceAll(quoteRegex, "")
                .replaceAll(replayRegex, "");
        final String postTime = row.getPostdate();
        content = FunctionUtils.checkContent(content);
        content = StringUtils.unEscapeHtml(content);
        final String name = row.getAuthor();
        final String uid = String.valueOf(row.getAuthorid());
        String tidStr = String.valueOf(param.tid);
        if (row.getPid() != 0) {
            postPrefix.append("[quote][pid=")
                    .append(row.getPid())
                    .append(',').append(tidStr).append(",").append(param.page)
                    .append("]")// Topic
                    .append("Reply");
            if (row.getISANONYMOUS()) {// 是匿名的人
                postPrefix.append("[/pid] [b]Post by [uid=")
                        .append("-1")
                        .append("]")
                        .append(name)
                        .append("[/uid][color=gray](")
                        .append(row.getLou())
                        .append("楼)[/color] (");
            } else {
                postPrefix.append("[/pid] [b]Post by [uid=")
                        .append(uid)
                        .append("]")
                        .append(name)
                        .append("[/uid] (");
            }
            postPrefix.append(postTime)
                    .append("):[/b]\n")
                    .append(content)
                    .append("[/quote]\n");
        }

        Bundle bundle = new Bundle();
        bundle.putInt("pid", row.getPid());
        bundle.putInt("fid", row.getFid());
        bundle.putInt("tid", param.tid);

        String prefix = StringUtils.removeBrTag(postPrefix.toString());
        if (!StringUtils.isEmpty(prefix)) {
            prefix = prefix + "\n";
        }
        mBaseView.showPostCommentDialog(prefix, bundle);
    }

    @Override
    public void postSupportTask(int tid, int pid) {
        if (mLikeTask == null) {
            mLikeTask = new LikeTask();
        }
        mLikeTask.execute(tid, pid, LikeTask.SUPPORT, data -> mBaseView.showToast(data));
    }

    @Override
    public void postOpposeTask(int tid, int pid) {
        if (mLikeTask == null) {
            mLikeTask = new LikeTask();
        }
        mLikeTask.execute(tid, pid, LikeTask.OPPOSE, data -> mBaseView.showToast(data));
    }

    @Override
    public void quote(ArticleListParam param, ThreadRowInfo row) {
        final String quoteRegex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
        final String replayRegex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
        StringBuilder postPrefix = new StringBuilder();
        String content = row.getContent()
                .replaceAll(quoteRegex, "")
                .replaceAll(replayRegex, "");
        final String postTime = row.getPostdate();
        String mention = null;
        final String name = row.getAuthor();
        final String uid = String.valueOf(row.getAuthorid());
        content = FunctionUtils.checkContent(content);
        content = StringUtils.unEscapeHtml(content);
        String tidStr = String.valueOf(param.tid);
        if (row.getPid() != 0) {
            mention = name;
            postPrefix.append("[quote][pid=")
                    .append(row.getPid())
                    .append(',').append(tidStr).append(",").append(param.page)
                    .append("]")// Topic
                    .append("Reply");
            if (row.getISANONYMOUS()) {// 是匿名的人
                postPrefix.append("[/pid] [b]Post by [uid=")
                        .append("-1")
                        .append("]")
                        .append(name)
                        .append("[/uid][color=gray](")
                        .append(row.getLou())
                        .append("楼)[/color] (");
            } else {
                postPrefix.append("[/pid] [b]Post by [uid=")
                        .append(uid)
                        .append("]")
                        .append(name)
                        .append("[/uid] (");
            }
            postPrefix.append(postTime)
                    .append("):[/b]\n")
                    .append(content)
                    .append("[/quote]\n");
        }

        Intent intent = new Intent();
        if (!StringUtils.isEmpty(mention)) {
            intent.putExtra("mention", mention);
        }
        intent.putExtra("prefix", StringUtils.removeBrTag(postPrefix.toString()));
        intent.putExtra("tid", tidStr);
        intent.putExtra("action", "reply");
        mBaseView.startPostActivity(intent);
    }
}
