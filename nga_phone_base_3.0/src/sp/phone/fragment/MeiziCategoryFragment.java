
package sp.phone.fragment;

import java.util.List;

import sp.phone.adapter.MeiziCategoryAdapter;
import sp.phone.bean.MeiziTopicMData;
import sp.phone.bean.MeiziUrlData;
import sp.phone.bean.MeiziCategory.MeiziCategoryItem;
import sp.phone.fragment.SignContainer.ListRefreshListener;
import sp.phone.interfaces.OnMeiziCategoryLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.HTMLMeiziCategoryLoadTask;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.task.MeiziLoadingFooterTask;
import sp.phone.task.MeiziLoadingFooterTask.ReloadListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.MeiziListViewUtils;
import sp.phone.utils.MeiziNavigationUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MeiziLoginActivity;

import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MeiziCategoryFragment extends Fragment implements OnMeiziCategoryLoadFinishedListener {

    private static final String TAG = MeiziCategoryFragment.class.getSimpleName();

    OnMeiziSelectedListener mCallback;
    
    public interface OnMeiziSelectedListener {  
        public void onMeiziSelect(MeiziUrlData meiziM);  
    }  


    private static String sHost = "http://www.dbmeizi.com";

    private static String bZhao = "http://www.baozhao.me/p/";
    
    private static String ddShai = "http://www.dadanshai.com";

    private static String sCategoryUrl = sHost + "/category/";
    
    private View mLoadingView;

    private Toast toast;
    
    private View mReloadView;

    private Button mReloadButton;

    private MeiziCategoryAdapter mAdapter;

    private MeiziLoadingFooterTask mLoadingFooter;

    private static MeiziCategoryItem mCategoryItem;
    
    boolean isontop=false;
    
	private MultiColumnPullToRefreshListView mAdapterView = null;
	PullToRefreshAttacher attacher = null;
	HTMLMeiziCategoryLoadTask task;
    private static Context mcontext;
    public MeiziCategoryFragment(){
    	
    }
    
    public MeiziCategoryFragment setInitData(MeiziCategoryItem categoryItem,Context context){
    	MeiziCategoryFragment mMeiziCategoryFragment = new MeiziCategoryFragment();
        mCategoryItem = categoryItem;
        mcontext=context;
        return mMeiziCategoryFragment;
    }
    
    public MeiziCategoryFragment(MeiziCategoryItem categoryItem,Context context) {
        mCategoryItem = categoryItem;
        mcontext=context;
    }
    private View contentView;
    public View getcontentView(){
    	return contentView;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {  
            mCallback = (OnMeiziSelectedListener) getActivity();  
        } catch (ClassCastException e) {  
            throw new ClassCastException("must implement OnHeadlineSelectedListener");  
        }  
		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e(TAG,
					"father activity should implement PullToRefreshAttacherOnwer");
		}
        View contentView = inflater.inflate(R.layout.fragment_category, null);
        mAdapterView = (MultiColumnPullToRefreshListView) contentView.findViewById(R.id.list);
        mAdapter = new MeiziCategoryAdapter(getActivity(), mAdapterView);
        mLoadingFooter = new MeiziLoadingFooterTask(getActivity(), new ReloadListener() {

            @Override
            public void onReload() {
                loadPage(mPage);
            }
        });

        mAdapterView.addFooterView(mLoadingFooter.getView());

        mAdapterView.setOnScrollListener(new MultiColumnListView.OnScrollListener() {

            @Override
			public void onScrollStateChanged(PLA_AbsListView view,
					int scrollState) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				 if (mLoadingFooter.getState() != MeiziLoadingFooterTask.State.Idle) {
	                    return;
	                }
				 	if(firstVisibleItem==0){
				 		isontop=true;
				 	}else{
				 		isontop=false;
				 	}
	                if (firstVisibleItem + visibleItemCount >= totalItemCount - 3
	                        && totalItemCount != 0
	                        && totalItemCount != mAdapterView.getHeaderViewsCount()
	                                + mAdapterView.getFooterViewsCount() && mAdapter.getCount() > 0) {
	                    loadNextPage();
	                }
			}
        });
        
        mAdapterView.setOnItemClickListener(new MultiColumnListView.OnItemClickListener() {


			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

                MeiziUrlData meiziM = (MeiziUrlData) mAdapterView.getAdapter().getItem(position);
                if(StringUtil.isEmpty(meiziM.topicUrl)){
    				if (toast != null)
    	        	{
    	        		toast.setText("��ɹ���ܿ���ϸҳ��");
    	        		toast.setDuration(Toast.LENGTH_SHORT);
    	        		toast.show();
    	        	} else
    	        	{
    	        		toast = Toast.makeText(getActivity(), "��ɹ���ܿ���ϸҳ��", Toast.LENGTH_SHORT);
    	        		toast.show();
    	        	}
                }else{
                	if(getActivity().findViewById(R.id.left_drawer)==null){
                		if (meiziM != null) {
                        String topicUrl = meiziM.topicUrl;
                        MeiziNavigationUtil.startTopicActivity(getActivity(), topicUrl);
                    }
                	}else{
                		mCallback.onMeiziSelect(meiziM);
                	}
                }
			}
        });
        mAdapterView.setOnItemLongClickListener(new MultiColumnListView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(PLA_AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub
                return true;
			}
        });

        mLoadingView = contentView.findViewById(R.id.loading);
        mReloadView = contentView.findViewById(R.id.retry);
        mReloadButton = (Button) mReloadView.findViewById(R.id.btn_reload);
        mReloadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh_saying();
                loadPage(mPage);
            }
        });

        showLoading();
        loadFirstPage();
        mAdapterView.setAdapter(mAdapter);
        mAdapterView.setOnRefreshListener(new MultiColumnPullToRefreshListView.OnRefreshListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if(isontop){
					loadFirstPageAndScrollToTop();
				}else{
					mAdapterView.onRefreshComplete();
				}
			}
        	
        });
        return contentView;
    }


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if(PhoneConfiguration.getInstance().HandSide==1){//lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if(flag==1 || flag==3||flag==5||flag==7){//�����б�UIFLAGΪ1����1+2����1+4����1+2+4
				inflater.inflate(R.menu.meizi_main_left, menu);
				}
			else{
				inflater.inflate(R.menu.meizi_main, menu);
			}
		}else{
			inflater.inflate(R.menu.meizi_main, menu);
		}
	}
    

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.meizi_refresh:
			loadFirstPageAndScrollToTop();
			return true;
		case R.id.meizi_back:
			getActivity().finish();
			return true;
		case R.id.meizi_login:
			Intent intent = new Intent(getActivity(),
					MeiziLoginActivity.class);
			startActivity(intent);
		default:
			return true;
		}
	}
    
    private void showContent() {
        mLoadingView.setVisibility(View.GONE);
        mReloadView.setVisibility(View.GONE);
        mAdapterView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
        mAdapterView.setVisibility(View.GONE);
    }

    private void showReload() {
        mLoadingView.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
        mAdapterView.setVisibility(View.GONE);
    }

    private void loadFirstPage() {
        mPage = 0;
        mAdapter.clearData();
        refresh_saying();
        loadPage(mPage);
    }

    private void loadNextPage() {
        refresh_saying();
        loadPage(mPage);
    }

    private int mPage = 0;

	private void loadPage(final int page) {
        mLoadingFooter.setState(MeiziLoadingFooterTask.State.Loading);
        String url;
    	task = new HTMLMeiziCategoryLoadTask(getActivity(),this);
    	if(mCategoryItem.getID()==-1){
        	url = sHost + "/fuli" + "?p=" + page;
        }else if(mCategoryItem.getID()==0){
        	url = bZhao + String.valueOf(page+1)+".html";
        }else if(mCategoryItem.getID()==-2){
        	url = ddShai+"/latest/" + String.valueOf(page+1);
        }else if(mCategoryItem.getID()==-3){
        	url = ddShai+"/hotest/" + String.valueOf(page+1);
        }else{
            url = sCategoryUrl + mCategoryItem.getID() + "?p=" + page;
        }
    	task.execute(url);
		
    }

    public void loadFirstPageAndScrollToTop() {
		ImageLoader.getInstance().clearMemoryCache();
        MeiziListViewUtils.smoothScrollListViewToTop(mAdapterView);
        loadFirstPage();
    }

	private void refresh_saying() {
		DefaultHeaderTransformer transformer = null;

		if (attacher != null) {
			uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
			headerTransformer = attacher.getHeaderTransformer();
			if (headerTransformer != null
					&& headerTransformer instanceof DefaultHeaderTransformer)
				transformer = (DefaultHeaderTransformer) headerTransformer;
		}

		if (transformer == null)
			ActivityUtil.getInstance().noticeSaying(this.getActivity());
		else
			transformer.setRefreshingText(ActivityUtil.getSaying());
		if (attacher != null)
			attacher.setRefreshing(true);
	}// ��Ч

	
	@Override
	public void datafinishLoad(List<MeiziUrlData> result) {
		if (attacher != null)
			attacher.setRefreshComplete();
		if(mAdapterView!=null)
			mAdapterView.onRefreshComplete();
		// TODO Auto-generated method stub
		if(result==null){
        	if (mPage == 0) {
                showReload();
            } else {
                mLoadingFooter.setState(MeiziLoadingFooterTask.State.Error);
            }
			if (toast != null)
        	{
        		toast.setText(R.string.msg_loading_failed);
        		toast.setDuration(Toast.LENGTH_SHORT);
        		toast.show();
        	} else
        	{
        		toast = Toast.makeText(getActivity(),  R.string.msg_loading_failed, Toast.LENGTH_SHORT);
        		toast.show();
        	}
		}else{
	        if(result.size()==0){
	        	if (mPage == 0) {
	                showReload();
	            } else {
	                mLoadingFooter.setState(MeiziLoadingFooterTask.State.Error);
	            }
				if (toast != null)
	        	{
	        		toast.setText(R.string.msg_loading_failed);
	        		toast.setDuration(Toast.LENGTH_SHORT);
	        		toast.show();
	        	} else
	        	{
	        		toast = Toast.makeText(getActivity(),  R.string.msg_loading_failed, Toast.LENGTH_SHORT);
	        		toast.show();
	        	}
	        }else{
	        	mAdapter.addData(result);
	            mPage++;
	                mLoadingFooter.setState(MeiziLoadingFooterTask.State.Idle);

	            showContent();
	        }
		}
        
	}
}
