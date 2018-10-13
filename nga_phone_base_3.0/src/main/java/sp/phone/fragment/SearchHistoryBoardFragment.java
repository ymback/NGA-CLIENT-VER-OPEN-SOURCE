package sp.phone.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.adapter.SearchHistoryAdapter;
import sp.phone.common.PreferenceKey;
import sp.phone.forumoperation.ParamKey;
import sp.phone.task.SearchBoardTask;
import sp.phone.util.ActivityUtils;

/**
 * Created by Justwen on 2018/10/12.
 */
public class SearchHistoryBoardFragment extends BaseRxFragment {

    protected List<String> mKeyList;

    private SharedPreferences mPreferences;

    protected SearchHistoryAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mKeyList = JSON.parseArray(mPreferences.getString(getPreferenceKey(), ""), String.class);
        if (mKeyList == null) {
            mKeyList = new ArrayList<>();
        }
        RecyclerView recyclerView = view.findViewById(android.R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SearchHistoryAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setData(mKeyList);
        mAdapter.setOnClickListener(v -> {
            Object tag = v.getTag();
            if (tag instanceof String) {
                query((String) tag);
            } else {
                int position = Integer.parseInt(tag.toString());
                mKeyList.remove(position);
                mAdapter.notifyDataSetChanged();
                saveHistory();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void query(String query) {

        if (TextUtils.isEmpty(query)) {
            return;
        } else if (!mKeyList.contains(query)) {
            addHistory(query);
        }

        ActivityUtils.getInstance().noticeSaying(getContext());

        SearchBoardTask.execute(query, data -> {
            if (getContext() == null) {
                return;
            }
            ActivityUtils.getInstance().dismiss();
            if (data == null) {
                showToast("没有找到符合条件的版面或者网络错误");
            } else {
                ARouter.getInstance()
                        .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                        .withInt(ParamKey.KEY_FID, data.getFid())
                        .withString(ParamKey.KEY_TITLE, data.getName())
                        .navigation(getContext());
            }

        });

    }

    protected void addHistory(String query) {
        mKeyList.add(0, query);
        mAdapter.notifyItemInserted(0);
        saveHistory();
    }

    protected void saveHistory() {
        mPreferences.edit()
                .putString(getPreferenceKey(), JSON.toJSONString(mKeyList))
                .apply();
    }

    protected String getPreferenceKey() {
        return PreferenceKey.KEY_SEARCH_HISTORY_BOARD;
    }
}