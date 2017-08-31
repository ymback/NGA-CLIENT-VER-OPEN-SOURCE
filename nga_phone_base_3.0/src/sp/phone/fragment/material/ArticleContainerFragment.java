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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.adapter.ArticlePagerAdapter;
import sp.phone.bean.ThreadData;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.ArticleListAction;
import sp.phone.fragment.BaseFragment;
import sp.phone.fragment.GotoDialogFragment;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwner;
import sp.phone.task.BookmarkTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;

/**
 * Created by Yang Yihang on 2017/7/9.
 */

public class ArticleContainerFragment extends BaseFragment implements OnThreadPageLoadFinishedListener, PagerOwner, OnClickListener {

    private ViewPager mViewPager;

    private ArticlePagerAdapter mPagerAdapter;

    private int mPosition;

    private ArticleListAction mArticleListAction;

    private TabLayout mTabLayout;

    private static final String GOTO_TAG = "goto";

    private String mTitle;

    private FloatingActionsMenu mFam;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mArticleListAction = args.getParcelable("ArticleListAction");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewPager = new ViewPager(getContext());
        mViewPager.setId(R.id.pager);
        mViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPagerAdapter = new ArticlePagerAdapter(getChildFragmentManager(), mArticleListAction);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                super.onPageSelected(position);
            }
        });

        mTabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            mTabLayout.setupWithViewPager(mViewPager);
        }
        return mViewPager;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        updateFloatingMenu();
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateFloatingMenu() {
        mActivity.findViewById(R.id.fab_post).setOnClickListener(this);
        mActivity.findViewById(R.id.fab_refresh).setOnClickListener(this);
        mFam = (FloatingActionsMenu) mActivity.findViewById(R.id.fab_menu);
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

    @Override
    public void finishLoad(ThreadData data) {
        mTitle = data.getThreadInfo().getSubject();
        setTitle(mTitle);
        int replyCount = data.getThreadInfo().getReplies() + 1; //没有包括主楼, 所以+1
        int count = replyCount / 20;
        if (replyCount % 20 != 0) {
            count++;
        }
        if (count <= 5) {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        mPagerAdapter.setCount(count);
    }

    private void reply() {
        Intent intent = new Intent();
        String tid = String.valueOf(mArticleListAction.getTid());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_reply:
                reply();

                break;
            case R.id.menu_refresh:
                mPagerAdapter.getChildAt(mPosition).loadPage();
                break;
            case R.id.menu_add_bookmark:
                BookmarkTask bt = new BookmarkTask(getContext());
                bt.execute(String.valueOf(mArticleListAction.getTid()));
                break;
//            case R.id.menu_orientation_lock:
//                handleLockOrientation(item);
//                break;
            case R.id.menu_goto_floor:
                createGotoDialog();
                break;
            case R.id.menu_share:
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareUrl = Utils.getNGAHost() + "read.php?";
                if (mArticleListAction.getPid() != 0) {
                    shareUrl = shareUrl + "pid=" + mArticleListAction.getPid() + " (分享自NGA安卓客户端开源版)";
                } else {
                    shareUrl = shareUrl + "tid=" + mArticleListAction.getTid() + " (分享自NGA安卓客户端开源版)";
                }
                if (!StringUtils.isEmpty(mTitle)) {
                    shareUrl = "《" + mTitle + "》 - 艾泽拉斯国家地理论坛，地址：" + shareUrl;
                }
                intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                String text = getResources().getString(R.string.share);
                startActivity(Intent.createChooser(intent, text));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.article_list_option_menu, menu);

//        MenuItem lock = menu.findItem(R.id.article_menuitem_lock);
//        int orientation = ThemeManager.getInstance().screenOrentation;
//        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                ||  orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            lock.setTitle(R.string.unlock_orientation);
//            lock.setIcon(R.drawable.ic_menu_always_landscape_portrait);
//
//        }
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
                mPagerAdapter.getChildAt(mViewPager.getCurrentItem()).loadPage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public int getCurrentPage() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_post:
                reply();
                break;
            case R.id.fab_refresh:
                mPagerAdapter.getChildAt(mPosition).loadPage();
                mFam.collapse();
                break;
        }
    }

    //    @SuppressWarnings({"unused", "deprecation"})
//    private void handleLockOrientation(MenuItem item) {
//        int preOrentation = ThemeManager.getInstance().screenOrentation;
//        int newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
//        ImageButton compat_item = null;// getActionItem(R.id.actionbar_compat_item_lock);
//
//        if (preOrentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                || preOrentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            // restore
//            // int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
//            ThemeManager.getInstance().screenOrentation = newOrientation;
//
//            setRequestedOrientation(newOrientation);
//            item.setTitle(R.string.lock_orientation);
//            item.setIcon(R.drawable.ic_lock_screen);
//            if (compat_item != null)
//                compat_item.setImageResource(R.drawable.ic_lock_screen);
//
//        } else {
//            newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//            Display dis = getWindowManager().getDefaultDisplay();
//            // Point p = new Point();
//            // dis.getSize(p);
//            if (dis.getWidth() < dis.getHeight()) {
//                newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//            }
//
//            ThemeManager.getInstance().screenOrentation = newOrientation;
//            setRequestedOrientation(newOrientation);
//            item.setTitle(R.string.unlock_orientation);
//            item.setIcon(R.drawable.ic_menu_always_landscape_portrait);
//            if (compat_item != null)
//                compat_item
//                        .setImageResource(R.drawable.ic_menu_always_landscape_portrait);
//        }
//
//        SharedPreferences share = getSharedPreferences(PERFERENCE,
//                MODE_MULTI_PROCESS);
//        SharedPreferences.Editor editor = share.edit();
//        editor.putInt(SCREEN_ORENTATION, newOrientation);
//        editor.apply();
//
//    }


}
