package sp.phone.common;

import android.content.Context;

import java.util.List;

import sp.phone.bean.User;

public interface UserManager {

    void initialize(Context context);

    User getActiveUser();

    int getActiveUserIndex();

    List<User> getUserList();

    void setActiveUser(int index);

    int toggleUser(boolean isNext);

    void addUser(User user);

    void addUser(String uid, String cid, String name, String replyString, int replyTotalNum, String blackList);

    void removeUser(int index);

    void setReplyString(int count, String replyString);

    String getCookie();

    void swapUser(int from, int to);

    void addToBlackList(String authorName, String authorId);

    void removeFromBlackList(String authorId);

    boolean checkBlackList(String authorId);

    List<sp.phone.common.User> getBlackList();

    void removeAllBlackList();

}
