package sp.phone.view.toolbar;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.mvp.contract.TopicPostContract.Presenter;

public class FormattedControlPanel extends ScrollView {

    private String[] mFontSize = {
            "默认", "[size=110%]", "[size=120%]",
            "[size=130%]", "[size=150%]", "[size=200%]", "[size=250%]",
            "[size=300%]", "[size=400%]", "[size=500%]"};

    private int[] mColorSpan = {-16777216, -7876885, -13149723, -16776961,
            -16777077, -23296, -47872, -2354116, -65536, -5103070,
            -7667712, -16744448, -13447886, -13726889, -16744320, -60269,
            -40121, -32944, -8388480, -11861886, -2180985, -744352,
            -6270419, -2987746, -4144960,};

    private String[] mFontColors = {
            "[color=skyblue]", "[color=royalblue]",
            "[color=blue]", "[color=darkblue]", "[color=orange]",
            "[color=orangered]", "[color=crimson]", "[color=red]",
            "[color=firebrick]", "[color=darkred]", "[color=green]",
            "[color=limegreen]", "[color=seagreen]", "[color=teal]",
            "[color=deeppink]", "[color=tomato]", "[color=coral]",
            "[color=purple]", "[color=indigo]", "[color=burlywood]",
            "[color=sandybrown]", "[color=sienna]", "[color=chocolate]",
            "[color=silver]"};

    private String[] mFontColorTitle;

    private Presenter mPresenter;

    private BaseAdapter mFontColorAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return mFontColorTitle.length; // 选项总个数
        }

        @Override
        public Object getItem(int arg0) {
            return mFontColorTitle[arg0];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView tv = convertView.findViewById(android.R.id.text1);
            tv.setText(mFontColorTitle[position]);
            tv.setTextColor(mColorSpan[position]);
            return convertView;
        }
    };

    public FormattedControlPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mFontColorTitle = context.getResources().getStringArray(R.array.colorchoose);
    }

    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onFinishInflate() {
        findViewById(R.id.btn_at).setOnClickListener(v -> mPresenter.insertAtFormat());
        findViewById(R.id.btn_quote).setOnClickListener(v -> mPresenter.insertQuoteFormat());
        findViewById(R.id.btn_url).setOnClickListener(v -> mPresenter.insertUrlFormat());
        findViewById(R.id.btn_bold).setOnClickListener(v -> mPresenter.insertBoldFormat());
        findViewById(R.id.btn_italic).setOnClickListener(v -> mPresenter.insertItalicFormat());
        findViewById(R.id.btn_under_line).setOnClickListener(v -> mPresenter.insertUnderLineFormat());
        findViewById(R.id.btn_delete_line).setOnClickListener(v -> mPresenter.insertDeleteLineFormat());

        Spinner spinner = findViewById(R.id.sp_size);
        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mFontSize));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mPresenter.insertFontSizeFormat(mFontSize[position] + "[/size]");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner = findViewById(R.id.sp_color);
        spinner.setAdapter(mFontColorAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mPresenter.insertFontColorFormat(mFontColors[position - 1] + "[/color]");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView deleteLineBtn = findViewById(R.id.btn_delete_line);
        deleteLineBtn.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        deleteLineBtn.getPaint().setAntiAlias(true);

        TextView underLineBtn = findViewById(R.id.btn_under_line);
        underLineBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        underLineBtn.getPaint().setAntiAlias(true);
        super.onFinishInflate();
    }


}