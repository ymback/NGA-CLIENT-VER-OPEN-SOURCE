package sp.phone.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.interfaces.OnMessageListLoadFinishedListener;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class MessageListAdapter extends BaseAdapter implements
        OnMessageListLoadFinishedListener {

    protected Context context;
    protected int count = 0;
    private LayoutInflater inflater;
    private MessageListInfo messageListInfo = null;
    private int selected = -1;

    public MessageListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public Object getItem(int arg0) {

        MessageThreadPageInfo entry = getEntry(arg0);
        if (entry == null || entry.getMid() == 0) {
            return null;
        }

        String ret = "mid=" + entry.getMid();

        return ret;

    }

    public int getCount() {
        return count;
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int position, View view, ViewGroup parent) {

        View convertView = view;// m.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.relative_messgae_list, null);
            TextView num = (TextView) convertView.findViewById(R.id.num);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView author = (TextView) convertView.findViewById(R.id.author);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView lasttime = (TextView) convertView.findViewById(R.id.lasttime);
            TextView lastReply = (TextView) convertView
                    .findViewById(R.id.last_reply);
            holder = new ViewHolder();
            holder.num = num;
            holder.title = title;
            holder.author = author;
            holder.lastReply = lastReply;
            holder.time = time;
            holder.lasttime = lasttime;
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        ThemeManager cfg = ThemeManager.getInstance();
        int colorId = cfg.getBackgroundColor(position);
        if (position == this.selected) {
            if (cfg.mode == ThemeManager.MODE_NIGHT)
                colorId = R.color.topiclist_selected_color;
            else
                colorId = R.color.holo_blue_light;
            ;
        }
        convertView.setBackgroundResource(colorId);

        handleJsonList(holder, position);
        return convertView;

    }

    public void setSelected(int position) {
        this.selected = position;
    }

    private void handleJsonList(ViewHolder holder, int position) {
        MessageThreadPageInfo entry = getEntry(position);
        // this.topicListInfo.getArticleEntryList().get(position);

        if (entry == null) {
            return;
        }
        Resources res = inflater.getContext().getResources();
        ThemeManager theme = ThemeManager.getInstance();
        boolean night = false;
        int nightLinkColor = res.getColor(R.color.night_link_color);
        if (theme.getMode() == ThemeManager.MODE_NIGHT)
            night = true;
        String fromuser;
        fromuser = entry.getFrom_username();
        if (StringUtil.isEmpty(fromuser)) {
            fromuser = "#SYSTEM#";
        }
        holder.author.setText(fromuser);
        holder.time.setText(entry.getTime());
        holder.lasttime.setText(entry.getLastTime());
        String lastPoster = entry.getLast_from_username();
        if (StringUtil.isEmpty(lastPoster))
            lastPoster = fromuser;
        holder.lastReply.setText(lastPoster);
        holder.num.setText(String.valueOf(entry.getPosts()));
        if (night) {
            holder.author.setTextColor(nightLinkColor);
            holder.time.setTextColor(nightLinkColor);
            holder.lasttime.setTextColor(nightLinkColor);
            holder.lastReply.setTextColor(nightLinkColor);
            holder.num.setTextColor(nightLinkColor);
        }
        holder.title.setTextColor(res.getColor(theme.getForegroundColor()));
        float size = PhoneConfiguration.getInstance().getTextSize();

        String titile = entry.getSubject();
        if (StringUtil.isEmpty(titile)) {
            titile = entry.getSubject();
            holder.title.setText(StringUtil.unEscapeHtml(titile));

        } else {
            holder.title.setText(StringUtil.removeBrTag(StringUtil
                    .unEscapeHtml(titile)));
        }

        holder.title.setTextSize(size);
        final TextPaint tp = holder.title.getPaint();
        tp.setFakeBoldText(false);

    }

    protected MessageThreadPageInfo getEntry(int position) {
        if (messageListInfo != null)
            return messageListInfo.getMessageEntryList().get(position);
        return null;
    }

    @Override
    public void jsonfinishLoad(MessageListInfo result) {
        this.messageListInfo = result;
        count = messageListInfo.getMessageEntryList().size();
        this.notifyDataSetChanged();

    }

    class ViewHolder {
        public TextView num;
        public TextView title;
        public TextView author;
        public TextView lastReply;
        public TextView time;
        public TextView lasttime;

    }

}
