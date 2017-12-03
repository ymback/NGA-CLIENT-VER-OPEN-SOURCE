package sp.phone.fragment.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import io.reactivex.annotations.NonNull;
import sp.phone.adapter.ArticlePagerAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.fragment.dialog.GotoDialogFragment;
import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.task.BookmarkTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.StringUtils;

/**
 * 帖子详情Fragment
 * Created by Yang Yihang on 2017/7/9.
 */

public class ArticleTabFragment extends BaseRxFragment {

    @BindView(R.id.pager)
    public ViewPager mViewPager;

    private ArticlePagerAdapter mPagerAdapter;

    private ArticleListParam mRequestParam;

    @BindView(R.id.tabs)
    public TabLayout mTabLayout;

    private static final String GOTO_TAG = "goto";

    @BindView(R.id.fab_menu)
    public FloatingActionsMenu mFam;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mRequestParam = getArguments().getParcelable(ParamKey.KEY_PARAM);
        }
        registerRxBus();
    }

    @Override
    protected void accept(@NonNull RxEvent rxEvent) {
        if (rxEvent.what == RxEvent.EVENT_ARTICLE_TAB_UPDATE) {
            int replyCount = rxEvent.arg + 1; //没有包括主楼, 所以+1
            int count = replyCount / 20;
            if (replyCount % 20 != 0) {
                count++;
            }
            if (count > mPagerAdapter.getCount()) {
                if (count <= 5) {
                    mTabLayout.setTabMode(TabLayout.MODE_FIXED);
                } else {
                    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
                mPagerAdapter.setCount(count);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (PhoneConfiguration.getInstance().isShownBottomTab()) {
            return inflater.inflate(R.layout.fragment_article_tab_bottom, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_article_tab, container, false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        updateFloatingMenu();
        mPagerAdapter = new ArticlePagerAdapter(getChildFragmentManager(), mRequestParam);
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateFloatingMenu() {
        if (PhoneConfiguration.getInstance().isLeftHandMode()) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mFam.getLayoutParams();
            lp.gravity = Gravity.START | Gravity.BOTTOM;
            mFam.setExpandDirection(FloatingActionsMenu.EXPAND_UP, FloatingActionsMenu.LABELS_ON_RIGHT_SIDE);
            mFam.setLayoutParams(lp);
        }
    }

    @Override
    public void onResume() {
        if (mFam != null) {
            mFam.collapse();
        }
        super.onResume();
    }

    @OnClick(R.id.fab_post)
    public void reply() {
        Intent intent = new Intent();
        String tid = String.valueOf(mRequestParam.tid);
        intent.putExtra("prefix", "");
        intent.putExtra("tid", tid);
        intent.putExtra("action", "reply");
        if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
            intent.setClass(getContext(),
                    PhoneConfiguration.getInstance().postActivityClass);
        } else {
            intent.setClass(getContext(),
                    PhoneConfiguration.getInstance().loginActivityClass);
        }
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_TOPIC_POST);
    }

    @OnClick(R.id.fab_refresh)
    public void refresh() {
        RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_ARTICLE_UPDATE, mViewPager.getCurrentItem()));
        mFam.collapse();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_bookmark:
                BookmarkTask bt = new BookmarkTask(getContext());
                bt.execute(String.valueOf(mRequestParam.tid));
                break;
            case R.id.menu_goto_floor:
                createGotoDialog();
                break;
            case R.id.menu_share:
                share();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void share() {
        String title = getString(R.string.share);
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(getActivity().getTitle())) {
            builder.append("《").append(getActivity().getTitle()).append("》 - 艾泽拉斯国家地理论坛，地址：");
        }
        builder.append(Utils.getNGAHost()).append("read.php?");
        if (mRequestParam.pid != 0) {
            builder.append("pid=").append(mRequestParam.pid).append(" (分享自NGA安卓客户端开源版)");
        } else {
            builder.append("tid=").append(mRequestParam.tid).append(" (分享自NGA安卓客户端开源版)");
        }
        FunctionUtils.share(getContext(), title, builder.toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.article_list_option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mFam != null) {
            menu.findItem(R.id.menu_reply).setVisible(false);
            menu.findItem(R.id.menu_refresh).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void createGotoDialog() {

        int count = mPagerAdapter.getCount();
        Bundle args = new Bundle();
        args.putInt("count", count);

        DialogFragment df = new GotoDialogFragment();
        df.setArguments(args);

        FragmentManager fm = getSupportFragmentManager();

        Fragment prev = fm.findFragmentByTag(GOTO_TAG);
        if (prev != null) {
            fm.beginTransaction().remove(prev).commit();
        }
        df.show(fm, GOTO_TAG);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.REQUEST_CODE_TOPIC_POST && resultCode == Activity.RESULT_OK) {
            if (mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
                RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_ARTICLE_UPDATE, mViewPager.getCurrentItem()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
