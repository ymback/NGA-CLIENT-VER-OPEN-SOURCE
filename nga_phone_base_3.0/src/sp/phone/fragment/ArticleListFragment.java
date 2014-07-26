package sp.phone.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.R;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.MissionDetialData;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.task.ReportTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ArticleListFragment extends Fragment implements
		OnThreadPageLoadFinishedListener, PerferenceConstant {
	final static private String TAG = ArticleListFragment.class.getSimpleName();
	/*
	 * static final int QUOTE_ORDER = 0; static final int REPLY_ORDER = 1;
	 * static final int COPY_CLIPBOARD_ORDER = 2; static final int
	 * SHOW_THISONLY_ORDER = 3; static final int SHOW_MODIFY_ORDER = 4; static
	 * final int SHOW_ALL = 5; static final int POST_COMMENT = 6; static final
	 * int SEARCH_POST = 7; static final int SEARCH_SUBJECT = 8;
	 */
	private ListView listview = null;
	private ArticleListAdapter articleAdpater;
	// private JsonThreadLoadTask task;
	private int page = 0;
	private int tid;
	private String title;
	private int pid;
	private int authorid;
	private boolean needLoad = true;
	private Object mActionModeCallback = null;
	private Toast toast;
	private ThreadData mData;
	private int mListPosition;
	private int mListFirstTop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		page = getArguments().getInt("page") + 1;
		tid = getArguments().getInt("id");
		pid = getArguments().getInt("pid", 0);
		authorid = getArguments().getInt("authorid", 0);
		articleAdpater = new ArticleListAdapter(this.getActivity());
		super.onCreate(savedInstanceState);
		String fatheractivityclassname = getActivity().getClass()
				.getSimpleName();
		if (!StringUtil.isEmpty(fatheractivityclassname)) {
			if (fatheractivityclassname.indexOf("TopicListActivity") < 0)
				setRetainInstance(true);
		}
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

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
				if (pid == 0) {
					inflater.inflate(R.menu.articlelist_context_menu, menu);
				} else {
					inflater.inflate(R.menu.articlelist_context_menu_with_tid,
							menu);
				}
				int position = listview.getCheckedItemPosition();
				ThreadRowInfo row = new ThreadRowInfo();
				if (position < listview.getCount())
					row = (ThreadRowInfo) listview.getItemAtPosition(position);

				MenuItem mi = (MenuItem) menu.findItem(R.id.ban_thisone);
				if (mi != null && row != null) {
					if (row.get_isInBlackList()) {// 处于屏蔽列表，需要去掉
						mi.setTitle(R.string.cancel_ban_thisone);
					} else {
						mi.setTitle(R.string.ban_thisone);
					}
				}
				MenuItem votemenu = (MenuItem) menu.findItem(R.id.vote_dialog);
				if (votemenu != null && StringUtil.isEmpty(row.getVote())) {
					menu.removeItem(R.id.vote_dialog);
				}
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
		loadPage();
		if (mData != null) {
			((OnThreadPageLoadFinishedListener) getActivity())
					.finishLoad(mData);
		}
		super.onResume();
		listview.setSelectionFromTop(mListPosition, mListFirstTop);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (listview.getChildCount() >= 1) {
			mListPosition = listview.getFirstVisiblePosition();
			mListFirstTop = listview.getChildAt(0).getTop();
		}
	}

	@TargetApi(11)
	private void RunParallen(JsonThreadLoadTask task, String url) {
		task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
	}

	@TargetApi(11)
	private void RunParallen(ReportTask task, String url) {
		task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
	}

	private void loadPage() {
		if (needLoad) {
			Log.d(TAG, "loadPage" + page);
			Activity activity = getActivity();
			JsonThreadLoadTask task = new JsonThreadLoadTask(activity, this);
			String url = HttpUtil.Server + "/read.php?" + "&page=" + page
					+ "&lite=js&noprefix&v2";
			if (tid != 0)
				url = url + "&tid=" + tid;
			if (pid != 0) {
				url = url + "&pid=" + pid;
			}

			if (authorid != 0) {
				url = url + "&authorid=" + authorid;
			}
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
		if (this.pid == 0) {
			inflater.inflate(R.menu.articlelist_context_menu, menu);

		} else {
			inflater.inflate(R.menu.articlelist_context_menu_with_tid, menu);
		}
		int position = listview.getCheckedItemPosition();
		ThreadRowInfo row = new ThreadRowInfo();
		if (position < listview.getCount())
			row = (ThreadRowInfo) listview.getItemAtPosition(position);

		MenuItem mi = (MenuItem) menu.findItem(R.id.ban_thisone);
		if (mi != null && row != null) {
			if (row.get_isInBlackList()) {// 处于屏蔽列表，需要去掉
				mi.setTitle(R.string.cancel_ban_thisone);
			} else {
				mi.setTitle(R.string.ban_thisone);
			}
		}
		MenuItem votemenu = (MenuItem) menu.findItem(R.id.vote_dialog);
		if (votemenu != null && StringUtil.isEmpty(row.getVote())) {
			menu.removeItem(R.id.vote_dialog);
		}

	}

	private void handleReport(ThreadRowInfo row) {
		/*
		 * String url="http://bbs.ngacn.cc/nuke.php?func=logpost&tid=" + tid +
		 * "&pid="+ row.getPid() +"&log"; ReportTask task= new
		 * ReportTask(getActivity()); if(ActivityUtil.isGreaterThan_2_3_3())
		 * RunParallen(task, url); else task.execute(url);
		 */
		DialogFragment df = new ReportDialogFragment();
		Bundle args = new Bundle();
		args.putInt("tid", tid);
		args.putInt("pid", row.getPid());
		df.setArguments(args);
		df.show(getFragmentManager(), null);

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

	private boolean isComment(ThreadRowInfo row) {

		return row.getAlterinfo() == null && row.getAttachs() == null
				&& row.getComments() == null
				&& row.getJs_escap_avatar() == null && row.getLevel() == null
				&& row.getSignature() == null;
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
			if (toast != null) {
				toast.setText(R.string.floor_error);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(getActivity(), R.string.floor_error,
						Toast.LENGTH_SHORT);
				toast.show();
			}
			position = 0;
		}
		StringBuffer postPrefix = new StringBuffer();
		String tidStr = String.valueOf(this.tid);

		ThreadRowInfo row = (ThreadRowInfo) listview
				.getItemAtPosition(position);
		if (row == null) {

			if (toast != null) {
				toast.setText(R.string.unknow_error);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(getActivity(), R.string.unknow_error,
						Toast.LENGTH_SHORT);
				toast.show();
			}
			return true;
		}
		String content = row.getContent();
		final String name = row.getAuthor();
		final String uid = String.valueOf(row.getAuthorid());
		boolean isanonymous = row.getISANONYMOUS();
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
			final String postTime = row.getPostdate();

			content = checkContent(content);
			content = StringUtil.unEscapeHtml(content);
			if (row.getPid() != 0) {
				mention = name;
				postPrefix.append("[quote][pid=");
				postPrefix.append(row.getPid());
				postPrefix.append(',').append(tidStr).append(",").append(page);
				postPrefix.append("]");// Topic
				postPrefix.append("Reply");
				if (row.getISANONYMOUS()) {// 是匿名的人
					postPrefix.append("[/pid] [b]Post by [uid=");
					postPrefix.append("-1");
					postPrefix.append("]");
					postPrefix.append(name);
					postPrefix.append("[/uid][color=gray](");
					postPrefix.append(row.getLou());
					postPrefix.append("楼)[/color] (");
				} else {
					postPrefix.append("[/pid] [b]Post by [uid=");
					postPrefix.append(uid);
					postPrefix.append("]");
					postPrefix.append(name);
					postPrefix.append("[/uid] (");
				}
				postPrefix.append(postTime);
				postPrefix.append("):[/b]\n");
				postPrefix.append(content);
				postPrefix.append("[/quote]\n");
			}

			// case R.id.r:

			if (!StringUtil.isEmpty(mention))
				intent.putExtra("mention", mention);
			intent.putExtra("prefix",
					StringUtil.removeBrTag(postPrefix.toString()));
			intent.putExtra("tid", tidStr);
			intent.putExtra("action", "reply");
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().postActivityClass);
			} else {
				intent.setClass(getActivity(),
						PhoneConfiguration.getInstance().loginActivityClass);
			}
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
			break;

		case R.id.signature_dialog:
			if (isanonymous) {
				errordialog();
			} else {
				Create_Signature_Dialog(row);
			}
			break;
		case R.id.vote_dialog:
			Create_Vote_Dialog(row);
			break;

		case R.id.ban_thisone:
			if (isanonymous) {
				if (toast != null) {
					toast.setText(R.string.cannot_add_to_blacklist_cause_anony);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(getActivity(),
							R.string.cannot_add_to_blacklist_cause_anony,
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} else {
				Set<Integer> blacklist = PhoneConfiguration.getInstance().blacklist;
				String blickliststring = "";
				if (row.get_isInBlackList()) {// 在屏蔽列表中，需要去除
					row.set_IsInBlackList(false);
					blacklist.remove(row.getAuthorid());
					if (toast != null) {
						toast.setText(R.string.remove_from_blacklist_success);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(),
								R.string.remove_from_blacklist_success,
								Toast.LENGTH_SHORT);
						toast.show();
					}
				} else {
					row.set_IsInBlackList(true);
					blacklist.add(row.getAuthorid());
					if (toast != null) {
						toast.setText(R.string.add_to_blacklist_success);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(),
								R.string.add_to_blacklist_success,
								Toast.LENGTH_SHORT);
						toast.show();
					}
				}
				PhoneConfiguration.getInstance().blacklist = blacklist;
				blickliststring = blacklist.toString();
				SharedPreferences share = getActivity().getSharedPreferences(
						PERFERENCE, Context.MODE_PRIVATE);
				Editor editor = share.edit();
				editor.putString(BLACK_LIST, blickliststring);
				editor.commit();
				if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().uid)) {
					MyApp app = (MyApp) getActivity().getApplication();
					app.upgradeUserdata(blacklist.toString());
				} else {
					if (toast != null) {
						toast.setText(R.string.cannot_add_to_blacklist_cause_logout);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(),
								R.string.cannot_add_to_blacklist_cause_logout,
								Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}
			break;
		case R.id.show_profile:
			if (isanonymous) {
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
		case R.id.avatar_dialog:
			if (isanonymous) {
				errordialog();
			} else {
				Create_Avatar_Dialog(row);
			}
			break;
		case R.id.edit:
			if (isComment(row)) {
				if (toast != null) {
					toast.setText(R.string.cannot_eidt_comment);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(getActivity(),
							R.string.cannot_eidt_comment, Toast.LENGTH_SHORT);
					toast.show();
				}
				break;
			}
			Intent intentModify = new Intent();
			intentModify.putExtra("prefix",
					StringUtil.unEscapeHtml(StringUtil.removeBrTag(content)));
			intentModify.putExtra("tid", tidStr);
			String pid = String.valueOf(row.getPid());// getPid(map.get("url"));
			intentModify.putExtra("pid", pid);
			intentModify.putExtra("title",
					StringUtil.unEscapeHtml(row.getSubject()));
			intentModify.putExtra("action", "modify");
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
				intentModify.setClass(getActivity(),
						PhoneConfiguration.getInstance().postActivityClass);
			} else {
				intentModify.setClass(getActivity(),
						PhoneConfiguration.getInstance().loginActivityClass);
			}
			startActivity(intentModify);
			if (PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
			break;
		case R.id.copy_to_clipboard:
			CopyDialog(row.getFormated_html_data());
			break;
		case R.id.show_this_person_only:

			if (null == getActivity().findViewById(R.id.item_detail_container)) {
				Intent intentThis = new Intent();
				intentThis.putExtra("tab", "1");
				intentThis.putExtra("tid", tid);
				intentThis.putExtra("authorid", row.getAuthorid());
				intentThis.putExtra("fromreplyactivity", 1);
				intentThis.setClass(getActivity(),
						PhoneConfiguration.getInstance().articleActivityClass);
				startActivity(intentThis);
				if (PhoneConfiguration.getInstance().showAnimation)
					getActivity().overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
			} else {
				int tid1 = tid;
				int authorid1 = row.getAuthorid();
				ArticleContainerFragment f = ArticleContainerFragment
						.createshowonly(tid1, authorid1);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.addToBackStack(null);
				f.setHasOptionsMenu(true);
				ft.replace(R.id.item_detail_container, f);
				ft.commit();

			}

			// restNotifier.reset(0, row.getAuthorid());
			// ActivityUtil.getInstance().noticeSaying(getActivity());

			break;
		case R.id.show_whole_thread:
			if (null == getActivity().findViewById(R.id.item_detail_container)) {
				ResetableArticle restNotifier = null;
				try {
					restNotifier = (ResetableArticle) getActivity();
				} catch (ClassCastException e) {
					Log.e(TAG, "father activity does not implements interface "
							+ ResetableArticle.class.getName());
					return true;
				}
				restNotifier.reset(0, 0, row.getLou());
				ActivityUtil.getInstance().noticeSaying(getActivity());
			} else {
				int tid1 = tid;
				ArticleContainerFragment f = ArticleContainerFragment
						.createshowall(tid1);
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.addToBackStack(null);
				f.setHasOptionsMenu(true);
				ft.replace(R.id.item_detail_container, f);
				ft.commit();
			}
			break;
		case R.id.send_message:
			if (isanonymous) {
				errordialog();
			} else {
				start_send_message(row);
			}
			break;
		case R.id.post_comment:
			final String dialog_tag = "post comment";
			FragmentTransaction ft = getActivity().getSupportFragmentManager()
					.beginTransaction();
			Fragment prev = getActivity().getSupportFragmentManager()
					.findFragmentByTag(dialog_tag);
			if (prev != null) {
				ft.remove(prev);
			}
			DialogFragment df = new PostCommentDialogFragment();
			Bundle b = new Bundle();
			b.putInt("pid", row.getPid());
			b.putInt("tid", this.tid);
			df.setArguments(b);
			df.show(ft, dialog_tag);

			break;
		case R.id.report:
			handleReport(row);
			break;
		case R.id.search_post:
			intent.putExtra("searchpost", 1);
		case R.id.search_subject:
			intent.putExtra("authorid", row.getAuthorid());
			intent.setClass(getActivity(),
					PhoneConfiguration.getInstance().topicActivityClass);
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);

			break;
		case R.id.item_share:
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			String shareUrl = "http://nga.178.com/read.php?";
			if (row.getPid() != 0) {
				shareUrl = shareUrl + "pid=" + row.getPid()
						+ " (分享自NGA安卓客户端开源版)";
			} else {
				shareUrl = shareUrl + "tid=" + tid + " (分享自NGA安卓客户端开源版)";
			}
			if (!StringUtil.isEmpty(this.title)) {
				shareUrl = "《" + this.title + "》 - 艾泽拉斯国家地理论坛，地址：" + shareUrl;
			}
			intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
			String text = getResources().getString(R.string.share);
			getActivity().startActivity(Intent.createChooser(intent, text));
			break;

		}
		return true;
	}

	private void start_send_message(ThreadRowInfo row) {
		Intent intent_bookmark = new Intent();
		intent_bookmark.putExtra("to", row.getAuthor());
		intent_bookmark.putExtra("action", "new");
		intent_bookmark.putExtra("messagemode", "yes");
		if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
			intent_bookmark.setClass(getActivity(),
					PhoneConfiguration.getInstance().messagePostActivityClass);
		} else {
			intent_bookmark.setClass(getActivity(),
					PhoneConfiguration.getInstance().loginActivityClass);
		}
		startActivity(intent_bookmark);
	}

	private void errordialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("这白痴匿名了,神马都看不到");
		builder.setTitle("看不到");
		builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listview);
				}
			}
		});
	}

	private void Create_Vote_Dialog(ThreadRowInfo row) {
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.vote_dialog, null);
		String name = row.getAuthor();
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setView(view);
		alert.setTitle("投票/投注");
		// COLOR

		ThemeManager theme = ThemeManager.getInstance();
		int bgColor = getResources().getColor(theme.getBackgroundColor(0));
		int fgColor = getResources().getColor(theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x", bgColor);

		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x", htmlfgColor);

		WebViewClient client = new ArticleListWebClient(getActivity());
		final WebView contentTV = (WebView) view.findViewById(R.id.votewebview);
		contentTV.setBackgroundColor(0);
		if (ActivityUtil.isGreaterThan_2_2()) {
			contentTV.setLongClickable(false);
			contentTV.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					return true;
				}
			});
		}
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
				|| ArticleUtil.isInWifi();
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(true);
		setting.setJavaScriptCanOpenWindowsAutomatically(true);
		contentTV.addJavascriptInterface(new ProxyBridge(), "ProxyBridge");
		contentTV.setFocusableInTouchMode(true);
		contentTV.setFocusable(true);
		contentTV.setHapticFeedbackEnabled(true);
		contentTV.setClickable(true);
		contentTV.requestFocusFromTouch();
		contentTV.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public void onProgressChanged(WebView view, int newProgress){
				super.onProgressChanged(view, newProgress);
                view.requestFocus(View.FOCUS_DOWN);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                            case MotionEvent.ACTION_UP:
                                if (!v.hasFocus()) {
                                    v.requestFocus(View.FOCUS_DOWN);
                                }
                                break;
                        }
                        return false;
                    }
                });
			}
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final android.webkit.JsResult result) {
				final AlertDialog.Builder b2 = new AlertDialog.Builder(getActivity())
						.setMessage(message)
						.setPositiveButton("确定",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								});

				b2.setCancelable(false);
				b2.create();
				b2.show();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final android.webkit.JsResult result) {
				final AlertDialog.Builder b1 = new AlertDialog.Builder(
						getActivity())
						.setMessage(message)
						.setPositiveButton("确定",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNeutralButton("取消",
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								})
						.setOnCancelListener(
								new AlertDialog.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										result.cancel();
									}
								});
				b1.create();
				b1.show();
				return true;
			}
		});
		contentTV.setWebViewClient(client);
		contentTV.loadDataWithBaseURL(
				null,
				VoteToHtmlText(row, showImage, ArticleUtil.showImageQuality(),
						fgColorStr, bgcolorStr), "text/html", "utf-8", null);
		contentTV.requestLayout();
		alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final Dialog dialog = alert.create();	
		dialog.show();
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listview);
				}
			}
		});
	}

	final class ProxyBridge {
		@JavascriptInterface
		public void postURL(String url) {
			ActivityUtil.getInstance().noticeSaying("正在提交...", getActivity());
			(new AsyncTask<String, Integer, String>() {
				@Override
				protected void onPostExecute(String result) {
					ActivityUtil.getInstance().dismiss();
					if(StringUtil.isEmpty(result))
						result="未知错误,请重试";
					if(result.startsWith("操作成功"))
						result="操作成功";
					if (toast != null) {
						toast.setText(result);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(),
								result,
								Toast.LENGTH_SHORT);
						toast.show();
					}
				}

				@Override
				protected String doInBackground(String... params) {
					if(StringUtil.isEmpty(params[0]))
						return "选择错误";
					String url="http://nga.178.com/nuke.php?"+params[0];
					HttpPostClient c =  new HttpPostClient(url);
					String cookie = PhoneConfiguration.getInstance().getCookie();
					c.setCookie(cookie);
					try {
						InputStream input = null;
						HttpURLConnection conn = c.post_body(params[0]);
						if(conn!=null){
							if (conn.getResponseCode() >= 500) 
							{
								input = null;
							}
							else{
								if(conn.getResponseCode() >= 400)
								{
									input = conn.getErrorStream();
			                    }
								else
									input = conn.getInputStream();
							}
						}else{
							return "网络错误";
						}

						if(input != null)
						{
						String js = IOUtils.toString(input, "gbk");
						if (null == js) {
							return getActivity().getString(R.string.network_error);
						}
						js = js.replaceAll("window.script_muti_get_var_store=", "");
						JSONObject o = null, oerror = null;
						try {
							o = (JSONObject) JSON.parseObject(js).get("data");
							oerror = (JSONObject) JSON.parseObject(js).get("error");
						} catch (Exception e) {
							Log.e(TAG, "can not parse :\n" + js);
						}
						if (o == null) {
							if (oerror == null) {
								return "请重新登录";
							}else {
								if (!StringUtil.isEmpty(oerror.getString("0"))) {
									return oerror.getString("0");
								}else{
									return "二哥又开始乱搞了";
								}
							}
						}else{
							if (!StringUtil.isEmpty(o.getString("0"))) {
							return o.getString("0");
							}else{
							return "二哥又开始乱搞了";
							}
						}
						}else{
							return "二哥在用服务器下毛片";
						}
					} catch (IOException e) {
					}
					return "";
				}
			}).execute(url);
		}

	}

	private void Create_Signature_Dialog(ThreadRowInfo row) {
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
				|| ArticleUtil.isInWifi();
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		contentTV
				.loadDataWithBaseURL(
						null,
						signatureToHtmlText(row, showImage,
								ArticleUtil.showImageQuality(), fgColorStr,
								bgcolorStr), "text/html", "utf-8", null);
		alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listview);
				}
			}
		});
	}

	private void Create_Avatar_Dialog(ThreadRowInfo row) {
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
				avatarToHtmlText(row, true, ArticleUtil.showImageQuality(),
						fgColorStr, bgcolorStr), "text/html", "utf-8", null);
		alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listview);
				}
			}

		});
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

	public String signatureToHtmlText(final ThreadRowInfo row,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(row.getSignature(),
				showImage, imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {
			ngaHtml = row.getAlterinfo();
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

	public String VoteToHtmlText(final ThreadRowInfo row, boolean showImage,
			int imageQuality, final String fgColorStr, final String bgcolorStr) {
		if (StringUtil.isEmpty(row.getVote()))
			return "本楼没有投票/投注内容";
		String ngaHtml = String.valueOf(row.getTid()) + ",'" + row.getVote()
				+ "'";
		ngaHtml = "<!DOCTYPE html><html><head><meta http-equiv=Content-Type content=\"text/html;charset=utf-8\">"
				+ "<script type=\"text/javascript\" src=\"file:///android_asset/vote/vote.js\"></script><link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/vote/vote.css\" />"
				+ " </head><body style=\"color:#"+fgColorStr+"\"bgcolor= '#"
				+ bgcolorStr
				+ "'><span id='votec'></span><script>vote("
				+ ngaHtml
				+ ")</script></body></html>";
		return ngaHtml;
	}

	public String avatarToHtmlText(final ThreadRowInfo row, boolean showImage,
			int imageQuality, final String fgColorStr, final String bgcolorStr) {
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
			ngaHtml = row.getAlterinfo();
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

	private void CopyDialog(String content) {
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.copy_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setView(view);
		alert.setTitle(R.string.copy_hint);
		final EditText commentdata = (EditText) view
				.findViewById(R.id.copy_data);
		content = content.replaceAll("(?i)" + "<img src='(.+?)'(.+?){0,}>",
				"$1");
		Spanned spanned = Html.fromHtml(content);
		commentdata.setText(spanned);
		commentdata.selectAll();
		alert.setPositiveButton("复制", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int start = commentdata.getSelectionStart();
				int end = commentdata.getSelectionEnd();
				CharSequence selectText = commentdata.getText().subSequence(
						start, end);
				if (selectText.length() > 0) {
					android.text.ClipboardManager cbm = (android.text.ClipboardManager) getActivity()
							.getSystemService(Activity.CLIPBOARD_SERVICE);
					cbm.setText(StringUtil.removeBrTag(selectText.toString()));
					if (toast != null) {
						toast.setText(R.string.copied_to_clipboard);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(),
								R.string.copied_to_clipboard,
								Toast.LENGTH_SHORT);
						toast.show();
					}
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
					if (toast != null) {
						toast.setText("请选择要复制的内容");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(getActivity(), "请选择要复制的内容",
								Toast.LENGTH_SHORT);
						toast.show();
					}
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
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(listview);
				}
			}

		});
	}

	@Override
	public void finishLoad(ThreadData data) {
		Log.d(TAG, "finishLoad");
		// ArticleListActivity father = (ArticleListActivity)
		// this.getActivity();
		if (null != data) {
			mData = data;
			articleAdpater.setData(data);
			articleAdpater.notifyDataSetChanged();

			if (0 != data.getThreadInfo().getQuote_from())
				tid = data.getThreadInfo().getQuote_from();
			if (!StringUtil.isEmpty(data.getThreadInfo().getSubject())) {
				title = data.getThreadInfo().getSubject();
			}
			OnThreadPageLoadFinishedListener father = null;
			try {
				father = (OnThreadPageLoadFinishedListener) getActivity();
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
