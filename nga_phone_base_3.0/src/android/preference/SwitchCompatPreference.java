package android.preference;

import android.content.Context;
import android.util.AttributeSet;

import sp.phone.common.PreferenceKey;


public class SwitchCompatPreference extends SwitchPreference {

    public SwitchCompatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchCompatPreference(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        preferenceManager.setSharedPreferencesName(PreferenceKey.PERFERENCE);
        super.onAttachedToHierarchy(preferenceManager);
    }
}
