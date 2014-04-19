package sp.phone.adapter;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TopicListAdapter extends BaseAdapter implements
		OnTopListLoadFinishedListener {

	private LayoutInflater inflater;
	protected Context context;
	private TopicListInfo topicListInfo = null;
	private int selected = -1;
	protected int count = 0;

	public TopicListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public Object getItem(int arg0) {

		ThreadPageInfo entry = getEntry(arg0);
		if (entry == null || entry.getTid() == 0) {
			return null;
		}

		String ret = "tid=" + entry.getTid();
		if (entry.getPid() != 0) {
			return ret + "&pid=" + entry.getPid();
		}

		return ret;

	}

	public int getCount() {
		return count;
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	class ViewHolder {
		public TextView num;
		public TextView title;
		public TextView author;
		public TextView lastReply;

	}

	public View getView(int position, View view, ViewGroup parent) {

		View convertView = view;// m.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.relative_topic_list, null);
			TextView num = (TextView) convertView.findViewById(R.id.num);
			TextView title = (TextView) convertView.findViewById(R.id.title);
			TextView author = (TextView) convertView.findViewById(R.id.author);
			TextView lastReply = (TextView) convertView
					.findViewById(R.id.last_reply);
			holder = new ViewHolder();
			holder.num = num;
			holder.title = title;
			holder.author = author;
			holder.lastReply = lastReply;
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
		ThreadPageInfo entry = getEntry(position);
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
		holder.author.setText(entry.getAuthor());
		if (night)
			holder.author.setTextColor(nightLinkColor);

		String lastPoster = entry.getLastposter_org();
		if (StringUtil.isEmpty(lastPoster))
			lastPoster = entry.getLastposter();
		holder.lastReply.setText(lastPoster);
		holder.num.setText("" + entry.getReplies());
		if (night) {
			holder.lastReply.setTextColor(nightLinkColor);
			holder.num.setTextColor(nightLinkColor);
		}
		holder.title.setTextColor(res.getColor(theme.getForegroundColor()));
		float size = PhoneConfiguration.getInstance().getTextSize();

		String titile = entry.getContent();
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

		if (!StringUtil.isEmpty(entry.getTitlefont())) {
			final String font = entry.getTitlefont();
			if (font.equals("~1~~") || font.equals("~~~1")) {
				tp.setFakeBoldText(true);
			} else if (font.startsWith("green")) {
				holder.title.setTextColor(res.getColor(R.color.title_green));
			} else if (font.startsWith("blue")) {
				holder.title.setTextColor(res.getColor(R.color.title_blue));
			} else if (font.startsWith("red")) {
				holder.title.setTextColor(res.getColor(R.color.title_red));
			} else if (font.startsWith("orange")) {
				holder.title.setTextColor(res.getColor(R.color.title_orange));
			}
		}

	}

	protected ThreadPageInfo getEntry(int position) {
		if (topicListInfo != null)
			return topicListInfo.getArticleEntryList().get(position);
		return null;
	}

	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		if (!result.get__SEARCHNORESULT()) {
			this.topicListInfo = result;
			count = topicListInfo.get__T__ROWS();
			this.notifyDataSetChanged();
		}

	}

}
