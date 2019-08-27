package sp.phone.ui.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import sp.phone.mvp.model.entity.BoardCategory;
import sp.phone.ui.fragment.BoardCategoryFragment;

/**
 * 版块分页Adapter
 */
public class BoardPagerAdapter extends FragmentStatePagerAdapter {

    private List<BoardCategory> mBoardCategories;

    public BoardPagerAdapter(FragmentManager fm, List<BoardCategory> categories) {
        super(fm);
        mBoardCategories = categories;
    }

    @Override
    public Fragment getItem(int index) {
        BoardCategoryFragment fragment = new BoardCategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("category", mBoardCategories.get(index));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mBoardCategories.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mBoardCategories.get(position).getName();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
