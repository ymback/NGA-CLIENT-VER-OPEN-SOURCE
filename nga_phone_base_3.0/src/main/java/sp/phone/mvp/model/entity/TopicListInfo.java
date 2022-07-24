package sp.phone.mvp.model.entity;

import java.util.ArrayList;
import java.util.List;

public class TopicListInfo {

    private String mName;

    private ArrayList<SubBoard> mSubBoardList = new ArrayList<>();

    private List<ThreadPageInfo> mThreadPageList = new ArrayList<>();

    public List<ThreadPageInfo> getThreadPageList() {
        return mThreadPageList;
    }

    public void setThreadPageList(List<ThreadPageInfo> threadPageList) {
        mThreadPageList.clear();
        mThreadPageList.addAll(threadPageList);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void addThreadPage(ThreadPageInfo threadPage) {
        mThreadPageList.add(threadPage);
    }

    public void addSubBoard(SubBoard subBoard) {
        mSubBoardList.add(subBoard);
    }

    public  ArrayList<SubBoard> getSubBoardList() {
        return mSubBoardList;
    }

    public int curTime;

}
