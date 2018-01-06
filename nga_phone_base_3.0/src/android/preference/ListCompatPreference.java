package android.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


public class ListCompatPreference extends ListPreference {

    public ListCompatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListCompatPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView summaryView = view.findViewById(android.R.id.summary);
        summaryView.setVisibility(View.VISIBLE);
        summaryView.setText(getEntry());
    }

    @Override
    protected boolean persistString(String value) {
        if (shouldPersist()) {
            // Shouldn't store null
            if (TextUtils.equals(value, getPersistedString(null))) {
                // It's already there, so the same as persisting
                return true;
            }

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putInt(getKey(), Integer.parseInt(value)).apply();
            return true;
        }
        return false;
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }
        if (getSharedPreferences().contains(getKey())){
            if (defaultReturnValue == null){
                defaultReturnValue = "0";
            }
            int value = getSharedPreferences().getInt(getKey(), Integer.parseInt(defaultReturnValue));
            return String.valueOf(value);
        } else{
            return defaultReturnValue;
        }

    }
}
