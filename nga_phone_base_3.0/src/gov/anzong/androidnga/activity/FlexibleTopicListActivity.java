package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import gov.anzong.androidnga.R;
import sp.phone.common.BoardManager;
import sp.phone.common.BoardManagerImpl;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.fragment.material.TopicListFragment;
import sp.phone.task.CheckReplyNotificationTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

/**
 * 帖子列表
 */
public class FlexibleTopicListActivity extends SwipeBackAppCompatActivity {

    private static String TAG = FlexibleTopicListActivity.class.getSimpleName();

    private boolean fromreplyactivity = false;

    private CheckReplyNotificationTask asynTask;

    private TopicListParam mRequestParam;

    private Menu mOptionMenu;

    private BoardManager mBoardManager;

    private TopicListParam getRequestParam() {
        Bundle bundle = getIntent().getExtras();

        TopicListParam requestParam = null;
        if (bundle != null) {
            requestParam = bundle.getParcelable("requestParam");
        }

        if (requestParam != null) {
            return requestParam;
        } else {
            requestParam = new TopicListParam();
        }

        String url = getIntent().getDataString();

        if (url != null) {
            requestParam.fid = StringUtils.getUrlParameter(url, "fid");
            requestParam.authorId = StringUtils.getUrlParameter(url, "authorid");
            requestParam.searchPost = StringUtils.getUrlParameter(url, "searchpost");
            requestParam.favor = StringUtils.getUrlParameter(url, "favor");
            requestParam.key = StringUtils.getStringBetween(url, 0, "key=", "&").result;
            requestParam.author = StringUtils.getStringBetween(url, 0, "author=", "&").result;
            requestParam.fidGroup = StringUtils.getStringBetween(url, 0, "fidgroup=", "&").result;
            requestParam.searchMode = false;
            requestParam.content = StringUtils.getUrlParameter(url, "content");
            requestParam.boardName = mBoardManager.getBoardName(String.valueOf(requestParam.fid));

        } else if (bundle != null) {
            requestParam.fid = bundle.getInt("fid", 0);
            requestParam.authorId = bundle.getInt("authorid", 0);
            requestParam.content = bundle.getInt("content", 0);
            requestParam.searchPost = bundle.getInt("searchpost", 0);
            requestParam.favor = bundle.getInt("favor", 0);
            requestParam.key = bundle.getString("key");
            requestParam.author = bundle.getString("author");
            requestParam.fidGroup = bundle.getString("fidgroup");
            if (!StringUtils.isEmpty(bundle.getString("searchmode"))) {
                if ("true".equals(bundle.getString("searchmode")))
                    requestParam.searchMode = true;
            }
            requestParam.boardName = bundle.getString("board_name");
            if (TextUtils.isEmpty(requestParam.boardName)) {
                requestParam.boardName = mBoardManager.getBoardName(String.valueOf(requestParam.fid));
            }
        }
        return requestParam;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBoardManager = BoardManagerImpl.getInstance();
        mRequestParam = getRequestParam();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);
        setupActionBar();
        setupTitle();
        setupFragment();

//        if (authorid > 0 || searchpost > 0 || favor > 0
//                || !StringUtils.isEmpty(key) || !StringUtils.isEmpty(author)
//                || !StringUtils.isEmpty(fidgroup)) {//!StringUtils.isEmpty(table) ||
//            fromreplyactivity = true;
//        }

    }

    private void setupFragment() {
        Fragment fragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("requestParam", mRequestParam);
        bundle.putBoolean("normal", isNormalTopicList());
        fragment.setArguments(bundle);
        fragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void setupTitle() {
        if (mRequestParam.favor != 0) {
            setTitle(R.string.bookmark_title);
        } else if (!StringUtils.isEmpty(mRequestParam.key)) {
            if (mRequestParam.content == 1) {
                if (!StringUtils.isEmpty(mRequestParam.fidGroup)) {
                    setTitle("搜索全站(包含正文):" + mRequestParam.key);
                } else {
                    setTitle("搜索(包含正文):" + mRequestParam.key);
                }
            } else {
                if (!StringUtils.isEmpty(mRequestParam.fidGroup)) {
                    setTitle("搜索全站:" + mRequestParam.key);
                } else {
                    setTitle("搜索:" + mRequestParam.key);
                }
            }
        } else if (!StringUtils.isEmpty(mRequestParam.author)) {
            if (mRequestParam.searchPost > 0) {
                final String title = "搜索" + mRequestParam.author + "的回复";
                setTitle(title);
            } else {
                final String title = "搜索" + mRequestParam.author + "的主题";
                setTitle(title);
            }
        } else if (mRequestParam.category == 1) {
            setTitle(mRequestParam.boardName + " - 精华区");
        } else {
            setTitle(mRequestParam.boardName);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.menu_add_bookmark) != null) {
            if (mBoardManager.isBookmarkBoard(String.valueOf(mRequestParam.fid))) {
                menu.findItem(R.id.menu_add_bookmark).setVisible(false);
                menu.findItem(R.id.menu_remove_bookmark).setVisible(true);
            } else {
                menu.findItem(R.id.menu_add_bookmark).setVisible(true);
                menu.findItem(R.id.menu_remove_bookmark).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isNormalTopicList() {
        return mRequestParam.category == 0
                && mRequestParam.key == null
                && mRequestParam.favor == 0
                && mRequestParam.author == null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isNormalTopicList()) {
            getMenuInflater().inflate(R.menu.topic_list_menu, menu);
            mOptionMenu = menu;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_add_bookmark:
                mBoardManager.addBookmark(String.valueOf(mRequestParam.fid), mRequestParam.boardName);
                item.setVisible(false);
                mOptionMenu.findItem(R.id.menu_remove_bookmark).setVisible(true);
                showToast(R.string.toast_add_bookmark_board);
                break;
            case R.id.menu_remove_bookmark:
                mBoardManager.removeBookmark(String.valueOf(mRequestParam.fid));
                item.setVisible(false);
                mOptionMenu.findItem(R.id.menu_add_bookmark).setVisible(true);
                showToast(R.string.toast_remove_bookmark_board);
                break;
            case R.id.menu_favorite:
                ActivityUtils.startFavoriteTopicActivity(this);
                break;
            case R.id.menu_recommend:
                showRecommendTopicList();
                break;
            case R.id.menu_search:
                Bundle bundle = new Bundle();
                bundle.putInt("id", mRequestParam.fid);
                bundle.putInt("authorid", mRequestParam.authorId);
                ActivityUtils.startSearchDialog(this, bundle);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showRecommendTopicList() {
        TopicListParam param = getRequestParam();
        param.category = 1;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("requestParam", param);
        intent.putExtras(bundle);
        ActivityUtils.startRecommendTopicActivity(this, intent);
    }

    @Override
    protected void onResume() {

        if (asynTask != null) {
            asynTask.cancel(true);
            asynTask = null;
        }
        long now = System.currentTimeMillis();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        if (now - config.lastMessageCheck > 30 * 1000 && config.notification) {// 30秒才爽啊艹
            NLog.d(TAG, "start to check Reply Notification");
            asynTask = new CheckReplyNotificationTask(this);
            asynTask.execute(config.getCookie());
        }
        super.onResume();
    }


}
