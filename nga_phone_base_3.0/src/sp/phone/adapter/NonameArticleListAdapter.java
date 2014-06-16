package sp.phone.adapter;

import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.PostActivity;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import noname.gson.parse.NonameReadBody;
import noname.gson.parse.NonameReadResponse;
import android.support.v4.app.Fragment;
import sp.phone.bean.Attachment;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.task.AvatarLoadTask;
//import sp.phone.task.ForumTagDecodTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.Des;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.R.integer;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NonameArticleListAdapter extends BaseAdapter implements
		OnLongClickListener {
	private static final String TAG = NonameArticleListAdapter.class.getSimpleName();
	private NonameReadResponse data;
	private static Context activity;
	private final SparseArray<SoftReference<View>> viewCache;
	private final Object lock = new Object();
	private final HashSet<String> urlSet = new HashSet<String>();
	static String userDistance = null;
	static String meter = null;
	static String kiloMeter = null;
	static String hide = null;
	static String legend = null;
	static String attachment = null;
	static String comment = null;
	static String sig = null;

	public NonameArticleListAdapter(Context activity) {
		super();
		this.activity = activity;
		this.viewCache = new SparseArray<SoftReference<View>>();
		client = new ArticleListWebClient((FragmentActivity) activity);
		if (userDistance == null)
			initStaticStrings(activity);
	}

	@Override
	public int getCount() {
		if (null == data)
			return 0;
		return data.data.posts.length;
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

	public void setData(NonameReadResponse data) {
		this.data = data;
	}

	public NonameReadResponse getData() {
		return data;
	}

	@Override
	public Object getItem(int position) {
		if (null == data)
			return null;

		return data.data.posts[position];
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	static class ViewHolder {
		TextView nickNameTV;
		WebView contentTV;
		TextView floorTV;
		TextView postTimeTV;
		int position = -1;
		ImageButton viewBtn;

	}

	static class WebViewTag {
		public ListView lv;
		public View holder;
	}

	@TargetApi(11)
	void setLayerType(WebView contentTV) {
		contentTV.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	}

	private static String buildHeader(NonameReadBody row, String fgColorStr) {
		if (row == null || StringUtil.isEmpty(row.title))
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append("<h4 style='color:").append(fgColorStr).append("' >")
				.append(row.title).append("</h4>");
		return sb.toString();
	}


	public static String distanceString(long distance) {
		String ret = Long.valueOf(distance).toString() + meter;
		if (distance > 1000) {
			ret = Long.valueOf(distance / 1000).toString() + kiloMeter;
		}
		return ret;
	}

	public static String convertToHtmlText(final NonameReadBody row,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(row.content.replaceAll("\n", "<br/>"), showImage,
				imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {

			ngaHtml = "<font color='red'>[" + hide + "]</font>";
		}
		ngaHtml = ngaHtml;
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

	private void handleContentTV(final WebView contentTV,
			final NonameReadBody row, int bgColor, int fgColor) {
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {

			contentTV.setLongClickable(false);
		}

		/*
		 * bgColor = bgColor & 0xffffff; final String bgcolorStr =
		 * String.format("%06x",bgColor);
		 * 
		 * int htmlfgColor = fgColor & 0xffffff; final String fgColorStr =
		 * String.format("%06x",htmlfgColor); if(row.getContent()== null){
		 * row.setContent(row.getSubject()); row.setSubject(null); }
		 */

		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
				|| isInWifi();

		WebSettings setting = contentTV.getSettings();
		// setting.setBlockNetworkImage(!showImage);
		// the network image url already replaced by local icon. this should not
		// be called and
		// webview will not work properly in android 4.4.
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);

		contentTV.setTag(row.floor);
		contentTV.loadDataWithBaseURL(null, fillFormated_html_data(row),
				"text/html", "utf-8", null);

		/*
		 * ForumTagDecodTask task= new ForumTagDecodTask(row, showImage,
		 * fgColorStr, bgcolorStr); if(ActivityUtil.isGreaterThan_2_3_3()){
		 * excuteOnExcutor(task,contentTV); }else{ task.execute(contentTV); }
		 */

	}


	private String fillFormated_html_data(NonameReadBody row) {

		ThemeManager theme = ThemeManager.getInstance();
		
		int bgColor = activity.getResources().getColor(
				theme.getBackgroundColor(row.floor%2));
		int fgColor = activity.getResources().getColor(
				theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x", bgColor);

		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x", htmlfgColor);

		String formated_html_data = convertToHtmlText(row,
				isShowImage(), showImageQuality(), fgColorStr, bgcolorStr);
		return formated_html_data;
	}
	
	/*
	 * @TargetApi(11) private void excuteOnExcutor(ForumTagDecodTask task,
	 * WebView contentTV){
	 * task.executeOnExecutor(ForumTagDecodTask.THREAD_POOL_EXECUTOR,
	 * contentTV); }
	 */

	private boolean isShowImage() {
		return PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
	}


	public static int showImageQuality() {
		if (isInWifi()) {
			return 0;
		} else {
			return PhoneConfiguration.getInstance().imageQuality;
		}
	}
	public static boolean isInWifi() {
		ConnectivityManager conMan = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}

	private final WebViewClient client;

	private Bitmap defaultAvatar = null;
	
	private ViewHolder initHolder(final View view) {
		final ViewHolder holder = new ViewHolder();
		holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);

		holder.floorTV = (TextView) view.findViewById(R.id.floor);
		holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
		new Thread(new Runnable() {
			public void run() {
				holder.contentTV = (WebView) view.findViewById(R.id.content);
			}
		}).run();
		/*
		 * holder.levelTV = (TextView) view.findViewById(R.id.level);
		 * holder.aurvrcTV= (TextView) view.findViewById(R.id.aurvrc);
		 * holder.postnumTV = (TextView) view.findViewById(R.id.postnum);
		 */
		return holder;
	}

	public static String TimeStamp2Date(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date(timestamp));
		return date;
	}
	
	private class MyListenerForReply implements OnClickListener {
		int mPosition;
		private View button;
		private long lastTimestamp = 0;

		public MyListenerForReply(int inPosition) {
			mPosition = inPosition;
		}

		@Override
		public void onClick(View v) {

			if (System.currentTimeMillis() - this.lastTimestamp <= 2000) {
				return;
			} else {
				this.lastTimestamp = System.currentTimeMillis();
			}

			this.button = v;
			this.button.setEnabled(false);

			(new AsyncTask<Void, Void, Void>() {

				@Override
				protected void onPostExecute(Void result) {
					MyListenerForReply.this.button.setEnabled(true);
				}

				@Override
				protected Void doInBackground(Void... params) {
					Intent intent = new Intent();
					StringBuffer postPrefix = new StringBuffer();
					String mention = null;

					final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
					final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
					NonameReadBody row = data.data.posts[mPosition];
					String content = row.content;
					final String name = row.hip;
					content = content.replaceAll(quote_regex, "");
					content = content.replaceAll(replay_regex, "");
					final long longposttime = row.ptime;
					String postTime ="";
					if(longposttime!=0){
						postTime = TimeStamp2Date(String.valueOf(longposttime));
					}
					final String tidStr = String.valueOf(data.data.tid);
					content = checkContent(content);
					content = StringUtil.unEscapeHtml(content);
					mention = name;
					postPrefix.append("[quote]");
					postPrefix.append("Reply");
					postPrefix.append(" [b]Post by [hip]");
					postPrefix.append(name);
					postPrefix.append("[/hip] (");
					postPrefix.append(postTime);
					postPrefix.append("):[/b]\n");
					postPrefix.append(content);
					postPrefix.append("[/quote]\n");
					if (!StringUtil.isEmpty(mention))
						intent.putExtra("mention", mention);
					intent.putExtra("prefix",
							StringUtil.removeBrTag(postPrefix.toString()));
					if (tidStr != null)
						intent.putExtra("tid", tidStr);
					intent.putExtra("action", "reply");

					if (!StringUtil
							.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
						intent.setClass(
								activity,
								PhoneConfiguration.getInstance().nonamePostActivityClass);
					} else {
						intent.setClass(
								activity,
								PhoneConfiguration.getInstance().loginActivityClass);
					}
					activity.startActivity(intent);
					if (PhoneConfiguration.getInstance().showAnimation)
						((Activity) activity).overridePendingTransition(
								R.anim.zoom_enter, R.anim.zoom_exit);
					return null;
				}
			}).execute();
		}

	}

	private String checkContent(String content) {
		int i;
		boolean mode = false;
		content = content.trim();
		String quotekeyword[][] = {
				{ "[customachieve]", "[/customachieve]" },// 0
				{ "[wow", "]]" },
				{ "[lol", "]]" },
				{ "[cnarmory", "]" },
				{ "[usarmory", "]" },
				{ "[twarmory", "]" },// 5
				{ "[euarmory", "]" },
				{ "[url", "[/url]" },
				{ "[color=", "[/color]" },
				{ "[size=", "[/size]" },
				{ "[font=", "[/font]" },// 10
				{ "[b]", "[/b]" },
				{ "[u]", "[/u]" },
				{ "[i]", "[/i]" },
				{ "[del]", "[/del]" },
				{ "[align=", "[/align]" },// 15
				{ "[h]", "[/h]" },
				{ "[l]", "[/l]" },
				{ "[r]", "[/r]" },
				{ "[list", "[/list]" },
				{ "[img]", "[/img]" },// 20
				{ "[album=", "[/album]" },
				{ "[code]", "[/code]" },
				{ "[code=lua]", "[/code] lua" },
				{ "[code=php]", "[/code] php" },
				{ "[code=c]", "[/code] c" },// 25
				{ "[code=js]", "[/code] javascript" },
				{ "[code=xml]", "[/code] xml/html" },
				{ "[flash]", "[/flash]" },
				{ "[table]", "[/table]" },
				{ "[tid", "[/tid]" },// 30
				{ "[pid", "[/pid]" }, { "[dice]", "[/dice]" },
				{ "[crypt]", "[/crypt]" },
				{ "[randomblock]", "[/randomblock]" }, { "[@", "]" },
				{ "[t.178.com/", "]" }, { "[collapse", "[/collapse]" }, };
		while (content.startsWith("\n")) {
			content = content.replaceFirst("\n", "");
		}
		if (content.length() > 100) {
			content = content.substring(0, 99);
			mode = true;
		}
		for (i = 0; i < 38; i++) {
			while (content.toLowerCase().lastIndexOf(quotekeyword[i][0]) > content
					.toLowerCase().lastIndexOf(quotekeyword[i][1])) {
				content = content.substring(0, content.toLowerCase()
						.lastIndexOf(quotekeyword[i][0]));
			}
		}
		if (mode) {
			content = content + "......";
		}
		return content.toString();
	}

	public View getView(int position, View view, ViewGroup parent) {
		MyListenerForReply myListenerForReply = null;
		final NonameReadBody row = data.data.posts[position];

		int lou = -1;
		if (row != null)
			lou = row.floor;
		ViewHolder holder = null;
		PhoneConfiguration config = PhoneConfiguration.getInstance();

		SoftReference<View> ref = viewCache.get(position);
		View cachedView = null;
		if (ref != null) {
			cachedView = ref.get();
		}
		if (cachedView != null) {
			// Log.d(TAG, "get view from cache ,floor " + lou);
			return cachedView;
		} else {
			// if(ref != null)
			// Log.i(TAG, "cached view recycle by system:" + lou);
			if (view == null || config.useViewCache) {
				// Log.d(TAG, "inflater new view ,floor " + lou);
				myListenerForReply = new MyListenerForReply(position);

				view = LayoutInflater.from(activity).inflate(
						R.layout.relative_nonamearitclelist, parent, false);
				WebView webView = (WebView) view.findViewById(R.id.content);
				webView.setHorizontalScrollBarEnabled(false);
				holder = initHolder(view);
				holder.viewBtn = (ImageButton) view
						.findViewById(R.id.listviewreplybtn);
				view.setTag(holder);
				if (config.useViewCache)
					viewCache.put(position, new SoftReference<View>(view));
			} else {
				holder = (ViewHolder) view.getTag();
				if (holder.position == position) {
					return view;
				}
				holder.contentTV.stopLoading();
				if (holder.contentTV.getHeight() > 300) {
					// Log.d(TAG, "skip and store a tall view ,floor " + lou);
					// if (config.useViewCache)
					viewCache.put(holder.position,
							new SoftReference<View>(view));

					view = LayoutInflater.from(activity).inflate(
							R.layout.relative_aritclelist, parent, false);
					WebView webView = (WebView) view.findViewById(R.id.content);
					webView.setHorizontalScrollBarEnabled(false);
					holder = initHolder(view);
					view.setTag(holder);

				}

			}

		}

		if (!PhoneConfiguration.getInstance().showReplyButton) {
			holder.viewBtn.setVisibility(View.GONE);
		} else {
			holder.viewBtn.setOnClickListener(myListenerForReply);
		}
		holder.position = position;
		ThemeManager theme = ThemeManager.getInstance();
		int colorId = theme.getBackgroundColor(position);
		view.setBackgroundResource(colorId);

		// colorId = theme.getBackgroundColor(2);

		if (row == null) {
			return view;
		}

		int fgColorId = ThemeManager.getInstance().getForegroundColor();
		final int fgColor = parent.getContext().getResources()
				.getColor(fgColorId);

		handleNickName(row, fgColor, holder.nickNameTV);

		/*
		 * TextView titleTV = holder.titleTV; if
		 * (!StringUtil.isEmpty(row.getSubject()) ) {
		 * titleTV.setText(StringUtil.unEscapeHtml(row.getSubject()));
		 * titleTV.setTextColor(fgColor);
		 * 
		 * }
		 */

		final int bgColor = parent.getContext().getResources()
				.getColor(colorId);

		final WebView contentTV = holder.contentTV;

		final String floor = String.valueOf(lou);
		TextView floorTV = holder.floorTV;
		floorTV.setText("[" + floor + " 楼]");
		floorTV.setTextColor(fgColor);

		if (ActivityUtil.isLessThan_4_3()) {
			new Thread(new Runnable() {
				public void run() {
					handleContentTV(contentTV, row, bgColor, fgColor);
				}
			}).start();
		} else {
			((Activity) parent.getContext()).runOnUiThread(new Runnable() {
				public void run() {
					handleContentTV(contentTV, row, bgColor, fgColor);
				}
			});
		}
		final long longposttime = row.ptime;
		String postTime ="";
		if(longposttime!=0){
			postTime = TimeStamp2Date(String.valueOf(longposttime));
		}
		TextView postTimeTV = holder.postTimeTV;
		postTimeTV.setText(postTime);
		postTimeTV.setTextColor(fgColor);
		return view;
	}

	private void handleNickName(NonameReadBody row, int fgColor,
			TextView nickNameTV) {

		String nickName = row.hip;
		nickNameTV.setText(nickName);
		TextPaint tp = nickNameTV.getPaint();
		tp.setFakeBoldText(true);// bold for Chinese character
		nickNameTV.setTextColor(fgColor);
	}



	@Override
	public void notifyDataSetChanged() {
		this.viewCache.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public boolean onLongClick(View v) {
		if (v instanceof WebView) {
			WebViewTag tag = (WebViewTag) v.getTag();
			tag.lv.showContextMenuForChild(tag.holder);
			return true;
		}
		return false;
	}

	private boolean isPending(String url) {
		boolean ret = false;
		synchronized (lock) {
			ret = urlSet.contains(url);
		}
		return ret;
	}



}
