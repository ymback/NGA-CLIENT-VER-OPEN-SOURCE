package sp.phone.mvp.contract;

import android.net.Uri;

import androidx.fragment.app.Fragment;

import sp.phone.mvp.model.entity.Board;
import sp.phone.param.TopicListParam;
import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;

/**
 * Created by Justwen on 2017/6/3.
 */

public interface TopicListContract {

    interface Model {

        void loadCache(OnHttpCallBack<TopicListInfo> callBack);

        void removeTopic(ThreadPageInfo info, OnHttpCallBack<String> callBack);

        void loadTopicList(int page, TopicListParam param, OnHttpCallBack<TopicListInfo> callBack);

        void loadTwentyFourList(TopicListParam param, OnHttpCallBack<TopicListInfo> callBack, int pageCount);

        void removeCacheTopic(ThreadPageInfo info, OnHttpCallBack<String> callBack);
    }

}
