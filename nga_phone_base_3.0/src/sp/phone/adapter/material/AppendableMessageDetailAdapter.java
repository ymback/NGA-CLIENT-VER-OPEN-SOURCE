package sp.phone.adapter.material;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.presenter.contract.tmp.MessageDetailContract;

/**
 * Created by Justwen on 2017/10/15.
 */

public class AppendableMessageDetailAdapter extends MessageDetailAdapter {

    private List<MessageDetailInfo> mInfoList = new ArrayList<>();

    private boolean mPrompted;

    private boolean mLoading;

    private boolean mEndOfList;

    private int mMid;

    private MessageDetailContract.IMessagePresenter mPresenter;

    public AppendableMessageDetailAdapter(Context context, MessageDetailContract.IMessagePresenter presenter,int mid) {
        super(context);
        mPresenter = presenter;
        mMid = mid;
    }


    @Override
    protected MessageArticlePageInfo getEntry(int position) {
        return mInfoList.get(position / 20).getMessageEntryList().get(position % 20);
    }

    @Override
    public void setData(MessageDetailInfo data) {
        mLoading = false;
        if (data == null) {
            return;
        }
        if (data.get__currentPage() <= mInfoList.size()) {
            clear();
        }
        mInfoList.add(data);
        mCount += data.getMessageEntryList().size();
        mEndOfList = data.get__nextPage() <= 0;
        notifyDataSetChanged();
    }

    public int getNextPage() {
        return mInfoList.size() + 1;
    }

    public void clear() {
        mCount = 0;
        mInfoList.clear();
        mEndOfList = false;
        mLoading = false;
        mPrompted = false;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position + 1 == getItemCount() && !mLoading) {
            if (!mEndOfList) {
                mLoading = true;
                mPresenter.loadNextPage(getNextPage(),mMid);
            } else if (!mPrompted) {
                mPresenter.showMessage(R.string.last_page_prompt_message_detail);
                mPrompted = true;
            }
        }

    }

}
