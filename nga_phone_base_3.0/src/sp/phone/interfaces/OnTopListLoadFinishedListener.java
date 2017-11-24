package sp.phone.interfaces;

import sp.phone.model.entity.TopicListInfo;

public interface OnTopListLoadFinishedListener {
    void jsonFinishLoad(TopicListInfo result);
    void onListLoadFailed();
}
