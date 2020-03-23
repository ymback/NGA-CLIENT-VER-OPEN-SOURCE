package gov.anzong.androidnga.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import sp.phone.param.ParamKey;
import sp.phone.param.TopicListParam;
import sp.phone.ui.fragment.TopicCacheFragment;

/**
 * @author Justwen
 */
public class TopicCacheActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setToolbarEnabled(true);
        super.onCreate(savedInstanceState);
        TopicListParam param = new TopicListParam();
        param.loadCache = true;
        Bundle bundle = new Bundle();
        bundle.putParcelable(ParamKey.KEY_PARAM, param);
        Fragment fragment = new TopicCacheFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }
}
