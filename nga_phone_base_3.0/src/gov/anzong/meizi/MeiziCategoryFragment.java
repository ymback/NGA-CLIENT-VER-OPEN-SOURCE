package gov.anzong.meizi;

import android.content.Context;
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
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;

import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.meizi.MeiziCategory.MeiziCategoryItem;
import gov.anzong.meizi.MeiziLoadingFooterTask.ReloadListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class MeiziCategoryFragment extends Fragment implements OnMeiziCategoryLoadFinishedListener {

    private static final String TAG = MeiziCategoryFragment.class.getSimpleName();
    private static String moe52 = "http://www.52moe.net/?paged=";
    private static String rosmm = "http://www.rosmm.com/";
    private static MeiziCategoryItem mCategoryItem;
    @SuppressWarnings("unused")
    private static Context mcontext;
    OnMeiziSelectedListener mCallback;
    boolean isontop = false;
    PullToRefreshAttacher attacher = null;
    HTMLMeiziCategoryLoadTask task;
    private View mLoadingView;
    private Toast toast;
    private View mReloadView;
    private Button mReloadButton;
    private MeiziCategoryAdapter mAdapter;
    private MeiziLoadingFooterTask mLoadingFooter;
    private MultiColumnPullToRefreshListView mAdapterView = null;
    private View contentView;
    private int mPage = 0, sid = 0;

    public MeiziCategoryFragment() {

    }

    public MeiziCategoryFragment(MeiziCategoryItem categoryItem, Context context) {
        mCategoryItem = categoryItem;
        mcontext = context;
    }

    public MeiziCategoryFragment setInitData(MeiziCategoryItem categoryItem, Context context) {
        MeiziCategoryFragment mMeiziCategoryFragment = new MeiziCategoryFragment();
        mCategoryItem = categoryItem;
        mcontext = context;
        return mMeiziCategoryFragment;
    }

    public View getcontentView() {
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
                if (firstVisibleItem == 0) {
                    isontop = true;
                } else {
                    isontop = false;
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
                if (getActivity().findViewById(R.id.left_drawer) == null) {
                    if (meiziM != null) {
                        String topicUrl = meiziM.TopicUrl;
                        MeiziNavigationUtil.startTopicActivity(getActivity(), topicUrl);
                    }
                } else {
                    mCallback.onMeiziSelect(meiziM);
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
        mAdapterView.setOnRefreshListener(new MultiColumnPullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                if (isontop) {
                    loadFirstPageAndScrollToTop();
                } else {
                    mAdapterView.onRefreshComplete();
                }
            }

        });
        return contentView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (PhoneConfiguration.getInstance().HandSide == 1) {//lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {//文章列表，UIFLAG为1或者1+2或者1+4或者1+2+4
                inflater.inflate(R.menu.meizi_main_left, menu);
            } else {
                inflater.inflate(R.menu.meizi_main, menu);
            }
        } else {
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
//		case R.id.meizi_login:
//			Intent intent = new Intent(getActivity(),
//					MeiziLoginActivity.class);
//			startActivity(intent);
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
        sid = 0;
        mAdapter.clearData();
        refresh_saying();
        loadPage(mPage);
    }

    private void loadNextPage() {
        refresh_saying();
        loadPage(mPage);
    }

    private void loadPage(final int page) {
        mLoadingFooter.setState(MeiziLoadingFooterTask.State.Loading);
        String url = "";
        task = new HTMLMeiziCategoryLoadTask(getActivity(), this);
        if (mCategoryItem.getID() == 1) {
            url = moe52 + String.valueOf(page + 1);
        } else if (mCategoryItem.getID() == 2) {
            if (sid == 0) {
                url = rosmm;
            } else {
                url = rosmm + "public/ajax.php?action=list&sid=" + String.valueOf(sid) + "&classid=";
            }
        }
        task.execute(url);

    }

    public void loadFirstPageAndScrollToTop() {
        Glide.get(getActivity()).clearMemory();
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
    }// 有效

    @Override
    public void datafinishLoad(List<MeiziUrlData> result) {
        if (attacher != null)
            attacher.setRefreshComplete();
        if (mAdapterView != null)
            mAdapterView.onRefreshComplete();
        // TODO Auto-generated method stub
        if (result == null) {
            if (mPage == 0) {
                showReload();
            } else {
                mLoadingFooter.setState(MeiziLoadingFooterTask.State.Error);
            }
            if (toast != null) {
                toast.setText(R.string.msg_loading_failed);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(getActivity(), R.string.msg_loading_failed, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (result.size() == 0) {
                if (mPage == 0) {
                    showReload();
                } else {
                    mLoadingFooter.setState(MeiziLoadingFooterTask.State.Error);
                }
                if (toast != null) {
                    toast.setText(R.string.msg_loading_failed);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(getActivity(), R.string.msg_loading_failed, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                mAdapter.addData(result);
                mPage++;
                mLoadingFooter.setState(MeiziLoadingFooterTask.State.Idle);

                showContent();
            }
        }

    }

    @Override
    public void datafinishLoad(List<MeiziUrlData> result, int sid) {
        // TODO Auto-generated method stub

        if (attacher != null)
            attacher.setRefreshComplete();
        if (mAdapterView != null)
            mAdapterView.onRefreshComplete();
        // TODO Auto-generated method stub
        if (result == null) {
            if (mPage == 0) {
                showReload();
            } else {
                mLoadingFooter.setState(MeiziLoadingFooterTask.State.Error);
            }
            if (toast != null) {
                toast.setText(R.string.msg_loading_failed);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(getActivity(), R.string.msg_loading_failed, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (result.size() == 0) {
                if (mPage == 0) {
                    showReload();
                } else {
                    mLoadingFooter.setState(MeiziLoadingFooterTask.State.Error);
                }
                if (toast != null) {
                    toast.setText(R.string.msg_loading_failed);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(getActivity(), R.string.msg_loading_failed, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                mAdapter.addData(result);
                this.sid = sid;
                mLoadingFooter.setState(MeiziLoadingFooterTask.State.Idle);

                showContent();
            }
        }

    }

    public interface OnMeiziSelectedListener {
        public void onMeiziSelect(MeiziUrlData meiziM);
    }
}
