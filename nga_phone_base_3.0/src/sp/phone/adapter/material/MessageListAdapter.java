package sp.phone.adapter.material;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.utils.StringUtils;

/**
 * Created by Justwen on 2017/10/1.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    private View.OnClickListener mClickListener;

    private LayoutInflater mLayoutInflater;

    private MessageListInfo mListInfo;

    private Resources mResources;

    public MessageListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mResources = context.getResources();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(mLayoutInflater.inflate(R.layout.relative_messgae_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        handleJsonList(holder, position);
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(mClickListener);
        }
        holder.itemView.setTag(getMidString(position));

    }

    public void setOnClickListener(View.OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mListInfo == null ? 0 : mListInfo.getMessageEntryList().size();
    }

    public void setData(MessageListInfo listInfo) {
        mListInfo = listInfo;
        notifyDataSetChanged();
    }

    private String getMidString(int position) {
        MessageThreadPageInfo entry = getEntry(position);
        if (entry == null || entry.getMid() == 0) {
            return null;
        }
        return "mid=" + entry.getMid();

    }

    private void handleJsonList(MessageViewHolder holder, int position) {
        MessageThreadPageInfo entry = getEntry(position);
        if (entry == null) {
            return;
        }
        ThemeManager theme = ThemeManager.getInstance();
        String fromUser = entry.getFrom_username();
        if (StringUtils.isEmpty(fromUser)) {
            fromUser = "#SYSTEM#";
        }
        holder.author.setText(fromUser);
        holder.time.setText(entry.getTime());
        holder.lastTime.setText(entry.getLastTime());
        String lastPoster = entry.getLast_from_username();
        if (StringUtils.isEmpty(lastPoster)) {
            lastPoster = fromUser;
        }
        holder.lastReply.setText(lastPoster);
        holder.num.setText(String.valueOf(entry.getPosts()));
        holder.title.setTextColor(mResources.getColor(theme.getForegroundColor()));
        float size = PhoneConfiguration.getInstance().getTextSize();

        String title = entry.getSubject();
        if (StringUtils.isEmpty(title)) {
            title = entry.getSubject();
            holder.title.setText(StringUtils.unEscapeHtml(title));

        } else {
            holder.title.setText(StringUtils.removeBrTag(StringUtils
                    .unEscapeHtml(title)));
        }

        holder.title.setTextSize(size);
        final TextPaint tp = holder.title.getPaint();
        tp.setFakeBoldText(false);

        int colorId = theme.getBackgroundColor(position);
        holder.itemView.setBackgroundResource(colorId);

    }

    protected MessageThreadPageInfo getEntry(int position) {
        return mListInfo == null ? null : mListInfo.getMessageEntryList().get(position);
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.num)
        TextView num;

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.author)
        TextView author;

        @BindView(R.id.last_reply)
        TextView lastReply;

        @BindView(R.id.time)
        TextView time;

        @BindView(R.id.lasttime)
        TextView lastTime;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
