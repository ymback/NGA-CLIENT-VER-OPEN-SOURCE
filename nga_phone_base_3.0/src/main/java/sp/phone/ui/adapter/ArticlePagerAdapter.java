package sp.phone.ui.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import sp.phone.param.ArticleListParam;
import sp.phone.param.ParamKey;
import sp.phone.ui.fragment.ArticleListFragment;

/**
 * 帖子详情分页Adapter
 * Created by Justwen on 2017/7/9.
 */

public class ArticlePagerAdapter extends FragmentStatePagerAdapter {

    private int mCount = 1;

    private ArticleListParam mRequestParam;

    private List<String> mPageIndexList;

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
        if (mPageIndexList != null) {
            param.page = Integer.parseInt(mPageIndexList.get(position));
        } else {
            param.page = position + 1;
        }
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

    public void setPageIndexList(List<String> pageIndexList) {
        mPageIndexList = pageIndexList;
        setCount(pageIndexList.size());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageIndexList == null ? String.valueOf(position + 1) : mPageIndexList.get(position);
    }
}
