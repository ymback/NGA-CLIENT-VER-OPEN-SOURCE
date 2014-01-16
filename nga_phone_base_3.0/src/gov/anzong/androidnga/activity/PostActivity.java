package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.RelativeSizeSpan;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SettingsActivity.FontSizeListener;

import org.apache.commons.io.IOUtils;

import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.adapter.SpinnerUserListAdapter;
import sp.phone.bean.User;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.ThreadPostAction;
import sp.phone.fragment.EmotionCategorySelectFragment;
import sp.phone.fragment.EmotionDialogFragment;
import sp.phone.fragment.ExtensionEmotionFragment;
import sp.phone.fragment.SearchDialogFragment;
import sp.phone.interfaces.EmotionCategorySelectedListener;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.task.FileUploadTask;
import sp.phone.utils.*;

import sp.phone.utils.MD5Util;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class PostActivity extends SwipeBackAppCompatActivity
	implements FileUploadTask.onFileUploaded,
	EmotionCategorySelectedListener,
	OnEmotionPickedListener{

	private final String LOG_TAG = Activity.class.getSimpleName();
	static private final String EMOTION_CATEGORY_TAG = "emotion_category";
	static private final String EMOTION_TAG = "emotion";
	private String prefix;
	private EditText titleText;
	private EditText bodyText;
	private ThreadPostAction act; 
	private String action;
	private String tid;
	private int fid;
	//private Button button_commit;
	//private Button button_cancel;
	//private ImageButton button_upload;
	//private ImageButton button_emotion;
	Object commit_lock = new Object();
	private Spinner userList;
	private String REPLY_URL="http://bbs.ngacn.cc/post.php?";
	final int REQUEST_CODE_SELECT_PIC = 1;

	private boolean loading;
	private FileUploadTask  uploadTask = null;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		
		super.onCreate(savedInstanceState);
		View v = this.getLayoutInflater().inflate(R.layout.reply, null);
		v.setBackgroundColor(getResources()
				.getColor(
					ThemeManager.getInstance().getBackgroundColor()
				));
		this.setContentView(v);
		
		if(PhoneConfiguration.getInstance().uploadLocation
				&& PhoneConfiguration.getInstance().location == null
				)
		{
			ActivityUtil.reflushLocation(this);
		}

		Intent intent = this.getIntent();
		prefix = intent.getStringExtra("prefix");
		if(prefix!=null){
			prefix=prefix.replaceAll("\\n\\n", "\n");
		}
		action = intent.getStringExtra("action");
		if(action.equals("new")){
			this.setTitle(R.string.new_thread);
		}else if(action.equals("reply")){
			setTitle(R.string.reply_thread);
			
		}else if(action.equals("modify")){
			setTitle(R.string.modify_thread);
			
		}
		tid = intent.getStringExtra("tid");
		fid = intent.getIntExtra("fid", -7);
		String title = intent.getStringExtra("title");
		String pid = intent.getStringExtra("pid");
		String mention = intent.getStringExtra("mention");
		if(tid == null)
			tid = "";


		
		act = new ThreadPostAction(tid, "", "");
		act.setAction_(action);
		act.setFid_(fid);
		this.act.set__ngaClientChecksum(getngaClientChecksum());
		if(!StringUtil.isEmpty(mention))
			act.setMention_(mention);
		if(pid !=null)
			act.setPid_(pid);
		loading = false;
		
		titleText = (EditText) findViewById(R.id.reply_titile_edittext);
		if(title!=null){
			titleText.setText(title);
		}
		titleText.setSelected(true);
		bodyText = (EditText) findViewById(R.id.reply_body_edittext);
		if(prefix != null){
			if(prefix.startsWith("[quote][pid=")&&prefix.endsWith("[/quote]\n")){
				SpannableString spanString = new SpannableString(prefix);
				spanString.setSpan(new BackgroundColorSpan(-1513240), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), prefix.indexOf("[b]Post by"), prefix.indexOf("):[/b]")+5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
				bodyText.append(spanString);
			}else{
				bodyText.append(prefix);
			}
			bodyText.setSelection(prefix.length());
		}
		ThemeManager tm = ThemeManager.getInstance();
		if(tm.getMode() == ThemeManager.MODE_NIGHT ){
			bodyText.setBackgroundResource(tm.getBackgroundColor());
			titleText.setBackgroundResource(tm.getBackgroundColor());
			int textColor = this.getResources().getColor(tm.getForegroundColor());
			bodyText.setTextColor(textColor);
			titleText.setTextColor(textColor);
		}


		
		
		userList = (Spinner) findViewById(R.id.user_list);
		if (userList != null) {
			SpinnerUserListAdapter adapter = new SpinnerUserListAdapter(this);
			userList.setAdapter(adapter);
			userList.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					User u = (User) parent.getItemAtPosition(position);
					MyApp app = (MyApp) getApplication();
					app.addToUserList(u.getUserId(), u.getCid(),
							u.getNickName());
					PhoneConfiguration.getInstance().setUid(u.getUserId());
					PhoneConfiguration.getInstance().setCid(u.getCid());

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}

			});
		}else{
			this.setNavigation();
		}
	}
	
	private String getngaClientChecksum()
	  {
		String str = null;
	    try
	    {
	    	str = MD5Util.MD5(new StringBuilder(String.valueOf(PhoneConfiguration.getInstance().getUid())).append("3ebd769858c56bd345898154e4b44427").append(System.currentTimeMillis() / 1000L).toString()) + System.currentTimeMillis() / 1000L;
	      return str;
	    }
	    catch (Exception localException)
	    {
	      while (true)
	    	  str = MD5Util.MD5(new StringBuilder("3ebd769858c56bd345898154e4b44427").append(System.currentTimeMillis() / 1000L).toString()) + System.currentTimeMillis() / 1000L;
	    }
	  }
	
	@TargetApi(11)
	private void setNavigation(){
		ActionBar actionBar = getSupportActionBar();
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		 
		 final SpinnerUserListAdapter categoryAdapter = new ActionBarUserListAdapter(this);
		 OnNavigationListener callback = new OnNavigationListener(){

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				User u = (User)categoryAdapter.getItem(itemPosition);
				MyApp app = (MyApp) getApplication();
				app.addToUserList(u.getUserId(), u.getCid(),
						u.getNickName());
				PhoneConfiguration.getInstance().setUid(u.getUserId());
				PhoneConfiguration.getInstance().setCid(u.getCid());
				return true;
			}
			 
		 };
		actionBar.setListNavigationCallbacks(categoryAdapter, callback);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(PhoneConfiguration.getInstance().HandSide==1){//lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if(flag>=4){//大于等于4肯定有
				 getMenuInflater().inflate(R.menu.post_menu_left, menu);
				}
			else{
				 getMenuInflater().inflate(R.menu.post_menu, menu);
			}
		}else{
			 getMenuInflater().inflate(R.menu.post_menu, menu);
		}
		 final int flags = 7;
		 /*
		  * ActionBar.DISPLAY_SHOW_HOME;//2
			flags |= ActionBar.DISPLAY_USE_LOGO;//1
			flags |= ActionBar.DISPLAY_HOME_AS_UP;//4
		  */
		 ReflectionUtil.actionBar_setDisplayOption(this, flags);
		 return true;
	}

	private ButtonCommitListener commitListener = null;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.upload :
            Intent intent = new Intent();  
            intent.setType("image/*");  
            intent.setAction(Intent.ACTION_GET_CONTENT);   
            startActivityForResult(intent,  REQUEST_CODE_SELECT_PIC);  
            break;
		case R.id.emotion:
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Fragment prev = getSupportFragmentManager().findFragmentByTag
					(EMOTION_CATEGORY_TAG);
			if (prev != null) {
	            ft.remove(prev);
	        }

	        DialogFragment newFragment = new EmotionCategorySelectFragment();
	        newFragment.show(ft, EMOTION_CATEGORY_TAG);
			break;
		case R.id.supertext:
			handleSupertext();
			break;
		case R.id.send:
			if(commitListener == null)
			{
				commitListener = new ButtonCommitListener(REPLY_URL);
			}
			commitListener.onClick(null);
			break;
		default:
			finish();
		}
		return true;
	}

	private AlertDialog handleSupertext(){
		final int index = bodyText.getSelectionStart();
	    LayoutInflater layoutInflater = getLayoutInflater();  
	    final View view = layoutInflater.inflate(R.layout.supertext_dialog, null);  
	    AlertDialog.Builder alert = new AlertDialog.Builder(this);   
	    alert.setView(view);  
		alert.setTitle(R.string.supertext_hint);
		final Spinner fontcolorSpinner;
		final Spinner fontsizeSpinner;
		RadioGroup selectradio ;
		final TextView font_size;
		final TextView font_color;
		final RadioButton atsomeone_button;
		final RadioButton urladd_button;
		final RadioButton quoteadd_button;
		final CheckBox bold_checkbox;//加粗
		final CheckBox italic_checkbox;//斜体
		final CheckBox underline_checkbox;//下划线
		final CheckBox fontcolor_checkbox;
		final CheckBox fontsize_checkbox;
		final CheckBox delline_checkbox;
		
		
		
		font_size = (TextView) view.findViewById(R.id.font_size);
		font_color = (TextView) view.findViewById(R.id.font_color);
		atsomeone_button=(RadioButton) view.findViewById(R.id.atsomeone);
		urladd_button=(RadioButton) view.findViewById(R.id.urladd);
		quoteadd_button=(RadioButton) view.findViewById(R.id.quoteadd);
		selectradio = (RadioGroup) view.findViewById(R.id.radioGroupA);
		bold_checkbox = (CheckBox) view.findViewById(R.id.bold);
		italic_checkbox = (CheckBox) view.findViewById(R.id.italic);
		underline_checkbox = (CheckBox) view.findViewById(R.id.underline);
		underline_checkbox.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		underline_checkbox.getPaint().setAntiAlias(true);// 抗锯齿
		fontcolor_checkbox = (CheckBox) view.findViewById(R.id.fontcolor);
		fontsize_checkbox = (CheckBox) view.findViewById(R.id.fontsize);
		delline_checkbox = (CheckBox) view.findViewById(R.id.delline);
		delline_checkbox.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		delline_checkbox.getPaint().setAntiAlias(true);// 抗锯齿

		final float defaultFontSize = font_size.getTextSize();
		
		final EditText input = (EditText) view.findViewById(R.id.inputsupertext_dataa);
		
		fontcolorSpinner =(Spinner) view.findViewById(R.id.font_color_spinner);
		fontsizeSpinner = (Spinner) view.findViewById(R.id.fontsize_spinner);
		final int scolorspan[]={-16777216,
				-7876885,-13149723,
				-16776961,-16777077,
				-23296,-47872,
				-2354116,-65536,
				-5103070,-7667712,
				-16744448,-13447886,
				-13726889,-16744320,
				-60269,-40121,
				-32944,-8388480,
				-11861886,-2180985,
				-744352,-6270419,
				-2987746,-4144960,
		};
		

		final String scolor[]={"[color=skyblue]","[color=royalblue]",
				"[color=blue]","[color=darkblue]",
				"[color=orange]","[color=orangered]",
				"[color=crimson]","[color=red]",
				"[color=firebrick]","[color=darkred]",
				"[color=green]","[color=limegreen]",
				"[color=seagreen]","[color=teal]",
				"[color=deeppink]","[color=tomato]",
				"[color=coral]","[color=purple]",
				"[color=indigo]","[color=burlywood]",
				"[color=sandybrown]","[color=sienna]",
				"[color=chocolate]","[color=silver]"};
		final String ssize[]={
				"[size=100%]","[size=110%]","[size=120%]","[size=130%]","[size=150%]",
				"[size=200%]","[size=250%]","[size=300%]","[size=400%]","[size=500%]"
		};
		final float ssizespan[]={1.0f,1.1f,1.2f,1.3f,1.5f,2.0f,2.5f,3.0f,4.0f,5.0f,1.2f};
		
		BaseAdapter adapterfontcolor =new BaseAdapter(){

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return view.getResources().getStringArray(R.array.colorchoose).length;    //选项总个数
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			 public View getView(int arg0, View arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				LinearLayout ll=new LinearLayout(PostActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);   //设置朝向
				TextView tv=new TextView(PostActivity.this);
                tv.setText(view.getResources().getStringArray(R.array.colorchoose)[arg0]);//设置内容
                tv.setTextColor(scolorspan[arg0]);//设置字体颜色
                ll.addView(tv);  //添加到LinearLayout中
                return ll;
			}};
		fontcolorSpinner.setAdapter(adapterfontcolor);
		fontcolorSpinner.setSelection(0);
		
		
		
		
		
		BaseAdapter adapterfontsize =new BaseAdapter(){

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return view.getResources().getStringArray(R.array.fontsizechoose).length;    //选项总个数
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			 public View getView(int arg0, View arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				LinearLayout ll=new LinearLayout(PostActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);   //设置朝向
				TextView tv=new TextView(PostActivity.this);
                tv.setText(view.getResources().getStringArray(R.array.fontsizechoose)[arg0]);//设置内容
                tv.setTextSize(ssizespan[arg0]*defaultFontSize);//设置字体大小
                ll.addView(tv);  //添加到LinearLayout中
                return ll;
			}};
		fontsizeSpinner.setAdapter(adapterfontsize);
		fontsizeSpinner.setSelection(0);
		
		//开始下面两个选项没有的
		font_size.setVisibility(View.GONE);
		font_color.setVisibility(View.GONE);
		fontsizeSpinner.setVisibility(View.GONE);
		fontcolorSpinner.setVisibility(View.GONE);
		//选中上面那排，下面的就不能选
		selectradio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				bold_checkbox.setChecked(false);
				italic_checkbox.setChecked(false);
				underline_checkbox.setChecked(false);
				fontcolor_checkbox.setChecked(false);
				fontsize_checkbox.setChecked(false);
				delline_checkbox.setChecked(false); 
				font_size.setVisibility(View.GONE);
				font_color.setVisibility(View.GONE);
				fontsizeSpinner.setVisibility(View.GONE);
				fontcolorSpinner.setVisibility(View.GONE);
				}

			
		});
		
		//选中下面那排，上面的别选了,加粗
		bold_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
                	atsomeone_button.setChecked(false);
                	urladd_button.setChecked(false);
                	quoteadd_button.setChecked(false);
                }
            } 
			
		});

		
		//选中下面那排，上面的别选了，斜体
		italic_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
                	atsomeone_button.setChecked(false);
                	urladd_button.setChecked(false);
                	quoteadd_button.setChecked(false);
                }
            } 
			
		});
		
		//选中下面那排，上面的别选了，下划线
		underline_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
                	atsomeone_button.setChecked(false);
                	urladd_button.setChecked(false);
                	quoteadd_button.setChecked(false);
                }
            } 
			
		});
		
		//选中下面那排，上面的别选了，颜色
		fontcolor_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
                	atsomeone_button.setChecked(false);
                	urladd_button.setChecked(false);
                	quoteadd_button.setChecked(false);
    				font_color.setVisibility(View.VISIBLE);
    				fontcolorSpinner.setVisibility(View.VISIBLE);
                }else{
    				font_color.setVisibility(View.GONE);
    				fontcolorSpinner.setVisibility(View.GONE);
                }
            } 
			
		});
		
		//选中下面那排，上面的别选了，字号
		fontsize_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
                	atsomeone_button.setChecked(false);
                	urladd_button.setChecked(false);
                	quoteadd_button.setChecked(false);
    				font_size.setVisibility(View.VISIBLE);
    				fontsizeSpinner.setVisibility(View.VISIBLE);
                }else{
    				font_size.setVisibility(View.GONE);
    				fontsizeSpinner.setVisibility(View.GONE);
                }
            } 
			
		});

		//选中下面那排，上面的别选了，删除线
		delline_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

			@Override 
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                // TODO Auto-generated method stub 
                if(isChecked){ 
                	atsomeone_button.setChecked(false);
                	urladd_button.setChecked(false);
                	quoteadd_button.setChecked(false);
                }
            } 
			
		});
		
		
		alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String inputdata=input.getText().toString();
				// TODO Auto-generated method stub
				if(!inputdata.replaceAll("\\n", "").trim().equals("")){
				if(atsomeone_button.isChecked()){
					inputdata="[@"+inputdata+"]";
				}else if(urladd_button.isChecked()){
					if(inputdata.startsWith("http:") || inputdata.startsWith("https:") ||
							inputdata.startsWith("ftp:") || inputdata.startsWith("gopher:") ||
							inputdata.startsWith("news:") || inputdata.startsWith("telnet:") ||
							inputdata.startsWith("mms:") || inputdata.startsWith("rtsp:")	){
						inputdata="[url]"+inputdata+"[/url]";
					}else{
						Toast.makeText(PostActivity.this, "URL需以http|https|ftp|gopher|news|telnet|mms|rtsp开头", Toast.LENGTH_SHORT).show();
					}
				}else if(quoteadd_button.isChecked()){
					inputdata="[quote]"+inputdata+"[/quote]";
				}else{
					if(fontcolor_checkbox.isChecked()){
						if(fontcolorSpinner.getSelectedItemPosition()>0){
						inputdata=scolor[fontcolorSpinner.getSelectedItemPosition()-1]+inputdata+"[/color]";
						}
					}
					if(italic_checkbox.isChecked()){
						inputdata="[i]"+inputdata+"[/i]";
					}
					if(bold_checkbox.isChecked()){
						inputdata="[b]"+inputdata+"[/b]";
					}
					if(underline_checkbox.isChecked()){
						inputdata="[u]"+inputdata+"[/u]";
					}
					if(delline_checkbox.isChecked()){
						inputdata="[del]"+inputdata+"[/del]";
					}
					if(fontsize_checkbox.isChecked()){
						if(fontsizeSpinner.getSelectedItemPosition()<10){
							inputdata=ssize[fontsizeSpinner.getSelectedItemPosition()]+inputdata+"[/size]";
							}else{
								inputdata="[h]"+inputdata+"[/h]";
							}
					}
				}
				SpannableString spanString = new SpannableString(inputdata);    
				
				
				if(atsomeone_button.isChecked()){
					spanString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, inputdata.length(), 
			                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
				}else if(urladd_button.isChecked()){
					if(input.getText().toString().startsWith("http:") || input.getText().toString().startsWith("https:") ||
							input.getText().toString().startsWith("ftp:") || input.getText().toString().startsWith("gopher:") ||
							input.getText().toString().startsWith("news:") || input.getText().toString().startsWith("telnet:") ||
							input.getText().toString().startsWith("mms:") || input.getText().toString().startsWith("rtsp:")	){
						spanString.setSpan(new URLSpan(input.getText().toString()), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
					}   
				}else if(quoteadd_button.isChecked()){
					spanString.setSpan(new BackgroundColorSpan(-1513240), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
				}else{
					if(fontcolor_checkbox.isChecked()){
							spanString.setSpan(new ForegroundColorSpan(scolorspan[fontcolorSpinner.getSelectedItemPosition()]), 0, inputdata.length(),  
					                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					if(italic_checkbox.isChecked()){
						spanString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
					}
					if(bold_checkbox.isChecked()){
						spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
					}
					if(underline_checkbox.isChecked()){
						spanString.setSpan(new UnderlineSpan(), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
					}
					if(delline_checkbox.isChecked()){
						spanString.setSpan(new StrikethroughSpan(), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
					}
					if(fontsize_checkbox.isChecked()){
						if(fontsizeSpinner.getSelectedItemPosition()<10){
							spanString.setSpan(new RelativeSizeSpan(ssizespan[fontsizeSpinner.getSelectedItemPosition()]), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							}else{
								spanString.setSpan(new BackgroundColorSpan(Color.GRAY), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								spanString.setSpan(new RelativeSizeSpan(1.2f), 0, inputdata.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								
							}
					}
				}
					if(bodyText.getText().toString().replaceAll("\\n", "").trim().equals("")){//NO INPUT DATA
						bodyText.setText("");
						bodyText.append(spanString);
					}else{
						if (index <= 0 || index >= bodyText.length() ){// pos @ begin / end
							bodyText.append(spanString);
						}else{
							bodyText.getText().insert(index,spanString);
						}
					}
				InputMethodManager imm = (InputMethodManager) bodyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}else{
				bodyText.setFocusableInTouchMode(true);
				bodyText.setFocusable(true);
				InputMethodManager imm = (InputMethodManager) bodyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                dialog.dismiss();
			}}
		});  
		alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
				InputMethodManager imm = (InputMethodManager) bodyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                dialog.dismiss();
            }
		});
		return alert.show();
	}
	
	@Override
	public void onEmotionPicked(String emotion){
		final int index = bodyText.getSelectionStart();
		String urltemp=emotion.replaceAll("\\n", "");
		if(urltemp.indexOf("http")>0){
		urltemp=urltemp.substring(5, urltemp.length()-6);
		String sourcefile=ExtensionEmotionAdapter.getPathByURI(urltemp);
		InputStream is = null;
		try {
			is = getResources().getAssets().open(sourcefile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(is!=null){
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable drawable = (Drawable) bd;
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		SpannableString spanString = new SpannableString(emotion);    
		ImageSpan span = new ImageSpan(drawable, 
				ImageSpan.ALIGN_BASELINE);  spanString.setSpan(span, 0, emotion.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(bodyText.getText().toString().replaceAll("\\n", "").trim().equals("")){
				bodyText.append(spanString);
				}else{
					if (index <= 0 || index >= bodyText.length() ){// pos @ begin / end
						bodyText.append(spanString);
					}else{
						bodyText.getText().insert(index,spanString);
					}
				}
		}}else{
			int[]  emotions= {1,2,3,24,25,26,27,28,29,
					30,32,33,34,35,36,37,38,39,
					4,40,41,42,43,5,6,7,8
					};
			for(int i=0;i<27;i++){
				if(emotion.indexOf("[s:"+String.valueOf(emotions[i])+"]")==0){
					String sourcefile="a"+String.valueOf(emotions[i])+".gif";
					InputStream is = null;
					try {
						is = getResources().getAssets().open(sourcefile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(is!=null){
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					BitmapDrawable bd = new BitmapDrawable(bitmap);
					Drawable drawable = (Drawable) bd;
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
					SpannableString spanString = new SpannableString(emotion);    
					ImageSpan span = new ImageSpan(drawable, 
					ImageSpan.ALIGN_BASELINE);  spanString.setSpan(span, 0, emotion.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					if (index <= 0 || index >= bodyText.length() ){// pos @ begin / end
							bodyText.append(spanString);
					}else{
							bodyText.getText().insert(index,spanString);
					}
				}else{
					bodyText.append(emotion);
				}
					break;}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_CANCELED ||data == null )
			return;
		switch(requestCode)
		{
		case REQUEST_CODE_SELECT_PIC :
				Log.i(LOG_TAG, " select file :" + data.getDataString() );
				uploadTask = new FileUploadTask(this, this, data.getData());
				break;
		default:
				;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}



	@Override
	protected void onResume() {
		if(action.equals("new")){
			titleText.requestFocus();
		}else{
			bodyText.requestFocus();
		}
		if(uploadTask != null){
			FileUploadTask temp = uploadTask;
			uploadTask = null;
			if(ActivityUtil.isGreaterThan_2_3_3()){
				RunParallel(temp);
			}
			else
			{
				temp.execute();
			}
		}
		super.onResume();
	}

	private String buildSig()
	{
		StringBuilder sb  = new StringBuilder();
		/*sb.append("\n[url=https://play.google.com/store/apps/details?fuck&id=gov.anzong.androidnga");
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		if(config.location != null && config.uploadLocation)
		{
			String loc = new StringBuilder().append(config.location.getLatitude())
							.append(",")
							.append(config.location.getLongitude()).toString();
			sb.append("&");
			try {
				sb.append(Des.enCrypto(loc, StringUtil.key));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append("ffff");
		} */
//		if(PhoneConfiguration.getInstance().showNewweiba){
//			String scolorweiba[]={"[color=silver]","[color=royalblue]","[color=blue]","[color=darkblue]","[color=orange]","[color=orangered]","[color=crimson]","[color=red]","[color=firebrick]","[color=darkred]","[color=green]","[color=limegreen]","[color=seagreen]","[color=teal]","[color=deeppink]","[color=tomato]","[color=coral]","[color=purple]","[color=indigo]","[color=burlywood]","[color=sandybrown]","[color=sienna]","[color=chocolate]","[color=silver]"};
//			String weibacolor = scolorweiba[(int)(Math.random()*23)];
//			sb.append("\n\n").append(weibacolor).append("----sent from my ")
//			.append(android.os.Build.MANUFACTURER).append(" ")
//			.append(android.os.Build.MODEL).append(",Android ")
//			.append(android.os.Build.VERSION.RELEASE)
//			.append(" [/color]\n");
//		}else{
//			sb.append("");
//		}
		sb.append("");
		return sb.toString();
		
	}
	
	@TargetApi(11)
	private void RunParallel(FileUploadTask task){
		task.executeOnExecutor(FileUploadTask.THREAD_POOL_EXECUTOR);
	}


	class ButtonCommitListener implements OnClickListener{

		private final String url;		
		ButtonCommitListener(String url){
			this.url = url;
		}
		@Override
		public void onClick(View v) {
			synchronized(commit_lock){
				if(loading == true){
					String avoidWindfury = PostActivity.this.getString(R.string.avoidWindfury);
					Toast.makeText(PostActivity.this, avoidWindfury, Toast.LENGTH_SHORT).show();
					return ;
				}
				loading = true;
			}
			
			if(action.equals("reply")){
				handleReply(v);
			}else if(action.equals("new")){
				handleNewThread(v);
			}else if(action.equals("modify")){
				handleNewThread(v);
			}
		}
		public void handleNewThread(View v){
			handleReply(v);
			
		}
		
		public void handleReply(View v1) {


			act.setPost_subject_(titleText.getText().toString());
			if(bodyText.getText().toString().length()>0){
			if(!act.getAction_().equals("modify"))
				act.setPost_content_(ColorTxtCheck() + buildSig());
			else
				act.setPost_content_(ColorTxtCheck());
			new ArticlePostTask(PostActivity.this).execute(url,act.toString());}

			
			
		}

		
	}
	private String ColorTxtCheck()
	{
		String xxtp="";
		if(PhoneConfiguration.getInstance().showColortxt){
			xxtp=ColorTxt();
		}else{
			xxtp=bodyText.getText().toString();
		}
		return xxtp.toString();
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
			content=content.substring(1);
		}
		while(content.startsWith("\r\n")){
			content=content.substring(2);
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
	
	private String ColorTxt()
	{
		String bodyString = bodyText.getText().toString().trim();
		while(bodyString.startsWith("\n")){
			bodyString=bodyString.substring(1);
		}
        String existquotetxt="";
        if(bodyString.toLowerCase().indexOf("[quote]")==0){
        	existquotetxt=bodyString.substring(0,bodyString.toLowerCase().indexOf("[/quote]"))+"[/quote]";
        	bodyString=bodyString.substring(bodyString.toLowerCase().indexOf("[/quote]")+8);
        }
        int i,ia,ib,itmp=0,bslenth,tmplenth;
        bslenth=bodyString.length();
        
        
		String scolor[]={"[color=skyblue]","[color=royalblue]","[color=blue]","[color=darkblue]","[color=orange]","[color=orangered]","[color=crimson]","[color=red]","[color=firebrick]","[color=darkred]","[color=green]","[color=limegreen]","[color=seagreen]","[color=teal]","[color=deeppink]","[color=tomato]","[color=coral]","[color=purple]","[color=indigo]","[color=burlywood]","[color=sandybrown]","[color=sienna]","[color=chocolate]","[color=silver]"};
		String keyword[][]={
				{"[customachieve]","[/customachieve]","16"},
				{"[wow","]]","2"},
				{"[lol","]]","2"},
				{"[cnarmory","]","1"},
				{"[usarmory","]","1"},
				{"[twarmory","]","1"},
				{"[euarmory","]","1"},
				{"[url","[/url]","6"},
				{"[size=","]","1"},
				{"[/size]","[/size]","7"},
				{"[font=","]","1"},
				{"[/font]","[/font]","7"},
				{"[b]","[b]","3"},
				{"[/b]","[/b]","4"},
				{"[u]","[u]","3"},
				{"[/u]","[/u]","4"},
				{"[i]","[i]","3"},
				{"[/i]","[/i]","4"},
				{"[del]","[del]","5"},
				{"[/del]","[/del]","6"},
				{"[align","]","1"},
				{"[/align]","[/align]","8"},
				{"[l]","[l]","3"},
				{"[/l]","[/l]","4"},
				{"[h]","[h]","3"},
				{"[/h]","[/h]","4"},
				{"[r]","[r]","3"},
				{"[/r]","[/r]","4"},
				{"[img]","[/img]","6"},
				{"[album=","[/album]","8"},
				{"[code]","[/code]","7"},
				{"[code=lua]","[/code] lua","11"},
				{"[code=php]","[/code] php","11"},
				{"[code=c]","[/code] c","9"},
				{"[code=js]","[/code] javascript","18"},
				{"[code=xml]","[/code] xml/html","16"},
				{"[flash]","[/flash]","8"},
				{"[table]","[table]","7"},
				{"[/table]","[/table]","8"},
				{"[tid","[/tid]","6"},
				{"[pid","[/pid]","6"},
				{"[dice]","[/dice]","7"},
				{"[crypt]","[/crypt]","8"},
				{"[randomblock]","[randomblock]","13"},
				{"[/randomblock]","[/randomblock]","14"},
				{"[@","]","1"},
				{"[t.178.com/","]","1"},
				{"[tr]","[tr]","4"},
				{"[/tr]","[/tr]","5"},
				{"[td","]","1"},
				{"[/td]","[/td]","5"},
				{"[*]","[*]","3"},
				{"[list","]","1"},
				{"[/list]","[/list]","7"},
				{"[collapse","]","1"},
				{"[/collapse]","[/collapse]","11"}};
		char[] arrtxtchar=bodyString.toCharArray();
		String txtsendout = scolor[(int)(Math.random()*23)];
		String quotetxt="";
		for(i=0;i<bslenth;i++)
			{
				if(Character.toString(arrtxtchar[i]).equals("\n")==false && Character.toString(arrtxtchar[i]).equals("[")==false && Character.toString(arrtxtchar[i]).equals(" ")==false){
					txtsendout += arrtxtchar[i]+"[/color]"+scolor[(int)(Math.random()*23)];/*开始就是普通文字的话就直接加彩色字体了*/
				}else if(Character.toString(arrtxtchar[i]).equals("[")){//首字符是[要判断
					if(bodyString.toLowerCase().indexOf("[quote]",i-1)==i){//是引用的话
		    			if(bodyString.toLowerCase().indexOf("[quote]",i-1)>bodyString.toLowerCase().indexOf("[/quote]",i-1)){//这个他妈的引用没完
		    				quotetxt=bodyString.substring(i+7);
			    			if(quotetxt.toLowerCase().lastIndexOf("[")>=0){//最后还有点留下来
			    				quotetxt=quotetxt.substring(0,quotetxt.toLowerCase().lastIndexOf("["));
			    			}
			    			while(quotetxt.endsWith(".")){
			    				quotetxt = quotetxt.substring(0,quotetxt.length()-1);
			    			}
		    		        bslenth=bodyString.length();
							txtsendout = txtsendout.substring(0,txtsendout.toLowerCase().lastIndexOf("[color"));
			    			quotetxt="[quote]"+checkContent(quotetxt)+"[/quote]";
		    		        txtsendout+=quotetxt+scolor[(int)(Math.random()*23)];
		    		        break;
		    			}else{
		        			quotetxt=bodyString.substring(i+7,bodyString.toLowerCase().indexOf("[/quote]",i));
		    			while(quotetxt.endsWith(".")){
		    				quotetxt = quotetxt.substring(0,quotetxt.length()-1);
		    			}
						txtsendout = txtsendout.substring(0,txtsendout.toLowerCase().lastIndexOf("[color"));
		    			quotetxt="[quote]"+checkContent(quotetxt)+"[/quote]";
	    		        txtsendout+=quotetxt+scolor[(int)(Math.random()*23)];
		    			i=bodyString.toLowerCase().indexOf("[/quote]",i)+7;
		    			}
		    	}else if(bodyString.toLowerCase().indexOf("[color",i-1)==i){
						if(bodyString.toLowerCase().indexOf("[/color]",i)>=0){
							txtsendout += bodyString.substring(bodyString.indexOf("]",i)+1,bodyString.toLowerCase().indexOf("[/color]",i)+8)+scolor[(int)(Math.random()*23)];
							i=bodyString.indexOf("[/color]",i)+7;
						}else{
							bodyString=bodyString.substring(0,i)+bodyString.substring(bodyString.toLowerCase().indexOf("]",i)+1,bslenth);
							i=bodyString.indexOf("]",i);
						}
					}else{
					for(ia=0;ia<56;ia++){
						 if(bodyString.toLowerCase().indexOf(keyword[ia][0],i-1)==i){
							if(bodyString.toLowerCase().indexOf(keyword[ia][1],i)>=0){
							txtsendout = txtsendout.substring(0,txtsendout.toLowerCase().lastIndexOf("[color"));
							txtsendout += bodyString.substring(i,bodyString.toLowerCase().indexOf( keyword[ia][1],i))+keyword[ia][1]+scolor[(int)(Math.random()*23)];
							i=bodyString.toLowerCase().indexOf(keyword[ia][1],i)+Integer.parseInt(keyword[ia][2])-1;}
							else{
								itmp=bodyString.indexOf("]",i);
								bodyString=bodyString.substring(0,i)+bodyString.substring(bodyString.toLowerCase().indexOf("]",i)+1,bslenth);
								i=itmp;
							}
							break;
						}
					}}
				}else if(Character.toString(arrtxtchar[i]).equals(" ") || Character.toString(arrtxtchar[i]).equals("\n")){
					txtsendout = txtsendout.substring(0,txtsendout.toLowerCase().lastIndexOf("[color"));
					txtsendout += bodyString.substring(i,i+1)+scolor[(int)(Math.random()*23)];
				}
			}
		if(txtsendout.toLowerCase().lastIndexOf("[color")>=0){
		txtsendout = txtsendout.substring(0,txtsendout.toLowerCase().lastIndexOf("[color"));}
		txtsendout = existquotetxt+txtsendout.replaceAll("&nbsp;"," ").trim();
		return txtsendout.toString();
	}
	
	private class ArticlePostTask extends AsyncTask<String, Integer, String>{

		final Context c;
		private final String result_start_tag = "<span style='color:#aaa'>&gt;</span>";
		private final String result_end_tag = "<br/>";
		private boolean keepActivity = false;
		public ArticlePostTask(Context context) {
			super();
			this.c = context;
		}
		
		@Override
		protected void onPreExecute() {
			ActivityUtil.getInstance().noticeSaying(c);
			super.onPreExecute();
		}

		@Override
		protected void onCancelled() {
			synchronized(commit_lock){
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled();
		}

		@Override
		protected void onCancelled(String result) {
			synchronized(commit_lock){
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled();
		}

		@Override
		protected String doInBackground(String... params) {
			if(params.length<2)
				return "parameter error";
			String ret = "网络错误";
			String url = params[0];
			String body = params[1];
			
			HttpPostClient c =  new HttpPostClient(url);
			String cookie = PhoneConfiguration.getInstance().getCookie();
			c.setCookie(cookie);
			try {
				InputStream input = null;
				HttpURLConnection conn = c.post_body(body);
				if(conn!=null){
					if (conn.getResponseCode() >= 500) 
					{
						input = null;
                        keepActivity = true;
						ret = "二哥在用服务器下毛片";
					}
					else{
						if(conn.getResponseCode() >= 400)
						{
							input = conn.getErrorStream();
	                        keepActivity = true;
	                    }
						else
							input = conn.getInputStream();
					}
				}
				else
					keepActivity = true;

				if(input != null)
				{
				String html = IOUtils.toString(input, "gbk");
				ret = getReplyResult(html);
				}
				else
					keepActivity = true;
			} catch (IOException e) {
				keepActivity = true;
				Log.e(LOG_TAG, Log.getStackTraceString(e));
				
			}
			return ret;
		}
		
		private String getReplyResult(String html){
			int start = html.indexOf(result_start_tag);
			if(start == -1)
				return "发帖失败";
			start += result_start_tag.length();
			int end = html.indexOf(result_end_tag, start);
			if(start == -1)
				return "发帖失败";
			return html.substring(start, end);
			
			
		}

		@Override
		protected void onPostExecute(String result) {
			String success_results[] = {"发贴完毕 ...", " @提醒每24小时不能超过50个"};
			if(keepActivity == false)
			{
				boolean success = false;
				for(int i=0; i< success_results.length; ++i)
				{
					if(result.contains(success_results[i])){
						success = true;
						break;
					}
				}
				if(!success)
					keepActivity = true;
			}
			
			Toast.makeText(c, result,
					Toast.LENGTH_LONG).show();
			PhoneConfiguration.getInstance().setRefreshAfterPost(true);
			ActivityUtil.getInstance().dismiss();
			if(!keepActivity)
				PostActivity.this.finish();
			synchronized(commit_lock){
				loading = false;
			}
				
			super.onPostExecute(result);
		}
		
		
	}

	@Override
	public int finishUpload(String attachments, String attachmentsCheck,
			String picUrl,Bitmap bitmap) {
		final int index = bodyText.getSelectionStart();
		this.act.appendAttachments_(attachments);
		act.appendAttachments_check_(attachmentsCheck);
		String spantmp="[img]./" +picUrl + "[/img]";
		if(bitmap!=null){
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			Drawable drawable = (Drawable) bd;
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			SpannableString spanStringS = new SpannableString(spantmp);
			ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);  
			spanStringS.setSpan(span, 0, spantmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			if(bodyText.getText().toString().replaceAll("\\n", "").trim().equals("")){//NO INPUT DATA
						bodyText.append(spanStringS);
						bodyText.append("\n");
					}
					else{
						if (index <= 0 || index >= bodyText.length() ){// pos @ begin / end
							if(bodyText.getText().toString().endsWith("\n")){
								bodyText.append(spanStringS);
								bodyText.append("\n");
							}else{
								bodyText.append("\n");
								bodyText.append(spanStringS);
								bodyText.append("\n");
								}
						}else{
								bodyText.getText().insert(index,spanStringS);
						}
						}
		}
		else{
			if(bodyText.getText().toString().replaceAll("\\n", "").trim().equals("")){//NO INPUT DATA
				bodyText.append("[img]./" +picUrl + "[/img]\n");
			}else{
				if (index <= 0 || index >= bodyText.length() ){// pos @ begin / end
					if(bodyText.getText().toString().endsWith("\n")){
						bodyText.append("[img]./" +picUrl + "[/img]\n");
					}else{
						bodyText.append("\n[img]./" +picUrl + "[/img]\n");
					}
					}else{
							bodyText.getText().insert(index,"[img]./" +picUrl + "[/img]");
				}
			}
		}
		InputMethodManager imm = (InputMethodManager) bodyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		return 1;
	}

	@Override
	public void onEmotionCategorySelected(int category) {
		final FragmentManager fm =  getSupportFragmentManager();
		FragmentTransaction ft =fm.beginTransaction();   
		final Fragment categoryFragment  = getSupportFragmentManager().
				findFragmentByTag(EMOTION_CATEGORY_TAG);
		if( categoryFragment != null)
			ft.remove(categoryFragment);
		ft.commit();
		
		ft =fm.beginTransaction();
		final Fragment prev = getSupportFragmentManager().
	    		   findFragmentByTag(EMOTION_TAG);
			if (prev != null) {
	            ft.remove(prev);
	        }

		DialogFragment newFragment = null;
		switch(category){
		case CATEGORY_BASIC:
		        newFragment = new EmotionDialogFragment();
			break;
		case CATEGORY_BAOZOU:
		case CATEGORY_XIONGMAO:
		case CATEGORY_TAIJUN:
		case CATEGORY_ALI:
		case CATEGORY_DAYANMAO:
		case CATEGORY_LUOXIAOHEI:
		case CATEGORY_MAJIANGLIAN:
		case CATEGORY_ZHAIYIN:
		case CATEGORY_YANGCONGTOU:
		case CATEGORY_ACNIANG:
		case CATEGORY_BIERDE:
		case CATEGORY_LINDABI:
		case CATEGORY_QUNIANG:
		case CATEGORY_NIWEIHEZHEMEDIAO:
			Bundle args = new Bundle();
			args.putInt("index", category-1);
			newFragment = new ExtensionEmotionFragment();
			newFragment.setArguments(args);
			break;
		default:
				
		
		}
		//ft.commit();
		//ft.addToBackStack(null);

		if(newFragment != null){
			ft.commit();
			newFragment.show(fm, EMOTION_TAG);
		}

	}

}
