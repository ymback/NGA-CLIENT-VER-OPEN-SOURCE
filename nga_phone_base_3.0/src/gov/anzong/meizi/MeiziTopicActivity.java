package gov.anzong.meizi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;

public class MeiziTopicActivity extends SwipeBackAppCompatActivity {

    public static final String ARG_KEY_URL = "arg_key_url";
    private MeiziTopicFragment mTopicFragment;
    private String mTopicUrl;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgs();
        view = LayoutInflater.from(this).inflate(R.layout.activity_topic, null);
        ;
        getSupportActionBar().setTitle("~ߣ~");
        setContentView(view);

        mTopicFragment = new MeiziTopicFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MeiziTopicFragment.ARG_KEY_URL, mTopicUrl);
        mTopicFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mTopicFragment).commit();
    }

    @Override
    protected void onResume() {
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(view);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.meizi_topic_menu, menu);
        final int flags = ThemeManager.ACTION_BAR_FLAG;
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.meizi_topic_refresh:
                if (mTopicFragment != null) {
                    mTopicFragment.reload();
                }
                break;
            default:
                finish();
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    private void initArgs() {
        Intent intent = getIntent();
        mTopicUrl = intent.getStringExtra(ARG_KEY_URL);
    }
}
