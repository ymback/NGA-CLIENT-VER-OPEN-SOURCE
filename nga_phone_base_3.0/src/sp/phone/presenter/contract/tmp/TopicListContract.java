package sp.phone.presenter.contract.tmp;

import sp.phone.bean.TopicListInfo;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public interface TopicListContract {

    interface Presenter extends BaseContract.Presenter<View> {

        void removeTopic(String tidArray, int position);

        void loadPage(int page, TopicListParam requestInfo);

        void loadNextPage(int page, TopicListParam requestInfo);
    }

    interface View extends BaseContract.View {

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

        void setData(TopicListInfo result);

        void clearData();

        void scrollTo(int position);

        void setNextPageEnabled(boolean enabled);

        void removeTopic(int position);

        void hideLoadingView();

    }

    interface Model extends BaseContract.Model {

        void removeTopic(String tidArray, OnHttpCallBack<String> callBack);

        void loadTopicList(String url,OnHttpCallBack<TopicListInfo> callBack);
    }

}
