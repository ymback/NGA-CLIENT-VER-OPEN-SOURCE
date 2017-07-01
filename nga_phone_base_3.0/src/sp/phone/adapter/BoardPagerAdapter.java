package sp.phone.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import sp.phone.fragment.BoardPagerFragment;
import sp.phone.interfaces.PageCategoryOwner;

public class BoardPagerAdapter extends FragmentStatePagerAdapter {

    final private int widthPercentage;
    private PageCategoryOwner pageCategoryOwner;

    public BoardPagerAdapter(FragmentManager fm, PageCategoryOwner pageCategoryOwner, int width) {
        super(fm);
        this.pageCategoryOwner = pageCategoryOwner;
        this.widthPercentage = width;

    }

    @Override
    public Fragment getItem(int index) {
        BoardPagerFragment fragment = (BoardPagerFragment) BoardPagerFragment.newInstance(index);
        fragment.setPageCategoryOwner(pageCategoryOwner);
        return fragment;
    }

    @Override
    public int getCount() {

        return pageCategoryOwner.getCategoryCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return pageCategoryOwner.getCategoryName(position);
    }

    @Override
    public float getPageWidth(int position) {
        return widthPercentage / 100.0f;
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Object obj = super.instantiateItem(container, position);
//        if (obj != null) {
//            try {
//                destroyItem(container, position, obj);
//            } catch (Exception e) {
//
//            }
//            return super.instantiateItem(container, position);
//        } else {
//            return obj;
//        }
//    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
