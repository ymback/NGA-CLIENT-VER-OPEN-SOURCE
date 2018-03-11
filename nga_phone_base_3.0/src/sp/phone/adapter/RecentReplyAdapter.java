package sp.phone.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahang.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import gov.anzong.androidnga.R;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.theme.ThemeManager;
import sp.phone.utils.ImageUtil;

public class RecentReplyAdapter extends RecyclerView.Adapter<RecentReplyAdapter.ViewHolder> {

    private List<RecentReplyInfo> mRecentReplyList;

    private List<RecentReplyInfo> mUnreadRecentReplyList;

    private View.OnClickListener mClickListener;

    private Context mContext;

    public RecentReplyAdapter(Context context) {
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
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_recent_reply_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RecentReplyInfo info = mRecentReplyList.get(mRecentReplyList.size() - 1 - position);
        holder.itemView.setOnClickListener(mClickListener);
        holder.userNameTv.setText(info.getUserName());
        holder.titleTv.setText(info.getTitle());
        holder.timeTv.setText(buildDateStr(info.getTimeStamp()));

        holder.userNameTv.setTextColor(ThemeManager.getInstance().getAccentColor(mContext));

        if (isUnread(info)) {
            holder.timeTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.titleTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.userNameTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            holder.timeTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.titleTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.userNameTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        ImageUtil.loadRoundCornerAvatar(holder.avatarIv, UserManagerImpl.getInstance().getAvatarUrl(info.getUserId()));

        holder.itemView.setTag(info);
    }

    private String buildDateStr(String timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp) * 1000);
        LogUtils.d(System.currentTimeMillis() + "  " + timeStamp);
        return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(calendar.getTime());
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

        TextView titleTv;

        TextView timeTv;

        ImageView avatarIv;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.user_name);
            titleTv = itemView.findViewById(R.id.title);
            timeTv = itemView.findViewById(R.id.time);
            avatarIv = itemView.findViewById(R.id.avatar);

        }
    }
}
