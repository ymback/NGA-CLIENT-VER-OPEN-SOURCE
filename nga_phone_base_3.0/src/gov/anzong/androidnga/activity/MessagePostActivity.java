package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;

import gov.anzong.androidnga.R;
import sp.phone.forumoperation.MessagePostAction;
import sp.phone.fragment.EmotionCategorySelectFragment;
import sp.phone.fragment.MessagePostContainer;
import sp.phone.fragment.material.MessagePostFragment;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.presenter.MessagePostPresenter;
import sp.phone.presenter.contract.MessagePostContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.FunctionUtils;
import sp.phone.common.PhoneConfiguration;

public class MessagePostActivity extends BasePostActivity implements OnEmotionPickedListener {

    private final String LOG_TAG = Activity.class.getSimpleName();

    private MessagePostAction mMessagePostAction;

    private MessagePostContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (PhoneConfiguration.getInstance().uploadLocation
                && PhoneConfiguration.getInstance().location == null) {
            ActivityUtils.reflushLocation(this);
        }

        Intent intent = this.getIntent();
        String prefix = intent.getStringExtra("prefix");
        String to = intent.getStringExtra("to");
        String action = intent.getStringExtra("action");
        int mid = intent.getIntExtra("mid", 0);
        String title = intent.getStringExtra("title");

        if (action.equals("new")) {
            setTitle(R.string.new_message);
        } else if (action.equals("reply")) {
            setTitle(R.string.reply_message);
        }

        mMessagePostAction = new MessagePostAction(mid, "", "");
        mMessagePostAction.setAction_(action);
        mMessagePostAction.set__ngaClientChecksum(FunctionUtils.getngaClientChecksum(this));

        if (prefix != null && prefix.startsWith("[quote][pid=") && prefix.endsWith("[/quote]\n")) {
            SpannableString spanString = new SpannableString(prefix);
            spanString.setSpan(new BackgroundColorSpan(-1513240), 0,
                    prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(
                    new StyleSpan(android.graphics.Typeface.BOLD),
                    prefix.indexOf("[b]Post by"),
                    prefix.indexOf("):[/b]") + 5,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            prefix = spanString.toString();
        }
        Bundle bundle = new Bundle();
        bundle.putString("prefix",prefix);
        bundle.putString("action",action);
        bundle.putString("to",to);
        bundle.putInt("mid",mid);
        bundle.putString("title",title);
        Fragment fragment =  getSupportFragmentManager().findFragmentById(android.R.id.content);
        if (fragment != null){
            mPresenter = new MessagePostPresenter((MessagePostContract.View) fragment);
            mPresenter.setMessagePostAction(mMessagePostAction);
            return;
        }
        if (PhoneConfiguration.getInstance().isMaterialMode()){
            fragment = new MessagePostFragment();
        } else {
            fragment = new MessagePostContainer();
        }
        fragment.setArguments(bundle);
        mPresenter = new MessagePostPresenter((MessagePostContract.View) fragment);
        fragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commit();

        mPresenter.setMessagePostAction(mMessagePostAction);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.emotion:
                FragmentTransaction ft = getSupportFragmentManager()
                        .beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(
                        EMOTION_CATEGORY_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }

                DialogFragment newFragment = new EmotionCategorySelectFragment();
                newFragment.show(ft, EMOTION_CATEGORY_TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }// OK

    @SuppressWarnings("deprecation")
    @Override
    public void onEmotionPicked(String emotion) {
        mPresenter.setEmoticon(emotion);
    }// OK


}