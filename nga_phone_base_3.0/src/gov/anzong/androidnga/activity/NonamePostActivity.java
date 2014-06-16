package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
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
import noname.gson.parse.NonameParseJson;
import noname.gson.parse.NonamePostResponse;

import org.apache.commons.io.IOUtils;

import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.adapter.SpinnerUserListAdapter;
import sp.phone.bean.User;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.NonameThreadPostAction;
import sp.phone.forumoperation.ThreadPostAction;
import sp.phone.fragment.EmotionCategorySelectFragment;
import sp.phone.fragment.EmotionDialogFragment;
import sp.phone.fragment.ExtensionEmotionFragment;
import sp.phone.fragment.SearchDialogFragment;
import sp.phone.interfaces.EmotionCategorySelectedListener;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.task.NonameFileUploadTask;
import sp.phone.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class NonamePostActivity extends SwipeBackAppCompatActivity implements
		EmotionCategorySelectedListener, OnEmotionPickedListener,
		NonameFileUploadTask.onFileUploaded {

	private final String LOG_TAG = Activity.class.getSimpleName();
	static private final String EMOTION_CATEGORY_TAG = "emotion_category";
	static private final String EMOTION_TAG = "emotion";
	private String prefix;
	private EditText titleText;
	private EditText bodyText;
	private NonameThreadPostAction act;
	private String action;
	private String tid;
	private int fid;
	// private Button button_commit;
	// private Button button_cancel;
	// private ImageButton button_upload;
	// private ImageButton button_emotion;
	Object commit_lock = new Object();
	private String REPLY_URL = "http://ngac.sinaapp.com/nganoname/post.php?";
	final int REQUEST_CODE_SELECT_PIC = 1;
	private View v;
	private boolean loading;
	private NonameFileUploadTask uploadTask = null;
	private Toast toast = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		int orentation = ThemeManager.getInstance().screenOrentation;
		if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(orentation);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		super.onCreate(savedInstanceState);
		v = this.getLayoutInflater().inflate(R.layout.reply, null);
		v.setBackgroundColor(getResources().getColor(
				ThemeManager.getInstance().getBackgroundColor()));
		this.setContentView(v);

		if (PhoneConfiguration.getInstance().uploadLocation
				&& PhoneConfiguration.getInstance().location == null) {
			ActivityUtil.reflushLocation(this);
		}

		Intent intent = this.getIntent();
		prefix = intent.getStringExtra("prefix");
		// if(prefix!=null){
		// prefix=prefix.replaceAll("\\n\\n", "\n");
		// }
		action = intent.getStringExtra("action");
		if (action.equals("new")) {
			getSupportActionBar().setTitle("发新匿名帖");
			tid = "-1";
		} else if (action.equals("reply")) {
			getSupportActionBar().setTitle("回复匿名帖");
			tid = intent.getStringExtra("tid");
		}
		String title = intent.getStringExtra("title");
		String mention = intent.getStringExtra("mention");
		if (tid == null)
			tid = "";

		act = new NonameThreadPostAction(tid, "", "");
		loading = false;

		titleText = (EditText) findViewById(R.id.reply_titile_edittext);
		if (title != null) {
			titleText.setText(title);
		}
		if (!action.equals("new")) {
			titleText.setHint(R.string.titlecannull);
		}
		titleText.setSelected(true);
		bodyText = (EditText) findViewById(R.id.reply_body_edittext);
		if (prefix != null) {
			if (prefix.startsWith("[quote][pid=")
					&& prefix.endsWith("[/quote]\n")) {
				SpannableString spanString = new SpannableString(prefix);
				spanString.setSpan(new BackgroundColorSpan(-1513240), 0,
						prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				spanString.setSpan(
						new StyleSpan(android.graphics.Typeface.BOLD),
						prefix.indexOf("[b]Post by"),
						prefix.indexOf("):[/b]") + 5,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				bodyText.append(spanString);
			} else {
				bodyText.append(prefix);
			}
			bodyText.setSelection(prefix.length());
		}
		ThemeManager tm = ThemeManager.getInstance();
		if (tm.getMode() == ThemeManager.MODE_NIGHT) {
			bodyText.setBackgroundResource(tm.getBackgroundColor());
			titleText.setBackgroundResource(tm.getBackgroundColor());
			int textColor = this.getResources().getColor(
					tm.getForegroundColor());
			bodyText.setTextColor(textColor);
			titleText.setTextColor(textColor);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag >= 4) {// 大于等于4肯定有
				getMenuInflater().inflate(R.menu.post_menu_left, menu);
			} else {
				getMenuInflater().inflate(R.menu.post_menu, menu);
			}
		} else {
			getMenuInflater().inflate(R.menu.post_menu, menu);
		}
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		/*
		 * ActionBar.DISPLAY_SHOW_HOME;//2 flags |=
		 * ActionBar.DISPLAY_USE_LOGO;//1 flags |=
		 * ActionBar.DISPLAY_HOME_AS_UP;//4
		 */
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED || data == null)
			return;
		switch (requestCode) {
		case REQUEST_CODE_SELECT_PIC:
			Log.i(LOG_TAG, " select file :" + data.getDataString());
			uploadTask = new NonameFileUploadTask(this, this, data.getData());
			break;
		default:
			;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private ButtonCommitListener commitListener = null;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.upload :
		// Intent intent = new Intent();
		// intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
		// break;
		case R.id.upload:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
			break;
		case R.id.emotion:
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			Fragment prev = getSupportFragmentManager().findFragmentByTag(
					EMOTION_CATEGORY_TAG);
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
			if (commitListener == null) {
				commitListener = new ButtonCommitListener(REPLY_URL);
			}
			commitListener.onClick(null);
			break;
		default:
			finish();
		}
		return true;
	}

	private void handleSupertext() {
		final int index = bodyText.getSelectionStart();
		LayoutInflater layoutInflater = getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.supertext_dialog,
				null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setTitle(R.string.supertext_hint);
		final Spinner fontcolorSpinner;
		final Spinner fontsizeSpinner;
		RadioGroup selectradio;
		final TextView font_size;
		final TextView font_color;
		final RadioButton atsomeone_button;
		final RadioButton urladd_button;
		final RadioButton quoteadd_button;
		final CheckBox bold_checkbox;// 加粗
		final CheckBox italic_checkbox;// 斜体
		final CheckBox underline_checkbox;// 下划线
		final CheckBox fontcolor_checkbox;
		final CheckBox fontsize_checkbox;
		final CheckBox delline_checkbox;

		font_size = (TextView) view.findViewById(R.id.font_size);
		font_color = (TextView) view.findViewById(R.id.font_color);
		atsomeone_button = (RadioButton) view.findViewById(R.id.atsomeone);
		urladd_button = (RadioButton) view.findViewById(R.id.urladd);
		quoteadd_button = (RadioButton) view.findViewById(R.id.quoteadd);
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

		final EditText input = (EditText) view
				.findViewById(R.id.inputsupertext_dataa);

		fontcolorSpinner = (Spinner) view.findViewById(R.id.font_color_spinner);
		fontsizeSpinner = (Spinner) view.findViewById(R.id.fontsize_spinner);
		final int scolorspan[] = { -16777216, -7876885, -13149723, -16776961,
				-16777077, -23296, -47872, -2354116, -65536, -5103070,
				-7667712, -16744448, -13447886, -13726889, -16744320, -60269,
				-40121, -32944, -8388480, -11861886, -2180985, -744352,
				-6270419, -2987746, -4144960, };

		final String scolor[] = { "[color=skyblue]", "[color=royalblue]",
				"[color=blue]", "[color=darkblue]", "[color=orange]",
				"[color=orangered]", "[color=crimson]", "[color=red]",
				"[color=firebrick]", "[color=darkred]", "[color=green]",
				"[color=limegreen]", "[color=seagreen]", "[color=teal]",
				"[color=deeppink]", "[color=tomato]", "[color=coral]",
				"[color=purple]", "[color=indigo]", "[color=burlywood]",
				"[color=sandybrown]", "[color=sienna]", "[color=chocolate]",
				"[color=silver]" };
		final String ssize[] = { "[size=100%]", "[size=110%]", "[size=120%]",
				"[size=130%]", "[size=150%]", "[size=200%]", "[size=250%]",
				"[size=300%]", "[size=400%]", "[size=500%]" };
		final float ssizespan[] = { 1.0f, 1.1f, 1.2f, 1.3f, 1.5f, 2.0f, 2.5f,
				3.0f, 4.0f, 5.0f, 1.2f };

		BaseAdapter adapterfontcolor = new BaseAdapter() {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return view.getResources().getStringArray(R.array.colorchoose).length; // 选项总个数
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
				LinearLayout ll = new LinearLayout(NonamePostActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向
				TextView tv = new TextView(NonamePostActivity.this);
				tv.setText(view.getResources().getStringArray(
						R.array.colorchoose)[arg0]);// 设置内容
				tv.setTextColor(scolorspan[arg0]);// 设置字体颜色
				ll.addView(tv); // 添加到LinearLayout中
				return ll;
			}
		};
		fontcolorSpinner.setAdapter(adapterfontcolor);
		fontcolorSpinner.setSelection(0);

		BaseAdapter adapterfontsize = new BaseAdapter() {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return view.getResources().getStringArray(
						R.array.fontsizechoose).length; // 选项总个数
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
				LinearLayout ll = new LinearLayout(NonamePostActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向
				TextView tv = new TextView(NonamePostActivity.this);
				tv.setText(view.getResources().getStringArray(
						R.array.fontsizechoose)[arg0]);// 设置内容
				tv.setTextSize(ssizespan[arg0] * defaultFontSize);// 设置字体大小
				ll.addView(tv); // 添加到LinearLayout中
				return ll;
			}
		};
		fontsizeSpinner.setAdapter(adapterfontsize);
		fontsizeSpinner.setSelection(0);

		// 开始下面两个选项没有的
		font_size.setVisibility(View.GONE);
		font_color.setVisibility(View.GONE);
		fontsizeSpinner.setVisibility(View.GONE);
		fontcolorSpinner.setVisibility(View.GONE);
		// 选中上面那排，下面的就不能选
		selectradio
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

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

		// 选中下面那排，上面的别选了,加粗
		bold_checkbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							atsomeone_button.setChecked(false);
							urladd_button.setChecked(false);
							quoteadd_button.setChecked(false);
						}
					}

				});

		// 选中下面那排，上面的别选了，斜体
		italic_checkbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							atsomeone_button.setChecked(false);
							urladd_button.setChecked(false);
							quoteadd_button.setChecked(false);
						}
					}

				});

		// 选中下面那排，上面的别选了，下划线
		underline_checkbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							atsomeone_button.setChecked(false);
							urladd_button.setChecked(false);
							quoteadd_button.setChecked(false);
						}
					}

				});

		// 选中下面那排，上面的别选了，颜色
		fontcolor_checkbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							atsomeone_button.setChecked(false);
							urladd_button.setChecked(false);
							quoteadd_button.setChecked(false);
							font_color.setVisibility(View.VISIBLE);
							fontcolorSpinner.setVisibility(View.VISIBLE);
						} else {
							font_color.setVisibility(View.GONE);
							fontcolorSpinner.setVisibility(View.GONE);
						}
					}

				});

		// 选中下面那排，上面的别选了，字号
		fontsize_checkbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							atsomeone_button.setChecked(false);
							urladd_button.setChecked(false);
							quoteadd_button.setChecked(false);
							font_size.setVisibility(View.VISIBLE);
							fontsizeSpinner.setVisibility(View.VISIBLE);
						} else {
							font_size.setVisibility(View.GONE);
							fontsizeSpinner.setVisibility(View.GONE);
						}
					}

				});

		// 选中下面那排，上面的别选了，删除线
		delline_checkbox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							atsomeone_button.setChecked(false);
							urladd_button.setChecked(false);
							quoteadd_button.setChecked(false);
						}
					}

				});

		alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String inputdata = input.getText().toString();
				// TODO Auto-generated method stub
				if (!inputdata.replaceAll("\\n", "").trim().equals("")) {
					if (atsomeone_button.isChecked()) {
						inputdata = "[@" + inputdata + "]";
					} else if (urladd_button.isChecked()) {
						if (inputdata.startsWith("http:")
								|| inputdata.startsWith("https:")
								|| inputdata.startsWith("ftp:")
								|| inputdata.startsWith("gopher:")
								|| inputdata.startsWith("news:")
								|| inputdata.startsWith("telnet:")
								|| inputdata.startsWith("mms:")
								|| inputdata.startsWith("rtsp:")) {
							inputdata = "[url]" + inputdata + "[/url]";
						} else {

							if (toast != null) {
								toast.setText("URL需以http|https|ftp|gopher|news|telnet|mms|rtsp开头");
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.show();
							} else {
								toast = Toast
										.makeText(
												NonamePostActivity.this,
												"URL需以http|https|ftp|gopher|news|telnet|mms|rtsp开头",
												Toast.LENGTH_SHORT);
								toast.show();
							}
						}
					} else if (quoteadd_button.isChecked()) {
						inputdata = "[quote]" + inputdata + "[/quote]";
					} else {
						if (fontcolor_checkbox.isChecked()) {
							if (fontcolorSpinner.getSelectedItemPosition() > 0) {
								inputdata = scolor[fontcolorSpinner
										.getSelectedItemPosition() - 1]
										+ inputdata + "[/color]";
							}
						}
						if (italic_checkbox.isChecked()) {
							inputdata = "[i]" + inputdata + "[/i]";
						}
						if (bold_checkbox.isChecked()) {
							inputdata = "[b]" + inputdata + "[/b]";
						}
						if (underline_checkbox.isChecked()) {
							inputdata = "[u]" + inputdata + "[/u]";
						}
						if (delline_checkbox.isChecked()) {
							inputdata = "[del]" + inputdata + "[/del]";
						}
						if (fontsize_checkbox.isChecked()) {
							if (fontsizeSpinner.getSelectedItemPosition() < 10) {
								inputdata = ssize[fontsizeSpinner
										.getSelectedItemPosition()]
										+ inputdata
										+ "[/size]";
							} else {
								inputdata = "[h]" + inputdata + "[/h]";
							}
						}
					}
					SpannableString spanString = new SpannableString(inputdata);

					if (atsomeone_button.isChecked()) {
						spanString.setSpan(new ForegroundColorSpan(Color.BLUE),
								0, inputdata.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else if (urladd_button.isChecked()) {
						if (input.getText().toString().startsWith("http:")
								|| input.getText().toString()
										.startsWith("https:")
								|| input.getText().toString()
										.startsWith("ftp:")
								|| input.getText().toString()
										.startsWith("gopher:")
								|| input.getText().toString()
										.startsWith("news:")
								|| input.getText().toString()
										.startsWith("telnet:")
								|| input.getText().toString()
										.startsWith("mms:")
								|| input.getText().toString()
										.startsWith("rtsp:")) {
							spanString.setSpan(new URLSpan(input.getText()
									.toString()), 0, inputdata.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					} else if (quoteadd_button.isChecked()) {
						spanString.setSpan(new BackgroundColorSpan(-1513240),
								0, inputdata.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						if (fontcolor_checkbox.isChecked()) {
							spanString.setSpan(
									new ForegroundColorSpan(
											scolorspan[fontcolorSpinner
													.getSelectedItemPosition()]),
									0, inputdata.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (italic_checkbox.isChecked()) {
							spanString.setSpan(new StyleSpan(
									android.graphics.Typeface.ITALIC), 0,
									inputdata.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (bold_checkbox.isChecked()) {
							spanString.setSpan(new StyleSpan(
									android.graphics.Typeface.BOLD), 0,
									inputdata.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (underline_checkbox.isChecked()) {
							spanString.setSpan(new UnderlineSpan(), 0,
									inputdata.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (delline_checkbox.isChecked()) {
							spanString.setSpan(new StrikethroughSpan(), 0,
									inputdata.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						if (fontsize_checkbox.isChecked()) {
							if (fontsizeSpinner.getSelectedItemPosition() < 10) {
								spanString.setSpan(
										new RelativeSizeSpan(
												ssizespan[fontsizeSpinner
														.getSelectedItemPosition()]),
										0, inputdata.length(),
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							} else {
								spanString.setSpan(new BackgroundColorSpan(
										Color.GRAY), 0, inputdata.length(),
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								spanString.setSpan(new RelativeSizeSpan(1.2f),
										0, inputdata.length(),
										Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

							}
						}
					}
					if (bodyText.getText().toString().replaceAll("\\n", "")
							.trim().equals("")) {// NO INPUT DATA
						bodyText.setText("");
						bodyText.append(spanString);
					} else {
						if (index <= 0 || index >= bodyText.length()) {// pos @
																		// begin
																		// / end
							bodyText.append(spanString);
						} else {
							bodyText.getText().insert(index, spanString);
						}
					}
					InputMethodManager imm = (InputMethodManager) bodyText
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				} else {
					bodyText.setFocusableInTouchMode(true);
					bodyText.setFocusable(true);
					InputMethodManager imm = (InputMethodManager) bodyText
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
					dialog.dismiss();
				}
			}
		});
		alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				InputMethodManager imm = (InputMethodManager) bodyText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
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
					ActivityUtil.getInstance().setFullScreen(v);
				}
			}

		});
	}

	@Override
	public void onEmotionPicked(String emotion) {
		final int index = bodyText.getSelectionStart();
		String urltemp = emotion.replaceAll("\\n", "");
		if (urltemp.indexOf("http") > 0) {
			urltemp = urltemp.substring(5, urltemp.length() - 6);
			String sourcefile = ExtensionEmotionAdapter.getPathByURI(urltemp);
			InputStream is = null;
			try {
				is = getResources().getAssets().open(sourcefile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (is != null) {
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				Drawable drawable = (Drawable) bd;
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				SpannableString spanString = new SpannableString(emotion);
				ImageSpan span = new ImageSpan(drawable,
						ImageSpan.ALIGN_BASELINE);
				spanString.setSpan(span, 0, emotion.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if (bodyText.getText().toString().replaceAll("\\n", "").trim()
						.equals("")) {
					bodyText.append(spanString);
				} else {
					if (index <= 0 || index >= bodyText.length()) {// pos @
																	// begin /
																	// end
						bodyText.append(spanString);
					} else {
						bodyText.getText().insert(index, spanString);
					}
				}
			}
		} else {
			int[] emotions = { 1, 2, 3, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34,
					35, 36, 37, 38, 39, 4, 40, 41, 42, 43, 5, 6, 7, 8 };
			for (int i = 0; i < 27; i++) {
				if (emotion.indexOf("[s:" + String.valueOf(emotions[i]) + "]") == 0) {
					String sourcefile = "a" + String.valueOf(emotions[i])
							+ ".gif";
					InputStream is = null;
					try {
						is = getResources().getAssets().open(sourcefile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (is != null) {
						Bitmap bitmap = BitmapFactory.decodeStream(is);
						BitmapDrawable bd = new BitmapDrawable(bitmap);
						Drawable drawable = (Drawable) bd;
						drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
								drawable.getIntrinsicHeight());
						SpannableString spanString = new SpannableString(
								emotion);
						ImageSpan span = new ImageSpan(drawable,
								ImageSpan.ALIGN_BASELINE);
						spanString.setSpan(span, 0, emotion.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						if (index <= 0 || index >= bodyText.length()) {// pos @
																		// begin
																		// / end
							bodyText.append(spanString);
						} else {
							bodyText.getText().insert(index, spanString);
						}
					} else {
						bodyText.append(emotion);
					}
					break;
				}
			}
		}
	}

	@Override
	protected void onResume() {
		if (action.equals("new")) {
			titleText.requestFocus();
		} else {
			bodyText.requestFocus();
		}
		if (uploadTask != null) {
			NonameFileUploadTask temp = uploadTask;
			uploadTask = null;
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				RunParallel(temp);
			} else {
				temp.execute();
			}
		}
		if (PhoneConfiguration.getInstance().fullscreen) {
			ActivityUtil.getInstance().setFullScreen(v);
		}
		super.onResume();
	}

	@TargetApi(11)
	private void RunParallel(NonameFileUploadTask task) {
		task.executeOnExecutor(NonameFileUploadTask.THREAD_POOL_EXECUTOR);
	}

	private String buildSig() {
		StringBuilder sb = new StringBuilder();
		/*
		 * sb.append(
		 * "\n[url=https://play.google.com/store/apps/details?fuck&id=gov.anzong.androidnga"
		 * ); PhoneConfiguration config = PhoneConfiguration.getInstance();
		 * if(config.location != null && config.uploadLocation) { String loc =
		 * new StringBuilder().append(config.location.getLatitude())
		 * .append(",") .append(config.location.getLongitude()).toString();
		 * sb.append("&"); try { sb.append(Des.enCrypto(loc, StringUtil.key)); }
		 * catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } sb.append("ffff"); }
		 */
		// if(PhoneConfiguration.getInstance().showNewweiba){
		// String
		// scolorweiba[]={"[color=silver]","[color=royalblue]","[color=blue]","[color=darkblue]","[color=orange]","[color=orangered]","[color=crimson]","[color=red]","[color=firebrick]","[color=darkred]","[color=green]","[color=limegreen]","[color=seagreen]","[color=teal]","[color=deeppink]","[color=tomato]","[color=coral]","[color=purple]","[color=indigo]","[color=burlywood]","[color=sandybrown]","[color=sienna]","[color=chocolate]","[color=silver]"};
		// String weibacolor = scolorweiba[(int)(Math.random()*23)];
		// sb.append("\n\n").append(weibacolor).append("----sent from my ")
		// .append(android.os.Build.MANUFACTURER).append(" ")
		// .append(android.os.Build.MODEL).append(",Android ")
		// .append(android.os.Build.VERSION.RELEASE)
		// .append(" [/color]\n");
		// }else{
		// sb.append("");
		// }
		sb.append("");
		return sb.toString();

	}

	class ButtonCommitListener implements OnClickListener {

		private final String url;

		ButtonCommitListener(String url) {
			this.url = url;
		}

		@Override
		public void onClick(View v) {
			if (action.equals("reply")) {
				if (bodyText.getText().toString().length() > 2) {
					synchronized (commit_lock) {
						if (loading == true) {
							String avoidWindfury = NonamePostActivity.this
									.getString(R.string.avoidWindfury);
							if (toast != null) {
								toast.setText(avoidWindfury);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.show();
							} else {
								toast = Toast.makeText(NonamePostActivity.this,
										avoidWindfury, Toast.LENGTH_SHORT);
								toast.show();
							}
							return;
						}
						loading = true;
					}
					handleReply(v);
				} else {
					if (toast != null) {
						toast.setText("正文内容至少3个字符");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(NonamePostActivity.this,
								"正文内容至少3个字符", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			} else if (action.equals("new")) {
				if (titleText.getText().toString().length() > 0
						&& bodyText.getText().toString().length() > 2) {
					synchronized (commit_lock) {
						if (loading == true) {
							String avoidWindfury = NonamePostActivity.this
									.getString(R.string.avoidWindfury);
							if (toast != null) {
								toast.setText(avoidWindfury);
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.show();
							} else {
								toast = Toast.makeText(NonamePostActivity.this,
										avoidWindfury, Toast.LENGTH_SHORT);
								toast.show();
							}
							return;
						}
						loading = true;
					}
					handleNewThread(v);
				} else {
					if (toast != null) {
						toast.setText("请输入正确的标题和正文内容，正文内容至少3个字符");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(NonamePostActivity.this,
								"请输入正确的标题和正文内容，正文内容至少3个字符", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}
		}

		public void handleNewThread(View v) {
			handleReply(v);

		}

		public void handleReply(View v1) {

			act.setPost_subject_(titleText.getText().toString());
			if (bodyText.getText().toString().length() > 0) {
				act.setPost_content_(ColorTxtCheck());
				new ArticlePostTask(NonamePostActivity.this).execute(url,
						act.toString());
			}

		}

	}

	private String ColorTxtCheck() {
		String xxtp = "";
		if (PhoneConfiguration.getInstance().showColortxt) {
			xxtp = ColorTxt();
		} else {
			xxtp = bodyText.getText().toString();
		}
		return xxtp.toString();
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
			content = content.substring(1);
		}
		while (content.startsWith("\r\n")) {
			content = content.substring(2);
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

	private String ColorTxt() {
		String bodyString = bodyText.getText().toString().trim();
		while (bodyString.startsWith("\n")) {
			bodyString = bodyString.substring(1);
		}
		String existquotetxt = "";
		if (bodyString.toLowerCase().indexOf("[quote]") == 0) {
			existquotetxt = bodyString.substring(0, bodyString.toLowerCase()
					.indexOf("[/quote]"))
					+ "[/quote]";
			bodyString = bodyString.substring(bodyString.toLowerCase().indexOf(
					"[/quote]") + 8);
		}
		int i, ia, ib, itmp = 0, bslenth, tmplenth;
		bslenth = bodyString.length();

		String scolor[] = { "[color=skyblue]", "[color=royalblue]",
				"[color=blue]", "[color=darkblue]", "[color=orange]",
				"[color=orangered]", "[color=crimson]", "[color=red]",
				"[color=firebrick]", "[color=darkred]", "[color=green]",
				"[color=limegreen]", "[color=seagreen]", "[color=teal]",
				"[color=deeppink]", "[color=tomato]", "[color=coral]",
				"[color=purple]", "[color=indigo]", "[color=burlywood]",
				"[color=sandybrown]", "[color=sienna]", "[color=chocolate]",
				"[color=silver]" };
		String keyword[][] = { { "[customachieve]", "[/customachieve]", "16" },
				{ "[wow", "]]", "2" }, { "[lol", "]]", "2" },
				{ "[cnarmory", "]", "1" }, { "[usarmory", "]", "1" },
				{ "[twarmory", "]", "1" }, { "[euarmory", "]", "1" },
				{ "[url", "[/url]", "6" }, { "[size=", "]", "1" },
				{ "[/size]", "[/size]", "7" }, { "[font=", "]", "1" },
				{ "[/font]", "[/font]", "7" }, { "[b]", "[b]", "3" },
				{ "[/b]", "[/b]", "4" }, { "[u]", "[u]", "3" },
				{ "[/u]", "[/u]", "4" }, { "[i]", "[i]", "3" },
				{ "[/i]", "[/i]", "4" }, { "[del]", "[del]", "5" },
				{ "[/del]", "[/del]", "6" }, { "[align", "]", "1" },
				{ "[/align]", "[/align]", "8" }, { "[l]", "[l]", "3" },
				{ "[/l]", "[/l]", "4" }, { "[h]", "[h]", "3" },
				{ "[/h]", "[/h]", "4" }, { "[r]", "[r]", "3" },
				{ "[/r]", "[/r]", "4" }, { "[img]", "[/img]", "6" },
				{ "[album=", "[/album]", "8" }, { "[code]", "[/code]", "7" },
				{ "[code=lua]", "[/code] lua", "11" },
				{ "[code=php]", "[/code] php", "11" },
				{ "[code=c]", "[/code] c", "9" },
				{ "[code=js]", "[/code] javascript", "18" },
				{ "[code=xml]", "[/code] xml/html", "16" },
				{ "[flash]", "[/flash]", "8" }, { "[table]", "[table]", "7" },
				{ "[/table]", "[/table]", "8" }, { "[tid", "[/tid]", "6" },
				{ "[pid", "[/pid]", "6" }, { "[dice]", "[/dice]", "7" },
				{ "[crypt]", "[/crypt]", "8" },
				{ "[randomblock]", "[randomblock]", "13" },
				{ "[/randomblock]", "[/randomblock]", "14" },
				{ "[@", "]", "1" }, { "[t.178.com/", "]", "1" },
				{ "[tr]", "[tr]", "4" }, { "[/tr]", "[/tr]", "5" },
				{ "[td", "]", "1" }, { "[/td]", "[/td]", "5" },
				{ "[*]", "[*]", "3" }, { "[list", "]", "1" },
				{ "[/list]", "[/list]", "7" }, { "[collapse", "]", "1" },
				{ "[/collapse]", "[/collapse]", "11" } };
		char[] arrtxtchar = bodyString.toCharArray();
		String txtsendout = scolor[(int) (Math.random() * 23)];
		String quotetxt = "";
		for (i = 0; i < bslenth; i++) {
			if (Character.toString(arrtxtchar[i]).equals("\n") == false
					&& Character.toString(arrtxtchar[i]).equals("[") == false
					&& Character.toString(arrtxtchar[i]).equals(" ") == false) {
				txtsendout += arrtxtchar[i] + "[/color]"
						+ scolor[(int) (Math.random() * 23)];/* 开始就是普通文字的话就直接加彩色字体了 */
			} else if (Character.toString(arrtxtchar[i]).equals("[")) {// 首字符是[要判断
				if (bodyString.toLowerCase().indexOf("[quote]", i - 1) == i) {// 是引用的话
					if (bodyString.toLowerCase().indexOf("[quote]", i - 1) > bodyString
							.toLowerCase().indexOf("[/quote]", i - 1)) {// 这个他妈的引用没完
						quotetxt = bodyString.substring(i + 7);
						if (quotetxt.toLowerCase().lastIndexOf("[") >= 0) {// 最后还有点留下来
							quotetxt = quotetxt.substring(0, quotetxt
									.toLowerCase().lastIndexOf("["));
						}
						while (quotetxt.endsWith(".")) {
							quotetxt = quotetxt.substring(0,
									quotetxt.length() - 1);
						}
						bslenth = bodyString.length();
						txtsendout = txtsendout.substring(0, txtsendout
								.toLowerCase().lastIndexOf("[color"));
						quotetxt = "[quote]" + checkContent(quotetxt)
								+ "[/quote]";
						txtsendout += quotetxt
								+ scolor[(int) (Math.random() * 23)];
						break;
					} else {
						quotetxt = bodyString.substring(i + 7, bodyString
								.toLowerCase().indexOf("[/quote]", i));
						while (quotetxt.endsWith(".")) {
							quotetxt = quotetxt.substring(0,
									quotetxt.length() - 1);
						}
						txtsendout = txtsendout.substring(0, txtsendout
								.toLowerCase().lastIndexOf("[color"));
						quotetxt = "[quote]" + checkContent(quotetxt)
								+ "[/quote]";
						txtsendout += quotetxt
								+ scolor[(int) (Math.random() * 23)];
						i = bodyString.toLowerCase().indexOf("[/quote]", i) + 7;
					}
				} else if (bodyString.toLowerCase().indexOf("[color", i - 1) == i) {
					if (bodyString.toLowerCase().indexOf("[/color]", i) >= 0) {
						txtsendout += bodyString
								.substring(
										bodyString.indexOf("]", i) + 1,
										bodyString.toLowerCase().indexOf(
												"[/color]", i) + 8)
								+ scolor[(int) (Math.random() * 23)];
						i = bodyString.indexOf("[/color]", i) + 7;
					} else {
						bodyString = bodyString.substring(0, i)
								+ bodyString.substring(bodyString.toLowerCase()
										.indexOf("]", i) + 1, bslenth);
						i = bodyString.indexOf("]", i);
					}
				} else {
					for (ia = 0; ia < 56; ia++) {
						if (bodyString.toLowerCase().indexOf(keyword[ia][0],
								i - 1) == i) {
							if (bodyString.toLowerCase().indexOf(
									keyword[ia][1], i) >= 0) {
								txtsendout = txtsendout.substring(0, txtsendout
										.toLowerCase().lastIndexOf("[color"));
								txtsendout += bodyString.substring(
										i,
										bodyString.toLowerCase().indexOf(
												keyword[ia][1], i))
										+ keyword[ia][1]
										+ scolor[(int) (Math.random() * 23)];
								i = bodyString.toLowerCase().indexOf(
										keyword[ia][1], i)
										+ Integer.parseInt(keyword[ia][2]) - 1;
							} else {
								itmp = bodyString.indexOf("]", i);
								bodyString = bodyString.substring(0, i)
										+ bodyString.substring(
												bodyString.toLowerCase()
														.indexOf("]", i) + 1,
												bslenth);
								i = itmp;
							}
							break;
						}
					}
				}
			} else if (Character.toString(arrtxtchar[i]).equals(" ")
					|| Character.toString(arrtxtchar[i]).equals("\n")) {
				txtsendout = txtsendout.substring(0, txtsendout.toLowerCase()
						.lastIndexOf("[color"));
				txtsendout += bodyString.substring(i, i + 1)
						+ scolor[(int) (Math.random() * 23)];
			}
		}
		if (txtsendout.toLowerCase().lastIndexOf("[color") >= 0) {
			txtsendout = txtsendout.substring(0, txtsendout.toLowerCase()
					.lastIndexOf("[color"));
		}
		txtsendout = existquotetxt
				+ txtsendout.replaceAll("&nbsp;", " ").trim();
		return txtsendout.toString();
	}

	private class ArticlePostTask extends AsyncTask<String, Integer, String> {

		final Context c;
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
			synchronized (commit_lock) {
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled();
		}

		@Override
		protected void onCancelled(String result) {
			synchronized (commit_lock) {
				loading = false;
			}
			ActivityUtil.getInstance().dismiss();
			super.onCancelled();
		}

		@Override
		protected String doInBackground(String... params) {
			if (params.length < 2)
				return "parameter error";
			String ret = "{\"error\":true,\"errorinfo\":\"\u7f51\u7edc\u9519\u8bef\"}";// 网络错误
			String url = params[0];
			Log.i("TAG", url);
			String body = params[1];

			HttpPostClient c = new HttpPostClient(url);
			c.setCookie("");
			try {
				InputStream input = null;
				HttpURLConnection conn = c.post_body(body);
				if (conn != null) {
					if (conn.getResponseCode() != 200) {
						input = null;
						keepActivity = true;
						ret = "{\"error\":true,\"errorinfo\":\"\u533f\u540d\u670d\u52a1\u5668\u51fa\u9519\u4e86\"}";// 匿名服务器出错
					} else {
						input = conn.getInputStream();
					}
				} else
					keepActivity = true;

				if (input != null) {
					ret = IOUtils.toString(input, "utf-8");
				} else
					keepActivity = true;
				Log.i("TAG", ret);
			} catch (IOException e) {
				keepActivity = true;
				Log.e(LOG_TAG, Log.getStackTraceString(e));

			}
			return ret;
		}

		@Override
		protected void onPostExecute(String result) {
			NonamePostResponse s = NonameParseJson.parsePost(result);
			if (keepActivity == false) {
				boolean success = !s.error;
				if (!success)
					keepActivity = true;
			}
			if (s.error) {// 出错
				Log.i("atg", "S");
				if (toast != null) {
					toast.setText(s.errorinfo);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(NonamePostActivity.this,
							s.errorinfo, Toast.LENGTH_SHORT);
					toast.show();
				}
			} else {
				if (toast != null) {
					toast.setText(s.data);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(NonamePostActivity.this, s.data,
							Toast.LENGTH_SHORT);
					toast.show();
				}
			}
			PhoneConfiguration.getInstance().setRefreshAfterPost(true);
			ActivityUtil.getInstance().dismiss();
			if (!keepActivity)
				NonamePostActivity.this.finish();
			synchronized (commit_lock) {
				loading = false;
			}

			super.onPostExecute(result);
		}

	}

	@Override
	public void onEmotionCategorySelected(int category) {
		final FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		final Fragment categoryFragment = getSupportFragmentManager()
				.findFragmentByTag(EMOTION_CATEGORY_TAG);
		if (categoryFragment != null)
			ft.remove(categoryFragment);
		ft.commit();

		ft = fm.beginTransaction();
		final Fragment prev = getSupportFragmentManager().findFragmentByTag(
				EMOTION_TAG);
		if (prev != null) {
			ft.remove(prev);
		}

		DialogFragment newFragment = null;
		switch (category) {
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
			args.putInt("index", category - 1);
			newFragment = new ExtensionEmotionFragment();
			newFragment.setArguments(args);
			break;
		default:

		}
		// ft.commit();
		// ft.addToBackStack(null);

		if (newFragment != null) {
			ft.commit();
			newFragment.show(fm, EMOTION_TAG);
		}

	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	@Override
	public int finishUpload(String picUrl, Uri uri) {
		String selectedImagePath2 = getPath(this, uri);
		final int index = bodyText.getSelectionStart();
		String spantmp = "[img]" + picUrl + "[/img]";
		if (!StringUtil.isEmpty(selectedImagePath2)) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath2,
					options); // 此时返回 bm 为空
			options.inJustDecodeBounds = false;
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenwidth = (int) (dm.widthPixels * 0.75);
			int screenheigth = (int) (dm.heightPixels * 0.75);
			int width = options.outWidth;
			int height = options.outHeight;
			float scaleWidth = ((float) screenwidth) / width;
			float scaleHeight = ((float) screenheigth) / height;
			if (scaleWidth < scaleHeight && scaleWidth < 1f) {// 不能放大啊,然后主要是哪个小缩放到哪个就行了
				options.inSampleSize = (int) (1 / scaleWidth);
			} else if (scaleWidth >= scaleHeight && scaleHeight < 1f) {
				options.inSampleSize = (int) (1 / scaleHeight);
			} else {
				options.inSampleSize = 1;
			}
			bitmap = BitmapFactory.decodeFile(selectedImagePath2, options);
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			Drawable drawable = (Drawable) bd;
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			SpannableString spanStringS = new SpannableString(spantmp);
			ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
			spanStringS.setSpan(span, 0, spantmp.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			if (bodyText.getText().toString().replaceAll("\\n", "").trim()
					.equals("")) {// NO INPUT DATA
				bodyText.append(spanStringS);
				bodyText.append("\n");
			} else {
				if (index <= 0 || index >= bodyText.length()) {// pos @ begin /
																// end
					if (bodyText.getText().toString().endsWith("\n")) {
						bodyText.append(spanStringS);
						bodyText.append("\n");
					} else {
						bodyText.append("\n");
						bodyText.append(spanStringS);
						bodyText.append("\n");
					}
				} else {
					bodyText.getText().insert(index, spanStringS);
				}
			}
		} else {
			if (bodyText.getText().toString().replaceAll("\\n", "").trim()
					.equals("")) {// NO INPUT DATA
				bodyText.append(spantmp + "\n");
			} else {
				if (index <= 0 || index >= bodyText.length()) {// pos @ begin /
																// end
					if (bodyText.getText().toString().endsWith("\n")) {
						bodyText.append(spantmp + "\n");
					} else {
						bodyText.append("\n" + spantmp + "\n");
					}
				} else {
					bodyText.getText().insert(index, spantmp);
				}
			}
		}
		InputMethodManager imm = (InputMethodManager) bodyText.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		return 1;
	}

}