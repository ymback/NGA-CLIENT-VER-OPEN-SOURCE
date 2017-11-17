package sp.phone.presenter.contract;

import sp.phone.bean.TopicListInfo;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.interfaces.OnTopListLoadFinishedListener;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public interface TopicListContract {

    interface Presenter extends BaseContract.Presenter{

        void refresh();

        void loadNextPage(OnTopListLoadFinishedListener callback);

        void jsonFinishLoad(TopicListInfo result);

        void removeBookmark(String tidId,int position);

        void showFirstItem();
    }

    interface View extends BaseContract.View<Presenter>{

        void setRefreshing(boolean refreshing);

        void setData(TopicListInfo result);

        int getNextPage();

        TopicListParam getTopicListRequestInfo();

        android.view.View getTopicListView();

        void scrollTo(int position);


    }

}
