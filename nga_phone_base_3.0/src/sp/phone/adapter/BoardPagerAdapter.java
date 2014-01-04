package sp.phone.adapter;

import sp.phone.fragment.BoardPagerFragment;
import sp.phone.interfaces.PageCategoryOwnner;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class BoardPagerAdapter extends FragmentStatePagerAdapter {
	
	private PageCategoryOwnner pageCategoryOwnner;
	final private int widthPercentage;
	public BoardPagerAdapter(FragmentManager fm, PageCategoryOwnner pageCategoryOwnner,int width) {
		super(fm);
		this.pageCategoryOwnner =  pageCategoryOwnner;
		this.widthPercentage = width;

	}
	@Override
	public Fragment getItem(int index) {
		//BoardCategory category = boardInfo.getCategory(arg0);
		return BoardPagerFragment.newInstance(index);
	}
	@Override
	public int getCount() {

		return pageCategoryOwnner.getCategoryCount();
	}
	@Override
	public CharSequence getPageTitle(int position) {
		
		return pageCategoryOwnner.getCategoryName(position);
	}
	@Override
	public float getPageWidth(int position) {
		return widthPercentage/100.0f;
	}
	
	




}
