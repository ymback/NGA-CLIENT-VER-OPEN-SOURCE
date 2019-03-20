package sp.phone.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.TopicTitleHelper;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.rxjava.RxUtils;

public class TopicListAdapter extends BaseAppendableAdapter<ThreadPageInfo, TopicListAdapter.TopicViewHolder> {

    public TopicListAdapter(Context context) {
        super(context);
    }

    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TopicViewHolder viewHolder = new TopicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_topic, parent, false));
        viewHolder.title.setTextSize(PhoneConfiguration.getInstance().getTopicTitleSize());
        RxUtils.clicks(viewHolder.itemView, mOnClickListener);
        viewHolder.itemView.setOnLongClickListener(mOnLongClickListener);
        return viewHolder;
    }

    @Override
    public void setData(List<ThreadPageInfo> dataList) {
        if (dataList == null) {
            super.setData(null);
        } else {
            super.appendData(dataList);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {

        ThreadPageInfo info = getItem(position);
        info.setPosition(position);
        holder.itemView.setTag(info);

        handleJsonList(holder, info);
    }

    private void handleJsonList(TopicViewHolder holder, ThreadPageInfo entry) {

        if (entry == null) {
            return;
        }
        holder.author.setText(entry.getAuthor());
        holder.lastReply.setText(entry.getLastPoster());
        holder.num.setText(String.valueOf(entry.getReplies()));
        holder.title.setText(TopicTitleHelper.handleTitleFormat(entry));
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.num)
        public TextView num;

        @BindView(R.id.title)
        public TextView title;

        @BindView(R.id.author)
        public TextView author;

        @BindView(R.id.last_reply)
        public TextView lastReply;

        public TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
