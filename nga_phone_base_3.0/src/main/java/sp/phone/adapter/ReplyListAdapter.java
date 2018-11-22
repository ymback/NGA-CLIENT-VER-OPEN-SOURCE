package sp.phone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.util.StringUtils;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/3/23.
 */

public class ReplyListAdapter extends BaseAppendableAdapter<ThreadPageInfo, ReplyListAdapter.ViewHolder> {

    public ReplyListAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.list_reply_ltem, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ThreadPageInfo pageInfo = getItem(position);
        ThreadPageInfo.ReplyInfo replyInfo = pageInfo.getReplyInfo();

        holder.mContentTv.setText(replyInfo.getContent());
        holder.mSubjectTv.setText(replyInfo.getSubject());
        holder.mPostDateTv.setText(StringUtils.timeStamp2Date2(replyInfo.getPostDate()));


        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setTag(pageInfo);

    }

    @Override
    public void setData(List<ThreadPageInfo> dataList) {
        if (dataList == null) {
            super.setData(dataList);
        } else {
            super.appendData(dataList);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_content)
        public TextView mContentTv;

        @BindView(R.id.tv_time)
        public TextView mPostDateTv;

        @BindView(R.id.tv_topic)
        public TextView mSubjectTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
