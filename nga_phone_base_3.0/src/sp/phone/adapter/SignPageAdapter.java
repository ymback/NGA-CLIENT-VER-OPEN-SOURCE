package sp.phone.adapter;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;

import gov.anzong.androidnga.R;
import sp.phone.bean.MissionDetialData;
import sp.phone.bean.SignData;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.SignData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SignPageAdapter extends BaseAdapter implements
		OnSignPageLoadFinishedListener {

	private LayoutInflater inflater;
	protected Context context;
	private SignData signData = null;
	private int selected = -1;
	protected int count = 0;

	boolean showImage = false;
	
	public SignPageAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
				|| isInWifi( context );
	}

	private boolean isInWifi(final Context activity) {
		ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return wifi == State.CONNECTED;
	}
	
	private int showImageQuality(){
		if (isInWifi(context))
		{
			return 0;
		}
		else
		{
			return PhoneConfiguration.getInstance().imageQuality;
		}
	}
	
	public Object getItem(int arg0) {

		MissionDetialData entry = getEntry(arg0);

		return null;

	}

	public int getCount() {
		return count;
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	class ViewHolder {
		public TextView missionid;
		public TextView missionname;
		public TextView missiondetial;
		public TextView missionstat;
		public WebView content;
		public TextView missionidtitle;

		/* FORPOS0 */
		public TextView nickName;
		public TextView signtime;
		public TextView signdate;
		public TextView signstate;
		public TextView lasttext;
		public TextView signdatedata;
		public TextView statetext;
		public ImageView avatarImage;
		public TextView successnum;
		public TextView availablenum;
		public View lineviewforshow;
	}

	class ViewHolderPos0 {
	}

	public View getView(int position, View view, ViewGroup parent) {

		View convertView = view;// m.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			if (position == 0) {
				convertView = inflater.inflate(R.layout.signresult, null);
				TextView nickName = (TextView) convertView
						.findViewById(R.id.nickName);
				TextView signtime = (TextView) convertView
						.findViewById(R.id.signtime);
				TextView signdate = (TextView) convertView
						.findViewById(R.id.signdate);
				TextView signstate = (TextView) convertView
						.findViewById(R.id.signstate);
				TextView lasttext = (TextView) convertView
						.findViewById(R.id.lasttext);
				TextView signdatedata = (TextView) convertView
						.findViewById(R.id.signdatedata);
				TextView statetext = (TextView) convertView
						.findViewById(R.id.statetext);
				ImageView avatarImage = (ImageView) convertView
						.findViewById(R.id.avatarImage);
				View lineviewforshow = (View) convertView
						.findViewById(R.id.lineviewforshow);
				TextView successnum = (TextView) convertView
						.findViewById(R.id.successnum);
				TextView availablenum = (TextView) convertView
						.findViewById(R.id.availablenum);

				holder = new ViewHolder();
				holder.nickName = nickName;
				holder.lineviewforshow = lineviewforshow;
				holder.signtime = signtime;
				holder.signdate = signdate;
				holder.signstate = signstate;
				holder.avatarImage = avatarImage;
				holder.lasttext = lasttext;
				holder.signdatedata = signdatedata;
				holder.statetext = statetext;
				holder.successnum = successnum;
				holder.availablenum = availablenum;
				convertView.setTag(holder);

			} else {
				convertView = inflater.inflate(R.layout.relative_signmissionstate_list,null);
				TextView missionid = (TextView) convertView.findViewById(R.id.missionid);
				TextView missionname = (TextView) convertView
						.findViewById(R.id.missionname);
				TextView missiondetial = (TextView) convertView
						.findViewById(R.id.missiondetial);
				TextView missionstat = (TextView) convertView
						.findViewById(R.id.missionstat);
				WebView content = (WebView) convertView
						.findViewById(R.id.content);
				TextView missionidtitle = (TextView) convertView
						.findViewById(R.id.missionidtitle);
				holder = new ViewHolder();
				
				holder.missionid = missionid;
				holder.missionname = missionname;
				holder.missiondetial = missiondetial;
				holder.missionstat = missionstat;
				holder.content = content;
				holder.missionidtitle = missionidtitle;
				convertView.setTag(holder);
			}

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
		MissionDetialData entry = getEntry(position);
		// this.signData.getArticleEntryList().get(position);

		if (entry == null && position!=0) {
			return;
		}
		Resources res = inflater.getContext().getResources();
		ThemeManager theme = ThemeManager.getInstance();
		boolean night = false;
		int nightLinkColor = res.getColor(R.color.night_link_color);
		if (theme.getMode() == ThemeManager.MODE_NIGHT) {
			night = true;
		}
		if (position == 0) {
			String userName;
			String uid;
			String signtime;
			String signdate;
			String signstate;
			String availablenum;
			String successnum;
			if (night) {
				holder.nickName.setTextColor(nightLinkColor);
				holder.signtime.setTextColor(nightLinkColor);
				holder.signdate.setTextColor(nightLinkColor);
				holder.signstate.setTextColor(nightLinkColor);
				holder.lasttext.setTextColor(nightLinkColor);
				holder.signdatedata.setTextColor(nightLinkColor);
				holder.statetext.setTextColor(nightLinkColor);
				holder.availablenum.setTextColor(nightLinkColor);
				holder.successnum.setTextColor(nightLinkColor);
			}
			if (StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {
				userName = "未知";
			} else {
				userName = PhoneConfiguration.getInstance().userName;
			}
			String userId = "-9999";
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().uid)) {
				userId =PhoneConfiguration.getInstance().uid;
			}
			if (StringUtil.isEmpty(signData.get__SignResult())) {
				signstate = "未知";
			} else {
				signstate = signData.get__SignResult();
			}
			if (signData.get__is_json_error()) {
				signtime = "未知";
				signdate = "未知";
				availablenum = "未知";
				successnum = "未知";
			} else {
				if (StringUtil.isEmpty(signData.get__Last_time())) {
					signtime = "未知";
				} else {
					signtime = signData.get__Last_time();
				}
				signdate = String.valueOf(signData.get__Continued()) + "/"
						+ String.valueOf(signData.get__Sum());
				if (signData.get__Availablerows() == 0) {
					availablenum = "未知";
				} else {
					availablenum = String
							.valueOf(signData.get__Availablerows())+"个";
				}
				if (signData.get__Successrows() == 0) {
					successnum = "未知";
				} else {
					successnum = String
							.valueOf(signData.get__Successrows())+"个";
				}

			}
			if(signData.get__Totalrows()>1){
				holder.lineviewforshow.setVisibility(View.VISIBLE);
			}
			holder.nickName.setText(userName);
			holder.signtime.setText(signtime);
			holder.signdate.setText(signdate);
			holder.signstate.setText(signstate);
			holder.availablenum.setText(availablenum);
			holder.successnum.setText(successnum);
			holder.avatarImage.setImageBitmap(getUserAvatat(userId));
			// 处理头像

		} else {
			String missionid;
			String missionname;
			String missiondetial;
			String missionstat;
			String info;
			if (night) {
				holder.missionid.setTextColor(nightLinkColor);
				holder.missionname.setTextColor(nightLinkColor);
				holder.missiondetial.setTextColor(nightLinkColor);
				holder.missionstat.setTextColor(nightLinkColor);
				holder.missionidtitle.setTextColor(nightLinkColor);
			}
			missionid = String.valueOf(entry.get__id());
			if (StringUtil.isEmpty(entry.get__name())) {
				missionname = "未知";
			} else {
				missionname = entry.get__name();
			}
			if (StringUtil.isEmpty(entry.get__detail())) {
				missiondetial = "未知";
			} else {
				missiondetial = entry.get__detail();
			}
			if (StringUtil.isEmpty(entry.get__stat())) {
				missionstat = "未知";
			} else {
				missionstat = entry.get__stat();
			}

			if (StringUtil.isEmpty(entry.get__info())) {
				info = "";
			} else {
				info = entry.get__info();
			}
			holder.missionid.setText(missionid);//这边有问题
			holder.missionname.setText(missionname);
			holder.missiondetial.setText(missiondetial);
			holder.missionstat.setText(missionstat);

			int bgColor = res.getColor(theme.getBackgroundColor(position));
			int fgColor = res.getColor(theme.getForegroundColor());
			bgColor = bgColor & 0xffffff;
			final String bgcolorStr = String.format("%06x",bgColor);
			
			int htmlfgColor = fgColor & 0xffffff;
			final String fgColorStr = String.format("%06x",htmlfgColor);

		    WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
		    holder.content.setBackgroundColor(0);
		    holder.content.setFocusableInTouchMode(false);
		    holder.content.setFocusable(false);
			if (ActivityUtil.isGreaterThan_2_2()) {
				holder.content.setLongClickable(false);
			}
			WebSettings setting = holder.content.getSettings();
			setting.setDefaultFontSize(PhoneConfiguration.getInstance()
					.getWebSize());
			setting.setJavaScriptEnabled(false);
			holder.content.setWebViewClient(client);
			holder.content.loadDataWithBaseURL(null, infoToHtmlText(info,showImage,showImageQuality(),fgColorStr,bgcolorStr),
					"text/html", "utf-8", null);
			holder.missionidtitle.setText("可完成任务ID:");
			if(entry.get__issuccessed()==true){
				holder.missionidtitle.setText("已完成任务ID:");
			}
		}
	}


	public String infoToHtmlText(final String info,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(info, showImage,
				imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {
			ngaHtml = "<font color='red'>[二哥压根不给我任务信息啊]</font>";
		}
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
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
	
	
	private Bitmap getUserAvatat(String userId) {
		Bitmap defaultAvatar = null, bitmap = null;
		if (PhoneConfiguration.getInstance().nikeWidth < 3) {
			return null;
		}
		if (defaultAvatar == null
				|| defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
			Resources res = inflater.getContext().getResources();
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
		}
		String avatarPath = HttpUtil.PATH_AVATAR + "/" + userId;
		String[] extension = { ".jpg", ".png", ".gif", ".jpeg", ".bmp" };
		for (int i = 0; i < 5; i++) {
			File f = new File(avatarPath+extension[i]);
			if (f.exists()) {
				
				bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath+extension[i]);
				if (bitmap == null) {
					f.delete();
				}
				long date = f.lastModified();
				if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
					f.delete();
				}
				break;
			}
		}
		if (bitmap!=null) {
			return bitmap;
		} else {
			return defaultAvatar;
		}

	}

	protected MissionDetialData getEntry(int position) {
		if (signData != null)
			return signData.getEntryList().get(position);
		return null;
	}

	@Override
	public void jsonfinishLoad(SignData result) {
		this.signData = result;
		count = signData.get__Totalrows();
		this.notifyDataSetChanged();

	}

	public void clear() {
		// TODO Auto-generated method stub

	}

}
