package sp.phone.presenter;

import android.content.Intent;
import android.os.Bundle;

import java.util.Set;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.model.ArticleListTask;
import sp.phone.presenter.contract.tmp.ArticleListContract;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.ResourceUtils;
import sp.phone.utils.StringUtils;

import static gov.anzong.androidnga.R.string.page;

/**
 * Created by Justwen on 2017/11/22.
 */

public class ArticleListPresenter implements ArticleListContract.Presenter {

    private ArticleListContract.View mArticleView;

    private ArticleListTask mTask;

    public ArticleListPresenter() {
        mTask = new ArticleListTask();
    }

    @Override
    public void attachView(ArticleListContract.View view) {
        mArticleView = view;
    }

    @Override
    public void detachView() {
        mArticleView = null;
    }

    @Override
    public boolean isAttached() {
        return mArticleView != null;
    }

    @Override
    public void loadPage(ArticleListParam param) {
        mArticleView.setRefreshing(true);
        mTask.loadPage(ResourceUtils.getContext(), param, new OnThreadPageLoadFinishedListener() {
            @Override
            public void finishLoad(ThreadData data) {
                if (isAttached()) {
                    mArticleView.hideLoadingView();
                    mArticleView.setRefreshing(false);
                    mArticleView.setData(data);
                }
            }
        });
    }

    @Override
    public void banThisSB(ThreadRowInfo row) {
        if (row.getISANONYMOUS()) {
            mArticleView.showToast(R.string.cannot_add_to_blacklist_cause_anony);
        } else {
            Set<Integer> blacklist = PhoneConfiguration.getInstance().blacklist;
            String blackListString;
            if (row.get_isInBlackList()) {// 在屏蔽列表中，需要去除
                row.set_IsInBlackList(false);
                blacklist.remove(row.getAuthorid());
                mArticleView.showToast(R.string.remove_from_blacklist_success);
            } else {
                row.set_IsInBlackList(true);
                blacklist.add(row.getAuthorid());
                mArticleView.showToast(R.string.add_to_blacklist_success);
            }
            PhoneConfiguration.getInstance().blacklist = blacklist;
            blackListString = blacklist.toString();
            if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().uid)) {
                UserManagerImpl.getInstance().setBlackList(blackListString);
            } else {
                mArticleView.showToast(R.string.cannot_add_to_blacklist_cause_logout);
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
        String tidStr = String.valueOf(param.getTid());
        if (row.getPid() != 0) {
            postPrefix.append("[quote][pid=")
                    .append(row.getPid())
                    .append(',').append(tidStr).append(",").append(param.getPageFromUrl())
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
        bundle.putInt("tid", param.getTid());

        String prefix = StringUtils.removeBrTag(postPrefix.toString());
        if (!StringUtils.isEmpty(prefix)) {
            prefix = prefix + "\n";
        }
        mArticleView.showPostCommentDialog(prefix, bundle);
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
        String tidStr = String.valueOf(param.getTid());
        if (row.getPid() != 0) {
            mention = name;
            postPrefix.append("[quote][pid=")
                    .append(row.getPid())
                    .append(',').append(tidStr).append(",").append(page)
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
        mArticleView.startPostActivity(intent);
    }
}
