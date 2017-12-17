package sp.phone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2017/12/17.
 */

public class SettingsBlackListFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.setting_title_black_list);
        super.onCreate(savedInstanceState);
    }
}
