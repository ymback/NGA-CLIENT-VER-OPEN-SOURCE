package sp.phone.presenter.contract.tmp;

import sp.phone.bean.TopicListInfo;
import sp.phone.forumoperation.TopicListParam;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public interface TopicListContract {

    interface Presenter extends BaseContract.Presenter<View> {

        void removeBookmark(String tidId, int position);

        void loadPage(int page, TopicListParam requestInfo);

        void loadNextPage(int page, TopicListParam requestInfo);
    }

    interface View extends BaseContract.View {

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

        void setData(TopicListInfo result);

        void clearData();

        android.view.View getTopicListView();

        void scrollTo(int position);

        void setNextPageEnabled(boolean enabled);

    }

}
