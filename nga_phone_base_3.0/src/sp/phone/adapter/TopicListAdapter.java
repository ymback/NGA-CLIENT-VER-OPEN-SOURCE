package sp.phone.adapter;

import java.math.BigInteger;
import java.util.Locale;

import com.alibaba.fastjson.util.Base64;

import gov.anzong.androidnga2.R;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
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

public class TopicListAdapter extends BaseAdapter implements
		OnTopListLoadFinishedListener {

	private LayoutInflater inflater;
	protected Context context;
	private TopicListInfo topicListInfo = null;
	private int selected = -1;
	protected int count = 0;
	final static int _FONT_RED = 1, _FONT_BLUE = 2, _FONT_GREEN = 4,
			_FONT_ORANGE = 8, _FONT_SILVER = 16, _FONT_B = 32, _FONT_I = 64,
			_FONT_U = 128;

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
			if(font.indexOf("~")>=0){
				if (font.equals("~1~~") || font.equals("~~~1")) {
					tp.setFakeBoldText(true);
				} else {
					String miscarray[] = font.toLowerCase(Locale.US).split("~");
					for (int i = 0; i < miscarray.length; i++) {
						if (miscarray[i].equals("green")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_green));
						} else if (miscarray[i].equals("blue")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_blue));
						} else if (miscarray[i].equals("red")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_red));
						} else if (miscarray[i].equals("orange")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_orange));
						}else if (miscarray[i].equals("b")) {
							holder.title.getPaint().setFakeBoldText(true);
						}else if (miscarray[i].equals("i")) {
							holder.title.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
						}else if (miscarray[i].equals("u")) {
							holder.title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
						}
						
					}
				}
			}
		}
		if (!StringUtil.isEmpty(entry.getTopicMisc())) {
			final String misc = entry.getTopicMisc();
			if(misc.indexOf("~")>=0){
				if (misc.equals("~1~~") || misc.equals("~~~1")) {
					tp.setFakeBoldText(true);
				} else {
					String miscarray[] = misc.toLowerCase(Locale.US).split("~");
					for (int i = 0; i < miscarray.length; i++) {
						if (miscarray[i].equals("green")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_green));
						} else if (miscarray[i].equals("blue")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_blue));
						} else if (miscarray[i].equals("red")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_red));
						} else if (miscarray[i].equals("orange")) {
							holder.title.setTextColor(res
									.getColor(R.color.title_orange));
						}else if (miscarray[i].equals("b")) {
							holder.title.getPaint().setFakeBoldText(true);
						}else if (miscarray[i].equals("i")) {
							holder.title.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
						}else if (miscarray[i].equals("u")) {
							holder.title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
						}
						
					}
				}
			}else{
				byte b[]=Base64.decodeFast(misc);
				if(b!=null){
					if(b.length==5){
						String miscstring=toBinary(b);
						String miscstringstart=miscstring.substring(0,8);
				        BigInteger src1= new BigInteger(miscstringstart,2);//转换为BigInteger类型
				        int d1 = src1.intValue();
				        if(d1==1){
				        	String miscstringend=miscstring.substring(8,miscstring.length());
				            BigInteger src2= new BigInteger(miscstringend,2);//转换为BigInteger类型
				            int d2 = src2.intValue();
				            if ((d2 & _FONT_GREEN)==_FONT_GREEN) {
								holder.title.setTextColor(res
										.getColor(R.color.title_green));
							} else if ((d2 & _FONT_BLUE)==_FONT_BLUE) {
								holder.title.setTextColor(res
										.getColor(R.color.title_blue));
							} else if ((d2 & _FONT_RED)==_FONT_RED) {
								holder.title.setTextColor(res
										.getColor(R.color.title_red));
							} else if ((d2 & _FONT_ORANGE)==_FONT_ORANGE) {
								holder.title.setTextColor(res
										.getColor(R.color.title_orange));
							} else if ((d2 & _FONT_SILVER)==_FONT_SILVER) {
								holder.title.setTextColor(res
										.getColor(R.color.silver));
							}
				            if ((d2 & _FONT_B)==_FONT_B) {
								holder.title.getPaint().setFakeBoldText(true);
							}
				            if ((d2 & _FONT_I)==_FONT_I) {
								holder.title.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
							}
				            if ((d2 & _FONT_U)==_FONT_U) {
								holder.title.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
							}
				            
				        }
					}
				}
			}
		}

	}


	private String toBinary( byte[] bytes )
	{
	    StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
	    for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
	        sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    return sb.toString();
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
