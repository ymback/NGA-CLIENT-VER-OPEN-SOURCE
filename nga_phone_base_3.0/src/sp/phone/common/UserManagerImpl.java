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

import sp.phone.bean.User;
import sp.phone.utils.StringUtils;

import static sp.phone.common.PreferenceKey.BLACK_LIST;
import static sp.phone.common.PreferenceKey.CID;
import static sp.phone.common.PreferenceKey.PENDING_REPLYS;
import static sp.phone.common.PreferenceKey.PERFERENCE;
import static sp.phone.common.PreferenceKey.REPLYTOTALNUM;
import static sp.phone.common.PreferenceKey.UID;
import static sp.phone.common.PreferenceKey.USER_ACTIVE_INDEX;
import static sp.phone.common.PreferenceKey.USER_LIST;
import static sp.phone.common.PreferenceKey.USER_NAME;


public class UserManagerImpl implements UserManager {

    private Context mContext;

    private List<User> mOldUserList;

    private int mActiveIndex;

    private List<sp.phone.common.User> mBlackList;

    private List<sp.phone.common.User> mUserList;

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
        SharedPreferences sp = mContext.getSharedPreferences(PERFERENCE, Context.MODE_PRIVATE);
        String userListString = sp.getString(USER_LIST, "");
        if (TextUtils.isEmpty(userListString)) {
            mOldUserList = new ArrayList<>();
        } else {
            mOldUserList = JSON.parseArray(userListString, User.class);
            if (mOldUserList == null) {
                mOldUserList = new ArrayList<>();
            }
        }
        mActiveIndex = sp.getInt(USER_ACTIVE_INDEX, 0);

        // TODO: need remove it
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        final String uid = sp.getString(UID, "");
        final String cid = sp.getString(CID, "");
        final String replyString = sp.getString(PENDING_REPLYS, "");
        final int replyTotalNum = Integer.parseInt(sp.getString(REPLYTOTALNUM, "0"));
        if (!StringUtils.isEmpty(uid) && !StringUtils.isEmpty(cid)) {
            config.setUid(uid);
            config.setCid(cid);
            config.setReplyString(replyString);
            config.setReplyTotalNum(replyTotalNum);
            final String name = sp.getString(USER_NAME, "");
            config.userName = name;
            if (StringUtils.isEmpty(userListString)) {
                addUser(uid, cid, name, replyString, replyTotalNum, sp.getString(BLACK_LIST, ""));
            }
        }

        String blackListStr = mPrefs.getString(PreferenceKey.BLACK_LIST, "");
        if (TextUtils.isEmpty(blackListStr)) {
            mBlackList = new ArrayList<>();
        } else {
            mBlackList = JSON.parseArray(blackListStr, sp.phone.common.User.class);
        }

        String userListStr = mPrefs.getString(PreferenceKey.USER_LIST, "");
        if (TextUtils.isEmpty(userListStr)) {
            mUserList = new ArrayList<>();
        } else {
            mUserList = JSON.parseArray(userListStr, sp.phone.common.User.class);
        }

        // versionUpgrade();
    }

    private void versionUpgrade() {
        mUserList.clear();
        for (User user : mOldUserList) {
            sp.phone.common.User newUser = new sp.phone.common.User();
            newUser.setNickName(user.getNickName());
            newUser.setUserId(user.getUserId());
            newUser.setCid(user.getCid());
            newUser.setReplyCount(user.getReplyTotalNum());
            newUser.setReplyString(user.getReplyString());
            mUserList.add(newUser);
        }
        mPrefs.edit().putString(PreferenceKey.USER_LIST, JSON.toJSONString(mUserList)).putInt(USER_ACTIVE_INDEX, mActiveIndex).apply();
    }

    @Override
    public int getActiveUserIndex() {
        return mActiveIndex;
    }

    @Nullable
    @Override
    public User getActiveUser() {
        return mUserList.isEmpty() ? null : mOldUserList.get(mActiveIndex);
    }

    @Override
    public List<User> getUserList() {
        return mOldUserList;
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
            mActiveIndex = mActiveIndex + mOldUserList.size() - 1;
        }

        mActiveIndex = mActiveIndex % mOldUserList.size();
        commit();
        return mActiveIndex;

    }

    @Override
    public void addUser(User user) {

        for (int i = 0; i < mOldUserList.size(); i++) {
            if (mOldUserList.get(i).getUserId().equals(user.getUserId())) {
                mOldUserList.set(i, user);
                commit();
                return;
            }
        }
        mOldUserList.add(user);
        commit();
    }

    @Override
    public void addUser(String uid, String cid, String name, String replyString, int replyTotalNum, String blackList) {
        User user = new User();
        user.setCid(cid);
        user.setUserId(uid);
        user.setNickName(name);
        user.setReplyString(replyString);
        user.setReplyTotalNum(replyTotalNum);
        addUser(user);
    }

    // TODO: need check more about this function
    @Override
    public void removeUser(int index) {
        mOldUserList.remove(index);
        if (mOldUserList.isEmpty() || mActiveIndex == index) {
            mActiveIndex = 0;
        }
        commit();
    }

    @Override
    public void setReplyString(int count, String replyString) {
        User user = getActiveUser();
        if (user != null) {
            user.setReplyTotalNum(count);
            user.setReplyString(replyString);
            commit();
        } else {
            PhoneConfiguration.getInstance().setReplyString(replyString);
            PhoneConfiguration.getInstance().setReplyTotalNum(count);
            SharedPreferences.Editor editor = mContext.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE).edit();
            editor.putString(PENDING_REPLYS, replyString)
                    .putString(REPLYTOTALNUM, String.valueOf(count))
                    .apply();
        }
    }

    private void commit() {
        String userListString = JSON.toJSONString(mOldUserList);
        User user = getActiveUser();
        SharedPreferences sp = mContext.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);

        String uid;
        String cid;
        String userName;
        String replyString;
        int replyCount;
        if (user != null) {
            uid = user.getUserId();
            cid = user.getCid();
            userName = user.getNickName();
            replyCount = user.getReplyTotalNum();
            replyString = user.getReplyString();
        } else {
            uid = "";
            cid = "";
            userName = "";
            replyCount = 0;
            replyString = "";
        }
        sp.edit().putString(USER_LIST, userListString)
                .putInt(USER_ACTIVE_INDEX, mActiveIndex)
                .putString(UID, uid)
                .putString(CID, cid)
                .putString(USER_NAME, userName)
                .putString(PENDING_REPLYS, replyString)
                .putString(REPLYTOTALNUM, String.valueOf(replyCount))
                .apply();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        config.setUid(uid);
        config.setCid(cid);
        config.userName = userName;
        config.setReplyTotalNum(replyCount);
        config.setReplyString(replyString);

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
                Collections.swap(mOldUserList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mOldUserList, i, i - 1);
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
        sp.phone.common.User user = new sp.phone.common.User();
        user.setUserId(authorId);
        user.setNickName(authorName);
        mBlackList.add(user);
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();

    }

    @Override
    public void removeFromBlackList(String authorId) {
        for (int i = 0; i < mBlackList.size(); i++) {
            sp.phone.common.User user = mBlackList.get(i);
            if (user.getUserId().equals(authorId)) {
                mBlackList.remove(i);
                mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
                return;
            }
        }
    }

    @Override
    public boolean checkBlackList(String authorId) {
        for (sp.phone.common.User user : mBlackList) {
            if (user.getUserId().equals(authorId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<sp.phone.common.User> getBlackList() {
        return mBlackList;
    }

    @Override
    public void removeAllBlackList() {
        mBlackList.clear();
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
    }
}
