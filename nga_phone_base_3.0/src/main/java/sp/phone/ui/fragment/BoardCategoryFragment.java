package sp.phone.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.DeviceUtils;
import sp.phone.common.BoardManagerImpl;
import sp.phone.mvp.model.entity.BoardCategory;
import sp.phone.ui.adapter.BoardCategoryAdapter;

/**
 * 版块分页
 */
public class BoardCategoryFragment extends Fragment {

    private static final String TAG = BoardCategoryFragment.class.getSimpleName();

    private RecyclerView mListView;

    private BoardCategoryAdapter mAdapter;

    private BoardCategory mBoardCategory;

    public static final int COLUMN_NUMBER = 3;

    private static final int COLUMN_NUMBER_LAND = 5;

    public static Fragment newInstance(BoardCategory category) {
        Fragment f = new BoardCategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable("category", category);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBoardCategory = getArguments().getParcelable("category");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mListView = view.findViewById(R.id.list);
        if (DeviceUtils.isLandscape(getContext())) {
            mListView.setLayoutManager(new GridLayoutManager(getContext(), COLUMN_NUMBER_LAND));
        } else {
            mListView.setLayoutManager(new GridLayoutManager(getContext(), COLUMN_NUMBER));
        }

        super.onViewCreated(view, savedInstanceState);

        mAdapter = new BoardCategoryAdapter(getActivity(), mBoardCategory);

        if (mBoardCategory.getCategoryIndex() == 0) {
            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, 0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    BoardManagerImpl.getInstance().swapBookmark(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    mListView.getAdapter().notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    BoardManagerImpl.getInstance().removeBookmark(viewHolder.getAdapterPosition());
                    mListView.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());

                }
            });
            touchHelper.attachToRecyclerView(mListView);
        }
        mListView.setAdapter(mAdapter);
    }
}
