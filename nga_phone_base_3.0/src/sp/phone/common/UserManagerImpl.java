package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sp.phone.common.PreferenceKey.PERFERENCE;
import static sp.phone.common.PreferenceKey.USER_ACTIVE_INDEX;
import static sp.phone.common.PreferenceKey.USER_LIST;


public class UserManagerImpl implements UserManager {

    private Context mContext;

    private int mActiveIndex;

    private List<User> mBlackList;

    private List<User> mUserList;

    private SharedPreferences mPrefs;

    private static class SingletonHolder {

        static UserManager sInstance = new UserManagerImpl();
    }

    public static UserManager getInstance() {
        return SingletonHolder.sInstance;
    }


    private UserManagerImpl() {
    }

    @Override
    public void initialize(Context context) {
        mContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        mActiveIndex = mPrefs.getInt(USER_ACTIVE_INDEX, 0);

        String blackListStr = mPrefs.getString(PreferenceKey.BLACK_LIST, "");
        if (TextUtils.isEmpty(blackListStr)) {
            mBlackList = new ArrayList<>();
        } else {
            mBlackList = JSON.parseArray(blackListStr, User.class);
            if (mBlackList == null) {
                mBlackList = new ArrayList<>();
            }
        }

        String userListStr = mPrefs.getString(PreferenceKey.USER_LIST, "");
        if (TextUtils.isEmpty(userListStr)) {
            mUserList = new ArrayList<>();
        } else {
            mUserList = JSON.parseArray(userListStr, User.class);
            if (mUserList == null) {
                mUserList = new ArrayList<>();
            }
        }

        versionUpgrade();
    }

    private void versionUpgrade() {
        SharedPreferences sp = mContext.getSharedPreferences(PERFERENCE, Context.MODE_PRIVATE);
        String userListString = sp.getString(USER_LIST, "");
        List<sp.phone.bean.User> oldUserList;
        if (TextUtils.isEmpty(userListString)) {
            oldUserList = new ArrayList<>();
        } else {
            oldUserList = JSON.parseArray(userListString, sp.phone.bean.User.class);
            if (oldUserList == null) {
                oldUserList = new ArrayList<>();
            }
        }
        if (!oldUserList.isEmpty() && mUserList.isEmpty()) {
            mActiveIndex = sp.getInt(USER_ACTIVE_INDEX, 0);
            for (sp.phone.bean.User user : oldUserList) {
                User newUser = new User();
                newUser.setNickName(user.getNickName());
                newUser.setUserId(user.getUserId());
                newUser.setCid(user.getCid());
                newUser.setReplyCount(user.getReplyTotalNum());
                newUser.setReplyString(user.getReplyString());
                mUserList.add(newUser);
            }
            mPrefs.edit().putString(PreferenceKey.USER_LIST, JSON.toJSONString(mUserList)).putInt(USER_ACTIVE_INDEX, mActiveIndex).apply();
        }
    }

    @Override
    public int getActiveUserIndex() {
        return mActiveIndex;
    }

    @Nullable
    @Override
    public User getActiveUser() {
        return mUserList.isEmpty() ? null : mUserList.get(mActiveIndex);
    }

    @Override
    public List<User> getUserList() {
        return mUserList;
    }

    @Override
    public String getCid() {
        User user = getActiveUser();
        return user != null ? user.getCid() : "";
    }

    @Override
    public String getUserName() {
        User user = getActiveUser();
        return user != null ? user.getNickName() : "";
    }

    @Override
    public String getUserId() {
        User user = getActiveUser();
        return user != null ? user.getUserId() : "";
    }

    @Override
    public void setActiveUser(int index) {
        mActiveIndex = index;
        commit();
    }

    @Override
    public int toggleUser(boolean isNext) {

        if (isNext) {
            mActiveIndex++;
        } else {
            mActiveIndex = mActiveIndex + mUserList.size() - 1;
        }

        mActiveIndex = mActiveIndex % mUserList.size();
        commit();
        return mActiveIndex;

    }

    @Override
    public void addUser(User user) {

        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).getUserId().equals(user.getUserId())) {
                mUserList.set(i, user);
                commit();
                return;
            }
        }
        mUserList.add(user);
        commit();
    }

    @Override
    public void addUser(String uid, String cid, String name, String replyString, int replyTotalNum) {
        User user = new User();
        user.setCid(cid);
        user.setUserId(uid);
        user.setNickName(name);
        user.setReplyString(replyString);
        user.setReplyCount(replyTotalNum);
        addUser(user);
    }

    @Override
    public void removeUser(int index) {
        mUserList.remove(index);
        if (mUserList.isEmpty() || mActiveIndex == index) {
            mActiveIndex = 0;
        }
        commit();
    }

    @Override
    public void setReplyString(int count, String replyString) {
        User user = getActiveUser();
        if (user != null) {
            user.setReplyCount(count);
            user.setReplyString(replyString);
            commit();
        }
    }

    @Override
    public int getReplyCount() {
        User user = getActiveUser();
        return user != null ? user.getReplyCount() : 0;
    }

    @Override
    public String getReplyString() {
        User user = getActiveUser();
        return user != null ? user.getReplyString() : null;
    }

    private void commit() {
        String userListString = JSON.toJSONString(mUserList);
        mPrefs.edit().putString(PreferenceKey.USER_LIST, userListString).apply();
    }

    @Override
    public String getCookie() {
        User user = getActiveUser();
        if (user != null
                && !TextUtils.isEmpty(user.getCid())
                && !TextUtils.isEmpty(user.getUserId())) {
            return "ngaPassportUid=" + user.getUserId() + "; ngaPassportCid=" + user.getCid();
        } else {
            return "";
        }
    }

    @Override
    public void swapUser(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mUserList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mUserList, i, i - 1);
            }
        }
        commit();
    }

    @Override
    public void addToBlackList(String authorName, String authorId) {
        for (int i = 0; i < mBlackList.size(); i++) {
            sp.phone.common.User user = mBlackList.get(i);
            if (user.getUserId().equals(authorId)) {
                return;
            }
        }
        User user = new User();
        user.setUserId(authorId);
        user.setNickName(authorName);
        mBlackList.add(user);
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
    }

    @Override
    public void addToBlackList(User user) {
        if (!mBlackList.contains(user)) {
            mBlackList.add(user);
        }
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
    }

    @Override
    public void removeFromBlackList(String authorId) {
        for (int i = 0; i < mBlackList.size(); i++) {
            User user = mBlackList.get(i);
            if (user.getUserId().equals(authorId)) {
                mBlackList.remove(i);
                mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
                return;
            }
        }
    }

    @Override
    public boolean checkBlackList(String authorId) {
        for (User user : mBlackList) {
            if (user.getUserId().equals(authorId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getBlackList() {
        return mBlackList;
    }

    @Override
    public void removeAllBlackList() {
        mBlackList.clear();
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
    }
}
