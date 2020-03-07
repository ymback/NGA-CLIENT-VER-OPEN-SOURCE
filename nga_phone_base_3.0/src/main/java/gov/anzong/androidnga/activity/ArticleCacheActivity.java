package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.widget.TabLayoutEx;
import sp.phone.param.ArticleListParam;
import sp.phone.param.ParamKey;
import sp.phone.ui.adapter.ArticlePagerAdapter;

/**
 * @author yangyihang
 */
public class ArticleCacheActivity extends BaseActivity {

    private ArticlePagerAdapter mPagerAdapter;

    private List<String> mCachePageList = new ArrayList<>();

    private ArticleListParam mRequestParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setToolbarEnabled(true);
        mRequestParam = getIntent().getParcelableExtra(ParamKey.KEY_PARAM);
        setTitle(mRequestParam.title);
        super.onCreate(savedInstanceState);
        loadCachePageList(String.valueOf(mRequestParam.tid));
        initViews();
    }

    private void initViews() {
        if (mConfig.isShowBottomTab()) {
            setContentView(R.layout.fragment_article_tab_bottom);
        } else {
            setContentView(R.layout.fragment_article_tab);
        }

        mPagerAdapter = new ArticlePagerAdapter(getSupportFragmentManager(), mRequestParam);
        mPagerAdapter.setPageIndexList(mCachePageList);

        findViewById(R.id.fab_menu).setVisibility(View.GONE);
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mPagerAdapter);

        TabLayoutEx tabLayout = findViewById(R.id.tabs);
        tabLayout.setUpWithViewPager(viewPager);
    }

    private void loadCachePageList(String tid) {
        mCachePageList.clear();
        String path = ContextUtils.getContext().getFilesDir().getAbsolutePath() + "/cache/" + tid;
        File[] cacheFiles = new File(path).listFiles();
        for (File cacheFile : cacheFiles) {
            if (!cacheFile.getName().contains(tid)) {
                String page = cacheFile.getName();
                mCachePageList.add(page.replace(".json", ""));
            }
        }
        Collections.sort(mCachePageList);

    }


}
