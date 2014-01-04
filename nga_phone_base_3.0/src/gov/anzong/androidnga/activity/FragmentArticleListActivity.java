package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import sp.phone.fragment.ArticleContainerFragment;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

class FragmentArticleListActivity extends FragmentActivity
implements PagerOwnner{
	static final private String TAG = FragmentArticleListActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.fragment_article_list_activity);
		FragmentManager fm = getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.item_detail_container);
		if(f == null)
		{
			f= new ArticleContainerFragment();
			f.setArguments(getIntent().getExtras());
			FragmentTransaction ft = fm.beginTransaction();		
			ft.replace(R.id.item_detail_container, f);
			
			ft.commit();
		}
		f.setHasOptionsMenu(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		//ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case android.R.id.home:
			case R.id.article_menuitem_back:
				finish();
				return true;
			default:
				
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public int getCurrentPage() {
		PagerOwnner child = null;
		try{
			
			 Fragment articleContainer = getSupportFragmentManager()	
						.findFragmentById(R.id.item_detail_container);
			 child  = (PagerOwnner) articleContainer;
			 return child.getCurrentPage();
		}catch(ClassCastException e){
			Log.e(TAG,"fragment in R.id.item_detail_container does not implements interface " 
					+ PagerOwnner.class.getName());
			return 0;
		}

	}

	@Override
	public void setCurrentItem(int index) {
		PagerOwnner child = null;
		try{
			
			 Fragment articleContainer = getSupportFragmentManager()	
						.findFragmentById(R.id.item_detail_container);
			 child  = (PagerOwnner) articleContainer;
			 child.setCurrentItem(index);
		}catch(ClassCastException e){
			Log.e(TAG,"fragment in R.id.item_detail_container does not implements interface " 
					+ PagerOwnner.class.getName());
			return ;
		}

		
	}
	
	

}
