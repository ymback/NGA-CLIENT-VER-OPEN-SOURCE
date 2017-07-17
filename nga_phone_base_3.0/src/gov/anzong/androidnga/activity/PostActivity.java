package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;

import gov.anzong.androidnga.R;
import sp.phone.forumoperation.TopicPostAction;
import sp.phone.fragment.EmotionCategorySelectFragment;
import sp.phone.fragment.TopicPostContainer;
import sp.phone.fragment.material.TopicPostFragment;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.presenter.TopicPostPresenter;
import sp.phone.presenter.contract.TopicPostContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.PermissionUtils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class PostActivity extends BasePostActivity implements OnEmotionPickedListener {

    public static final int REQUEST_CODE_SELECT_PIC = 1;

    private final String LOG_TAG = Activity.class.getSimpleName();

    private TopicPostContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (PhoneConfiguration.getInstance().uploadLocation
                && PhoneConfiguration.getInstance().location == null) {
            ActivityUtils.reflushLocation(this);
        }

        Intent intent = this.getIntent();
        String prefix = intent.getStringExtra("prefix");
        // if(prefix!=null){
        // prefix=prefix.replaceAll("\\n\\n", "\n");
        // }
        String action = intent.getStringExtra("action");
        switch (action) {
            case "new":
                setTitle(R.string.new_thread);
                break;
            case "reply":
                setTitle(R.string.reply_thread);
                break;
            case "modify":
                setTitle(R.string.modify_thread);
                break;
        }
        String tid = intent.getStringExtra("tid");
        int fid = intent.getIntExtra("fid", -7);
        String title = intent.getStringExtra("title");
        String pid = intent.getStringExtra("pid");
        String mention = intent.getStringExtra("mention");
        if (tid == null)
            tid = "";

        TopicPostAction act = new TopicPostAction(tid, "", "");
        act.setAction_(action);
        act.setFid_(fid);
        act.set__ngaClientChecksum(FunctionUtil.getngaClientChecksum(this));
        if (!StringUtil.isEmpty(mention))
            act.setMention_(mention);
        if (pid != null)
            act.setPid_(pid);

        if (prefix != null) {
            if (prefix.startsWith("[quote][pid=")
                    && prefix.endsWith("[/quote]\n")) {
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
        }
        Bundle bundle =  new Bundle();
        bundle.putString("prefix",prefix);
        bundle.putString("action",action);
        bundle.putString("title",title);
        Fragment fragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
        if (fragment == null){
            if (PhoneConfiguration.getInstance().isMaterialMode()){
                fragment = new TopicPostFragment();
            } else {
                fragment = new TopicPostContainer();
            }
            mPresenter  = new TopicPostPresenter((TopicPostContract.View) fragment);
            mPresenter.setTopicPostAction(act);
            fragment.setArguments(bundle);
            fragment.setHasOptionsMenu(true);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content,fragment).commit();
        } else {
            mPresenter  = new TopicPostPresenter((TopicPostContract.View) fragment);
            mPresenter.setTopicPostAction(act);
        }

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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEmotionPicked(String emotion) {
        mPresenter.setEmoticon(emotion);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mPresenter.prepareUploadFile();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}