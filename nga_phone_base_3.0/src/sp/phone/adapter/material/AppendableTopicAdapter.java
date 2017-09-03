package sp.phone.adapter.material;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.utils.ActivityUtils;

public class AppendableTopicAdapter extends TopicListAdapter {

    final private List<TopicListInfo> mInfoList;

    private final NextJsonTopicListLoader mLoader;

    private Set<Integer> mTidSet;

    private boolean mIsEndOfList = false;

    private boolean mIsPrompted = false;

    private boolean mIsLoading = false;

    public AppendableTopicAdapter(Context context, NextJsonTopicListLoader loader) {
        super(context);
        mInfoList = new ArrayList<>();
        mTidSet = new HashSet<>();
        mLoader = loader;
    }

    @Override
    protected ThreadPageInfo getEntry(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position < mInfoList.get(i).get__T__ROWS()) {
                return mInfoList.get(i).getArticleEntryList().get(position);
            }
            position -= mInfoList.get(i).get__T__ROWS();
        }
        return null;
    }

    @Override
    public void jsonFinishLoad(TopicListInfo result) {
        mIsLoading = false;
        if (result.get__SEARCHNORESULT()) {
            ActivityUtils.showToast(mContext, "结果已搜索完毕");
            mIsLoading = false;
        }
        ActivityUtils.getInstance().dismiss();
        if (mCount != 0) {
            List<ThreadPageInfo> threadList = new ArrayList<>();
            for (int i = 0; i < result.getArticleEntryList().size(); i++) {
                ThreadPageInfo info = result.getArticleEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int tid = info.getTid();
                if (!mTidSet.contains(tid)) {
                    threadList.add(info);
                    mTidSet.add(tid);
                }
            }
            result.set__T__ROWS(threadList.size());
            result.setArticleEntryList(threadList);
        } else {
            for (int i = 0; i < result.getArticleEntryList().size(); i++) {
                ThreadPageInfo info = result.getArticleEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int tid = info.getTid();
                mTidSet.add(tid);
            }

        }
        mInfoList.add(result);
        mCount += result.get__T__ROWS();
        mIsEndOfList = mCount >= (result.get__ROWS());
        this.notifyDataSetChanged();
    }

    public void clear() {
        mCount = 0;
        mInfoList.clear();
        mTidSet.clear();
        mIsPrompted = false;
        setSelected(-1);
    }

    public int getNextPage() {
        return mInfoList.size() + 1;
    }

    public boolean getIsEnd() {
        return mIsEndOfList;
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        if (position + 1 == mCount && !mIsLoading) {
            if (mIsEndOfList) {
                if (!mIsPrompted) {
                    ActivityUtils.showToast(mContext, mContext.getString(R.string.last_page_prompt));
                    mIsPrompted = true;
                }
            } else {
                mIsLoading = true;
                mLoader.loadNextPage(this);
            }
        }

        super.onBindViewHolder(holder, position);
    }

    public void remove(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position < mInfoList.get(i).get__T__ROWS()) {
                mInfoList.get(i).getArticleEntryList().remove(position);
                mInfoList.get(i).set__T__ROWS(mInfoList.get(i).getArticleEntryList().size());
                mCount--;
            }
            position -= mInfoList.get(i).get__T__ROWS();
        }
    }

    public String getTidArray(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position < mInfoList.get(i).get__T__ROWS()) {
                return mInfoList.get(i).getArticleEntryList().get(position).getTidarray();
            }
            position -= mInfoList.get(i).get__T__ROWS();
        }
        return null;
    }
}
