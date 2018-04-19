package sp.phone.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.util.StringUtils;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/3/11.
 */

public class LoadingLayout extends LinearLayout {

    public LoadingLayout(Context context) {
        this(context,null);
    }

    public LoadingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.include_loading_view,this,true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        TextView textView = findViewById(R.id.saying);
        textView.setText(StringUtils.getSaying());
    }
}
