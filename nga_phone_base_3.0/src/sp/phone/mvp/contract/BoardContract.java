package sp.phone.mvp.contract;

import java.util.List;

import sp.phone.common.User;
import sp.phone.mvp.contract.tmp.BaseContract;

/**
 * Created by Justwen on 2017/6/29.
 */

public interface BoardContract {

    interface Presenter extends BaseContract.Presenter {

        void loadBoardInfo();

        boolean addBoard(String fid,String name);

        void toggleUser(List<User> userList);

        void toTopicListPage(int position,String fidString);

        void notifyDataSetChanged();

        void clearRecentBoards();

    }

    interface View extends BaseContract.View<Presenter> {

        int switchToNextUser();

        void jumpToLogin();

        void updateHeaderView();

        void notifyDataSetChanged();

        int getCurrentItem();
    }

    interface Model extends BaseContract.Model {

    }
}
