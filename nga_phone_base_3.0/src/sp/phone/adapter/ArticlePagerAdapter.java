package sp.phone.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import sp.phone.forumoperation.ArticleListAction;
import sp.phone.fragment.material.ArticleListFragment;

/**
 * 帖子详情分页Adapter
 * Created by Yang Yihang on 2017/7/9.
 */

public class ArticlePagerAdapter extends FragmentStatePagerAdapter {

    private SparseArray<ArticleListFragment> mRegisteredFragments = new SparseArray<>();

    private ArticleListAction mArticleListAction;

    private int mCount = 1;


    public ArticlePagerAdapter(FragmentManager fm, ArticleListAction action) {
        super(fm);
        mArticleListAction = action;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("ArticleListAction",mArticleListAction);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ArticleListFragment fragment = (ArticleListFragment) super.instantiateItem(container, position);
        fragment.setPage(position+1);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        if (mCount < count) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

    public ArticleListFragment getChildAt(int position) {
        return mRegisteredFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position + 1);
    }
}
