package sp.phone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import gov.anzong.androidnga.R;
import sp.phone.adapter.BlackListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.User;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2017/12/17.
 */

public class SettingsBlackListFragment extends BaseFragment implements View.OnClickListener {

    private BlackListAdapter mListAdapter;

    private RecyclerViewEx mListView;

    private UserManager mUserManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.setting_title_black_list);
        mUserManager = UserManagerImpl.getInstance();
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_user, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        mListAdapter = new BlackListAdapter(getContext(), mUserManager.getBlackList());
        mListAdapter.setOnClickListener(this);

        mListView = view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setAdapter(mListAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                User user = (User) viewHolder.itemView.getTag();
                mUserManager.removeFromBlackList(user.getUserId());
                mListAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        //将recycleView和ItemTouchHelper绑定
        touchHelper.attachToRecyclerView(mListView);

    }

    @Override
    public void onClick(View v) {
        User user = (User) v.getTag();
        showUserProfile(user.getNickName());
    }

    private void showUserProfile(String userName) {
        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().profileActivityClass);
        intent.putExtra("mode", "username");
        intent.putExtra("username", userName);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_black_list_option_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_all) {
            mUserManager.removeAllBlackList();
            mListAdapter.notifyDataSetChanged();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
