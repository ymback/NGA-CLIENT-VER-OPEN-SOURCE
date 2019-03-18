package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.alibaba.android.arouter.facade.annotation.Route;

import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.common.PreferenceKey;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.fragment.ArticleSearchFragment;
import sp.phone.fragment.ArticleTabFragment;
import sp.phone.util.StringUtils;

/**
 * 帖子详情页, 是否MD都用这个
 */
@Route(path = ARouterConstants.ACTIVITY_TOPIC_CONTENT)
public class ArticleListActivity extends BaseActivity implements PreferenceKey {

    private ArticleListParam mRequestParam;

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(android.R.id.content);

        if (fragment == null) {
            if (mRequestParam.searchPost == 0) {
                fragment = new ArticleTabFragment();
            } else {
                fragment = new ArticleSearchFragment();
            }
            fragment.setHasOptionsMenu(true);
            Bundle bundle = new Bundle();
            bundle.putParcelable(ParamKey.KEY_PARAM, mRequestParam);
            fragment.setArguments(bundle);
            fm.beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }

    private ArticleListParam getArticleListParam() {

        Bundle bundle = getIntent().getExtras();
        String url = getIntent().getDataString();
        ArticleListParam param = null;
        if (url != null) {
            param = new ArticleListParam();
            param.tid = StringUtils.getUrlParameter(url, "tid");
            param.pid = StringUtils.getUrlParameter(url, "pid");
            param.authorId = StringUtils.getUrlParameter(url, "authorid");
            param.page = StringUtils.getUrlParameter(url, "page");
            param.searchPost = StringUtils.getUrlParameter(url,ParamKey.KEY_SEARCH_POST);
        } else if (bundle != null) {
            param = bundle.getParcelable(ParamKey.KEY_PARAM);
            if (param == null) {
                param = new ArticleListParam();
                param.tid = bundle.getInt(ParamKey.KEY_TID, 0);
                param.pid = bundle.getInt(ParamKey.KEY_PID, 0);
                param.authorId = bundle.getInt(ParamKey.KEY_AUTHOR_ID, 0);
                param.searchPost = bundle.getInt(ParamKey.KEY_SEARCH_POST, 0);
                param.title = bundle.getString(ParamKey.KEY_TITLE);
            }
        }

        return param;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        mRequestParam = getArticleListParam();
        super.onCreate(savedInstanceState);
        setupFragment();
        if (mRequestParam.title != null) {
            setTitle(mRequestParam.title);
        }
    }


}
