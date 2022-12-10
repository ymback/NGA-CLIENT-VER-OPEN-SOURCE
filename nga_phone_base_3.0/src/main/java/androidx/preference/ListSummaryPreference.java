package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ListSummaryPreference extends ListPreference {

    public ListSummaryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListSummaryPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView summaryView = holder.itemView.findViewById(android.R.id.summary);
        summaryView.setVisibility(View.VISIBLE);
        summaryView.setText(getEntry());
    }
}
