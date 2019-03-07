package sp.phone.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import sp.phone.common.BoardManager;
import sp.phone.common.BoardManagerImpl;
import sp.phone.fragment.BoardCategoryFragment;

/**
 * 版块分页Adapter
 */
public class BoardPagerAdapter extends FragmentStatePagerAdapter {


    private BoardManager mBoardManager = BoardManagerImpl.getInstance();

    public BoardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        BoardCategoryFragment fragment = new BoardCategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("category", mBoardManager.getCategory(index));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mBoardManager.getCategorySize();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mBoardManager.getCategory(position).getName();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
