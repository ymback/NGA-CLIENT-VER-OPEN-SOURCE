package sp.phone.interfaces;

import sp.phone.mvp.model.entity.TopicListInfo;

public interface OnTopListLoadFinishedListener {
    void jsonFinishLoad(TopicListInfo result);
    void onListLoadFailed();
}
