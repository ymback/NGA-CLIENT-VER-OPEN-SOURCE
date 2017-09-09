package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.forumoperation.ArticleListAction;
import sp.phone.fragment.material.ArticleContainerFragment;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

/**
 * 帖子详情页, 是否MD都用这个
 */
public class ArticleListActivity extends SwipeBackAppCompatActivity implements PreferenceKey {

    private static final String TAG = "ArticleListActivity";

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(ArticleContainerFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new ArticleContainerFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("ArticleListAction",getArticleListAction());
            fragment.setArguments(bundle);
            fm.beginTransaction().replace(R.id.container,fragment,ArticleContainerFragment.class.getSimpleName()).commit();
        }
        fragment.setHasOptionsMenu(true);
    }

    private ArticleListAction getArticleListAction() {
        ArticleListAction articleListAction = new ArticleListAction();
        int tid;
        int pid;
        int authorId;
        int pageFromUrl = 0;
        String url = getIntent().getDataString();
        if (null != url) {
            tid = getUrlParameter(url, "tid");
            pid = getUrlParameter(url, "pid");
            authorId = getUrlParameter(url, "authorid");
            pageFromUrl = getUrlParameter(url, "page");
        } else {
            tid = getIntent().getIntExtra("tid", 0);
            pid = getIntent().getIntExtra("pid", 0);
            authorId = getIntent().getIntExtra("authorid", 0);
        }

        int fromReplyActivity = getIntent().getIntExtra("fromreplyactivity", 0);
        if (authorId != 0) {
            fromReplyActivity = 1;
        }
        articleListAction.setTid(tid);
        articleListAction.setPageFromUrl(pageFromUrl);
        articleListAction.setPid(pid);
        articleListAction.setAuthorId(authorId);
        articleListAction.setFromReplyActivity(fromReplyActivity);
        return articleListAction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        setupActionBar((Toolbar) findViewById(R.id.toolbar));
        setupFragment();

    }

    private int getContentViewId() {
        if (PhoneConfiguration.getInstance().isShownBottomTab()) {
            return R.layout.activity_article_list_bottom_tab;
        } else {
            return R.layout.activity_article_list;
        }
    }

    private int getUrlParameter(String url, String paraName) {
        if (StringUtils.isEmpty(url)) {
            return 0;
        }
        final String pattern = paraName + "=";
        int start = url.indexOf(pattern);
        if (start == -1)
            return 0;
        start += pattern.length();
        int end = url.indexOf("&", start);
        if (end == -1)
            end = url.length();
        String value = url.substring(start, end);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        } catch (Exception e) {
            NLog.e(TAG, "invalid url:" + url);
        }

        return ret;
    }

//
//    @Override
//    public void finishLoad(ThreadData data) {
//        int exactCount = 1 + data.getThreadInfo().getReplies() / 20;
//        if (mTabsAdapter.getCount() != exactCount && this.authorid == 0) {
//            if (this.pid != 0)
//                exactCount = 1;
//            mTabsAdapter.setCount(exactCount);
//        }
//        if (this.authorid > 0) {
//            exactCount = 1 + data.get__ROWS() / 20;
//            mTabsAdapter.setCount(exactCount);
//        }
//        if (tid != data.getThreadInfo().getTid()) // mirror thread
//            tid = data.getThreadInfo().getTid();
//        fid = data.getThreadInfo().getFid();
//        getSupportActionBar().setTitle(
//                StringUtils.unEscapeHtml(data.getThreadInfo().getSubject()));
//
//        title = data.getThreadInfo().getSubject();
//
//        attacher.setRefreshComplete();
//    }

}
