package sp.phone.common;

import androidx.annotation.NonNull;

/**
 * Created by Justwen on 2017/12/26.
 */

public class User {

    @NonNull
    private String mUserId;

    private String mNickName;

    private String mCid;

    private String mAvatarUrl;

    public User() {
    }

    public User(@NonNull String userId, String nickName, String cid) {
        mUserId = userId;
        mNickName = nickName;
        mCid = cid;
    }

    public User(@NonNull String userId, String nickName) {
        mUserId = userId;
        mNickName = nickName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getCid() {
        return mCid;
    }

    public void setCid(String cid) {
        mCid = cid;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && mUserId.equals(getUserId());
    }
}
