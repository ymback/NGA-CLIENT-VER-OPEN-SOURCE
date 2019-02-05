package sp.phone.common;

import android.content.Context;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.ThreadPageInfo;

/**
 * Created by Justwen on 2018/1/17.
 */

public class TopicHistoryManager {

    private Context mContext;

    private List<ThreadPageInfo> mTopicList;

    private static final int MAX_HISTORY_TOPIC_COUNT = 40;

    private static class SingleTonHolder {

        private static TopicHistoryManager sInstance = new TopicHistoryManager();
    }

    public static TopicHistoryManager getInstance() {
        return SingleTonHolder.sInstance;
    }

    private TopicHistoryManager() {
        mContext = ApplicationContextHolder.getContext();
        String topicStr = PreferenceManager.getDefaultSharedPreferences(mContext).getString(PreferenceKey.KEY_TOPIC_HISTORY, null);
        if (topicStr != null) {
            mTopicList = JSON.parseArray(topicStr, ThreadPageInfo.class);
        }
        if (mTopicList == null) {
            mTopicList = new ArrayList<>();
        }
    }

    public void addTopicHistory(ThreadPageInfo topic) {
        if (mTopicList.contains(topic)) {
            mTopicList.remove(topic);
        } else if (mTopicList.size() >= MAX_HISTORY_TOPIC_COUNT){
            mTopicList.remove(mTopicList.size() - 1);
        }
        mTopicList.add(0,topic);
        commit();
    }

    public void removeTopicHistory(ThreadPageInfo topic) {
        if (mTopicList.contains(topic)) {
            mTopicList.remove(topic);
            commit();
        }
    }

    public void removeTopicHistory(int index) {
        mTopicList.remove(index);
        commit();
    }

    public List<ThreadPageInfo> getTopicHistoryList() {
        return mTopicList;
    }

    public void removeAllTopicHistory() {
        mTopicList.clear();
        commit();
    }

    private void commit() {
        String topicStr = JSON.toJSONString(mTopicList);
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(PreferenceKey.KEY_TOPIC_HISTORY,topicStr)
                .apply();
    }

}
