package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.alibaba.android.arouter.facade.annotation.Route;

import sp.phone.common.PreferenceKey;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.fragment.material.ArticleListFragment;
import sp.phone.fragment.material.ArticleListReplyFragment;
import sp.phone.fragment.material.ArticleTabFragment;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;

/**
 * 帖子详情页, 是否MD都用这个
 */
@Route(path = ActivityUtils.PATH_ARTICLE_LIST)
public class ArticleListActivity extends SwipeBackAppCompatActivity implements PreferenceKey {

    private ArticleListParam mRequestParam;

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(ArticleListFragment.class.getSimpleName());

        if (fragment == null) {
            if (mRequestParam.searchPost == 0) {
                fragment = new ArticleTabFragment();
            } else {
                fragment = new ArticleListReplyFragment();
            }
            fragment.setHasOptionsMenu(true);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ParamKey.KEY_PARAM, mRequestParam);
            fragment.setArguments(bundle);
            fm.beginTransaction().replace(android.R.id.content, fragment, ArticleTabFragment.class.getSimpleName()).commit();
        }
    }

    private ArticleListParam getArticleListParam() {

        Bundle bundle = getIntent().getExtras();
        ArticleListParam param;

        if (bundle != null) {
            param = bundle.getParcelable(ParamKey.KEY_PARAM);
            if (param == null) {
                param = new ArticleListParam();
                param.tid = bundle.getInt(ParamKey.KEY_TID, 0);
                param.pid = bundle.getInt(ParamKey.KEY_PID, 0);
                param.authorId = bundle.getInt(ParamKey.KEY_AUTHOR_ID, 0);
                param.searchPost = bundle.getInt(ParamKey.KEY_SEARCH_POST, 0);
                param.title = bundle.getString(ParamKey.KEY_TITLE);
            }
        } else {
            String url = getIntent().getDataString();
            param = new ArticleListParam();
            if (url != null) {
                param.tid = StringUtils.getUrlParameter(url, "tid");
                param.pid = StringUtils.getUrlParameter(url, "pid");
                param.authorId = StringUtils.getUrlParameter(url, "authorid");
                param.page = StringUtils.getUrlParameter(url, "page");
            }
        }

        return param;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRequestParam = getArticleListParam();
        super.onCreate(savedInstanceState);
        setupFragment();
        if (mRequestParam.title != null) {
            setTitle(mRequestParam.title);
        }
    }


}
