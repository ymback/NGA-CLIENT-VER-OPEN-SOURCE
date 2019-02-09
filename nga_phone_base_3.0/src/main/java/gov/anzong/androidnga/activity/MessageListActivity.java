package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.fragment.MessageListFragment;
import sp.phone.util.StringUtils;

@Route(path = ARouterConstants.ACTIVITY_MESSAGE_LIST)
public class MessageListActivity extends BaseActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        setupActionBar();
        initSpanner();
        initFab();
        initFragment();
    }

    private void initSpanner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        if (spinner == null) {
            return;
        }
        spinner.setAdapter(new ActionBarUserListAdapter(this));
        spinner.setSelection(UserManagerImpl.getInstance().getActiveUserIndex());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UserManager um = UserManagerImpl.getInstance();
                if (position != um.getActiveUserIndex()) {
                    um.setActiveUser(position);
                    initFragment();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initFragment() {
        Fragment fragment = new MessageListFragment();
        fragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void initFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab == null) {
            return;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(ARouterConstants.ACTIVITY_MESSAGE_POST)
                        .withString("action", "new")
                        .navigation(MessageListActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        String midString = (String) v.getTag();
        if (StringUtils.isEmpty(midString)) {
            return;
        }
        midString = midString.trim();
        int mid = StringUtils.getUrlParameter(midString, "mid");
        Intent intent = new Intent();
        intent.putExtra("mid", mid);
        intent.setClass(this, PhoneConfiguration.getInstance().messageDetialActivity);
        startActivity(intent);
    }
}
