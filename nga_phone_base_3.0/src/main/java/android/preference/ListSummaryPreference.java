package android.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Justwen on 2017/7/16.
 */

public class ListSummaryPreference extends ListPreference {

    public ListSummaryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListSummaryPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        summaryView.setVisibility(View.VISIBLE);
        summaryView.setText(getEntry());
    }
}
