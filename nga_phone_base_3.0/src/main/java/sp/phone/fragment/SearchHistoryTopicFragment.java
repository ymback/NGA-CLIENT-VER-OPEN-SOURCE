package sp.phone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.common.PreferenceKey;

/**
 * Created by Justwen on 2018/10/12.
 */
public class SearchHistoryTopicFragment extends SearchHistoryBoardFragment {

    private boolean mCurrentBoard = true;

    private boolean mWithContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        View headerView = inflater.inflate(R.layout.layout_search_topic_panel, rootView, false);
        rootView.addView(headerView, 0);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RadioGroup radioGroup = view.findViewById(R.id.search_user_panel);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mCurrentBoard = checkedId == R.id.btn_1);
        CheckBox checkBox = view.findViewById(R.id.cb_with_content);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> mWithContent = isChecked);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected String getPreferenceKey() {
        return PreferenceKey.KEY_SEARCH_HISTORY_TOPIC;
    }

    @Override
    public void query(String query) {
        if (TextUtils.isEmpty(query)) {
            return;
        } else if (!mKeyList.contains(query)) {
            addHistory(query);
        }

        Postcard postcard = ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                .withInt("content", mWithContent ? 1 : 0)
                .withString("key", query);
        int fid = getArguments().getInt("fid");
        if (mCurrentBoard && fid != 0) {
            postcard.withInt("fid", fid);
        }
        postcard.navigation(getContext());

    }
}