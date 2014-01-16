package sp.phone.fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashSet;

import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.R;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
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

public class ArticleListFragment extends Fragment
	implements OnThreadPageLoadFinishedListener,PerferenceConstant{
	final static private String TAG = ArticleListFragment.class.getSimpleName();
	/*static final int QUOTE_ORDER = 0;
	static final int REPLY_ORDER = 1;
	static final int COPY_CLIPBOARD_ORDER = 2;
	static final int SHOW_THISONLY_ORDER = 3;
	static final int SHOW_MODIFY_ORDER = 4;
	static final int SHOW_ALL = 5;
	static final int POST_COMMENT = 6;
	static final int SEARCH_POST = 7;
	static final int SEARCH_SUBJECT = 8;*/
	private ListView listview=null;
	private ArticleListAdapter articleAdpater;
	//private JsonThreadLoadTask task;
	private int page=0;
	private int tid;
	private int pid;
	private int authorid;
	private boolean needLoad = true;
	private Object mActionModeCallback = null;
	private static Context activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		page = getArguments().getInt("page") + 1;
		tid = getArguments().getInt("id");
		pid = getArguments().getInt("pid", 0);
		authorid = getArguments().getInt("authorid", 0);
		articleAdpater = new ArticleListAdapter(this.getActivity());
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listview = new ListView(this.getActivity());
		

		listview.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
		listview.setDivider(null);
		
		
			activeActionMode();
			listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listview.setOnItemLongClickListener(new OnItemLongClickListener() {

				@TargetApi(11)
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					ListView lv = (ListView)parent;
					lv.setItemChecked(position, true);
					if (mActionModeCallback != null)
					{
						((ActionBarActivity) getActivity()).startSupportActionMode((Callback) mActionModeCallback);
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
	private void activeActionMode(){
		mActionModeCallback = new ActionMode.Callback() {
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				if(pid == 0){
					inflater.inflate(R.menu.articlelist_context_menu, menu);
					
				}else{
					inflater.inflate(R.menu.articlelist_context_menu_with_tid, menu);
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
				//int position = listview.getCheckedItemPosition();
				//listview.setItemChecked(position, false);
				
			}
			
		};
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume pid="+pid+"&page="+page);
		//setHasOptionsMenu(true);
		if (PhoneConfiguration.getInstance().isRefreshAfterPost()) {
			
			PagerOwnner father = null;
			try{
				father = (PagerOwnner) getActivity();
				if (father.getCurrentPage() == page) {
					PhoneConfiguration.getInstance().setRefreshAfterPost(false);
					//this.task = null;
					this.needLoad = true;
				}
			}catch(ClassCastException e){
				Log.e(TAG,"father activity does not implements interface " 
						+ PagerOwnner.class.getName());
				
			}

			
		}
		this.loadPage();
		super.onResume();
	}
	
	
	@TargetApi(11)
	private void RunParallen(JsonThreadLoadTask task, String url){
		task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
	}
	
	@TargetApi(11)
	private void RunParallen(ReportTask task, String url){
		task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
	}
	
	private void loadPage(){
		if(needLoad){

			Activity activity = getActivity();
			JsonThreadLoadTask task= new JsonThreadLoadTask(activity,this);
			String url = HttpUtil.Server + 
					"/read.php?"
					+"&page="+page
					+"&lite=js&noprefix&v2";
			if(tid !=0)
				url = url + "&tid="+ tid;
			if(pid !=0){
				url = url + "&pid="+ pid;
			}
			
			if(authorid !=0){
				url = url + "&authorid="+ authorid;
			}
			if(ActivityUtil.isGreaterThan_2_3_3())
				RunParallen(task, url);
			else
				task.execute(url);
		}else{
			ActivityUtil.getInstance().dismiss();
		}
		
		
	}
	

	
	
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		if(this.pid == 0){
			inflater.inflate(R.menu.articlelist_context_menu, menu);
			
		}else{
			inflater.inflate(R.menu.articlelist_context_menu_with_tid, menu);
		}

		
		
	}
	
	private void handleReport(ThreadRowInfo row){
		/*String url="http://bbs.ngacn.cc/nuke.php?func=logpost&tid="
				+ tid + "&pid="+ row.getPid()
				+"&log";
		ReportTask task= new ReportTask(getActivity());
		if(ActivityUtil.isGreaterThan_2_3_3())
			RunParallen(task, url);
		else
			task.execute(url);*/
		DialogFragment df = new ReportDialogFragment();
		Bundle args = new Bundle();
		args.putInt("tid", tid);
		args.putInt("pid", row.getPid());
		df.setArguments(args);
		df.show(getFragmentManager(), null);

	}
	

	private String checkContent(String content){
		int i;
		boolean mode=false;
		content=content.trim();
		String quotekeyword[][]={
				{"[customachieve]","[/customachieve]"},//0
				{"[wow","]]"},
				{"[lol","]]"},
				{"[cnarmory","]"},
				{"[usarmory","]"},
				{"[twarmory","]"},//5
				{"[euarmory","]"},
				{"[url","[/url]"},
				{"[color=","[/color]"},
				{"[size=","[/size]"},
				{"[font=","[/font]"},//10
				{"[b]","[/b]"},
				{"[u]","[/u]"},
				{"[i]","[/i]"},
				{"[del]","[/del]"},
				{"[align=","[/align]"},//15
				{"[h]","[/h]"},
				{"[l]","[/l]"},
				{"[r]","[/r]"},
				{"[list","[/list]"},
				{"[img]","[/img]"},//20
				{"[album=","[/album]"},
				{"[code]","[/code]"},
				{"[code=lua]","[/code] lua"},
				{"[code=php]","[/code] php"},
				{"[code=c]","[/code] c"},//25
				{"[code=js]","[/code] javascript"},
				{"[code=xml]","[/code] xml/html"},
				{"[flash]","[/flash]"},
				{"[table]","[/table]"},
				{"[tid","[/tid]"},//30
				{"[pid","[/pid]"},
				{"[dice]","[/dice]"},
				{"[crypt]","[/crypt]"},
				{"[randomblock]","[/randomblock]"},
				{"[@","]"},
				{"[t.178.com/","]"},
				{"[collapse","[/collapse]"},};
		while(content.startsWith("\n")){
			content=content.replaceFirst("\n", "");
		}
		if (content.length() > 100) {
			content = content.substring(0, 99);
			mode=true;
			}
			for(i=0;i<38;i++){
				while(content.toLowerCase().lastIndexOf(quotekeyword[i][0]) > content.toLowerCase().lastIndexOf(quotekeyword[i][1]))
					{
						content = content.substring(0,content.toLowerCase().lastIndexOf(quotekeyword[i][0]));
					}
			}
			if (mode) {
					content=content+"......";
			}
	return content.toString();
	}
	
	private boolean isComment(ThreadRowInfo row){
		
		return row.getAlterinfo() == null && row.getAttachs() == null && row.getComments() == null
				&& row.getJs_escap_avatar() == null && row.getLevel() == null && row.getSignature() == null;
	}
	
	@Override


	public boolean onContextItemSelected(MenuItem item) {
		
		Log.d(TAG, "onContextItemSelected,tid="
				+tid+ ",page="+page );
		PagerOwnner father = null;
		try{
			 father = (PagerOwnner) getActivity();
		}catch(ClassCastException e){
			Log.e(TAG,"father activity does not implements interface " 
					+ PagerOwnner.class.getName());
			return true;
		}
		
		if(father == null)
			return false;
		
		if(father.getCurrentPage() != page){
			return false;
		}
		
	
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = this.listview.getCheckedItemPosition();
		if(info != null){
			position = info.position;
		}
		if(position <0 || position >= listview.getAdapter().getCount()){
			Toast.makeText(getActivity(), R.string.floor_error,
					Toast.LENGTH_LONG	).show();
			position = 0;
		}
		StringBuffer postPrefix = new StringBuffer();
		String tidStr = String.valueOf(this.tid);

		
		ThreadRowInfo row = (ThreadRowInfo) listview.getItemAtPosition(position);
		if(row == null){
			Toast.makeText(getActivity(), R.string.unknow_error, Toast.LENGTH_LONG	).show();
			return true;
		}
		String content = row.getContent();
		String signature = row.getSignature();
		final String name = row.getAuthor();
		String mention=null;
		Intent intent = new Intent();
		switch(item.getItemId())
		//if( REPLY_POST_ORDER ==item.getItemId())
		{
		case R.id.quote_subject:

			final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
            final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
			content = content.replaceAll(quote_regex, "");
            content = content.replaceAll(replay_regex, "");
			final String postTime = row.getPostdate();
			
			content=checkContent(content);
			content = StringUtil.unEscapeHtml(content);
			if(row.getPid() != 0){
                mention = name;
				postPrefix.append("[quote][pid=");
				postPrefix.append(row.getPid());
                postPrefix.append(',')
                .append(tidStr)
                .append(",")
                .append(page);
				postPrefix.append("]");//Topic
				postPrefix.append("Reply");
                postPrefix.append("[/pid] [b]Post by ");
                postPrefix.append(name);
                postPrefix.append(" (");
                postPrefix.append(postTime);
                postPrefix.append("):[/b]\n");
                postPrefix.append(content);
                postPrefix.append("[/quote]\n");
			}

		//case R.id.r:	
			
			if(!StringUtil.isEmpty(mention))
				intent.putExtra("mention", mention);
			intent.putExtra("prefix", StringUtil.removeBrTag(postPrefix.toString()) );
			intent.putExtra("tid", tidStr);
			intent.putExtra("action", "reply");	
			intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter,
						R.anim.zoom_exit);
			break;
			
		case R.id.signature_dialog:
			Create_Signature_Dialog(row);
			break;
		case R.id.edit :
			if(isComment(row)){
				Toast.makeText(getActivity(), R.string.cannot_eidt_comment, Toast.LENGTH_SHORT).show();
				break;
			}
			Intent intentModify = new Intent();
			intentModify.putExtra("prefix", StringUtil.unEscapeHtml(StringUtil.removeBrTag(content)));
			intentModify.putExtra("tid", tidStr);
			String pid = String.valueOf(row.getPid());//getPid(map.get("url"));
			intentModify.putExtra("pid", pid);
			intentModify.putExtra("title",StringUtil.unEscapeHtml(row.getSubject()));
			intentModify.putExtra("action", "modify");	
			intentModify.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
			startActivity(intentModify);
            if(PhoneConfiguration.getInstance().showAnimation)
			    getActivity().overridePendingTransition(R.anim.zoom_enter,
					    R.anim.zoom_exit);
			break;
		case R.id.copy_to_clipboard:
//			//if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB )
//			//{
//				android.text.ClipboardManager  cbm = (android.text.ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
//				cbm.setText(StringUtil.removeBrTag(content));
//			//}else{
//				//android.content.ClipboardManager  cbm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//				//cbm.setPrimaryClip(ClipData.newPlainText("content", content));
//			//}
//
//			Toast.makeText(getActivity(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
			CopyDialog(content);
			break;
		case R.id.show_this_person_only:
			Intent intentThis = new Intent();
			intentThis.putExtra("tab", "1");
			intentThis.putExtra("tid",tid );
			intentThis.putExtra("authorid",row.getAuthorid() );
			
			intentThis.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
			startActivity(intentThis);
			if(PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		
			//restNotifier.reset(0, row.getAuthorid());
			//ActivityUtil.getInstance().noticeSaying(getActivity());

			break;
		case R.id.show_whole_thread:
			ResetableArticle restNotifier = null;
			try{
				 restNotifier= (ResetableArticle)getActivity();
			}catch(ClassCastException e){
				Log.e(TAG,"father activity does not implements interface " 
						+ ResetableArticle.class.getName());
				return true;
			}
			restNotifier.reset(0, 0,row.getLou());
			ActivityUtil.getInstance().noticeSaying(getActivity());
			break;
		case R.id.post_comment:
			final String dialog_tag = "post comment";
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(dialog_tag);
	        if (prev != null) {
	            ft.remove(prev);
	        }
			DialogFragment df = new PostCommentDialogFragment();
			Bundle b = new Bundle();
			b.putInt("pid",	row.getPid());
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
			intent.setClass(getActivity(), PhoneConfiguration.getInstance().topicActivityClass);
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		
			break;
		case R.id.item_share:
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			String shareUrl = "http://bbs.ngacn.cc/read.php?";
			if(row.getPid() != 0){
				shareUrl = shareUrl + "pid="+row.getPid();
			}
			else
			{
				shareUrl = shareUrl + "tid="+tid;
			}
			intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
			String text = getResources().getString(R.string.share);
			getActivity().startActivity(Intent.createChooser(intent, text));
			break;

			
			
		}
		return true;
	}
	private AlertDialog Create_Signature_Dialog(ThreadRowInfo row) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();  
	    final View view = layoutInflater.inflate(R.layout.signature_dialog, null);  
	    String name = row.getAuthor();
	    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());   
	    alert.setView(view);  
		alert.setTitle(name+"的签名");
		//COLOR

		ThemeManager theme = ThemeManager.getInstance();
		int bgColor = getResources().getColor(theme.getBackgroundColor(0));
		int fgColor = getResources().getColor(theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x",bgColor);
		
		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x",htmlfgColor);
		
		
	    WebViewClient client = new ArticleListWebClient(getActivity());
		WebView contentTV = (WebView) view.findViewById(R.id.signature);
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {
			contentTV.setLongClickable(false);
		}
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || ArticleUtil.isInWifi();
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		contentTV.loadDataWithBaseURL(null, signatureToHtmlText(row,showImage,ArticleUtil.showImageQuality(),fgColorStr,bgcolorStr),
				"text/html", "utf-8", null);
		alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		});
		return alert.show();
	}

	public String signatureToHtmlText(final ThreadRowInfo row,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(row.getSignature(), showImage,
				imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		if (StringUtil.isEmpty(ngaHtml)) {
			ngaHtml = row.getAlterinfo();
		}
		if (StringUtil.isEmpty(ngaHtml)) {
			ngaHtml = "<font color='red'>[" + this.getString(R.string.hide) + "]</font>";
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
	
	
	private AlertDialog  CopyDialog(String content) {
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();  
	    final View view = layoutInflater.inflate(R.layout.copy_dialog, null);  
	    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());   
	    alert.setView(view);  
		alert.setTitle(R.string.copy_hint);
		final EditText commentdata = (EditText) view.findViewById(R.id.copy_data);
		commentdata.setText(content);
		commentdata.selectAll(); 
		alert.setPositiveButton("复制", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int start=commentdata.getSelectionStart();  
                int end=commentdata.getSelectionEnd();   
                CharSequence selectText=commentdata.getText().subSequence(start, end); 
                if(selectText.length()>0){
                    android.text.ClipboardManager  cbm = (android.text.ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
    				cbm.setText(StringUtil.removeBrTag(selectText.toString()));
    				Toast.makeText(getActivity(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    				try  
    		        {  
    		            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
    		            field.setAccessible(true);  
    		            field.set(dialog, true);  
    		        } catch(Exception e) {  
    		            e.printStackTrace();  
    		        }  
                }else{
            		commentdata.selectAll(); 
                	Toast.makeText(getActivity(), "请选择要复制的内容", Toast.LENGTH_SHORT).show();
    				try  
    		           {  
    		               Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
    		               field.setAccessible(true);  
    		               field.set(dialog, false);  
    		           }catch(Exception e) {  
    		               e.printStackTrace();  
    		           }  
                }
			}
		});  
		alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
				try  
		        {  
		            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
		            field.setAccessible(true);  
		            field.set(dialog, true);  
		        } catch(Exception e) {  
		            e.printStackTrace();  
		        }  
            }
		});
		return alert.show();
		// TODO Auto-generated method stub
		
	}
	@Override
	public void finishLoad(ThreadData data) {
		Log.d(TAG, "finishLoad");
		//ArticleListActivity father = (ArticleListActivity) this.getActivity();
		if(null != data){
			articleAdpater.setData(data);
			articleAdpater.notifyDataSetChanged();

			if( 0 != data.getThreadInfo().getQuote_from())
				tid = data.getThreadInfo().getQuote_from();
			OnThreadPageLoadFinishedListener father = null;
			try{
				father = (OnThreadPageLoadFinishedListener)getActivity();
				if(father != null)
					father.finishLoad(data);
			}catch(ClassCastException e){
				Log.e(TAG, "father activity should implements OnThreadPageLoadFinishedListener");
			}

		}
		this.needLoad = false;
		
	}
	
	
	

}
