package sp.phone.fragment.material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.bean.TopicListRequestInfo;
import sp.phone.interfaces.EnterJsonArticle;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.presenter.contract.TopicListContract;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.StringUtil;
import sp.phone.view.NestedListView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public class TopicListFragment extends MaterialCompatFragment implements TopicListContract.View,AdapterView.OnItemLongClickListener{

    private  static final String TAG = TopicListFragment.class.getSimpleName();

    private TopicListRequestInfo mRequestInfo;

    private PullToRefreshAttacher mAttacher = null;

    private AppendableTopicAdapter mAdapter;

    private ListView mListView;

    private TopicListInfo mTopicListInfo;

    private TopicListContract.Presenter mPresenter;

    private boolean mFromReplayActivity;

    private boolean mIsVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestInfo = getArguments().getParcelable("requestInfo");
        if (mRequestInfo.authorId > 0 || mRequestInfo.searchPost > 0 || mRequestInfo.favor > 0
                || !StringUtil.isEmpty(mRequestInfo.key) || !StringUtil.isEmpty(mRequestInfo.author)
                || !StringUtil.isEmpty(mRequestInfo.fidGroup)) {//!StringUtil.isEmpty(table) ||
            mFromReplayActivity = true;
        }
        setRetainInstance(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getParentFragment() == null){
            return super.onCreateView(inflater,container,savedInstanceState);
        } else {
            return createView(inflater,container,savedInstanceState);
        }
    }

    @Override
    public View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createView(inflater,container,savedInstanceState);
    }

    private View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mListView = new NestedListView(getContext());
        mListView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (getParentFragment() == null){
            mAttacher = getAttacher();
            mListView.setNestedScrollingEnabled(false);
        } else  {
            mAttacher = ((PullToRefreshAttacherOnwer) getParentFragment()).getAttacher();
        }
        mAdapter = new AppendableTopicAdapter(getContext(), mAttacher, new NextJsonTopicListLoader() {
            @Override
            public void loadNextPage(OnTopListLoadFinishedListener callback) {
                mPresenter.loadNextPage(callback);
            }
        });
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new EnterJsonArticle(mActivity, mFromReplayActivity));
        if (mRequestInfo.favor != 0) {
            Toast.makeText(getActivity(), "长按可删除收藏的帖子", Toast.LENGTH_SHORT).show();
            mListView.setLongClickable(true);
            mListView.setOnItemLongClickListener(this);
        }
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mTopicListInfo != null) {
            mPresenter.jsonFinishLoad(mTopicListInfo);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mTopicListInfo == null && getUserVisibleHint() && mPresenter != null){
            mPresenter.refresh();
        }
        super.onResume();
    }

    public void refreshSaying() {
        DefaultHeaderTransformer transformer = null;

        if (mAttacher != null) {
            uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
            headerTransformer = mAttacher.getHeaderTransformer();
            if (headerTransformer != null && headerTransformer instanceof DefaultHeaderTransformer)
                transformer = (DefaultHeaderTransformer) headerTransformer;
        }

        if (transformer == null)
            ActivityUtil.getInstance().noticeSaying(this.getActivity());
        else
            transformer.setRefreshingText(ActivityUtil.getSaying());
        if (mAttacher != null)
            mAttacher.setRefreshing(true);
    }

    @Override
    public int getNextPage() {
        return mAdapter.getNextPage();
    }

    @Override
    public TopicListRequestInfo getTopicListRequestInfo() {
        return mRequestInfo;
    }

    @Override
    public View getTopicListView() {
        return mListView;
    }


    @Override
    public void setPresenter(TopicListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (refreshing){
            refreshSaying();
        } else {
            mAttacher.setRefreshComplete();
        }
    }

    @Override
    public void setData(TopicListInfo result) {
        mTopicListInfo = result;
        mAdapter.clear();
        mAdapter.jsonfinishLoad(result);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
        Object a = parent.getAdapter();
        AppendableTopicAdapter adapter = null;
        if (a instanceof AppendableTopicAdapter) {
            adapter = (AppendableTopicAdapter) a;
        } else if (a instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
            adapter = (AppendableTopicAdapter) ha.getWrappedAdapter();
            position -= ha.getHeadersCount();
        }
        final AppendableTopicAdapter finalAdapter = adapter;
        final int finalPosition = position;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mPresenter.removeBookmark(finalAdapter.getTidArray(finalPosition), finalPosition);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(this.getString(R.string.delete_favo_confirm_text))
                .setPositiveButton(R.string.confirm, dialogClickListener)
                .setNegativeButton(R.string.cancle, dialogClickListener);
        final AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

}
