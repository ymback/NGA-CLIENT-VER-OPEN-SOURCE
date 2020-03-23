package sp.phone.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/3/11.
 */

public class EmptyLayout extends FrameLayout {

    private TextView mEmptyTextView;

    public EmptyLayout(Context context) {
        this(context, null);
    }

    public EmptyLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addEmptyTextView();
    }

    private void addEmptyTextView() {
        mEmptyTextView = new TextView(getContext());
        mEmptyTextView.setText(R.string.error_load_failed);
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(mEmptyTextView, lp);
    }

    public void setEmptyText(CharSequence text) {
        mEmptyTextView.setText(text);
    }

}
