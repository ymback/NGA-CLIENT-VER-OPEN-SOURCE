package sp.phone.fragment;

import gov.anzong.androidnga.activity.MainActivity;
import gov.anzong.androidnga.activity.MessagePostActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashSet;

import sp.phone.adapter.AppendableMessageAdapter;
import sp.phone.adapter.AppendableMessageDetialAdapter;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetialInfo;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.NextJsonMessageDetialLoader;
import sp.phone.interfaces.NextJsonMessageListLoader;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.interfaces.OnMessageDetialLoadFinishedListener;
import sp.phone.interfaces.OnMessageListLoadFinishedListener;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonMessageDetialLoadTask;
import sp.phone.task.JsonMessageListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MessageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MessageDetialListContainer extends Fragment implements
		OnMessageDetialLoadFinishedListener, NextJsonMessageDetialLoader,PerferenceConstant {
	final String TAG = MessageDetialListContainer.class.getSimpleName();
	static final int MESSAGE_SENT = 1;

	PullToRefreshAttacher attacher = null;
	private ListView listView;
	AppendableMessageDetialAdapter adapter;
	boolean canDismiss = true;
	private Object mActionModeCallback = null;
	final private String ALERT_DIALOG_TAG = "alertdialog";
	int mid;
	String title, to;
	String url;


	public static MessageDetialListContainer create(int mid){
		MessageDetialListContainer f = new MessageDetialListContainer();
		Bundle args = new Bundle ();
		args.putInt("mid", mid);
		f.setArguments(args);
		return f;
	}

	public MessageDetialListContainer() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
			container.setBackgroundResource(R.color.night_bg_color);
		}

		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e(TAG,
					"father activity should implement PullToRefreshAttacherOnwer");
		}

		listView = new ListView(getActivity());
		listView.setDivider(null);
		activeActionMode();
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@TargetApi(11)
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView lv = (ListView) parent;
				lv.setItemChecked(position, true);
				if (mActionModeCallback != null) {
					((ActionBarActivity) getActivity())
							.startSupportActionMode((Callback) mActionModeCallback);
					return true;
				}
				return false;
			}
		});
		// mPullRefreshListView.setAdapter(adapter);
		try {
			OnItemClickListener listener = (OnItemClickListener) getActivity();
			// mPullRefreshListView.setOnItemClickListener(listener);
			listView.setOnItemClickListener(listener);
		} catch (ClassCastException e) {
			Log.e(TAG, "father activity should implenent OnItemClickListener");
		}

		// mPullRefreshListView.setOnRefreshListener(new
		// ListRefreshListener());\
		if (attacher != null)
			attacher.addRefreshableView(listView, new ListRefreshListener());

		url = getArguments().getString("url");
		
		if (url != null) {
			String tmp = StringUtil.getStringBetween(url, 0, "mid=", "&").result;
			if(!StringUtil.isEmpty(tmp)){
				mid=Integer.parseInt(tmp, 0);
			}
		} else {
			mid = getArguments().getInt("mid", 0);
		}
		// JsonTopicListLoadTask task = new
		// JsonTopicListLoadTask(getActivity(),this);
		// task.execute(getUrl(1));
		return listView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		canDismiss = true;
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.refresh();
		super.onViewCreated(view, savedInstanceState);
	}

	@TargetApi(11)
	private void activeActionMode() {
		mActionModeCallback = new ActionMode.Callback() {

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.messagedetail_context_menu, menu);

				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				onContextItemSelected(item);
				mode.finish();
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// int position = listview.getCheckedItemPosition();
				// listview.setItemChecked(position, false);

			}

		};
	}
	
    @Override  
    public void onPrepareOptionsMenu(Menu menu) {  
        if( menu.findItem(R.id.night_mode)!=null){
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {  
                menu.findItem(R.id.night_mode).setIcon(  
                        R.drawable.ic_action_brightness_high);    
                menu.findItem(R.id.night_mode).setTitle(R.string.change_daily_mode);
            }else{
                menu.findItem(R.id.night_mode).setIcon(  
                        R.drawable.ic_action_bightness_low);    
                menu.findItem(R.id.night_mode).setTitle(R.string.change_night_mode);
            }
        }
        // getSupportMenuInflater().inflate(R.menu.book_detail, menu);  
        super.onPrepareOptionsMenu(menu);  
    }  

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		PagerOwnner father = null;
		try {
			father = (PagerOwnner) getActivity();
		} catch (ClassCastException e) {
			Log.e(TAG, "father activity does not implements interface "
					+ PagerOwnner.class.getName());
			return true;
		}

		if (father == null)
			return false;

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = this.listView.getCheckedItemPosition();
		if (info != null) {
			position = info.position;
		}
		if (position < 0 || position >= listView.getAdapter().getCount()) {
			Toast.makeText(getActivity(), R.string.floor_error,
					Toast.LENGTH_LONG).show();
			position = 0;
		}
		StringBuffer postPrefix = new StringBuffer();

		boolean isadmin = false;
		MessageArticlePageInfo row = (MessageArticlePageInfo) listView
				.getItemAtPosition(position);
		if (row == null) {
			Toast.makeText(getActivity(), R.string.unknow_error,
					Toast.LENGTH_LONG).show();
			return true;
		}
		if (row.getFrom().trim().equals("0")) {
			isadmin = true;
		}
		String content = row.getContent();
		String signature = row.getSignature();
		final String name = row.getAuthor();
		final String uid = String.valueOf(row.getFrom());
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case R.id.signature_dialog:
			if (isadmin) {
				errordialog();
			} else {
				Create_Signature_Dialog(row);
			}
			break;
		case R.id.avatar_dialog:
			if (isadmin) {
				errordialog();
			} else {
				Create_Avatar_Dialog(row);
			}
			break;
		case R.id.send_message:
			if (isadmin) {
				errordialog();
			} else {
				start_send_message(row);
			}
			break;
		case R.id.show_profile:
			if (isadmin) {
				errordialog();
			} else {
				intent.putExtra("mode", "username");
				intent.putExtra("username", row.getAuthor());
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().profileActivityClass);
				startActivity(intent);
				if (PhoneConfiguration.getInstance().showAnimation)
					getActivity().overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
			}
			break;
		case R.id.search_post:
			if (isadmin) {
				errordialog();
			} else {
				intent.putExtra("searchpost", 1);
				intent.putExtra("authorid", row.getFrom());
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().topicActivityClass);
				startActivity(intent);
				if (PhoneConfiguration.getInstance().showAnimation)
					getActivity().overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
			}
			break;
		case R.id.search_subject:
			if (isadmin) {
				errordialog();
			} else {
				intent.putExtra("authorid", row.getFrom());
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().topicActivityClass);
				startActivity(intent);
				if (PhoneConfiguration.getInstance().showAnimation)
					getActivity().overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
			}
			break;
		case R.id.copy_to_clipboard:
			CopyDialog(content);
			break;

		case R.id.quote_subject:

			final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
			final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
			content = content.replaceAll(quote_regex, "");
			content = content.replaceAll(replay_regex, "");
			final String postTime = row.getTime();

			content = checkContent(content);
			content = StringUtil.unEscapeHtml(content);
			postPrefix.append("[quote]");
			postPrefix.append(" [b]Post by [uid=");
			postPrefix.append(uid);
			postPrefix.append("]");
			postPrefix.append(name);
			postPrefix.append("[/uid] (");
			postPrefix.append(postTime);
			postPrefix.append("):[/b]\n");
			postPrefix.append(content);
			postPrefix.append("[/quote]\n");

			// case R.id.r:

			intent.putExtra("prefix",
					StringUtil.removeBrTag(postPrefix.toString()));
			intent.putExtra("mid", mid);
			intent.putExtra("action", "reply");
			intent.putExtra("title", title);
			intent.putExtra("to", to);
			intent.putExtra("messagemode", "yes");
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
				intent.setClass(
						getActivity(),
						PhoneConfiguration.getInstance().messagePostActivityClass);
			} else {
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().loginActivityClass);
			}
			startActivityForResult(intent,123);
			if (PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
			break;

		}
		return true;
	}




	private void nightMode(final MenuItem menu) {
	
		String alertString = getString(R.string.change_nigmtmode_string_message);
		final AlertDialogFragment f = AlertDialogFragment.create(alertString);
		f.setOkListener(new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				
				ThemeManager tm = ThemeManager.getInstance();
				SharedPreferences share = getActivity().getSharedPreferences(PERFERENCE,
						Activity.MODE_PRIVATE);
				int mode = ThemeManager.MODE_NORMAL;
				if (tm.getMode() == ThemeManager.MODE_NIGHT) {//是晚上模式，改白天的
					menu.setIcon(  
		                    R.drawable.ic_action_bightness_low); 
					menu.setTitle(R.string.change_night_mode);
					Editor editor = share.edit();
					editor.putBoolean(NIGHT_MODE, false);
					editor.commit();
				}else{
					menu.setIcon(  
		                    R.drawable.ic_action_brightness_high); 
					menu.setTitle(R.string.change_daily_mode);
					Editor editor = share.edit();
					editor.putBoolean(NIGHT_MODE, true);
					editor.commit();
					mode = ThemeManager.MODE_NIGHT;
				}
				ThemeManager.getInstance().setMode(mode);
				Intent intent = getActivity().getIntent();
				getActivity().overridePendingTransition(0, 0);
				getActivity().finish();
				getActivity().overridePendingTransition(0, 0);
				getActivity().startActivity(intent);
			}
			
		});
		f.setCancleListener(new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				f.dismiss();
			}
			
		});
		f.show(getActivity().getSupportFragmentManager(),ALERT_DIALOG_TAG);
	}
	
	
	private void errordialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("这白痴是系统账号,神马都看不到");
		builder.setTitle("看不到");
		builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});

		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listView);
				}
			}

		});
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

	private void CopyDialog(String content) {
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.copy_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setView(view);
		alert.setTitle(R.string.copy_hint);
		final EditText commentdata = (EditText) view
				.findViewById(R.id.copy_data);
		commentdata.setText(content);
		commentdata.selectAll();
		alert.setPositiveButton("复制", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int start = commentdata.getSelectionStart();
				int end = commentdata.getSelectionEnd();
				CharSequence selectText = commentdata.getText().subSequence(
						start, end);
				if (selectText.length() > 0) {
					android.text.ClipboardManager cbm = (android.text.ClipboardManager) getActivity()
							.getSystemService(Activity.CLIPBOARD_SERVICE);
					cbm.setText(StringUtil.removeBrTag(selectText.toString()));
					Toast.makeText(getActivity(), R.string.copied_to_clipboard,
							Toast.LENGTH_SHORT).show();
					try {
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					commentdata.selectAll();
					Toast.makeText(getActivity(), "请选择要复制的内容",
							Toast.LENGTH_SHORT).show();
					try {
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listView);
				}
			}

		});
		// TODO Auto-generated method stub

	}
	private void start_send_message(MessageArticlePageInfo row){
		Intent intent_bookmark = new Intent();
		intent_bookmark.putExtra("to", row.getAuthor());
		intent_bookmark.putExtra("action", "new");
		intent_bookmark.putExtra("messagemode", "yes");
		if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
			intent_bookmark
					.setClass(
							getActivity(),
							PhoneConfiguration.getInstance().messagePostActivityClass);
		} else {
			intent_bookmark.setClass(getActivity(),
					PhoneConfiguration.getInstance().loginActivityClass);
		}
		startActivity(intent_bookmark);
	}
	
	private void Create_Avatar_Dialog(MessageArticlePageInfo row) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.signature_dialog,
				null);
		String name = row.getAuthor();
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setView(view);
		alert.setTitle(name + "的头像");
		// COLOR

		ThemeManager theme = ThemeManager.getInstance();
		int bgColor = getResources().getColor(theme.getBackgroundColor(0));
		int fgColor = getResources().getColor(theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x", bgColor);

		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x", htmlfgColor);

		WebViewClient client = new ArticleListWebClient(getActivity());
		WebView contentTV = (WebView) view.findViewById(R.id.signature);
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {
			contentTV.setLongClickable(false);
		}
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		contentTV.loadDataWithBaseURL(
				null,
				avatarToHtmlText(row, true, MessageUtil.showImageQuality(),
						fgColorStr, bgcolorStr), "text/html", "utf-8", null);
		alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});
		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listView);
				}
			}

		});
	}

	public String avatarToHtmlText(final MessageArticlePageInfo row,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = null;
		if (row.getJs_escap_avatar().equals("")) {
			ngaHtml = StringUtil
					.decodeForumTag(
							"这家伙是骷髅党,头像什么的没有啦~<br/><img src='file:///android_asset/default_avatar.png' style= 'max-width:100%;' >",
							showImage, imageQuality, imageURLSet);
		} else {
			ngaHtml = StringUtil.decodeForumTag(
					"[img]" + parseAvatarUrl(row.getJs_escap_avatar())
							+ "[/img]", showImage, imageQuality, imageURLSet);
		}
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {
			ngaHtml = "<font color='red'>[" + this.getString(R.string.hide)
					+ "]</font>";
		}
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
				+ "<body bgcolor= '#"
				+ bgcolorStr
				+ "'>"
				+ "<font color='#"
				+ fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

		return ngaHtml;
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

	private void Create_Signature_Dialog(MessageArticlePageInfo row) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.signature_dialog,
				null);
		String name = row.getAuthor();
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setView(view);
		alert.setTitle(name + "的签名");
		// COLOR

		ThemeManager theme = ThemeManager.getInstance();
		int bgColor = getResources().getColor(theme.getBackgroundColor(0));
		int fgColor = getResources().getColor(theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x", bgColor);

		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x", htmlfgColor);

		WebViewClient client = new ArticleListWebClient(getActivity());
		WebView contentTV = (WebView) view.findViewById(R.id.signature);
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {
			contentTV.setLongClickable(false);
		}
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
				|| MessageUtil.isInWifi();
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		contentTV
				.loadDataWithBaseURL(
						null,
						signatureToHtmlText(row, showImage,
								MessageUtil.showImageQuality(), fgColorStr,
								bgcolorStr), "text/html", "utf-8", null);
		alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});

		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listView);
				}
			}

		});
	}

	public String signatureToHtmlText(final MessageArticlePageInfo row,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(row.getSignature(),
				showImage, imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {
			ngaHtml = "<font color='red'>[" + this.getString(R.string.hide)
					+ "]</font>";
		}
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
				+ "<body bgcolor= '#"
				+ bgcolorStr
				+ "'>"
				+ "<font color='#"
				+ fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

		return ngaHtml;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.articlelist_context_menu, menu);

	}

	private void refresh_saying() {
		DefaultHeaderTransformer transformer = null;

		if (attacher != null) {
			uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
			headerTransformer = attacher.getHeaderTransformer();
			if (headerTransformer != null
					&& headerTransformer instanceof DefaultHeaderTransformer)
				transformer = (DefaultHeaderTransformer) headerTransformer;
		}

		if (transformer == null)
			ActivityUtil.getInstance().noticeSaying(this.getActivity());
		else {
			transformer.setRefreshingText(ActivityUtil.getSaying());
		}
		if (attacher != null)
			attacher.setRefreshing(true);
	}

	void refresh() {
		JsonMessageDetialLoadTask task = new JsonMessageDetialLoadTask(
				getActivity(), this);
		// ActivityUtil.getInstance().noticeSaying(this.getActivity());
		if (this.getActivity() != null) {
			adapter = new AppendableMessageDetialAdapter(this.getActivity(),
					attacher, this);
			refresh_saying();
			task.execute(getUrl(1, mid, true, true));
		}
	}

	public String getUrl(int page, int mid, boolean isend, boolean restart) {

		String jsonUri = HttpUtil.Server
				+ "/nuke.php?__lib=message&__act=message&act=read&";

		jsonUri += "page=" + page + "&mid=" + String.valueOf(mid) + "&lite=js&noprefix";

		return jsonUri;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuId;
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
				menuId = R.menu.messagedetail_menu_left;
			} else {
				menuId = R.menu.messagedetail_menu;
			}
		} else {
			menuId = R.menu.messagedetail_menu;
		}
		inflater.inflate(menuId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.article_menuitem_refresh:
			this.refresh();
			break;
		case R.id.night_mode:
			nightMode(item);
			break;
		case R.id.article_menuitem_reply:
			Intent intent_bookmark = new Intent();
			intent_bookmark.putExtra("mid", mid);
			intent_bookmark.putExtra("title", title);
			intent_bookmark.putExtra("to", to);
			intent_bookmark.putExtra("action", "reply");
			intent_bookmark.putExtra("messagemode", "yes");
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
				intent_bookmark
						.setClass(
								getActivity(),
								PhoneConfiguration.getInstance().messagePostActivityClass);
			} else {
				intent_bookmark.setClass(getActivity(),
						PhoneConfiguration.getInstance().loginActivityClass);
			}
			startActivityForResult(intent_bookmark,123);
			break;
		case R.id.article_menuitem_back:
		default:
			// case android.R.id.home:
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(this).commit();
			OnChildFragmentRemovedListener father = null;
			try {
				father = (OnChildFragmentRemovedListener) getActivity();
				father.OnChildFragmentRemoved(getId());
			} catch (ClassCastException e) {
				Log.e(TAG, "father activity does not implements interface "
						+ OnChildFragmentRemovedListener.class.getName());

			}
			break;
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		canDismiss = false;
		super.onSaveInstanceState(outState);
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==123){
			refresh();
		}
	}
	
	@Override
	public void finishLoad(MessageDetialInfo result) {
		if (attacher != null)
			attacher.setRefreshComplete();

		if (result == null){
			return;
		}
		
		title = result.get_Title();
		to = result.get_Alluser();
		adapter.finishLoad(result);
		listView.setAdapter(adapter);
		if (canDismiss)
			ActivityUtil.getInstance().dismiss();

	}

	@TargetApi(11)
	private void RunParallen(JsonMessageDetialLoadTask task) {
		task.executeOnExecutor(JsonMessageListLoadTask.THREAD_POOL_EXECUTOR,
				getUrl(adapter.getNextPage(), mid, adapter.getIsEnd(), false));
	}

	@Override
	public void loadNextPage(OnMessageDetialLoadFinishedListener callback) {
		JsonMessageDetialLoadTask task = new JsonMessageDetialLoadTask(
				getActivity(), callback);
		refresh_saying();
		if (ActivityUtil.isGreaterThan_2_3_3())
			RunParallen(task);
		else
			task.execute(getUrl(adapter.getNextPage(), mid, adapter.getIsEnd(),
					false));
	}

	
	
	class ListRefreshListener implements
			PullToRefreshAttacher.OnRefreshListener {

		/*
		 * @Override public void onPullDownToRefresh(
		 * PullToRefreshBase<ListView> refreshView) { refresh();
		 * 
		 * }
		 * 
		 * @Override public void onPullUpToRefresh( PullToRefreshBase<ListView>
		 * refreshView) { JsonTopicListLoadTask task = new
		 * JsonTopicListLoadTask(getActivity(), new
		 * OnTopListLoadFinishedListener(){
		 * 
		 * @Override public void jsonfinishLoad( TopicListInfo result) {
		 * mPullRefreshListView.onRefreshComplete(); if(result == null) return;
		 * ActivityUtil.getInstance().dismiss(); adapter.jsonfinishLoad(result);
		 * 
		 * }
		 * 
		 * } ); ActivityUtil.getInstance().noticeSaying(getActivity());
		 * task.execute(getUrl(adapter.getNextPage()));
		 * 
		 * }
		 */

		@Override
		public void onRefreshStarted(View view) {

			refresh();
		}
	}
}
