package sp.phone.fragment.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import butterknife.BindView;
import butterknife.OnClick;
import gov.anzong.androidnga.R;
import sp.phone.common.BoardManager;
import sp.phone.common.BoardManagerImpl;
import sp.phone.common.PreferenceKey;
import sp.phone.utils.ActivityUtils;

/**
 * Created by Justwen on 2017/11/19.
 */

public class TopicListFragment extends TopicSearchFragment {

    private Menu mOptionMenu;

    private BoardManager mBoardManager;

    @BindView(R.id.fab_menu)
    public FloatingActionsMenu mFam;

    @BindView(R.id.appbar)
    public AppBarLayout mAppBarLayout;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mBoardManager = BoardManagerImpl.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setTitle() {
        if (mRequestParam.title != null) {
            setTitle(mRequestParam.title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topic_list_board, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFloatingMenu();
    }

    private void updateFloatingMenu() {
        if (mConfig.getBoolean(PreferenceKey.LEFT_HAND)) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mFam.getLayoutParams();
            lp.gravity = Gravity.START | Gravity.BOTTOM;
            mFam.setExpandDirection(FloatingActionsMenu.EXPAND_UP, FloatingActionsMenu.LABELS_ON_RIGHT_SIDE);
            mFam.setLayoutParams(lp);
        }
    }

    @Override
    public void hideLoadingView() {
        AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        lp.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        super.hideLoadingView();
    }

    @Override
    public void scrollTo(int position) {
        if (position == 0) {
            mAppBarLayout.setExpanded(true, true);
        }
        super.scrollTo(position);
    }

    @Override
    public void onResume() {
        mFam.collapse();
        super.onResume();
    }

    @OnClick(R.id.fab_refresh)
    public void refresh() {
        mFam.collapse();
        mPresenter.loadPage(1, mRequestParam);
    }

    @OnClick(R.id.fab_post)
    public void startPostActivity() {
        Intent intent = new Intent();
        intent.putExtra("fid", mRequestParam.fid);
        intent.putExtra("action", "new");
        ActivityUtils.startPostActivity(getContext(), intent);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mBoardManager.isBookmarkBoard(String.valueOf(mRequestParam.fid))) {
            menu.findItem(R.id.menu_add_bookmark).setVisible(false);
            menu.findItem(R.id.menu_remove_bookmark).setVisible(true);
        } else {
            menu.findItem(R.id.menu_add_bookmark).setVisible(true);
            menu.findItem(R.id.menu_remove_bookmark).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topic_list_menu, menu);
        mOptionMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_bookmark:
                mBoardManager.addBookmark(String.valueOf(mRequestParam.fid), mRequestParam.title);
                item.setVisible(false);
                mOptionMenu.findItem(R.id.menu_remove_bookmark).setVisible(true);
                showToast(R.string.toast_add_bookmark_board);
                break;
            case R.id.menu_remove_bookmark:
                mBoardManager.removeBookmark(String.valueOf(mRequestParam.fid));
                item.setVisible(false);
                mOptionMenu.findItem(R.id.menu_add_bookmark).setVisible(true);
                showToast(R.string.toast_remove_bookmark_board);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
