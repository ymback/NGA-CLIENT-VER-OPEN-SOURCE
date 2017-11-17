package sp.phone.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import sp.phone.forumoperation.TopicListParam;
import sp.phone.fragment.material.TopicListFragment;
import sp.phone.presenter.TopicListPresenter;
import sp.phone.presenter.contract.TopicListContract;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public class TopicViewPagerAdapter extends FragmentPagerAdapter {

    private String mTabs[] = {"全部", "精华"};

    private TopicListParam mRequestInfo;

    private Fragment[] mFragments = new Fragment[3];

    private TopicListContract.Presenter[] mPresenters;


    public TopicViewPagerAdapter(FragmentManager fm, TopicListContract.Presenter[] presenters,TopicListParam requestInfo) {
        super(fm);
        mRequestInfo = requestInfo;
        mPresenters = presenters;
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments[position] == null){
            mFragments[position] = new TopicListFragment();
            try {
                TopicListParam info = (TopicListParam) mRequestInfo.clone();
                info.category = position;
                Bundle bundle = new Bundle();
                bundle.putParcelable("requestInfo",info);
                mFragments[position].setArguments(bundle);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }
        return mFragments[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,position);
        mPresenters[position] = new TopicListPresenter((TopicListContract.View) fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }
}
