package sp.phone.common;

import android.content.Context;

import java.util.List;

public interface UserAgentManager {
    void initialize(Context context);
    void toggleUserAgent(int position);

    void addUserAgent(UserAgent keyword);

    List<UserAgent> getUserAgents();
    void removeUserAgent(int index);
}
