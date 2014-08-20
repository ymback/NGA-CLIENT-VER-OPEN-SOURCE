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
import sp.phone.listener.MyListenerForClient;
import sp.phone.listener.MyListenerForReply;
import sp.phone.task.AvatarLoadTask;
//import sp.phone.task.ForumTagDecodTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.Des;
import sp.phone.utils.FunctionUtil;
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
import android.os.Build;
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
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArticleListAdapter extends BaseAdapter implements
		AvatarLoadCompleteCallBack {
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
	static String blacklistban = null;
	static String legend = null;
	static String attachment = null;
	static String comment = null;
	static String sig = null;

	final WebViewClient client;

	public ArticleListAdapter(Context activity) {
		super();
		this.activity = activity;
		this.viewCache = new SparseArray<SoftReference<View>>();
		if (userDistance == null)
			initStaticStrings(activity);
		client = new ArticleListWebClient((FragmentActivity) activity);
	}

	@Override
	public int getCount() {
		if (null == data)
			return 0;
		return data.getRowNum();
	}

	private static void initStaticStrings(Context activity) {
		userDistance = activity.getString(R.string.user_distance);
		meter = activity.getString(R.string.meter);
		kiloMeter = activity.getString(R.string.kilo_meter);
		hide = activity.getString(R.string.hide);
		blacklistban = activity.getString(R.string.blacklistban);
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
		RelativeLayout articlelistrelativelayout;
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

	private static String buildHeader(ThreadRowInfo row, String fgColorStr) {
		if (row == null
				|| (StringUtil.isEmpty(row.getSubject()) && !row
						.getISANONYMOUS()))
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append("<h4 style='color:").append(fgColorStr).append("' >");
		if (!StringUtil.isEmpty(row.getSubject()))
			sb.append(row.getSubject());
		if (row.getISANONYMOUS())
			sb.append("<font style='color:#D00;font-weight: bold;'>")
					.append("[匿名]").append("</font>");
		sb.append("</h4>");
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
			final String bgcolorStr,Context context) {
		if(StringUtil.isEmpty(hide)){
			if(context!=null)
			initStaticStrings(context);
		}
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(row.getContent(), showImage,
				imageQuality, imageURLSet);
		if (row.get_isInBlackList()) {
			ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
					+ "<body bgcolor= '#"
					+ bgcolorStr
					+ "'>"
					+ "<font color='red' size='2'>["
					+ blacklistban
					+ "]</font>" + "</font></body>";
		} else {
			if (imageURLSet.size() == 0) {
				imageURLSet = null;
			}
			if (StringUtil.isEmpty(ngaHtml)) {
				ngaHtml = row.getAlterinfo();
			}
			if (StringUtil.isEmpty(ngaHtml)) {
				ngaHtml = "<font color='red'>[" + hide + "]</font>";
			}
			ngaHtml = ngaHtml
					+ buildComment(row, fgColorStr, showImage, imageQuality)
					+ buildAttachment(row, showImage, imageQuality, imageURLSet)
					+ buildSignature(row, showImage, imageQuality)
					+ buildVote(row);
			ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
					+ buildHeader(row, fgColorStr)
					+ "<body bgcolor= '#"
					+ bgcolorStr
					+ "'>"
					+ "<font color='#"
					+ fgColorStr
					+ "' size='2'>" + ngaHtml + "</font></body>";
		}
		return ngaHtml;
	}

	private Bitmap defaultAvatar = null;

	private void handleAvatar(ImageView avatarIV, ThreadRowInfo row) {

		final int lou = row.getLou();
		final String avatarUrl = FunctionUtil.parseAvatarUrl(row
				.getJs_escap_avatar());//
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

	private ViewHolder initHolder(final View view) {
		final ViewHolder holder = new ViewHolder();
		holder.articlelistrelativelayout = (RelativeLayout) view
				.findViewById(R.id.articlelistrelativelayout);
		holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);
		holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
		holder.floorTV = (TextView) view.findViewById(R.id.floor);
		holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
		holder.contentTV = (WebView) view.findViewById(R.id.content);
		holder.contentTV.setHorizontalScrollBarEnabled(false);
		holder.viewBtn = (ImageButton) view.findViewById(R.id.listviewreplybtn);
		holder.clientBtn = (ImageButton) view.findViewById(R.id.clientbutton);
		return holder;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ThreadRowInfo row = data.getRowList().get(position);

		int lou = -1;
		if (row != null)
			lou = row.getLou();
		ViewHolder holder = null;
		boolean needin = false;
		SoftReference<View> ref = viewCache.get(position);
		View cachedView = null;
		if (ref != null) {
			cachedView = ref.get();
		}
		if (cachedView != null) {
			if (((ViewHolder) cachedView.getTag()).position == position) {
				Log.d(TAG, "get view from cache ,floor " + lou);
				return cachedView;
			} else {
				view = LayoutInflater.from(activity).inflate(
						R.layout.relative_aritclelist, parent, false);
				holder = initHolder(view);
				holder.position = position;
				view.setTag(holder);
				viewCache.put(position, new SoftReference<View>(view));
			}
		} else {
			view = LayoutInflater.from(activity).inflate(
					R.layout.relative_aritclelist, parent, false);
			holder = initHolder(view);
			holder.position = position;
			view.setTag(holder);
			viewCache.put(position, new SoftReference<View>(view));
		}
		if (!PhoneConfiguration.getInstance().showReplyButton) {
			holder.viewBtn.setVisibility(View.GONE);
		} else {
			MyListenerForReply myListenerForReply = new MyListenerForReply(
					position, data, activity);
			holder.viewBtn.setOnClickListener(myListenerForReply);
		}
		ThemeManager theme = ThemeManager.getInstance();
		int colorId = theme.getBackgroundColor(position);
		view.setBackgroundResource(colorId);

		// colorId = theme.getBackgroundColor(2);

		if (row == null) {
			return view;
		}

		handleAvatar(holder.avatarIV, row);

		int fgColorId = ThemeManager.getInstance().getForegroundColor();
		final int fgColor = parent.getContext().getResources()
				.getColor(fgColorId);

		FunctionUtil.handleNickName(row, fgColor, holder.nickNameTV, activity);

		final int bgColor = parent.getContext().getResources()
				.getColor(colorId);

		final WebView contentTV = holder.contentTV;

		final String floor = String.valueOf(lou);
		TextView floorTV = holder.floorTV;
		floorTV.setText("[" + floor + " 楼]");
		floorTV.setTextColor(fgColor);

		if (!StringUtil.isEmpty(row.getFromClientModel())) {
			MyListenerForClient myListenerForClient = new MyListenerForClient(
					position, data, activity, parent);
			String from_client_model = row.getFromClientModel();
			if (from_client_model.equals("ios")) {
				holder.clientBtn.setImageResource(R.drawable.ios);// IOS
			} else if (from_client_model.equals("wp")) {
				holder.clientBtn.setImageResource(R.drawable.wp);// WP
			} else if (from_client_model.equals("unknown")) {
				holder.clientBtn.setImageResource(R.drawable.unkonwn);// 未知orBB
			}
			holder.clientBtn.setVisibility(View.VISIBLE);
			holder.clientBtn.setOnClickListener(myListenerForClient);
		}
		if (ActivityUtil.isLessThan_4_3()) {
			new Thread(new Runnable() {
				public void run() {
					FunctionUtil.handleContentTV(contentTV, row, bgColor,
							fgColor, activity, null, client);
				}
			}).start();
		} else if (ActivityUtil.isLessThan_4_4()) {
			((Activity) parent.getContext()).runOnUiThread(new Runnable() {
				public void run() {
					FunctionUtil.handleContentTV(contentTV, row, bgColor,
							fgColor, activity, null, client);
				}
			});
		} else {
			FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor,
					activity, null, client);
		}
		TextView postTimeTV = holder.postTimeTV;
		postTimeTV.setText(row.getPostdate());
		postTimeTV.setTextColor(fgColor);
		if (needin) {
			view.invalidate();
		}
		return view;
	}

	private static String buildAttachment(ThreadRowInfo row, boolean showImage,
			int imageQuality, HashSet<String> imageURLSet) {

		if (row == null || row.getAttachs() == null
				|| row.getAttachs().size() == 0) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		ThemeManager theme = ThemeManager.getInstance();
		ret.append("<br/><br/>").append(attachment).append("<hr/><br/>");
		// ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;margin:0px 0px 10px 30px;padding:10px;color:#6b2d25;max-width:100%;'>");
		if (theme.mode == theme.MODE_NIGHT) {
			ret.append("<table style='background:#000000;border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:2'>");
		} else {
			ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:2'>");
		}
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
			// String url = "http://img.nga.178.com/attachments/" +
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

	private static String buildComment(ThreadRowInfo row, String fgColor,
			boolean showImage, int imageQuality) {
		if (row == null || row.getComments() == null
				|| row.getComments().size() == 0) {
			return "";
		}

		StringBuilder ret = new StringBuilder();
		ret.append("<br/></br>").append(comment).append("<hr/><br/>");
		ret.append("<table border='1px' cellspacing='0px' style='border-collapse:collapse;");
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
			String avatarUrl = FunctionUtil.parseAvatarUrl(comment
					.getJs_escap_avatar());
			ret.append(avatarUrl);
			ret.append("' style= 'max-width:32;'>");

			ret.append("</td><td>");
			ret.append(StringUtil.decodeForumTag(comment.getContent(),
					showImage, imageQuality, null));
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

	private static String buildVote(ThreadRowInfo row) {
		if (row == null || StringUtil.isEmpty(row.getVote())) {
			return "";
		}
		return "<br/><hr/>" + "本楼有投票/投注内容,长按本楼在菜单中点击投票/投注按钮";
	}

	@Override
	public void notifyDataSetChanged() {
		this.viewCache.clear();
		super.notifyDataSetChanged();
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