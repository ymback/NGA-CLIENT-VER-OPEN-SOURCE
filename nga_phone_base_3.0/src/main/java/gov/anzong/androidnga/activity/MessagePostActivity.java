package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.alibaba.android.arouter.facade.annotation.Route;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.forumoperation.MessagePostParam;
import sp.phone.fragment.MessagePostFragment;

@Route(path = ARouterConstants.ACTIVITY_MESSAGE_POST)
public class MessagePostActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        MessagePostParam postParam = getMessagePostParam();
        if (postParam.getAction().equals("new")) {
            setTitle(R.string.new_message);
        } else if (postParam.getAction().equals("reply")) {
            setTitle(R.string.reply_message);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("param", postParam);
        Fragment fragment = new MessagePostFragment();
        fragment.setArguments(bundle);
        fragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();

    }

    private MessagePostParam getMessagePostParam() {
        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        MessagePostParam postParam = null;
        if (action.equals("new")) {
            postParam = new MessagePostParam();
        } else if (action.equals("reply")) {
            String subject = intent.getStringExtra("title");
            int mid = intent.getIntExtra("mid", 0);
            postParam = new MessagePostParam(action, mid, subject);
        }
        if (postParam != null) {
            postParam.setRecipient(intent.getStringExtra("to"));
        }
        return postParam;
    }


}