package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;

import com.alibaba.android.arouter.facade.annotation.Route;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.forumoperation.PostParam;
import sp.phone.fragment.TopicPostFragment;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.util.FunctionUtils;
import sp.phone.util.StringUtils;

@Route(path = ARouterConstants.ACTIVITY_POST)
public class PostActivity extends BaseActivity {

    public static final int REQUEST_CODE_SELECT_PIC = 1;

    private final String LOG_TAG = Activity.class.getSimpleName();


    private TopicPostFragment mPostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setupActionBar();

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

        PostParam act = new PostParam(tid, "", "");
        act.setAction(action);
        act.setFid(fid);
        act.set__ngaClientChecksum(FunctionUtils.getngaClientChecksum(this));
        if (!StringUtils.isEmpty(mention))
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
        Bundle bundle = new Bundle();
        bundle.putString("prefix", prefix);
        bundle.putString("action", action);
        bundle.putString("title", title);

        bundle.putParcelable("param",act);
        mPostFragment = new TopicPostFragment();
        mPostFragment.setArguments(bundle);
        mPostFragment.setHasOptionsMenu(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, mPostFragment).commit();

    }

    @Override
    public void onBackPressed() {
        if (!mPostFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}