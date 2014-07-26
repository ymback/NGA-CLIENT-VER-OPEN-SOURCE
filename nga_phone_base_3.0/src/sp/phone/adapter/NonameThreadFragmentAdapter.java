package sp.phone.adapter;

import sp.phone.fragment.NonameArticleListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.Toast;

public class NonameThreadFragmentAdapter extends FragmentStatePagerAdapter 
implements OnPageChangeListener {

	private int pageCount=1;
	private Bundle arguments = new Bundle();
	private final Context mContext;
	private final Class<?> clss;
	
	public NonameThreadFragmentAdapter(FragmentActivity activity,
			FragmentManager fm,
			ViewPager pager,
			Class<?> FragmentClass) {
		super(fm);
		mContext = activity;
		this.clss = FragmentClass;
		pager.setOnPageChangeListener(this);
		pager.setAdapter(this);
	}

	@Override
	public Fragment getItem(int position) {
		Bundle args = new Bundle(arguments);
		args.putInt("page", position);
		Fragment f = Fragment.instantiate(mContext, clss.getName(), args);
		
		return f;
	}
	
	@Override
	public int getCount() {

		return pageCount;
	}
	
	public void setCount(int pageCount){
		this.pageCount = pageCount;
		this.notifyDataSetChanged();
	}
	
	public void setArgument(String key, int value){
		arguments.putInt(key, value);
	}
	
	public void setArgument(String key, String value){
		arguments.putString(key, value);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	
	boolean isnotifyDataSetChanged=false;
	
	public void notifyDataSetChangedChangeMode(){
		isnotifyDataSetChanged=true;
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		if(isnotifyDataSetChanged){
			isnotifyDataSetChanged=false;
		    return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}
	
	private Toast lastToast = null;
	@Override
	public void onPageSelected(int arg0) {
		if(lastToast != null)
			lastToast.cancel();
		lastToast = 
		Toast.makeText(mContext, ""+ (arg0+1) + "/" + pageCount, Toast.LENGTH_SHORT);
		lastToast.show();
		
	}



}
