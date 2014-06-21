package sp.phone.adapter;

import java.math.BigInteger;
import java.util.Locale;

import noname.gson.parse.NonameThreadBody;
import noname.gson.parse.NonameThreadResponse;

import com.alibaba.fastjson.util.Base64;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnNonameTopListLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NonameTopicListAdapter extends BaseAdapter implements
	OnNonameTopListLoadFinishedListener {

	private LayoutInflater inflater;
	protected Context context;
	private NonameThreadResponse topicListInfo = null;
	private int selected = -1;
	protected int count = 0;

	public NonameTopicListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public Object getItem(int arg0) {

		NonameThreadBody entry = getEntry(arg0);

		return entry;

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
		NonameThreadBody entry = getEntry(position);
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
		holder.author.setText(entry.hip);
		if (night)
			holder.author.setTextColor(nightLinkColor);

		String lastPoster = entry.replyhip;
		if (StringUtil.isEmpty(lastPoster))
			lastPoster = entry.hip;
		holder.lastReply.setText(lastPoster);
		holder.num.setText(String.valueOf(entry.nreply));
		if (night) {
			holder.lastReply.setTextColor(nightLinkColor);
			holder.num.setTextColor(nightLinkColor);
		}
		holder.title.setTextColor(res.getColor(theme.getForegroundColor()));
		float size = PhoneConfiguration.getInstance().getTextSize();

		String titile = entry.title;
		if (StringUtil.isEmpty(titile)) {
			titile = "����";
			holder.title.setText(StringUtil.unEscapeHtml(titile));

		} else {
			holder.title.setText(StringUtil.removeBrTag(StringUtil
					.unEscapeHtml(titile)));
		}

		holder.title.setTextSize(size);
		final TextPaint tp = holder.title.getPaint();
		tp.setFakeBoldText(false);
	}



	public static String TimeStamp2Date(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date(timestamp));
		return date;
	}
	
	protected NonameThreadBody getEntry(int position) {
		if (topicListInfo != null)
			return topicListInfo.data.threads[position];
		return null;
	}

	@Override
	public void jsonfinishLoad(NonameThreadResponse result) {
			this.topicListInfo = result;
			count = topicListInfo.data.threads.length;
			this.notifyDataSetChanged();

	}

}
