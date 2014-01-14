package sp.phone.adapter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.PostActivity;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

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
import android.view.Display;
import android.view.Gravity;
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
import android.widget.Toast;

public class ArticleListAdapter extends BaseAdapter implements
		OnLongClickListener, AvatarLoadCompleteCallBack {
	private static final String TAG = ArticleListAdapter.class.getSimpleName();
	private ThreadData data;
	private Context activity;
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

	public ArticleListAdapter(Context activity) {
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
		return data.getRowNum();
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

	public void setData(ThreadData data) {
		this.data = data;
	}

	public ThreadData getData() {
		return data;
	}

	@Override
	public Object getItem(int position) {
		if (null == data)
			return null;

		return data.getRowList().get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	private boolean isInWifi() {
		ConnectivityManager conMan = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}

	static class ViewHolder {
		TextView nickNameTV;
		ImageView avatarIV;
		WebView contentTV;
		TextView floorTV;
		TextView postTimeTV;
		TextView levelTV;
		TextView aurvrcTV;
		TextView postnumTV;
		int position = -1;
		ImageButton viewBtn;
		ImageButton clientBtn;

	}

	static class WebViewTag {
		public ListView lv;
		public View holder;
	}

	@TargetApi(11)
	void setLayerType(WebView contentTV) {
		contentTV.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
	}

	private static String buildHeader(ThreadRowInfo row, String fgColorStr) {
		if (row == null || StringUtil.isEmpty(row.getSubject()))
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append("<h4 style='color:").append(fgColorStr).append("' >")
				.append(row.getSubject()).append("</h3>");
		return sb.toString();
	}

	private static String buildLocation(final ThreadRowInfo row) {
		PhoneConfiguration config = PhoneConfiguration.getInstance();

		String authorid = Integer.valueOf(row.getAuthorid()).toString();
		if (config.location == null || config.uploadLocation == false
				|| authorid.equals(config.uid) || row.getContent() == null) {
			return "";
		}
		if (!row.getContent().endsWith("[/url]")) {
			return "";
		}
		int quote_pos = -1;
		quote_pos = row.getContent().lastIndexOf("[/quote]");
		String startTag = "https://play.google.com/store/apps/details?id=gov.anzong.androidnga&amp;";
		int start = -1;
		int end = -1;
		String endStr = "ffff]----sent from my";
		start = row.getContent().lastIndexOf(startTag);
		end = row.getContent().lastIndexOf(endStr);
		if (quote_pos > start || start == -1 || end == -1 || start >= end) {
			return "";
		}
		String loc = row.getContent().substring(start + startTag.length(), end);
		try {
			loc = Des.deCrypto(loc, StringUtil.key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		String locs[] = loc.split(",");
		if (locs == null || locs.length != 2) {
			return "";
		}
		String encodedName = "";
		try {
			encodedName = URLEncoder.encode(row.getAuthor(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long distance = ActivityUtil.distanceBetween(config.location, locs[0],
				locs[1]);
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"https://maps.google.com.hk/?ie=UTF8&hl=zh-cn&q=")
				.append(loc).append("(").append(encodedName).append(")\"")
				.append(" >").append(userDistance)
				.append(distanceString(distance)).append("</a></br>");
		return sb.toString();
	}

	public static String distanceString(long distance) {
		String ret = Long.valueOf(distance).toString() + meter;
		if (distance > 1000) {
			ret = Long.valueOf(distance / 1000).toString() + kiloMeter;
		}
		return ret;
	}

	public static String convertToHtmlText(final ThreadRowInfo row,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(row.getContent(), showImage,
				imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {
			ngaHtml = row.getAlterinfo();
		}
		if (StringUtil.isEmpty(ngaHtml)) {

			ngaHtml = "<font color='red'>[" + hide + "]</font>";
		}
		ngaHtml = ngaHtml + buildComment(row, fgColorStr)
				+ buildAttachment(row, showImage, imageQuality, imageURLSet)
				+ buildSignature(row, showImage, imageQuality);
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
				+ buildHeader(row, fgColorStr)
				+ "<body bgcolor= '#"
				+ bgcolorStr
				+ "'>"
				+ "<font color='#"
				+ fgColorStr
				+ "' size='2'>"
				+ buildLocation(row)
				+ ngaHtml
				+ "</font></body>";

		return ngaHtml;
	}

	private void handleContentTV(final WebView contentTV,
			final ThreadRowInfo row, int bgColor, int fgColor) {

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
		//setting.setBlockNetworkImage(!showImage);
        // the network image url already replaced by local icon. this should not be called and
        // webview will not work properly in android 4.4.
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);

		contentTV.setTag(row.getLou());
		contentTV.loadDataWithBaseURL(null, row.getFormated_html_data(),
				"text/html", "utf-8", null);

		/*
		 * ForumTagDecodTask task= new ForumTagDecodTask(row, showImage,
		 * fgColorStr, bgcolorStr); if(ActivityUtil.isGreaterThan_2_3_3()){
		 * excuteOnExcutor(task,contentTV); }else{ task.execute(contentTV); }
		 */

	}

	/*
	 * @TargetApi(11) private void excuteOnExcutor(ForumTagDecodTask task,
	 * WebView contentTV){
	 * task.executeOnExecutor(ForumTagDecodTask.THREAD_POOL_EXECUTOR,
	 * contentTV); }
	 */

	private final WebViewClient client;

	private Bitmap defaultAvatar = null;

	private void handleAvatar(ImageView avatarIV, ThreadRowInfo row) {

		final int lou = row.getLou();
		final String avatarUrl = parseAvatarUrl(row.getJs_escap_avatar());//
		final String userId = String.valueOf(row.getAuthorid());
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

	private ViewHolder initHolder(View view) {
		ViewHolder holder = new ViewHolder();
		holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);
		holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
		holder.contentTV = (WebView) view.findViewById(R.id.content);
		holder.floorTV = (TextView) view.findViewById(R.id.floor);
		holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
		/*
		 * holder.levelTV = (TextView) view.findViewById(R.id.level);
		 * holder.aurvrcTV= (TextView) view.findViewById(R.id.aurvrc);
		 * holder.postnumTV = (TextView) view.findViewById(R.id.postnum);
		 */
		return holder;
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
			
			if (System.currentTimeMillis() - this.lastTimestamp <= 2000)
			{
				return;
			}
			else
			{
				this.lastTimestamp = System.currentTimeMillis();
			}

			this.button = v;
			this.button.setEnabled(false);
			
			(new AsyncTask<Void, Void, Void>() {

				@Override
				protected void onPostExecute(Void result)
				{
					MyListenerForReply.this.button.setEnabled(true);
				}
				
				@Override
				protected Void doInBackground(Void... params) {
					Intent intent = new Intent();
					StringBuffer postPrefix = new StringBuffer();
					String mention = null;

					final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
					final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
					ThreadRowInfo row = data.getRowList().get(mPosition);
					String content = row.getContent();
					final String name = row.getAuthor();
					int page = (row.getLou() + 20) / 20;// 以楼数计算page
					content = content.replaceAll(quote_regex, "");
					content = content.replaceAll(replay_regex, "");
					final String postTime = row.getPostdate();
					final String tidStr = String.valueOf(row.getTid());
					content = checkContent(content);
					content = StringUtil.unEscapeHtml(content);
					if (row.getPid() != 0) {
						mention = name;
						postPrefix.append("[quote][pid=");
						postPrefix.append(row.getPid());
						postPrefix.append(',');
						if (tidStr != null) {
							postPrefix.append(tidStr);
							postPrefix.append(",");
						}
						if (page > 0)
							postPrefix.append(page);
						postPrefix.append("]");// Topic
						postPrefix.append("Reply");
						postPrefix.append("[/pid] [b]Post by ");
						postPrefix.append(name);
						postPrefix.append(" (");
						postPrefix.append(postTime);
						postPrefix.append("):[/b]\n");
						postPrefix.append(content);
						postPrefix.append("[/quote]\n");
					}
					if (!StringUtil.isEmpty(mention))
						intent.putExtra("mention", mention);
					intent.putExtra("prefix",
							StringUtil.removeBrTag(postPrefix.toString()));
					if (tidStr != null)
						intent.putExtra("tid", tidStr);
					intent.putExtra("action", "reply");
					intent.setClass(activity,
							PhoneConfiguration.getInstance().postActivityClass);
					activity.startActivity(intent);
					if (PhoneConfiguration.getInstance().showAnimation)
						((Activity) activity).overridePendingTransition(
								R.anim.zoom_enter, R.anim.zoom_exit);
					return null;
				}
			}).execute();
		}

	}

	private class MyListenerForClient implements OnClickListener {
		int mPosition;
		private View button;
		private long lastTimestamp = 0;

		public MyListenerForClient(int inPosition) {
			mPosition = inPosition;
		}

		@Override
		public void onClick(View v) {
			
			ThreadRowInfo row = data.getRowList().get(mPosition);
			String from_client = row.getFromClient();
			String deviceinfo = null;
			if(from_client.indexOf(" ")>0){
				String clientappcode=from_client.substring(0,from_client.indexOf(" "));
				if(clientappcode.equals("1")){
					if(from_client.length()==2){
						deviceinfo="发送自Life Style苹果客户端 机型及系统:未知";
					}else{
						deviceinfo="发送自Life Style苹果客户端 机型及系统:"+from_client.substring(2);
					}
				}else if(clientappcode.equals("8")){
					if(from_client.length()==2){
						deviceinfo="发送自178版NGA客户端 机型及系统:未知";
					}else{
						deviceinfo="发送自178版NGA客户端 机型及系统:"+from_client.substring(2);
					}
				}else if(clientappcode.equals("100")){
					if(from_client.length()==4){
						deviceinfo="发送自安卓客户端 机型及系统:未知";
					}else{
						deviceinfo="发送自安卓客户端 机型及系统:"+from_client.substring(4);
					}
				}else if(clientappcode.equals("101")){
					if(from_client.length()==4){
						deviceinfo="发送自苹果客户端 机型及系统:未知";
					}else{
						deviceinfo="发送自苹果客户端 机型及系统:"+from_client.substring(4);
					}
				}else if(clientappcode.equals("102")){
					if(from_client.length()==4){
						deviceinfo="发送自Blackberry客户端 机型及系统:未知";
					}else{
						deviceinfo="发送自Blackberry客户端 机型及系统:"+from_client.substring(4);
					}
				}else if(clientappcode.equals("103")){
					if(from_client.length()==4){
						deviceinfo="发送自Windows Phone客户端 机型及系统:未知";
					}else{
						deviceinfo="发送自Windows Phone客户端 机型及系统:"+from_client.substring(4);
					}
				}else{
					if(from_client.length()==(from_client.indexOf(" ")+1)){
						deviceinfo="发送自未知客户端 机型及系统:未知";
					}else{
						deviceinfo="发送自未知客户端 机型及系统:"+from_client.substring(from_client.indexOf(" ")+1);
					}
				}
			    Dialog dialog = new Dialog(activity, R.style.ClientDialog);  
			    dialog.setContentView(R.layout.client_dialog);
			    TextView textview=(TextView)  dialog.findViewById(R.id.client_device_dialog);
			    textview.setText(deviceinfo);

		        Window dialogWindow = dialog.getWindow();
		        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

		        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		        lp.width = (int)(wm.getDefaultDisplay().getWidth()); //设置宽度
		        dialog.getWindow().setAttributes(lp); 
			    dialog.show();  
			    dialog.setCanceledOnTouchOutside(true);
			}

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
		MyListenerForClient myListenerForClient = null;
		ThreadRowInfo row = data.getRowList().get(position);

		int lou = -1;
		if (row != null)
			lou = row.getLou();
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
				myListenerForClient = new MyListenerForClient(position);
				view = LayoutInflater.from(activity).inflate(
						R.layout.relative_aritclelist, parent, false);
				holder = initHolder(view);
				holder.viewBtn = (ImageButton) view.findViewById(R.id.listviewreplybtn);
				holder.clientBtn = (ImageButton) view.findViewById(R.id.clientbutton);
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
					holder = initHolder(view);
					view.setTag(holder);

				}

			}

		}


		if(!PhoneConfiguration.getInstance().showReplyButton){
			holder.viewBtn.setVisibility(View.GONE);
		}else{
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

		handleAvatar(holder.avatarIV, row);

		int fgColorId = ThemeManager.getInstance().getForegroundColor();
		int fgColor = parent.getContext().getResources().getColor(fgColorId);

		handleNickName(row, fgColor, holder.nickNameTV);

		/*
		 * TextView titleTV = holder.titleTV; if
		 * (!StringUtil.isEmpty(row.getSubject()) ) {
		 * titleTV.setText(StringUtil.unEscapeHtml(row.getSubject()));
		 * titleTV.setTextColor(fgColor);
		 * 
		 * }
		 */

		int bgColor = parent.getContext().getResources().getColor(colorId);

		WebView contentTV = holder.contentTV;
		handleContentTV(contentTV, row, bgColor, fgColor);

		final String floor = String.valueOf(lou);
		TextView floorTV = holder.floorTV;
		floorTV.setText("[" + floor + " 楼]");
		floorTV.setTextColor(fgColor);

		if(row.getFromClient()!=null && !row.getFromClient().trim().equals("")){
			String from_client = row.getFromClient();
			if(from_client.indexOf(" ")>0){
				String clientappcode=from_client.substring(0,from_client.indexOf(" "));
			if(clientappcode.equals("1") || clientappcode.equals("101")){
				holder.clientBtn.setImageResource(R.drawable.ios);//IOS
			}else if(clientappcode.equals("103")){
				holder.clientBtn.setImageResource(R.drawable.wp);//候总
			}else if(!clientappcode.equals("8")&&!clientappcode.equals("100")){
				holder.clientBtn.setImageResource(R.drawable.unkonwn);//未知orBB
			}
			holder.clientBtn.setVisibility(View.VISIBLE);
			holder.clientBtn.setOnClickListener(myListenerForClient);}
		}
		TextView postTimeTV = holder.postTimeTV;
		postTimeTV.setText(row.getPostdate());
		postTimeTV.setTextColor(fgColor);
		return view;
	}

	private void handleNickName(ThreadRowInfo row, int fgColor,
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

	private static String buildAttachment(ThreadRowInfo row, boolean showImage,
			int imageQuality, HashSet<String> imageURLSet) {

		if (row == null || row.getAttachs() == null
				|| row.getAttachs().size() == 0) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		ret.append("<br/><br/>").append(attachment).append("<hr/><br/>");
		// ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;margin:0px 0px 10px 30px;padding:10px;color:#6b2d25;max-width:100%;'>");
		ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:2'>");
		ret.append("<tbody>");
		Iterator<Entry<String, Attachment>> it = row.getAttachs().entrySet()
				.iterator();
		int attachmentCount = 0;
		while (it.hasNext()) {
			Entry<String, Attachment> entry = it.next();
			if (imageURLSet != null && imageURLSet.size() > 0
					&& imageURLSet.contains(entry.getValue().getAttachurl())) {
				continue;
			}
			// String url = "http://img.ngacn.cc/attachments/" +
			// entry.getValue().getAttachurl();
			ret.append("<tr><td><a href='http://"
					+ HttpUtil.NGA_ATTACHMENT_HOST + "/attachments/");
			ret.append(entry.getValue().getAttachurl());
			ret.append("'>");
			if (showImage) {
				String attachURL = "http://" + HttpUtil.NGA_ATTACHMENT_HOST
						+ "/attachments/" + entry.getValue().getAttachurl();
				if ("1".equals(entry.getValue().getThumb())) {
					attachURL = attachURL + ".thumb.jpg";
					// ret.append(entry.getValue().getExt());
				} else {
					attachURL = StringUtil.buildOptimizedImageURL(attachURL,
							imageQuality);
				}
				ret.append("<img src='");
				ret.append(attachURL);
			} else {
				ret.append("<img src='file:///android_asset/ic_offline_image.png");
			}

			ret.append("' style= 'max-width:70%;'></a>");

			ret.append("</td></tr>");
			attachmentCount++;
		}
		ret.append("</tbody></table>");
		if (attachmentCount == 0)
			return "";
		else
			return ret.toString();
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
			Log.e(TAG, "cann't handle avatar url " + js_escap_avatar);
		}
		return ret;
	}

	private static String buildComment(ThreadRowInfo row, String fgColor) {
		if (row == null || row.getComments() == null
				|| row.getComments().size() == 0) {
			return "";
		}

		StringBuilder ret = new StringBuilder();
		ret.append("<br/></br>").append(comment).append("<hr/><br/>");
		ret.append("<table  border='1px' cellspacing='0px' style='border-collapse:collapse;");
		ret.append("color:");
		ret.append(fgColor);
		ret.append("'>");

		ret.append("<tbody>");

		Iterator<ThreadRowInfo> it = row.getComments().iterator();
		while (it.hasNext()) {
			ThreadRowInfo comment = it.next();
			ret.append("<tr><td>");
			ret.append("<span style='font-weight:bold' >");
			ret.append(comment.getAuthor());
			ret.append("</span><br/>");
			ret.append("<img src='");
			String avatarUrl = parseAvatarUrl(comment.getJs_escap_avatar());
			ret.append(avatarUrl);
			ret.append("' style= 'max-width:32;'>");

			ret.append("</td><td>");
			ret.append(comment.getContent());
			ret.append("</td></tr>");

		}
		ret.append("</tbody></table>");
		return ret.toString();
	}

	private static String buildSignature(ThreadRowInfo row, boolean showImage,
			int imageQuality) {
		if (row == null || row.getSignature() == null
				|| row.getSignature().length() == 0
				|| !PhoneConfiguration.getInstance().showSignature) {
			return "";
		}
		return "<br/></br>"
				+ sig
				+ "<hr/><br/>"
				+ StringUtil.decodeForumTag(row.getSignature(), showImage,
						imageQuality, null);
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

}
