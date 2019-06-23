package sp.phone.mvp.model;

import sp.phone.mvp.contract.UserContract;

/**
 * Created by Justwen on 2019/6/23.
 */
public class UserModel extends BaseModel implements UserContract.Model {

    private UserModel() {

    }

    private static class SingletonHolder {
        private static UserModel sInstance = new UserModel();
    }

    public static UserModel getInstance() {
        return SingletonHolder.sInstance;
    }
}
