package sp.phone.fragment.material;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import gov.anzong.androidnga.activity.MyApp;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.bean.User;
import sp.phone.fragment.MessageListContainer;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class MessageListFragment extends MaterialCompatFragment implements AdapterView.OnItemClickListener{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("短消息");
    }

    @Override
    protected View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();


        Fragment fragment = fm.findFragmentById(getContainerId());
        if (fragment == null) {
            fragment = new MessageListContainer();
            Bundle args = new Bundle();// (getIntent().getExtras());
            if (null != mActivity.getIntent().getExtras()) {
                args.putAll(mActivity.getIntent().getExtras());
            }
            fragment.setArguments(args);
            fm.beginTransaction().add(getContainerId(), fragment).commit();
        }
        return super.onCreateContainerView(inflater, container, savedInstanceState);
    }

    @Override
    protected SpinnerAdapter getSpinnerAdapter() {
        return new ActionBarUserListAdapter(getContext());
    }

    @Override
    protected View.OnClickListener getFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_bookmark = new Intent();
                intent_bookmark.putExtra("action", "new");
                intent_bookmark.putExtra("messagemode", "yes");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent_bookmark.setClass(getActivity(),
                            PhoneConfiguration.getInstance().messagePostActivityClass);
                } else {
                    intent_bookmark.setClass(getActivity(),
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivityForResult(intent_bookmark, 321);
            }
        };
    }

    @Override
    protected void onSpinnerItemSelected(Spinner spinner,int position) {
        User u = (User) spinner.getAdapter().getItem(position);
        MyApp app = (MyApp) mActivity.getApplication();
        app.addToUserList(u.getUserId(), u.getCid(),
                u.getNickName(), u.getReplyString(), u.getReplyTotalNum(), u.getBlackList());
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        config.setUid(u.getUserId());
        config.setCid(u.getCid());
        config.setNickname(u.getNickName());
        config.setReplyString(u.getReplyString());
        config.setReplyTotalNum(u.getReplyTotalNum());
        config.blacklist = StringUtil.blackliststringtolisttohashset(u.getBlackList());
        MessageListContainer fragment = (MessageListContainer) getChildFragmentManager().findFragmentById(getContainerId());
        if (fragment != null) {
            fragment.onCategoryChanged(position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
