package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.base.util.ThreadUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.db.AppDatabase;
import sp.phone.http.bean.ThreadData;
import sp.phone.http.bean.ThreadRowInfo;


public class UserManagerImpl implements UserManager {

    private Context mContext;

    private int mActiveIndex;

    private List<User> mBlackList;

    private List<User> mUserList;

    private SharedPreferences mPrefs;

    private SharedPreferences mAvatarPreferences;

    // TODO: 2018/4/15  temp solution
    private boolean mAvatarUpdated;

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

        mAvatarPreferences = context.getSharedPreferences(PreferenceKey.PREFERENCE_AVATAR, Context.MODE_PRIVATE);

        mActiveIndex = PreferenceUtils.getData(PreferenceKey.USER_ACTIVE_INDEX, 0);

        String blackListStr = mPrefs.getString(PreferenceKey.BLACK_LIST, "");
        if (TextUtils.isEmpty(blackListStr)) {
            mBlackList = new ArrayList<>();
        } else {
            mBlackList = JSON.parseArray(blackListStr, User.class);
            if (mBlackList == null) {
                mBlackList = new ArrayList<>();
            }
        }

        mUserList = AppDatabase.getInstance().userDao().loadUser();
        transformData();
    }

    private void transformData() {
        if (mUserList.isEmpty()) {
            String oldUserStr = PreferenceUtils.getData(PreferenceKey.USER_LIST, "");
            if (!TextUtils.isEmpty(oldUserStr)) {
                List<User> oldList = JSON.parseArray(oldUserStr, User.class);
                PreferenceUtils.edit().remove(PreferenceKey.USER_LIST).apply();
                if (oldList != null) {
                    mUserList.addAll(oldList);
                    saveUsers();
                }
            }
        }
    }

    private void saveUsers() {
        ThreadUtils.postOnSubThread(() -> {
            synchronized (this) {
                AppDatabase.getInstance().userDao().updateUsers(mUserList.toArray(new User[0]));
            }
        });
    }

    @Override
    public int getActiveUserIndex() {
        return mActiveIndex;
    }

    @Nullable
    @Override
    public User getActiveUser() {
        return mUserList == null || mUserList.isEmpty() ? null : mUserList.get(mActiveIndex);
    }

    @Override
    public List<User> getUserList() {
        return mUserList;
    }

    @Override
    public boolean hasValidUser() {
        return mUserList != null && !mUserList.isEmpty();
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
    public void setAvatarUrl(int userId, String url) {
        for (User user : mBlackList) {
            if (user.getUserId().equals(String.valueOf(userId))) {
                if (user.getAvatarUrl() == null) {
                    user.setAvatarUrl(url);
                    commit();
                }
                return;
            }
        }

        for (User user : mUserList) {
            if (user.getUserId().equals(String.valueOf(userId))) {
                if (user.getAvatarUrl() == null || !mAvatarUpdated) {
                    user.setAvatarUrl(url);
                    commit();
                    mAvatarUpdated = true;
                }
                return;
            }
        }
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
        mActiveIndex = getNextActiveIndex(isNext);
        commit();
        return mActiveIndex;

    }

    private int getNextActiveIndex(boolean isNext) {
        int index = isNext ? mActiveIndex + 1 : mActiveIndex + mUserList.size() - 1;
        return index % mUserList.size();
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
    public void addUser(String uid, String cid, String name) {
        User user = new User();
        user.setCid(cid);
        user.setUserId(uid);
        user.setNickName(name);
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

    private void commit() {
        mPrefs.edit()
                .putInt(PreferenceKey.USER_ACTIVE_INDEX, mActiveIndex)
                .putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList))
                .apply();
        saveUsers();
    }

    @Override
    public String getCookie() {
        return getCookie(getActiveUser());
    }

    @Override
    public String getCookie(User user) {
        if (user != null
                && !TextUtils.isEmpty(user.getCid())
                && !TextUtils.isEmpty(user.getUserId())) {
            return "ngaPassportUid=" + user.getUserId() + "; ngaPassportCid=" + user.getCid();
        } else {
            return "";
        }
    }

    @Override
    public String getNextCookie() {
        int nextIndex = getNextActiveIndex(true);
        return nextIndex != mActiveIndex ? getCookie(mUserList.get(nextIndex)) : null;
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
    public int getUserSize() {
        return mUserList == null ? 0 : mUserList.size();
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

    @Override
    public void putAvatarUrl(String uid, String url) {
        if (!TextUtils.isEmpty(url)) {
            mAvatarPreferences.edit().putString(uid, url).apply();
        }
    }

    @Override
    public void putAvatarUrl(ThreadData info) {
        if (info.getRowList() == null) {
            return;
        }
        SharedPreferences.Editor editor = mAvatarPreferences.edit();
        for (ThreadRowInfo rowInfo : info.getRowList()) {
            String uid = String.valueOf(rowInfo.getAuthorid());
            String url = rowInfo.getJs_escap_avatar();
            if (!TextUtils.isEmpty(uid) && !uid.equals("0") && !TextUtils.isEmpty(url)) {
                editor.putString(uid, url);
                setAvatarUrl(Integer.parseInt(uid), url);
            }
        }
        editor.apply();
    }

    @Override
    public String getAvatarUrl(String uid) {
        return TextUtils.isEmpty(uid) || uid.equals("0") ? "" : mAvatarPreferences.getString(uid, "");
    }

    @Override
    public void clearAvatarUrl() {
        mAvatarPreferences.edit().clear().apply();
    }
}
