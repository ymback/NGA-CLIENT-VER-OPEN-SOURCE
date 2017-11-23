package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import sp.phone.common.PreferenceKey;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.fragment.material.ArticleListFragment;
import sp.phone.fragment.material.ArticleListReplyFragment;
import sp.phone.fragment.material.ArticleTabFragment;
import sp.phone.utils.StringUtils;

/**
 * 帖子详情页, 是否MD都用这个
 */
public class ArticleListActivity extends SwipeBackAppCompatActivity implements PreferenceKey {

    private static final String TAG = "ArticleListActivity";

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(ArticleListFragment.class.getSimpleName());
        if (fragment == null) {
            ArticleListParam param = getArticleListParam();
            if (param.getFromReplyActivity() == 0) {
                fragment = new ArticleTabFragment();
            } else {
                fragment = new ArticleListReplyFragment();
            }
            fragment.setHasOptionsMenu(true);
            Bundle bundle = new Bundle();
            bundle.putParcelable("articleListParam", getArticleListParam());
            fragment.setArguments(bundle);
            fm.beginTransaction().replace(android.R.id.content, fragment, ArticleTabFragment.class.getSimpleName()).commit();
        }
        fragment.setHasOptionsMenu(true);
    }

    private ArticleListParam getArticleListParam() {
        ArticleListParam articleListParam = new ArticleListParam();
        int tid;
        int pid;
        int authorId;
        int pageFromUrl = 0;
        String url = getIntent().getDataString();
        if (null != url) {
            tid = StringUtils.getUrlParameter(url, "tid");
            pid = StringUtils.getUrlParameter(url, "pid");
            authorId = StringUtils.getUrlParameter(url, "authorid");
            pageFromUrl = StringUtils.getUrlParameter(url, "page");
        } else {
            tid = getIntent().getIntExtra("tid", 0);
            pid = getIntent().getIntExtra("pid", 0);
            authorId = getIntent().getIntExtra("authorid", 0);
        }

        int fromReplyActivity = getIntent().getIntExtra("searchpost", 0);
        if (authorId != 0) {
            fromReplyActivity = 1;
        }
        articleListParam.setTid(tid);
        articleListParam.setPageFromUrl(pageFromUrl);
        articleListParam.setPid(pid);
        articleListParam.setAuthorId(authorId);
        articleListParam.setFromReplyActivity(fromReplyActivity);
        return articleListParam;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragment();
        if (getIntent().getStringExtra("title") != null) {
            setTitle(getIntent().getStringExtra("title"));
        }
    }


}
