package sp.phone.adapter.material;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.presenter.contract.tmp.MessageListContract;

/**
 * Created by Justwen on 2017/10/1.
 */

public class AppendableMessageListAdapter extends MessageListAdapter {

    private MessageListContract.Presenter mPresenter;

    private List<MessageListInfo> mInfoList = new ArrayList<>();

    private Set<Integer> mMidSet = new HashSet<>();

    private boolean mPrompted;

    private boolean mEndOfList;

    private boolean mLoading;

    private int mCount;

    public AppendableMessageListAdapter(Context context, MessageListContract.Presenter presenter) {
        super(context);
        mPresenter = presenter;
    }

    @Override
    protected MessageThreadPageInfo getEntry(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position < (mInfoList.get(i).get__currentPage() * mInfoList.get(i).get__rowsPerPage())) {
                return mInfoList.get(i).getMessageEntryList().get(position);
            }
            position -= mInfoList.get(i).get__rowsPerPage();
        }
        return null;
    }

    public int getNextPage() {
        return mInfoList.size() + 1;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (position + 1 == getItemCount() && !mLoading) {
            if (!mEndOfList) {
                mLoading = true;
                mPresenter.loadNextPage(getNextPage());
            } else if (!mPrompted) {
                mPresenter.showMessage(R.string.last_page_prompt_message);
                mPrompted = true;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    @Override
    public void setData(MessageListInfo result) {
        mLoading = false;
        if (result == null) {
            return;
        }

        if (mCount != 0) {
            List<MessageThreadPageInfo> threadList = new ArrayList<>();
            for (int i = 0; i < result.getMessageEntryList().size(); i++) {
                MessageThreadPageInfo info = result.getMessageEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int mid = info.getMid();
                if (!mMidSet.contains(mid)) {
                    threadList.add(info);
                    mMidSet.add(mid);
                }
            }
            result.setMessageEntryList(threadList);
        } else {
            for (int i = 0; i < result.getMessageEntryList().size(); i++) {
                MessageThreadPageInfo info = result.getMessageEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int mid = info.getMid();
                mMidSet.add(mid);
            }

        }

        mInfoList.add(result);
        mCount += result.getMessageEntryList().size();
        mEndOfList = result.get__nextPage() <= 0;
        notifyDataSetChanged();

    }
}
