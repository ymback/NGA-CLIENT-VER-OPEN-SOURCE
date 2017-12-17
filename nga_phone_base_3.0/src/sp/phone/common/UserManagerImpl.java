package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    private List<User> mUserList;

    private int mActiveIndex;

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
        SharedPreferences sp = mContext.getSharedPreferences(PERFERENCE, Context.MODE_PRIVATE);
        String userListString = sp.getString(USER_LIST, "");
        if (TextUtils.isEmpty(userListString)) {
            mUserList = new ArrayList<>();
        } else {
            mUserList = JSON.parseArray(userListString, User.class);
            if (mUserList == null) {
                mUserList = new ArrayList<>();
            }
        }
        mActiveIndex = sp.getInt(USER_ACTIVE_INDEX, 0);

        // TODO: need remove it
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        final String uid = sp.getString(UID, "");
        final String cid = sp.getString(CID, "");
        final String replyString = sp.getString(PENDING_REPLYS, "");
        final int replyTotalNum = Integer.parseInt(sp.getString(REPLYTOTALNUM, "0"));
        final Set<Integer> blackList = StringUtils.blackListStringToHashset(sp.getString(BLACK_LIST, ""));
        if (!StringUtils.isEmpty(uid) && !StringUtils.isEmpty(cid)) {
            config.setUid(uid);
            config.setCid(cid);
            config.setReplyString(replyString);
            config.setReplyTotalNum(replyTotalNum);
            config.blacklist = blackList;
            final String name = sp.getString(USER_NAME, "");
            config.userName = name;
            if (StringUtils.isEmpty(userListString)) {
                addUser(uid, cid, name, replyString, replyTotalNum, sp.getString(BLACK_LIST, ""));
            }
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
    public void addUser(String uid, String cid, String name, String replyString, int replyTotalNum, String blackList) {
        if (blackList == null) {
            blackList = "";
        }
        User user = new User();
        user.setCid(cid);
        user.setUserId(uid);
        user.setNickName(name);
        user.setReplyString(replyString);
        user.setReplyTotalNum(replyTotalNum);
        user.setBlackList(blackList);
        addUser(user);
    }

    // TODO: need check more about this function
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
        String userListString = JSON.toJSONString(mUserList);
        User user = getActiveUser();
        SharedPreferences sp = mContext.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);

        String uid;
        String cid;
        String userName;
        String replyString;
        int replyCount;
        String blackList;
        if (user != null) {
            uid = user.getUserId();
            cid = user.getCid();
            userName = user.getNickName();
            replyCount = user.getReplyTotalNum();
            replyString = user.getReplyString();
            blackList = user.getBlackList();
        } else {
            uid = "";
            cid = "";
            userName = "";
            replyCount = 0;
            replyString = "";
            blackList = "";
        }
        sp.edit().putString(USER_LIST, userListString)
                .putInt(USER_ACTIVE_INDEX, mActiveIndex)
                .putString(UID, uid)
                .putString(CID, cid)
                .putString(USER_NAME, userName)
                .putString(PENDING_REPLYS, replyString)
                .putString(REPLYTOTALNUM, String.valueOf(replyCount))
                .putString(BLACK_LIST, blackList)
                .apply();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        config.setUid(uid);
        config.setCid(cid);
        config.userName = userName;
        config.setReplyTotalNum(replyCount);
        config.setReplyString(replyString);
        config.blacklist = StringUtils.blackListStringToHashset(blackList);

    }

    @Override
    public void setBlackList(String blackList) {
        User user = getActiveUser();
        if (user != null) {
            user.setBlackList(blackList);
            commit();
        }
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
}
