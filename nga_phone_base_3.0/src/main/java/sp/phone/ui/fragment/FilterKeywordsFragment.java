package sp.phone.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import io.reactivex.annotations.Nullable;
import sp.phone.ui.adapter.FilterKeywordsAdapter;
import sp.phone.common.FilterKeyword;
import sp.phone.common.FilterKeywordsManagerImpl;
import sp.phone.view.RecyclerViewEx;


public class FilterKeywordsFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerViewEx mListView;
    private FilterKeywordsAdapter mListAdapter;
    private FilterKeywordsManagerImpl mFilterKeywordsManager;
    private TextView mKeywordView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.filter_keywords);
        setHasOptionsMenu(false);
        mFilterKeywordsManager = FilterKeywordsManagerImpl.getInstance();
        super.onCreate(savedInstanceState);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_keywords, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        mListAdapter = new FilterKeywordsAdapter(getContext(), mFilterKeywordsManager.getKeywords());
        mListAdapter.setOnClickListener(this);

        mListView = view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setAdapter(mListAdapter);

        mKeywordView = view.findViewById(R.id.new_keyword);
        mKeywordView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String keyword = mKeywordView.getText().toString();
                    mKeywordView.setText("");
                    mFilterKeywordsManager.addKeyword(new FilterKeyword(keyword));
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
                mFilterKeywordsManager.removeKeyword(position);
                for (int i = 0; i < mListAdapter.getItemCount(); i++) {
                    mListAdapter.notifyItemChanged(i);
                }
            }
        });

        touchHelper.attachToRecyclerView(mListView);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Checkable) {
            int position = (int) view.getTag();
            mFilterKeywordsManager.toggleKeyword(position);
        }
    }
}
