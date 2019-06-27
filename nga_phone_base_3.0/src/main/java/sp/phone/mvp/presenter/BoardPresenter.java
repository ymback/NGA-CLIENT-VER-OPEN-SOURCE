package sp.phone.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.TopicListActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.util.ToastUtils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.User;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.model.BoardModel;
import sp.phone.mvp.model.entity.Board;
import sp.phone.mvp.model.entity.BoardCategory;
import sp.phone.param.ParamKey;
import sp.phone.ui.fragment.NavigationDrawerFragment;
import sp.phone.util.ARouterUtils;
import sp.phone.util.ActivityUtils;
import sp.phone.util.HttpUtil;
import sp.phone.util.NLog;
import sp.phone.util.StringUtils;

/**
 * 版块管理
 * Created by Justwen on 2017/6/29.
 */

public class BoardPresenter extends BasePresenter<NavigationDrawerFragment, BoardModel> implements BoardContract.Presenter {

    private UserManager mUserManager;

    public BoardPresenter() {
        super();
        mUserManager = UserManagerImpl.getInstance();
    }


    @Override
    public void loadBoardInfo() {

    }

    @Override
    public boolean addBoard(String fid, String name) {
        if (name.equals("")) {
            ToastUtils.showToast("请输入版面名称");
            return false;
        } else {
            Pattern pattern = Pattern.compile("-?[0-9]*");
            Matcher match = pattern.matcher(fid);
            boolean checkInt = true;
            try {
                Integer.parseInt(fid);
            } catch (NumberFormatException e) {
                checkInt = false;
            }
            if (!match.matches() || fid.equals("") || !checkInt) {
                ToastUtils.showToast("请输入正确的版面ID(个人版面要加负号)");
                return false;
            } else {// CHECK PASS, READY TO ADD FID
                for (int i = 0; i < mBaseModel.getCategorySize(); i++) {
                    BoardCategory curr = mBaseModel.getBoardCategory(i);
                    for (int j = 0; j < curr.size(); j++) {
                        String URL = String.valueOf(curr.getBoard(j).getFid());
                        if (URL.equals(fid)) {
                            ToastUtils.showToast("该版面已经存在于列表" + curr.get(j).getName() + "中");
                            return false;
                        }
                    }
                }
                ToastUtils.showToast("添加成功");
                mBaseModel.addBookmark(Integer.parseInt(fid), 0, name);
                return true;
            }
        }
    }

    @Override
    public void toggleUser(List<User> userList) {
        if (userList != null && userList.size() > 1) {
            int index = mBaseView.switchToNextUser();
            mUserManager.setActiveUser(index);
            ToastUtils.showToast("切换账户成功,当前账户名:" + mUserManager.getActiveUser().getNickName());
        } else {
            mBaseView.jumpToLogin();
        }
    }

    /**
     * 跳转到对应版块
     *
     * @param position
     * @param fidString
     */
    @Override
    public void toTopicListPage(int position, String fidString) {
        if (position != 0 && !HttpUtil.HOST_PORT.equals("")) {
            HttpUtil.HOST = HttpUtil.HOST_PORT + HttpUtil.Servlet_timer;
        }
        int fid = 0;
        try {
            fid = Integer.parseInt(fidString);
        } catch (Exception e) {
            final String tag = this.getClass().getSimpleName();
            NLog.e(tag, NLog.getStackTraceString(e));
            NLog.e(tag, "invalid fid " + fidString);
        }
        if (fid == 0) {
            String tip = fidString + "绝对不存在";
            ToastUtils.showToast(tip);
            return;
        }

        NLog.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

        String url = HttpUtil.Server + "/thread.php?fid=" + fidString + "&rss=1";
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        if (!StringUtils.isEmpty(config.getCookie())) {
            url = url + "&" + config.getCookie().replace("; ", "&");
        } else if (fid < 0) {
            mBaseView.jumpToLogin();
            return;
        }
        if (!StringUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.putExtra("tab", "1");
            intent.putExtra("fid", fid);
            intent.setClass(mBaseView.getContext(), config.topicActivityClass);
            mBaseView.getContext().startActivity(intent);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        loadBoardInfo();
        mBaseView.notifyDataSetChanged();
    }

    @Override
    public void clearRecentBoards() {
        mBaseModel.removeAllBookmarks();
        mBaseView.notifyDataSetChanged();
    }

    @Override
    public void startUserProfile(String userName) {
        ARouterUtils.build(ARouterConstants.ACTIVITY_PROFILE)
                .withString("mode", "username")
                .withString("username", userName)
                .navigation(mBaseView.getContext());
    }

    @Override
    public void startLogin() {
        ARouterUtils.build(ARouterConstants.ACTIVITY_LOGIN).navigation((Activity) mBaseView.getContext(), ActivityUtils.REQUEST_CODE_LOGIN);
    }

    @Override
    public BoardCategory getBookmarkCategory() {
        return mBaseModel.getBookmarkCategory();
    }

    @Override
    public List<BoardCategory> getBoardCategories() {
        return mBaseModel.getBoardCategories();
    }

    @Override
    public void clearAllBookmarkBoards() {
        mBaseModel.removeAllBookmarks();
        mBaseView.notifyDataSetChanged();
    }

    @Override
    public void swapBookmarkBoard(int from, int to) {
        mBaseModel.swapBookmark(from, to);
    }

    @Override
    public void addBookmarkBoard(int fid, int stid, String name) {
        if (mBaseModel.isBookmark(fid, stid)) {
            ToastUtils.showToast("该版面已存在");
        } else {
            mBaseModel.addBookmark(fid, stid, name);
            ToastUtils.showToast("添加成功");
        }
    }

    @Override
    public void showTopicList(Board board) {
        showTopicList(board.getFid(), board.getStid(), board.getName());
    }

    @Override
    public void showTopicList(int fid, int stid, String boardName) {
        ARouterUtils.build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                .withInt(ParamKey.KEY_FID, fid)
                .withInt(ParamKey.KEY_STID, stid)
                .withString(ParamKey.KEY_TITLE, boardName)
                .navigation(mBaseView.getContext());
    }

    @Override
    public void showTopicList(String url) {
        Intent intent = new Intent(mBaseView.getContext(), TopicListActivity.class);
        intent.setData(Uri.parse(url));
        if (mBaseView.getContext() != null) {
            mBaseView.getContext().startActivity(intent);
        }
    }

    @Override
    public void showTopicContent(String url) {
        Intent intent = new Intent(mBaseView.getContext(), ArticleListActivity.class);
        intent.setData(Uri.parse(url));
        if (mBaseView.getContext() != null) {
            mBaseView.getContext().startActivity(intent);
        }
    }

    @Override
    protected BoardModel onCreateModel() {
        return BoardModel.getInstance();
    }
}
