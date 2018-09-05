package sp.phone.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.util.ImageUtils;
import sp.phone.util.StringUtils;
import sp.phone.common.ApiConstants;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ImageUtils;
import sp.phone.util.StringUtils;

public class RecentNotificationAdapter extends RecyclerView.Adapter<RecentNotificationAdapter.ViewHolder> {

    private List<RecentReplyInfo> mRecentReplyList;

    private List<RecentReplyInfo> mUnreadRecentReplyList;

    private View.OnClickListener mClickListener;

    private Context mContext;

    public RecentNotificationAdapter(Context context) {
        mContext = context;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setRecentReplyList(List<RecentReplyInfo> recentReplyList) {
        mRecentReplyList = recentReplyList;
        notifyDataSetChanged();
    }

    public void setUnreadRecentReplyList(List<RecentReplyInfo> unreadRecentReplyList) {
        mUnreadRecentReplyList = unreadRecentReplyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_recent_notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RecentReplyInfo info = mRecentReplyList.get(mRecentReplyList.size() - 1 - position);
        holder.itemView.setOnClickListener(mClickListener);
        holder.userNameTv.setText(info.getUserName());
        holder.topicTv.setText(info.getTitle());
        holder.typeTv.setText(getTypeStr(info.getType()));
        holder.timeTv.setText(StringUtils.timeStamp2Date1(info.getTimeStamp()));

        if (isUnread(info)) {
            holder.timeTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.topicTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.userNameTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.typeTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            holder.timeTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.topicTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.userNameTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.typeTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        ImageUtils.loadRoundCornerAvatar(holder.avatarIv, UserManagerImpl.getInstance().getAvatarUrl(info.getUserId()));

        holder.itemView.setTag(info);
    }

    private String getTypeStr(int type) {
        switch (type) {
            case ApiConstants.NGA_NOTIFICATION_TYPE_TOPIC_REPLY:
                return "回复了你的主题";
            case ApiConstants.NGA_NOTIFICATION_TYPE_REPLY_REPLY:
                return "回复了你的回复";
            case ApiConstants.NGA_NOTIFICATION_TYPE_REPLY_COMMENT:
                return "评论了你的主题";
            case ApiConstants.NGA_NOTIFICATION_TYPE_TOPIC_COMMENT:
                return "评论了你的回复";
            case ApiConstants.NGA_NOTIFICATION_TYPE_REPLY_AT:
                return "在回复中@了你";
            case ApiConstants.NGA_NOTIFICATION_TYPE_TOPIC_AT:
                return "在主题中@了你";
            default:
                return "回复了你的主题";

        }
    }

    private boolean isUnread(RecentReplyInfo info) {
        if (mUnreadRecentReplyList != null) {
            for (RecentReplyInfo unreadInfo : mUnreadRecentReplyList) {
                if (unreadInfo.getPidStr().equals(info.getPidStr())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mRecentReplyList == null ? 0 : mRecentReplyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTv;

        TextView topicTv;

        TextView timeTv;

        ImageView avatarIv;

        TextView typeTv;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.tv_user_name);
            topicTv = itemView.findViewById(R.id.tv_topic);
            timeTv = itemView.findViewById(R.id.tv_time);
            typeTv = itemView.findViewById(R.id.tv_type);
            avatarIv = itemView.findViewById(R.id.iv_avatar);

        }
    }
}
