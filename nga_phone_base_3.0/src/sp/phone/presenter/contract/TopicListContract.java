package sp.phone.presenter.contract;

import android.view.View;
import android.widget.AdapterView;

import sp.phone.bean.TopicListInfo;
import sp.phone.bean.TopicListRequestInfo;
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
    }

    interface View extends BaseContract.View<Presenter>{

        void setRefreshing(boolean refreshing);

        void setData(TopicListInfo result);

        int getNextPage();

        TopicListRequestInfo getTopicListRequestInfo();

        android.view.View getTopicListView();

    }

}
