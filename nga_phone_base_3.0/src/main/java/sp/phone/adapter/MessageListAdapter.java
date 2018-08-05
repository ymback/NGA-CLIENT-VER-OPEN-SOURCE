package sp.phone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.util.StringUtils;
import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2017/10/1.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> implements RecyclerViewEx.IAppendableAdapter {

    private List<MessageListInfo> mInfoList = new ArrayList<>();

    private boolean mPrompted;

    private boolean mEndOfList;

    private int mTotalCount;

    private Context mContext;

    private View.OnClickListener mClickListener;

    public MessageListAdapter(Context context) {
        mContext = context;
    }

    protected MessageThreadPageInfo getEntry(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position < (mInfoList.get(i).get__currentPage() * mInfoList.get(i).get__rowsPerPage())) {
                return mInfoList.get(i).getMessageEntryList().get(position);
            }
            position -= mInfoList.get(i).get__rowsPerPage();
        }
        return null;
    }

    @Override
    public int getNextPage() {
        return mInfoList.size() + 1;
    }

    @Override
    public boolean hasNextPage() {
        return !mEndOfList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_message, parent, false));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        handleJsonList(holder, position);
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(mClickListener);
        }
        holder.itemView.setTag(getMidString(position));

        if (position + 1 == getItemCount()
                && !hasNextPage()
                && !mPrompted) {
            Toast.makeText(mContext, R.string.last_page_prompt_message, Toast.LENGTH_SHORT).show();
            mPrompted = true;
        }
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
        holder.title.setTextColor(ApplicationContextHolder.getColor(theme.getForegroundColor()));
        float size = PhoneConfiguration.getInstance().getTopicTitleSize();

        String title = entry.getSubject();
        if (StringUtils.isEmpty(title)) {
            title = entry.getSubject();
            holder.title.setText(StringUtils.unEscapeHtml(title));

        } else {
            holder.title.setText(StringUtils.removeBrTag(StringUtils
                    .unEscapeHtml(title)));
        }

        holder.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
        final TextPaint tp = holder.title.getPaint();
        tp.setFakeBoldText(false);

        int colorId = theme.getBackgroundColor(position);
        holder.itemView.setBackgroundResource(colorId);

    }

    public void setOnClickListener(View.OnClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mTotalCount;
    }

    private void reset() {
        mTotalCount = 0;
        mPrompted = false;
        mInfoList.clear();
    }

    public void setData(MessageListInfo result) {
        if (result == null) {
            return;
        } else if (result.get__currentPage() == 1) {
            reset();
        }

        mInfoList.add(result);
        mTotalCount += result.getMessageEntryList().size();
        mEndOfList = result.get__nextPage() <= 0;
        notifyDataSetChanged();

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
