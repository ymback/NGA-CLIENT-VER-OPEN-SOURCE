package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.common.BoardManagerImpl;
import sp.phone.common.NotificationController;
import sp.phone.forumoperation.ParamKey;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.fragment.TopicFavoriteFragment;
import sp.phone.fragment.TopicListFragment;
import sp.phone.fragment.TopicSearchFragment;
import sp.phone.util.ActivityUtils;
import sp.phone.util.StringUtils;

/**
 * 帖子列表
 */
@Route(path = ARouterConstants.ACTIVITY_TOPIC_LIST)
public class TopicListActivity extends BaseActivity {

    private static String TAG = TopicListActivity.class.getSimpleName();

    private TopicListParam mRequestParam;

    private TopicListParam getRequestParam() {

        Bundle bundle = getIntent().getExtras();
        String url = getIntent().getDataString();
        TopicListParam requestParam = null;
        if (url != null) {
            requestParam = new TopicListParam();
            requestParam.authorId = StringUtils.getUrlParameter(url, "authorid");
            requestParam.searchPost = StringUtils.getUrlParameter(url, "searchpost");
            requestParam.favor = StringUtils.getUrlParameter(url, "favor");
            requestParam.key = StringUtils.getStringBetween(url, 0, "key=", "&").result;
            requestParam.author = StringUtils.getStringBetween(url, 0, "author=", "&").result;
            requestParam.fidGroup = StringUtils.getStringBetween(url, 0, "fidgroup=", "&").result;
            requestParam.content = StringUtils.getUrlParameter(url, "content");
            requestParam.fid = StringUtils.getUrlParameter(url, "fid");
        } else if (bundle != null) {
            requestParam = bundle.getParcelable(ParamKey.KEY_PARAM);
            if (requestParam == null) {
                requestParam = new TopicListParam();
                requestParam.fid = bundle.getInt(ParamKey.KEY_FID, 0);
                requestParam.authorId = bundle.getInt(ParamKey.KEY_AUTHOR_ID, 0);
                requestParam.content = bundle.getInt(ParamKey.KEY_CONTENT, 0);
                requestParam.searchPost = bundle.getInt(ParamKey.KEY_SEARCH_POST, 0);
                requestParam.favor = bundle.getInt(ParamKey.KEY_FAVOR, 0);
                requestParam.key = bundle.getString(ParamKey.KEY_KEY);
                requestParam.author = bundle.getString(ParamKey.KEY_AUTHOR);
                requestParam.fidGroup = bundle.getString(ParamKey.KEY_FID_GROUP);
                requestParam.title = bundle.getString(ParamKey.KEY_TITLE);
                requestParam.recommend = bundle.getInt(ParamKey.KEY_RECOMMEND, 0);
            }
        }

        if (requestParam != null && TextUtils.isEmpty(requestParam.title)) {
            requestParam.title = BoardManagerImpl.getInstance().getBoardName(String.valueOf(requestParam.fid));
        }
        return requestParam;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideActionBar();
        mRequestParam = getRequestParam();
        super.onCreate(savedInstanceState);
        setupFragment();
    }

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(android.R.id.content) == null) {
            Fragment fragment;
            if (mRequestParam.favor != 0) {
                fragment = new TopicFavoriteFragment();
            } else if (isBoardTopicList()) {
                fragment = new TopicListFragment();
            } else {
                fragment = new TopicSearchFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(ParamKey.KEY_PARAM, mRequestParam);
            fragment.setArguments(bundle);
            fragment.setHasOptionsMenu(true);
            fm.beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }

    private boolean isBoardTopicList() {
        return mRequestParam.recommend == 0
                && mRequestParam.key == null
                && mRequestParam.favor == 0
                && mRequestParam.authorId == 0
                && mRequestParam.author == null
                && mRequestParam.searchPost == 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_favorite:
                ActivityUtils.startFavoriteTopicActivity(this);
                break;
            case R.id.menu_recommend:
                showRecommendTopicList();
                break;
            case R.id.menu_search:
                Bundle bundle = new Bundle();
                bundle.putInt("fid", mRequestParam.fid);
                bundle.putInt("authorid", mRequestParam.authorId);
                ActivityUtils.startSearchDialog(this, bundle);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showRecommendTopicList() {
        TopicListParam param = (TopicListParam) mRequestParam.clone();
        param.recommend = 1;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ParamKey.KEY_PARAM, param);
        intent.putExtras(bundle);
        ActivityUtils.startRecommendTopicActivity(this, intent);
    }

    @Override
    protected void onResume() {
        NotificationController.getInstance().checkNotificationDelay();
        super.onResume();
    }


}
