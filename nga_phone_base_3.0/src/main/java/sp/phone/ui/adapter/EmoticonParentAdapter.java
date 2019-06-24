package sp.phone.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import sp.phone.util.EmoticonUtils;

/**
 * Created by Justwen on 2018/6/8.
 */
public class EmoticonParentAdapter extends PagerAdapter {

    private Context mContext;

    private int mHeight;

    private static final int COLUMN_COUNT = 4;

    public EmoticonParentAdapter(Context context, int height) {
        mContext = context;
        mHeight = height;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, COLUMN_COUNT));
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        EmoticonChildAdapter adapter = new EmoticonChildAdapter(mContext, mHeight);
        adapter.setData(EmoticonUtils.EMOTICON_LABEL[position][0], EmoticonUtils.EMOTICON_URL[position]);

        recyclerView.setAdapter(adapter);

        container.addView(recyclerView);
        return recyclerView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EmoticonUtils.EMOTICON_LABEL[position][1];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return EmoticonUtils.EMOTICON_LABEL.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
