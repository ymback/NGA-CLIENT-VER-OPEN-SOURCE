package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import gov.anzong.androidnga.R;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.fragment.MessageListFragment;
import sp.phone.util.StringUtils;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.fragment.MessageListFragment;
import sp.phone.util.ActivityUtils;
import sp.phone.util.StringUtils;

public class MessageListActivity extends SwipeBackAppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideActionBar();
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
                Intent intent_bookmark = new Intent();
                intent_bookmark.putExtra("action", "new");
                intent_bookmark.putExtra("messagemode", "yes");
                ActivityUtils.startMessagePostActivity(MessageListActivity.this, intent_bookmark);
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
