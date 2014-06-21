package sp.phone.adapter;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Locale;

import com.alibaba.fastjson.util.Base64;

import gov.anzong.androidnga.R;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetialInfo;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.interfaces.OnMessageDetialLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MessageDetialAdapter extends BaseAdapter implements
	OnMessageDetialLoadFinishedListener,AvatarLoadCompleteCallBack,OnLongClickListener {

	private LayoutInflater inflater;
	protected Context context;
	private MessageDetialInfo messageListInfo = null;
	private int selected = -1;
	protected int count = 0;
	static String userDistance = null;
	static String meter = null;
	static String kiloMeter = null;
	static String hide = null;
	static String legend = null;
	static String attachment = null;
	static String comment = null;
	static String sig = null;
	private final Object lock = new Object();
	private final HashSet<String> urlSet = new HashSet<String>();
	private final WebViewClient client;

	public MessageDetialAdapter(Context context) {
		this.context = context;
		client = new ArticleListWebClient((FragmentActivity) context);
		this.inflater = LayoutInflater.from(context);
		if(userDistance==null)
		initStaticStrings(context);
	}
	static class WebViewTag {
		public ListView lv;
		public View holder;
	}
	
	private void initStaticStrings(Context activity) {
		userDistance = activity.getString(R.string.user_distance);
		meter = activity.getString(R.string.meter);
		kiloMeter = activity.getString(R.string.kilo_meter);
		hide = activity.getString(R.string.hide);
		legend = activity.getString(R.string.legend);
		attachment = activity.getString(R.string.attachment);
		comment = activity.getString(R.string.comment);
		sig = activity.getString(R.string.sig);
	}



	private void handleContentTV(final WebView contentTV,
			final MessageArticlePageInfo row, int bgColor, int fgColor) {
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {

			contentTV.setLongClickable(false);
		}

		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
				|| isInWifi();

		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);

		contentTV.setTag(row.getLou());
		contentTV.loadDataWithBaseURL(null, row.getFormated_html_data(),
				"text/html", "utf-8", null);
	}
	public Object getItem(int arg0) {

		MessageArticlePageInfo entry = getEntry(arg0);
		if (entry == null) {
			return null;
		}
		return entry;
	}
	
	@Override
	public int getCount() {
		return count;
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	class ViewHolder {
		public TextView nickName;
		public TextView floor;
		public TextView postTime;
		public ImageView avatarImage;
		public WebView content;

	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View convertView = view;// m.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.relative_messagedetaillist, null);
			TextView nickName = (TextView) convertView.findViewById(R.id.nickName);
			TextView floor = (TextView) convertView.findViewById(R.id.floor);
			TextView postTime = (TextView) convertView.findViewById(R.id.postTime);
			ImageView avatarImage = (ImageView) convertView.findViewById(R.id.avatarImage);
			WebView content = (WebView) convertView.findViewById(R.id.content);
			holder = new ViewHolder();
			holder.nickName = nickName;
			holder.floor = floor;
			holder.postTime = postTime;
			holder.avatarImage = avatarImage;
			holder.content=content;
			if (ActivityUtil.isGreaterThan_2_2()) {
			holder.content.setLongClickable(false);
			}
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

		handleJsonList(holder, position, parent, convertView);
		return convertView;

	}

	public void setSelected(int position) {
		this.selected = position;
	}

	private void handleJsonList(final ViewHolder holder, int position,ViewGroup parent,View view) {
		final MessageArticlePageInfo entry = getEntry(position);
		// this.topicListInfo.getArticleEntryList().get(position);

		if (entry == null) {
			return;
		}
		Resources res = inflater.getContext().getResources();
		ThemeManager theme = ThemeManager.getInstance();
		holder.postTime.setText(entry.getTime());
		String floor = String.valueOf(entry.getLou());
		holder.floor.setText("[" + floor + " ¥]");
		holder.nickName.setTextColor(res.getColor(theme.getForegroundColor()));
		holder.postTime.setTextColor(res.getColor(theme.getForegroundColor()));
		holder.floor.setTextColor(res.getColor(theme.getForegroundColor()));
		

		handleNickName(entry,res.getColor(theme.getForegroundColor()),holder.nickName);
		handleAvatar(holder.avatarImage, entry);

		int colorId = theme.getBackgroundColor(position+1);
		final int bgColor = parent.getContext().getResources()
				.getColor(colorId);
		int fgColorId = theme.getForegroundColor();
		final int fgColor = parent.getContext().getResources()
				.getColor(fgColorId);
		view.setBackgroundResource(colorId);
		if (ActivityUtil.isLessThan_4_3()) {
			new Thread(new Runnable() {
				public void run() {
					handleContentTV(holder.content, entry, bgColor, fgColor);
				}
			}).start();
		} else {
			((Activity) parent.getContext()).runOnUiThread(new Runnable() {
				public void run() {
					handleContentTV(holder.content, entry, bgColor, fgColor);
				}
			});
		}
	}


	private static String parseAvatarUrl(String js_escap_avatar) {
		// "js_escap_avatar":"{ \"t\":1,\"l\":2,\"0\":{ \"0\":\"http://pic2.178.com/53/533387/month_1109/93ba4788cc8c7d6c75453fa8a74f3da6.jpg\",\"cX\":0.47,\"cY\":0.78},\"1\":{ \"0\":\"http://pic2.178.com/53/533387/month_1108/8851abc8674af3adc622a8edff731213.jpg\",\"cX\":0.49,\"cY\":0.68}}"
		if (null == js_escap_avatar)
			return null;

		int start = js_escap_avatar.indexOf("http");
		if (start == 0 || start == -1)
			return js_escap_avatar;
		int end = js_escap_avatar.indexOf("\"", start);//
		if (end == -1)
			end = js_escap_avatar.length();
		String ret = null;
		try {
			ret = js_escap_avatar.substring(start, end);
		} catch (Exception e) {
			Log.e("TAG", "cann't handle avatar url " + js_escap_avatar);
		}
		return ret;
	}
	
	
	private void handleNickName(MessageArticlePageInfo row, int fgColor,
			TextView nickNameTV) {

		String nickName = row.getAuthor();
		// int now = 0;
		if ("-1".equals(row.getYz()))// nuked
		{
			fgColor = nickNameTV.getResources().getColor(R.color.title_red);
			nickName += "(VIP)";
		} else if (!StringUtil.isEmpty(row.getMute_time())
				&& !"0".equals(row.getMute_time())) {
			fgColor = nickNameTV.getResources().getColor(R.color.title_orange);
			nickName += "(" + legend + ")";
		}
		nickNameTV.setText(nickName);
		TextPaint tp = nickNameTV.getPaint();
		tp.setFakeBoldText(true);// bold for Chinese character
		nickNameTV.setTextColor(fgColor);
	}
	private Bitmap defaultAvatar = null;
	private void handleAvatar(ImageView avatarIV, MessageArticlePageInfo row) {

		final int lou = row.getLou();
		final String avatarUrl = parseAvatarUrl(row.getJs_escap_avatar());//
		final String userId = row.getFrom();
		if (PhoneConfiguration.getInstance().nikeWidth < 3) {
			avatarIV.setImageBitmap(null);
			return;
		}
		if (defaultAvatar == null
				|| defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
			Resources res = avatarIV.getContext().getResources();
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			this.defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
		}

		Object tagObj = avatarIV.getTag();
		if (tagObj instanceof AvatarTag) {
			AvatarTag origTag = (AvatarTag) tagObj;
			if (origTag.isDefault == false) {
				ImageUtil.recycleImageView(avatarIV);
				// Log.d(TAG, "recycle avatar:" + origTag.lou);
			} else {
				// Log.d(TAG, "default avatar, skip recycle");
			}
		}

		AvatarTag tag = new AvatarTag(lou, true);
		avatarIV.setImageBitmap(defaultAvatar);
		avatarIV.setTag(tag);
		if (!StringUtil.isEmpty(avatarUrl)) {
			final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists() && !isPending(avatarUrl)) {

					Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
					if (bitmap != null) {
						avatarIV.setImageBitmap(bitmap);
						tag.isDefault = false;
					} else
						f.delete();
					long date = f.lastModified();
					if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
						f.delete();
					}

				} else {
					final boolean downImg = isInWifi()
							|| PhoneConfiguration.getInstance()
									.isDownAvatarNoWifi();

					new AvatarLoadTask(avatarIV, null, downImg, lou, this)
							.execute(avatarUrl, avatarPath, userId);

				}
			}
		}

	}

	private boolean isInWifi() {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}
	
	private boolean isPending(String url) {
		boolean ret = false;
		synchronized (lock) {
			ret = urlSet.contains(url);
		}
		return ret;
	}

	@Override
	public void OnAvatarLoadStart(String url) {
		synchronized (lock) {
			this.urlSet.add(url);
		}

	}

	@Override
	public void OnAvatarLoadComplete(String url) {
		synchronized (lock) {
			this.urlSet.remove(url);
		}

	}
	protected MessageArticlePageInfo getEntry(int position) {
		if (messageListInfo != null)
			return messageListInfo.getMessageEntryList().get(position);
		return null;
	}

	@Override
	public void finishLoad(MessageDetialInfo result) {
			this.messageListInfo = result;
			count = messageListInfo.getMessageEntryList().size();
			this.notifyDataSetChanged();

	}

	public static String convertToHtmlText(final MessageArticlePageInfo row,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(row.getContent(), showImage,
				imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {

			ngaHtml = "<font color='red'>[" + hide + "]</font>";
		}
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
				+ buildHeader(row, fgColorStr)
				+ "<body bgcolor= '#"
				+ bgcolorStr
				+ "'>"
				+ "<font color='#"
				+ fgColorStr
				+ "' size='2'>"
				+ ngaHtml
				+ "</font></body>";

		return ngaHtml;
	}
	private static String buildHeader(MessageArticlePageInfo row, String fgColorStr) {
		if (row == null || StringUtil.isEmpty(row.getSubject()))
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append("<h4 style='color:").append(fgColorStr).append("' >")
				.append(row.getSubject()).append("</h4>");
		return sb.toString();
	}
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		if (v instanceof WebView) {
			WebViewTag tag = (WebViewTag) v.getTag();
			tag.lv.showContextMenuForChild(tag.holder);
			return true;
		}
		return false;
	}

}
