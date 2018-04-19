package sp.phone.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.fragment.ArticleListFragment;

/**
 * 帖子详情分页Adapter
 * Created by Justwen on 2017/7/9.
 */

public class ArticlePagerAdapter extends FragmentStatePagerAdapter {

    private int mCount = 1;

    private ArticleListParam mRequestParam;

    public ArticlePagerAdapter(FragmentManager fm, ArticleListParam param) {
        super(fm);
        mRequestParam = param;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ParamKey.KEY_PARAM, getRequestParam(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    private ArticleListParam getRequestParam(int position) {
        ArticleListParam param = (ArticleListParam) mRequestParam.clone();
        param.page = position + 1;
        return param;
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

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position + 1);
    }
}
