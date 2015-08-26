package gov.anzong.meizi;

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
import android.widget.ListView;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class MeiziTopicFragment extends Fragment implements OnMeiziTopicLoadFinishedListener {

    public static final String ARG_KEY_URL = "arg_key_url";
    private static final String TAG = MeiziTopicFragment.class.getSimpleName();
    HTMLMeiziTopicLoadTask task;
    private ListView mListView;
    private View mLoadingView;
    private View mReloadView;
    private Button mReloadButton;
    private MeiziTopicAdapter mAdapter;
    private View mHeader;
    private TextView mHeaderTitle;
    private TextView mHeaderDate;
    private MeiziTopicMData mTopicM;
    private String mUrl;
    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initFragmentArgs();
        View contentView = inflater.inflate(R.layout.fragment_topic, null);

        mAdapter = new MeiziTopicAdapter(getActivity());
        mListView = (ListView) contentView.findViewById(R.id.listView);
        mHeader = initHeader(inflater);
        mListView.addHeaderView(mHeader);
        mListView.setAdapter(mAdapter);


        if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
            contentView.setBackgroundColor(getResources().getColor(R.color.night_bg_color));
            mListView.setBackgroundColor(getResources().getColor(R.color.night_bg_color));
        }
        mLoadingView = contentView.findViewById(R.id.loading);
        mReloadView = contentView.findViewById(R.id.retry);
        mReloadButton = (Button) mReloadView.findViewById(R.id.btn_reload);
        mReloadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        showLoading();
        loadData();
        return contentView;
    }

    private void initFragmentArgs() {
        Bundle bundle = getArguments();
        mUrl = bundle.getString(ARG_KEY_URL);
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mReloadView.setVisibility(View.GONE);
    }

    private void showContent() {
        mLoadingView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        mReloadView.setVisibility(View.GONE);
    }

    private void showReload() {
        mLoadingView.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        mReloadView.setVisibility(View.VISIBLE);
    }

    private View initHeader(LayoutInflater inflater) {
        View header = inflater.inflate(R.layout.listitem_topic, null);
        mHeaderTitle = (TextView) header.findViewById(R.id.title);
        mHeaderDate = (TextView) header.findViewById(R.id.date);

        if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
            header.setBackgroundColor(getResources().getColor(R.color.night_bg_color));
            mHeaderTitle.setTextColor(getResources().getColor(R.color.night_fore_color));
            mHeaderDate.setTextColor(getResources().getColor(R.color.night_fore_color));
        }
        ;
        return header;
    }

    public void loadData() {
        task = new HTMLMeiziTopicLoadTask(getActivity(), this);
        task.execute(mUrl);
    }

    public void reload() {
        showLoading();
        loadData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meizi_topic_menu_dualscreen, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.meizi_topic_refresh:
                loadData();
                ActivityUtil.getInstance().noticeSaying(getActivity());
                break;
            case R.id.article_menuitem_back:
            default:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(this).commit();
                OnChildFragmentRemovedListener father = null;
                try {
                    father = (OnChildFragmentRemovedListener) getActivity();
                    father.OnChildFragmentRemoved(getId());
                } catch (ClassCastException e) {
                    Log.e(TAG, "father activity does not implements interface "
                            + OnChildFragmentRemovedListener.class.getName());

                }
                break;
        }
        return true;
    }

    @Override
    public void datafinishLoad(MeiziTopicMData result) {
        // TODO Auto-generated method stub
        mTopicM = result;
        if (mTopicM != null) {
            mAdapter.setData(mTopicM.content);
            if (StringUtil.isEmpty(mTopicM.date)) {
                mHeaderDate.setVisibility(View.GONE);
            } else {
                mHeaderDate.setVisibility(View.VISIBLE);
                mHeaderDate.setText((mTopicM.date));
            }
            mHeaderTitle.setText(mTopicM.title);

            showContent();
        } else {
            showReload();
        }
    }
}
