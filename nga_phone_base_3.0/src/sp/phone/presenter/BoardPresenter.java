package sp.phone.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.PreferenceConstant;
import sp.phone.bean.User;
import sp.phone.interfaces.PageCategoryOwner;
import sp.phone.presenter.contract.BoardContract;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

/**
 * Created by Yang Yihang on 2017/6/29.
 */

public class BoardPresenter implements BoardContract.Presenter, PageCategoryOwner {


    private BoardContract.View mView;

    private BoardHolder mBoardInfo;

    public BoardPresenter(BoardContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public Context getContext() {
        return mView.getContext();
    }

    @Override
    public void loadBoardInfo() {
        MyApp app = (MyApp) getContext().getApplicationContext();
        if (PhoneConfiguration.getInstance().iconmode) {
            mBoardInfo = app.loadDefaultBoardOld();
        } else {
            mBoardInfo = app.loadDefaultBoard();
        }
    }

    @Override
    public boolean addBoard(String fid, String name) {
        if (name.equals("")) {
            mView.showToast("请输入版面名称");
            return false;
        } else {
            Pattern pattern = Pattern.compile("-{0,1}[0-9]*");
            Matcher match = pattern.matcher(fid);
            boolean checkInt = true;
            try {
                Integer.parseInt(fid);
            } catch (Exception e) {
                checkInt = false;
            }
            if (!match.matches() || fid.equals("") || !checkInt) {
                mView.showToast("请输入正确的版面ID(个人版面要加负号)");
                return false;
            } else {// CHECK PASS, READY TO ADD FID
                int i = 0;
                for (i = 0; i < mBoardInfo.getCategoryCount(); i++) {
                    BoardCategory curr = mBoardInfo.getCategory(i);
                    for (int j = 0; j < curr.size(); j++) {
                        String URL = curr.get(j).getUrl();
                        if (URL.equals(fid)) {
                            mView.showToast("该版面已经存在于列表" + mBoardInfo.getCategoryName(i) + "中");
                            return false;
                        }
                    }// for j
                }// for i
                addFid(name, fid);
                mView.showToast("添加成功" + mBoardInfo.getCategoryName(i) + "中");
                return true;
            }
        }
    }

    @Override
    public void toggleUser(List<User> userList) {
        if (userList != null && userList.size() > 1) {
            int index = mView.switchToNextUser();
            User user = userList.get(index);
            MyApp app = (MyApp) getContext().getApplicationContext();
            PhoneConfiguration config = PhoneConfiguration.getInstance();
            app.addToUserList(user.getUserId(), user.getCid(),
                    user.getNickName(), user.getReplyString(),
                    user.getReplyTotalNum(), user.getBlackList());
            config.setUid(user.getUserId());
            config.setNickname(user.getNickName());
            config.setCid(user.getCid());
            config.setReplyString(user.getReplyString());
            config.setReplyTotalNum(user.getReplyTotalNum());
            config.blacklist = StringUtil.blackliststringtolisttohashset(user.getBlackList());
            mView.showToast("切换账户成功,当前账户名:" + user.getNickName());
        } else {
            mView.jumpToLogin();
        }
    }

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
            Log.e(tag, Log.getStackTraceString(e));
            Log.e(tag, "invalid fid " + fidString);
        }
        if (fid == 0) {
            String tip = fidString + "绝对不存在";
            mView.showToast(tip);
            return;
        }

        Log.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

        String url = HttpUtil.Server + "/thread.php?fid=" + fidString + "&rss=1";
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        if (!StringUtil.isEmpty(config.getCookie())) {
            url = url + "&" + config.getCookie().replace("; ", "&");
        } else if (fid < 0) {
            mView.jumpToLogin();
            return;
        }
        if (!StringUtil.isEmpty(url)) {
            Intent intent = new Intent();
            intent.putExtra("tab", "1");
            intent.putExtra("fid", fid);
            intent.setClass(getContext(), config.topicActivityClass);
            getContext().startActivity(intent);
            addToRecent(fidString);
            if (mView.getCurrentItem() <= 1) {
                mView.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        loadBoardInfo();
        mView.notifyDataSetChanged();
    }

    @Override
    public void clearRecentBoards() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(PreferenceConstant.PERFERENCE, Context.MODE_PRIVATE).edit();
        editor.putString(PreferenceConstant.RECENT_BOARD, "").apply();
        mBoardInfo.getCategory(0).getBoardList().clear();
        mView.notifyDataSetChanged();
    }

    private void addFid(String Name, String Fid) {
        boolean addFidAlreadyExist = false;
        BoardCategory addFid = null;
        int i;
        for (i = 0; i < mBoardInfo.getCategoryCount(); i++) {
            if (mBoardInfo.getCategoryName(i).equals(getContext().getString(R.string.addfid))) {
                addFidAlreadyExist = true;
                addFid = mBoardInfo.getCategory(i);
                break;
            }
        }
        if (!addFidAlreadyExist) {// 没有
            List<Board> boardList = new ArrayList<Board>();
            Board b;
            if (PhoneConfiguration.getInstance().iconmode) {
                b = new Board(i + 1, Fid, Name, R.drawable.oldpdefault);
            } else {
                b = new Board(i + 1, Fid, Name, R.drawable.pdefault);
            }
            boardList.add(b);
            saveFid(boardList);
        } else {// 有了
            Board b;
            if (PhoneConfiguration.getInstance().iconmode) {
                b = new Board(i, Fid, Name, R.drawable.oldpdefault);
            } else {
                b = new Board(i, Fid, Name, R.drawable.pdefault);
            }
            addFid.add(b);
            saveFid(addFid.getBoardList());
        }
    }

    private void saveFid(List<Board> boardList) {
        String addFidStr = JSON.toJSONString(boardList);
        SharedPreferences.Editor editor = getContext().getSharedPreferences(PreferenceConstant.PERFERENCE, Context.MODE_PRIVATE).edit();
        editor.putString(PreferenceConstant.ADD_FID, addFidStr);
        editor.apply();
    }


    @Override
    public int getCategoryCount() {
        return mBoardInfo == null ? 0 : mBoardInfo.getCategoryCount();
    }

    @Override
    public String getCategoryName(int position) {
        return mBoardInfo == null ? "" : mBoardInfo.getCategoryName(position);
    }

    @Override
    public BoardCategory getCategory(int category) {
        return mBoardInfo == null ? null : mBoardInfo.getCategory(category);
    }

    private void saveRecent(List<Board> boardList) {
        String recentStr = JSON.toJSONString(boardList);
        SharedPreferences.Editor editor = getContext().getSharedPreferences(PreferenceConstant.PERFERENCE,Context.MODE_PRIVATE).edit();
        editor.putString(PreferenceConstant.RECENT_BOARD, recentStr).apply();
    }

    private void addToRecent(String fidString) {

        BoardCategory recent = mBoardInfo.getCategory(0);
        recent.remove(fidString);
        for (int i = 1; i < mBoardInfo.getCategoryCount(); i++) {
            BoardCategory curr = mBoardInfo.getCategory(i);
            for (int j = 0; j < curr.size(); j++) {
                Board board = curr.get(j);
                if (board.getUrl().equals(fidString)) {
                    Board newBoard = new Board(0, board.getUrl(), board.getName(),
                            board.getIcon());
                    recent.addFront(newBoard);
                    saveRecent(recent.getBoardList());
                    return;
                }// if
            }// for j

        }// for i

    }


}
