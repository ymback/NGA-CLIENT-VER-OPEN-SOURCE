package sp.phone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.common.PreferenceKey;
import sp.phone.common.User;
import sp.phone.common.UserManagerImpl;

/**
 * Created by Justwen on 2018/10/12.
 */
public class SearchHistoryUserFragment extends SearchHistoryBoardFragment {

    private boolean mUserNameMode = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);
        View headerView = inflater.inflate(R.layout.layout_search_user_panel, rootView, false);
        rootView.addView(headerView, 0);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RadioGroup radioGroup = view.findViewById(R.id.search_user_panel);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> mUserNameMode = checkedId == R.id.btn_user_name);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected String getPreferenceKey() {
        return PreferenceKey.KEY_SEARCH_HISTORY_USER;
    }

    @Override
    public void query(String query) {
        String mode = mUserNameMode ? "username" : "uid";
        if (TextUtils.isEmpty(query)) {
            User user = UserManagerImpl.getInstance().getActiveUser();
            if (user == null) {
                return;
            }
            query = mUserNameMode ? user.getNickName() : user.getUserId();
        } else if (!mKeyList.contains(query)) {
            addHistory(query);
        }
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_PROFILE)
                .withString("mode", mode)
                .withString(mode, query)
                .navigation(getContext());

    }

}