package sp.phone.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

import gov.anzong.androidnga.R;
import sp.phone.adapter.BoardCatagoryAdapter;
import sp.phone.interfaces.PageCategoryOwner;
import sp.phone.utils.PhoneConfiguration;

public class BoardPagerFragment extends Fragment {
    private static final String TAG = BoardPagerFragment.class.getSimpleName();
    int category;
    GridView listview;
    BaseAdapter adapter;

    private PageCategoryOwner mPageCategoryOwner;

    // View v;
    public static Fragment newInstance(int category) {
        Fragment f = new BoardPagerFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);
        f.setArguments(args);
        return f;

    }

    public void setPageCategoryOwner(PageCategoryOwner pageCategoryOwner) {
        mPageCategoryOwner = pageCategoryOwner;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        category = getArguments() != null ? getArguments().getInt("category")
                : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        listview = (GridView) inflater.inflate(R.layout.category_grid,
                container, false);

        if (PhoneConfiguration.getInstance().showAnimation) {
            LayoutAnimationController anim = AnimationUtils
                    .loadLayoutAnimation(this.getActivity(),
                            R.anim.grid_wave_scale);
            listview.setLayoutAnimation(anim);
        }

        return listview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        listview.setOnItemClickListener(listener);


        if (mPageCategoryOwner == null && getActivity() instanceof PageCategoryOwner) {
            mPageCategoryOwner = (PageCategoryOwner) getActivity();
        }

        adapter = new BoardCatagoryAdapter(getResources(), getActivity()
                .getLayoutInflater(), mPageCategoryOwner.getCategory(category));

        listview.setAdapter(adapter);

    }

}
