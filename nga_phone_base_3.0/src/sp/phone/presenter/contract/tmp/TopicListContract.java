package sp.phone.presenter.contract.tmp;

import sp.phone.model.entity.ThreadPageInfo;
import sp.phone.model.entity.TopicListInfo;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/6/3.
 */

public interface TopicListContract {

    interface Presenter extends BaseContract.Presenter<View> {

        void removeTopic(ThreadPageInfo info, int position);

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

        void removeTopic(ThreadPageInfo info, OnHttpCallBack<String> callBack);

        void loadTopicList(int page,TopicListParam param,OnHttpCallBack<TopicListInfo> callBack);
    }

}
