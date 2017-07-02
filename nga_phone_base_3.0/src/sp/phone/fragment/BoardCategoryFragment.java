package sp.phone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

import gov.anzong.androidnga.R;
import sp.phone.adapter.BoardCatagoryAdapter;
import sp.phone.bean.BoardCategory;

public class BoardCategoryFragment extends Fragment {

    private static final String TAG = BoardCategoryFragment.class.getSimpleName();

    private GridView mListView;

    private BaseAdapter mAdapter;

    private BoardCategory mBoardCategory;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mListView = (GridView) inflater.inflate(R.layout.category_grid, container, false);
        return mListView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnItemClickListener listener = null;
        if (getParentFragment() instanceof OnItemClickListener) {
            listener = (OnItemClickListener) getParentFragment();
        } else if (getActivity() instanceof OnItemClickListener) {
            listener = (OnItemClickListener) getActivity();
        } else {
            Log.e(TAG,
                    "Activity or parentFragment should implements "
                            + OnItemClickListener.class.getSimpleName());
        }

        mListView.setOnItemClickListener(listener);

        mAdapter = new BoardCatagoryAdapter(getResources(), getActivity().getLayoutInflater(),mBoardCategory);

        mListView.setAdapter(mAdapter);
    }

}
