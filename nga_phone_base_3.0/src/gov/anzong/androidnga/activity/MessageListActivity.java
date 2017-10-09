package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import gov.anzong.androidnga.R;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.fragment.material.MessageListFragment;
import sp.phone.utils.StringUtils;

public class MessageListActivity extends SwipeBackAppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        initActionBar();
        initSpanner();
        initFab();
        initFragment();
    }

    private void initActionBar() {
        if (getSupportActionBar() == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    private void initSpanner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        if (spinner == null) {
            return;
        }
        spinner.setAdapter(new ActionBarUserListAdapter(this));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UserManagerImpl.getInstance().setActiveUser(position);
                initFragment();
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
                if (UserManagerImpl.getInstance().getActiveUser() != null) {
                    intent_bookmark.setClass(MessageListActivity.this,
                            PhoneConfiguration.getInstance().messagePostActivityClass);
                } else {
                    intent_bookmark.setClass(MessageListActivity.this,
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivityForResult(intent_bookmark, 321);
            }
        });
    }


//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (!dualScreen) {// 非平板
//            if (null == onItemClickNewActivity) {
//                onItemClickNewActivity = new EnterJsonMessageThread(this);
//            }
//            onItemClickNewActivity.onItemClick(parent, view, position, id);
//
//        } else {
//            String guid = (String) parent.getItemAtPosition(position);
//            if (StringUtils.isEmpty(guid))
//                return;
//
//            guid = guid.trim();
//
//            int mid = StringUtils.getUrlParameter(guid, "mid");
//            Fragment f = new MessageDetialListContainer();
//            Bundle args = new Bundle();// (getIntent().getExtras());
//            args.putInt("mid", mid);
//            f.setArguments(args);
//            FragmentManager fm = getSupportFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.replace(R.id.item_detail_container, f);
//            Fragment f1 = fm.findFragmentById(R.id.item_list);
//            f1.setHasOptionsMenu(false);
//            f.setHasOptionsMenu(true);
//            ft.commit();
//
//            ListView listview = (ListView) parent;
//            Object a = parent.getAdapter();
//            MessageListAdapter adapter = null;
//            if (a instanceof MessageListAdapter) {
//                adapter = (MessageListAdapter) a;
//            } else if (a instanceof HeaderViewListAdapter) {
//                HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
//                adapter = (MessageListAdapter) ha.getWrappedAdapter();
//                position -= ha.getHeadersCount();
//            }
//            adapter.setSelected(position);
//            listview.setItemChecked(position, true);
//
//        }
//
//    }

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
