package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import gov.anzong.androidnga.R;


public class SuperTextDialogFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener {

    private EditText mInputText;

    private EditText mBodyText;

    private Spinner mFontColorSpinner;
    private Spinner mFontSizeSpinner;
    private TextView mFontSizeTv;
    private TextView mFontColorTv;
    private RadioButton mAtSomeoneBtn;
    private RadioButton mUrlAddBtn;
    private RadioButton mQuoteAddBtn;
    private CheckBox mFontBoldCb;// 加粗
    private CheckBox mFontItalicCb;// 斜体
    private CheckBox mFontUnderLineCb;// 下划线
    private CheckBox mFontColorCb;
    private CheckBox mFontSizeCb;
    private CheckBox mFontDelLineCb;

    private float mDefaultFontSize;


    private int[] mColorSpan = {-16777216, -7876885, -13149723, -16776961,
            -16777077, -23296, -47872, -2354116, -65536, -5103070,
            -7667712, -16744448, -13447886, -13726889, -16744320, -60269,
            -40121, -32944, -8388480, -11861886, -2180985, -744352,
            -6270419, -2987746, -4144960,};

    private String[] mColors = {"[color=skyblue]", "[color=royalblue]",
            "[color=blue]", "[color=darkblue]", "[color=orange]",
            "[color=orangered]", "[color=crimson]", "[color=red]",
            "[color=firebrick]", "[color=darkred]", "[color=green]",
            "[color=limegreen]", "[color=seagreen]", "[color=teal]",
            "[color=deeppink]", "[color=tomato]", "[color=coral]",
            "[color=purple]", "[color=indigo]", "[color=burlywood]",
            "[color=sandybrown]", "[color=sienna]", "[color=chocolate]",
            "[color=silver]"};

    private String[] mSize = {"[size=100%]", "[size=110%]", "[size=120%]",
            "[size=130%]", "[size=150%]", "[size=200%]", "[size=250%]",
            "[size=300%]", "[size=400%]", "[size=500%]"};

    private float[] mSizeSpan = {1.0f, 1.1f, 1.2f, 1.3f, 1.5f, 2.0f, 2.5f,
            3.0f, 4.0f, 5.0f, 1.2f};


    private BaseAdapter mFontColorAdapter = new BaseAdapter() {


        @Override
        public int getCount() {
            return getContext().getResources().getStringArray(R.array.colorchoose).length; // 选项总个数
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向
            TextView tv = new TextView(getContext());
            tv.setText(getContext().getResources().getStringArray(
                    R.array.colorchoose)[arg0]);// 设置内容
            tv.setTextColor(mColorSpan[arg0]);// 设置字体颜色
            ll.addView(tv); // 添加到LinearLayout中
            return ll;
        }
    };


    private BaseAdapter mFontSizeAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return getContext().getResources().getStringArray(
                    R.array.fontsizechoose).length; // 选项总个数
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向
            TextView tv = new TextView(getContext());
            tv.setText(getContext().getResources().getStringArray(
                    R.array.fontsizechoose)[arg0]);// 设置内容
            tv.setTextSize(mSizeSpan[arg0] * mDefaultFontSize);// 设置字体大小
            ll.addView(tv); // 添加到LinearLayout中
            return ll;
        }
    };

    public SuperTextDialogFragment(EditText mBodyText) {
        this.mBodyText = mBodyText;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInputText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getViews(View view) {
        mFontSizeTv = (TextView) view.findViewById(R.id.font_size);
        mFontColorTv = (TextView) view.findViewById(R.id.font_color);
        mAtSomeoneBtn = (RadioButton) view.findViewById(R.id.atsomeone);
        mUrlAddBtn = (RadioButton) view.findViewById(R.id.urladd);
        mQuoteAddBtn = (RadioButton) view.findViewById(R.id.quoteadd);
        mFontBoldCb = (CheckBox) view.findViewById(R.id.bold);
        mFontItalicCb = (CheckBox) view.findViewById(R.id.italic);
        mFontUnderLineCb = (CheckBox) view.findViewById(R.id.underline);
        mFontColorCb = (CheckBox) view.findViewById(R.id.fontcolor);
        mFontSizeCb = (CheckBox) view.findViewById(R.id.fontsize);
        mFontDelLineCb = (CheckBox) view.findViewById(R.id.delline);
        mFontColorSpinner = (Spinner) view.findViewById(R.id.font_color_spinner);
        mFontSizeSpinner = (Spinner) view.findViewById(R.id.fontsize_spinner);
        mInputText = (EditText) view.findViewById(R.id.inputsupertext_dataa);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_super_text, null);
        getViews(view);
        mFontBoldCb.setOnCheckedChangeListener(this);
        mFontItalicCb.setOnCheckedChangeListener(this);
        mFontUnderLineCb.setOnCheckedChangeListener(this);
        mFontDelLineCb.setOnCheckedChangeListener(this);
        mFontColorCb.setOnCheckedChangeListener(this);
        mFontSizeCb.setOnCheckedChangeListener(this);
        mAtSomeoneBtn.setOnCheckedChangeListener(this);
        mUrlAddBtn.setOnCheckedChangeListener(this);
        mQuoteAddBtn.setOnCheckedChangeListener(this);

        mFontDelLineCb.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mFontDelLineCb.getPaint().setAntiAlias(true);// 抗锯齿

        mDefaultFontSize = mFontSizeTv.getTextSize();

        mFontColorSpinner.setAdapter(mFontColorAdapter);
        mFontColorSpinner.setSelection(0);

        mFontSizeSpinner.setAdapter(mFontSizeAdapter);
        mFontSizeSpinner.setSelection(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view)
                .setTitle(R.string.supertext_hint)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int index = mBodyText.getSelectionStart();
                        String inputData = mInputText.getText().toString();
                        if (!inputData.replaceAll("\\n", "").trim().equals("")) {

                            inputData = formatInputDate(inputData);
                            SpannableString spanString = getSpannableString(inputData);


                            if (mBodyText.getText().toString().replaceAll("\\n", "")
                                    .trim().equals("")) {// NO INPUT DATA
                                mBodyText.setText("");
                                mBodyText.append(spanString);
                            } else {
                                if (index <= 0 || index >= mBodyText.length()) {// pos @
                                    mBodyText.append(spanString);
                                } else {
                                    mBodyText.getText().insert(index, spanString);
                                }
                            }
                        }
                    }
                });
        return builder.create();
    }

    private SpannableString getSpannableString(String inputData) {
        SpannableString spanString = new SpannableString(inputData);

        if (mAtSomeoneBtn.isChecked()) {
            spanString.setSpan(new ForegroundColorSpan(Color.BLUE),
                    0, inputData.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (mUrlAddBtn.isChecked()) {
            if (mInputText.getText().toString().startsWith("http:")
                    || mInputText.getText().toString()
                    .startsWith("https:")
                    || mInputText.getText().toString()
                    .startsWith("ftp:")
                    || mInputText.getText().toString()
                    .startsWith("gopher:")
                    || mInputText.getText().toString()
                    .startsWith("news:")
                    || mInputText.getText().toString()
                    .startsWith("telnet:")
                    || mInputText.getText().toString()
                    .startsWith("mms:")
                    || mInputText.getText().toString()
                    .startsWith("rtsp:")) {
                spanString.setSpan(new URLSpan(mInputText.getText()
                                .toString()), 0, inputData.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (mQuoteAddBtn.isChecked()) {
            spanString.setSpan(new BackgroundColorSpan(-1513240),
                    0, inputData.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            if (mFontColorCb.isChecked()) {
                spanString.setSpan(
                        new ForegroundColorSpan(
                                mColorSpan[mFontColorSpinner
                                        .getSelectedItemPosition()]),
                        0, inputData.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (mFontItalicCb.isChecked()) {
                spanString.setSpan(new StyleSpan(
                                android.graphics.Typeface.ITALIC), 0,
                        inputData.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (mFontBoldCb.isChecked()) {
                spanString.setSpan(new StyleSpan(
                                android.graphics.Typeface.BOLD), 0,
                        inputData.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (mFontUnderLineCb.isChecked()) {
                spanString.setSpan(new UnderlineSpan(), 0,
                        inputData.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (mFontDelLineCb.isChecked()) {
                spanString.setSpan(new StrikethroughSpan(), 0,
                        inputData.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (mFontSizeCb.isChecked()) {
                if (mFontSizeSpinner.getSelectedItemPosition() < 10) {
                    spanString.setSpan(
                            new RelativeSizeSpan(
                                    mSizeSpan[mFontSizeSpinner
                                            .getSelectedItemPosition()]),
                            0, inputData.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    spanString.setSpan(new BackgroundColorSpan(
                                    Color.GRAY), 0, inputData.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanString.setSpan(new RelativeSizeSpan(1.2f),
                            0, inputData.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                }
            }
        }
        return spanString;
    }


    private String formatInputDate(String inputData) {
        if (mAtSomeoneBtn.isChecked()) {
            inputData = "[@" + inputData + "]";
        } else if (mUrlAddBtn.isChecked()) {
            if (inputData.startsWith("http:")
                    || inputData.startsWith("https:")
                    || inputData.startsWith("ftp:")
                    || inputData.startsWith("gopher:")
                    || inputData.startsWith("news:")
                    || inputData.startsWith("telnet:")
                    || inputData.startsWith("mms:")
                    || inputData.startsWith("rtsp:")) {
                inputData = "[url]" + inputData + "[/url]";
            } else {

                Toast
                        .makeText(
                                getContext(),
                                "URL需以http|https|ftp|gopher|news|telnet|mms|rtsp开头",
                                Toast.LENGTH_SHORT).show();
            }
        } else if (mQuoteAddBtn.isChecked()) {
            inputData = "[quote]" + inputData + "[/quote]";
        } else {
            if (mFontColorCb.isChecked() && mFontColorSpinner.getSelectedItemPosition() > 0) {
                inputData = mColors[mFontColorSpinner
                        .getSelectedItemPosition() - 1]
                        + inputData + "[/color]";
            }
            if (mFontItalicCb.isChecked()) {
                inputData = "[i]" + inputData + "[/i]";
            }
            if (mFontBoldCb.isChecked()) {
                inputData = "[b]" + inputData + "[/b]";
            }
            if (mFontUnderLineCb.isChecked()) {
                inputData = "[u]" + inputData + "[/u]";
            }
            if (mFontDelLineCb.isChecked()) {
                inputData = "[del]" + inputData + "[/del]";
            }
            if (mFontSizeCb.isChecked()) {
                if (mFontSizeSpinner.getSelectedItemPosition() < 10) {
                    inputData = mSize[mFontSizeSpinner
                            .getSelectedItemPosition()]
                            + inputData
                            + "[/size]";
                } else {
                    inputData = "[h]" + inputData + "[/h]";
                }
            }
        }
        return inputData;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.bold:
            case R.id.italic:
            case R.id.underline:
            case R.id.delline:
                if (isChecked) {
                    mAtSomeoneBtn.setChecked(false);
                    mUrlAddBtn.setChecked(false);
                    mQuoteAddBtn.setChecked(false);
                }
                break;

            case R.id.fontsize:
                if (isChecked) {
                    mAtSomeoneBtn.setChecked(false);
                    mUrlAddBtn.setChecked(false);
                    mQuoteAddBtn.setChecked(false);
                    mFontSizeTv.setVisibility(View.VISIBLE);
                    mFontSizeSpinner.setVisibility(View.VISIBLE);
                } else {
                    mFontSizeTv.setVisibility(View.GONE);
                    mFontSizeSpinner.setVisibility(View.GONE);
                }
                break;
            case R.id.fontcolor:
                if (isChecked) {
                    mAtSomeoneBtn.setChecked(false);
                    mUrlAddBtn.setChecked(false);
                    mQuoteAddBtn.setChecked(false);
                    mFontColorTv.setVisibility(View.VISIBLE);
                    mFontColorSpinner.setVisibility(View.VISIBLE);
                } else {
                    mFontColorTv.setVisibility(View.GONE);
                    mFontColorSpinner.setVisibility(View.GONE);
                }
                break;
            case R.id.urladd:
            case R.id.quoteadd:
            case R.id.atsomeone:
                if (isChecked) {
                    if (id == R.id.urladd) {
                        mAtSomeoneBtn.setChecked(false);
                        mQuoteAddBtn.setChecked(false);
                    } else if (id == R.id.quoteadd) {
                        mUrlAddBtn.setChecked(false);
                        mAtSomeoneBtn.setChecked(false);
                    } else {
                        mUrlAddBtn.setChecked(false);
                        mQuoteAddBtn.setChecked(false);
                    }
                    mFontBoldCb.setChecked(false);
                    mFontItalicCb.setChecked(false);
                    mFontUnderLineCb.setChecked(false);
                    mFontColorCb.setChecked(false);
                    mFontSizeCb.setChecked(false);
                    mFontDelLineCb.setChecked(false);
                    mFontSizeTv.setVisibility(View.GONE);
                    mFontColorTv.setVisibility(View.GONE);
                    mFontSizeSpinner.setVisibility(View.GONE);
                    mFontColorSpinner.setVisibility(View.GONE);
                }
                break;
            default:
                break;

        }

    }
}
