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

import noname.gson.parse.NonameReadBody;
import noname.gson.parse.NonameReadResponse;
import android.support.v4.app.Fragment;
import sp.phone.adapter.ArticleListAdapter.ViewHolder;
import sp.phone.bean.Attachment;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.listener.MyListenerForNonameReply;
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
	private NonameReadResponse mData;
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
		if (userDistance == null)
			initStaticStrings(activity);
	}

	@Override
	public int getCount() {
		if (null == mData)
			return 0;
		return mData.data.posts.length;
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
		this.mData = data;
	}

	public NonameReadResponse getData() {
		return mData;
	}

	@Override
	public Object getItem(int position) {
		if (null == mData)
			return null;

		return mData.data.posts[position];
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
	

	
	private ViewHolder initHolder(final View view) {
		final ViewHolder holder = new ViewHolder();
		holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);

		holder.floorTV = (TextView) view.findViewById(R.id.floor);
		holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
		holder.contentTV = (WebView) view.findViewById(R.id.content);
		holder.contentTV.setHorizontalScrollBarEnabled(false);
		holder.viewBtn = (ImageButton) view
				.findViewById(R.id.listviewreplybtn);
		/*
		 * holder.levelTV = (TextView) view.findViewById(R.id.level);
		 * holder.aurvrcTV= (TextView) view.findViewById(R.id.aurvrc);
		 * holder.postnumTV = (TextView) view.findViewById(R.id.postnum);
		 */
		return holder;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final NonameReadBody row = mData.data.posts[position];

		int lou = -1;
		if (row != null)
			lou = row.floor;
		ViewHolder holder = null;
		boolean needin = false;
		SoftReference<View> ref = viewCache.get(position);
		View cachedView = null;
		if (ref != null) {
			cachedView = ref.get();
		}
		if (cachedView != null) {
			if(((ViewHolder) cachedView.getTag()).position==position){
				Log.d(TAG, "get view from cache ,floor " + lou);
				return cachedView;
			}else{
				if (view == null) {
					view = LayoutInflater.from(activity).inflate(
							R.layout.relative_nonamearitclelist, parent, false);
					holder = initHolder(view);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
					needin = true;
				}
				holder.position=position;
				viewCache.put(position,
						new SoftReference<View>(view));
			}
		} else {
			if (view == null) {
				view = LayoutInflater.from(activity).inflate(
						R.layout.relative_nonamearitclelist, parent, false);
				holder = initHolder(view);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
				needin = true;
			}
			holder.position=position;
			viewCache.put(position,
					new SoftReference<View>(view));
		}

		if (!PhoneConfiguration.getInstance().showReplyButton) {
			holder.viewBtn.setVisibility(View.GONE);
		} else {
			MyListenerForNonameReply myListenerForReply = new MyListenerForNonameReply(position, activity, mData);
			holder.viewBtn.setOnClickListener(myListenerForReply);
		}
		holder.position = position;
		ThemeManager theme = ThemeManager.getInstance();
		int colorId = theme.getBackgroundColor(position);
		view.setBackgroundResource(colorId);


		if (row == null) {
			return view;
		}

		int fgColorId = ThemeManager.getInstance().getForegroundColor();
		final int fgColor = parent.getContext().getResources()
				.getColor(fgColorId);

		FunctionUtil.handleNickName(row, fgColor, holder.nickNameTV);

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
		floorTV.setText("[" + floor + " ¥]");
		floorTV.setTextColor(fgColor);
		final long longposttime = row.ptime;
		String postTime ="";
		if(longposttime!=0){
			postTime = StringUtil.TimeStamp2Date(String.valueOf(longposttime));
		}
		TextView postTimeTV = holder.postTimeTV;
		postTimeTV.setText(postTime);
		postTimeTV.setTextColor(fgColor);
		if (ActivityUtil.isLessThan_4_3()) {
			new Thread(new Runnable() {
				public void run() {
					FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor, activity, null);
				}
			}).start();
		} else if (ActivityUtil.isLessThan_4_4()) {
			((Activity) parent.getContext()).runOnUiThread(new Runnable() {
				public void run() {
					FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor, activity, null);
				}
			});
		} else {
			FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor, activity, null);
		}
		if (needin) {
			view.invalidate();
		}
		return view;
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



}
