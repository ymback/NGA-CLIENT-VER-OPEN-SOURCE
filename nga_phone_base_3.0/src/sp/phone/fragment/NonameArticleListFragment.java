package sp.phone.fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashSet;

import noname.gson.parse.NonameReadBody;
import noname.gson.parse.NonameReadResponse;
import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.PostActivity;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.adapter.NonameArticleListAdapter;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.OnNonameThreadPageLoadFinishedListener;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.task.JsonNonameThreadLoadTask;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.task.ReportTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.Des;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class NonameArticleListFragment extends Fragment implements
		OnNonameThreadPageLoadFinishedListener, PerferenceConstant {
	final static private String TAG = NonameArticleListFragment.class.getSimpleName();
	/*
	 * static final int QUOTE_ORDER = 0; static final int REPLY_ORDER = 1;
	 * static final int COPY_CLIPBOARD_ORDER = 2; static final int
	 * SHOW_THISONLY_ORDER = 3; static final int SHOW_MODIFY_ORDER = 4; static
	 * final int SHOW_ALL = 5; static final int POST_COMMENT = 6; static final
	 * int SEARCH_POST = 7; static final int SEARCH_SUBJECT = 8;
	 */
	private ListView listview = null;
	private NonameArticleListAdapter articleAdpater;
	// private JsonThreadLoadTask task;
	private int page = 0;
	private int tid;
	private String title;
	private int pid;
	private boolean needLoad = true;
	private Object mActionModeCallback = null;
	private static Context activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		page = getArguments().getInt("page") + 1;
		tid = getArguments().getInt("id");
		articleAdpater = new NonameArticleListAdapter(this.getActivity());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listview = new ListView(this.getActivity());

		listview.setBackgroundResource(ThemeManager.getInstance()
				.getBackgroundColor());
		listview.setDivider(null);

		activeActionMode();
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

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

		listview.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);

		return listview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		listview.setAdapter(articleAdpater);
		super.onActivityCreated(savedInstanceState);
	}

	@TargetApi(11)
	private void activeActionMode() {
		mActionModeCallback = new ActionMode.Callback() {

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.nonamearticlelist_context_menu, menu);

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
	public void onResume() {
		Log.d(TAG, "onResume pid=" + pid + "&page=" + page);
		// setHasOptionsMenu(true);
		if (PhoneConfiguration.getInstance().isRefreshAfterPost()) {

			PagerOwnner father = null;
			try {
				father = (PagerOwnner) getActivity();
				if (father.getCurrentPage() == page) {
					PhoneConfiguration.getInstance().setRefreshAfterPost(false);
					// this.task = null;
					this.needLoad = true;
				}
			} catch (ClassCastException e) {
				Log.e(TAG, "father activity does not implements interface "
						+ PagerOwnner.class.getName());

			}

		}
		this.loadPage();
		super.onResume();
	}

	@TargetApi(11)
	private void RunParallen(JsonNonameThreadLoadTask task, String url) {
		task.executeOnExecutor(JsonNonameThreadLoadTask.THREAD_POOL_EXECUTOR, url);
	}

	@TargetApi(11)
	private void RunParallen(ReportTask task, String url) {
		task.executeOnExecutor(JsonNonameThreadLoadTask.THREAD_POOL_EXECUTOR, url);
	}

	private void loadPage() {
		if (needLoad) {

			Activity activity = getActivity();
			JsonNonameThreadLoadTask task = new JsonNonameThreadLoadTask(activity, this);
			String url = HttpUtil.NonameServer + "/read.php?" + "&page=" + page
					+ "&lite=js&noprefix&v2";
			if (tid != 0)
				url = url + "&tid=" + tid;

			if (ActivityUtil.isGreaterThan_2_3_3())
				RunParallen(task, url);
			else
				task.execute(url);
		} else {
			ActivityUtil.getInstance().dismiss();
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.nonamearticlelist_context_menu, menu);


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

	public static String TimeStamp2Date(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date(timestamp));
		return date;
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		Log.d(TAG, "onContextItemSelected,tid=" + tid + ",page=" + page);
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

		if (father.getCurrentPage() != page) {
			return false;
		}

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = this.listview.getCheckedItemPosition();
		if (info != null) {
			position = info.position;
		}
		if (position < 0 || position >= listview.getAdapter().getCount()) {
			Toast.makeText(getActivity(), R.string.floor_error,
					Toast.LENGTH_LONG).show();
			position = 0;
		}
		StringBuffer postPrefix = new StringBuffer();
		String tidStr = String.valueOf(this.tid);

		NonameReadBody row = (NonameReadBody) listview
				.getItemAtPosition(position);
		if (row == null) {
			Toast.makeText(getActivity(), R.string.unknow_error,
					Toast.LENGTH_LONG).show();
			return true;
		}
		String content = row.content;
		final String name = row.hip;
		String mention = null;
		Intent intent = new Intent();
		switch (item.getItemId())
		// if( REPLY_POST_ORDER ==item.getItemId())
		{
		case R.id.quote_subject:

			final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
			final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
			content = content.replaceAll(quote_regex, "");
			content = content.replaceAll(replay_regex, "");
			final long longposttime = row.ptime;
			String postTime ="";
			if(longposttime!=0){
				postTime = TimeStamp2Date(String.valueOf(longposttime));
			}

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

			// case R.id.r:

			if (!StringUtil.isEmpty(mention))
				intent.putExtra("mention", mention);
			intent.putExtra("prefix",
					StringUtil.removeBrTag(postPrefix.toString()));
			intent.putExtra("tid", tidStr);
			intent.putExtra("action", "reply");
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().nonamePostActivityClass);
			} else {
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().loginActivityClass);
			}
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
			break;
		case R.id.copy_to_clipboard:
			CopyDialog(content);
			break;

		}
		return true;
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
					ActivityUtil.getInstance().setFullScreen(listview);
				}
			}

		});
		// TODO Auto-generated method stub

	}

	@Override
	public void finishLoad(NonameReadResponse data) {
		Log.d(TAG, "finishLoad");
		// ArticleListActivity father = (ArticleListActivity)
		// this.getActivity();
		if (null != data) {
			articleAdpater.setData(data);
			articleAdpater.notifyDataSetChanged();

				tid = data.data.tid;
				title = data.data.title;
			OnNonameThreadPageLoadFinishedListener father = null;
			try {
				father = (OnNonameThreadPageLoadFinishedListener) getActivity();
				if (father != null)
					father.finishLoad(data);
			} catch (ClassCastException e) {
				Log.e(TAG,
						"father activity should implements OnThreadPageLoadFinishedListener");
			}

		}
		this.needLoad = false;

	}

}
