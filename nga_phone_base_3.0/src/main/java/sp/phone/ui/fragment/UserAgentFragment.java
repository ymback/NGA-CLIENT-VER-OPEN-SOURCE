package sp.phone.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gov.anzong.androidnga.R;
import io.reactivex.annotations.Nullable;
import sp.phone.common.UserAgent;
import sp.phone.common.UserAgentManagerImpl;
import sp.phone.ui.adapter.UserAgentsAdapter;
import sp.phone.view.RecyclerViewEx;

public class UserAgentFragment extends BaseFragment implements View.OnClickListener {

    private UserAgentManagerImpl mUserAgentManager;
    private TextView mKeywordView;
    private RecyclerViewEx mListView;
    private UserAgentsAdapter mListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.setting_nga_ua);
        setHasOptionsMenu(false);
        mUserAgentManager = UserAgentManagerImpl.getInstance();
        super.onCreate(savedInstanceState);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_agent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        mListAdapter = new UserAgentsAdapter(getContext(), mUserAgentManager.getUserAgents());
        mListAdapter.setOnClickListener(this);

        mListView = view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setAdapter(mListAdapter);

        mKeywordView = view.findViewById(R.id.new_ua);
        mKeywordView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String keyword = mKeywordView.getText().toString();
                    mKeywordView.setText("");
                    mUserAgentManager.addUserAgent(new UserAgent(keyword));
                    mListAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mListAdapter.notifyItemRemoved(position);
                mUserAgentManager.removeUserAgent(position);
                for (int i = 0; i < mListAdapter.getItemCount(); i++) {
                    mListAdapter.notifyItemChanged(i);
                }
            }
        });

        touchHelper.attachToRecyclerView(mListView);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Checkable) {
            int position = (int) v.getTag();
            mUserAgentManager.toggleUserAgent(position);
            if(((Checkable) v).isChecked()){
                mUserAgentManager.closeOthers(position);
                for (int i = 0; i < mListAdapter.getItemCount(); i++) {
                    if (i != position) {
                        mListAdapter.notifyItemChanged(i);
                    }
                }
            }else{
                ((Checkable) v).setChecked(true);
            }
        }
    }
}
