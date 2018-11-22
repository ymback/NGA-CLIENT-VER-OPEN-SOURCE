package sp.phone.view.toolbar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.TopicPostContract.Presenter;

public class CategoryControlPanel extends FrameLayout implements OnHttpCallBack<List<String>> {

    private List<String> mCategoryList;

    private TextView mEmptyView;

    private ListView mListView;

    private Presenter mPresenter;

    private ProgressBar mProgressBar;

    public CategoryControlPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        mProgressBar = findViewById(R.id.progress_bar);
        mListView = findViewById(R.id.list);
        mEmptyView = findViewById(R.id.tv_error);
        updateVisibility();
        mListView.setOnItemClickListener((parent, view, position, id) -> mPresenter.insertTopicCategory(mCategoryList.get(position)));
        setOnClickListener(v -> {
            if (mEmptyView.isShown()) {
                mPresenter.loadTopicCategory(this);
                updateVisibility();
            }
        });
        super.onFinishInflate();
    }

    private void updateVisibility() {
        if (mCategoryList == null) {
            mEmptyView.setVisibility(GONE);
            mListView.setVisibility(GONE);
            mProgressBar.setVisibility(VISIBLE);
        } else {
            mEmptyView.setVisibility(GONE);
            mListView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
        }
    }

    @Override
    public void onError(String text) {
        mProgressBar.setVisibility(GONE);
        mListView.setVisibility(GONE);
        mEmptyView.setVisibility(VISIBLE);
    }

    @Override
    public void onSuccess(List<String> data) {
        mCategoryList = data;
        mProgressBar.setVisibility(GONE);
        mListView.setVisibility(VISIBLE);
        mListView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, data));
    }

    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}