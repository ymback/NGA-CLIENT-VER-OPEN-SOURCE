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
import android.widget.Checkable;
import android.widget.FrameLayout;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.LoginActivity;
import sp.phone.adapter.UserListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2017/12/17.
 */

public class SettingsUserFragment extends BaseFragment implements View.OnClickListener {

    private UserListAdapter mListAdapter;

    private RecyclerViewEx mListView;

    private UserManager mUserManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.setting_title_user);
        setHasOptionsMenu(true);
        mUserManager = UserManagerImpl.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable FrameLayout container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_user, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        mListAdapter = new UserListAdapter(getContext());
        mListAdapter.setOnClickListener(this);

        mListView = view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setAdapter(mListAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                mUserManager.swapUser(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                mListAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mUserManager.removeUser(viewHolder.getAdapterPosition());
                mListAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                for (int i = 0; i < mListAdapter.getItemCount(); i++) {
                    mListAdapter.notifyItemChanged(i);
                }
            }
        });
        //将recycleView和ItemTouchHelper绑定
        touchHelper.attachToRecyclerView(mListView);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_user_option_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_user) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        int position = (int) v.getTag();

        if (v instanceof Checkable) {
            if (((Checkable) v).isChecked()) {
                setActiveUser(position);
            } else {
                ((Checkable) v).setChecked(true);
            }
        } else {
            showUserProfile(position);
        }

    }

    private void setActiveUser(int position) {
        mUserManager.setActiveUser(position);
        for (int i = 0; i < mListAdapter.getItemCount(); i++) {
            if (i != position) {
                mListAdapter.notifyItemChanged(i);
            }
        }
    }

    private void showUserProfile(int position) {
        String userName = mUserManager.getUserList().get(position).getNickName();
        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().profileActivityClass);
        intent.putExtra("mode", "username");
        intent.putExtra("username", userName);
        startActivity(intent);
    }
}
