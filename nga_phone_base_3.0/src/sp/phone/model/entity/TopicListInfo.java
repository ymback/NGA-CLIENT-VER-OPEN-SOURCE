package sp.phone.model.entity;

import java.util.ArrayList;
import java.util.List;

public class TopicListInfo {

    private List<ThreadPageInfo> mThreadPageList = new ArrayList<>();

    public List<ThreadPageInfo> getThreadPageList() {
        return mThreadPageList;
    }

    public void setThreadPageList(List<ThreadPageInfo> threadPageList) {
        mThreadPageList = threadPageList;
    }

    public void addThreadPage(ThreadPageInfo threadPage) {
        mThreadPageList.add(threadPage);
    }

}
