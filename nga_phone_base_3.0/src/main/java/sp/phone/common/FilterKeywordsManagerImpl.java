package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class FilterKeywordsManagerImpl implements FilterKeywordsManager {

    private Context mContext;

    private SharedPreferences mPrefs;

    private List<FilterKeyword> mFilterKeywords;

    private static class SingletonHolder {

        static FilterKeywordsManagerImpl sInstance = new FilterKeywordsManagerImpl();
    }

    public static FilterKeywordsManagerImpl getInstance() {
        return SingletonHolder.sInstance;
    }

    private FilterKeywordsManagerImpl() {
    }

    @Override
    public void initialize(Context context) {
        mContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String blackListStr = mPrefs.getString(PreferenceKey.FILTER_KEYWORDS_LIST, "");
        if (TextUtils.isEmpty(blackListStr)) {
            mFilterKeywords = new ArrayList<>();
        } else {
            mFilterKeywords = JSON.parseArray(blackListStr, FilterKeyword.class);
            if (mFilterKeywords == null) {
                mFilterKeywords = new ArrayList<>();
            }
        }

        versionUpgrade();
    }

    private void versionUpgrade() {
    }

    @Override
    public void toggleKeyword(int position) {
        FilterKeyword keyword = mFilterKeywords.get(position);
        keyword.setEnabled(!keyword.isEnabled());
        commit();
    }

    @Override
    public void addKeyword(FilterKeyword keyword) {
        mFilterKeywords.add(keyword);
        commit();
    }

    @Override
    public List<FilterKeyword> getKeywords() {
        return mFilterKeywords;
    }

    @Override
    public void removeKeyword(int index) {
        mFilterKeywords.remove(index);
        commit();
    }

    private void commit() {
        mPrefs.edit()
                .putString(PreferenceKey.FILTER_KEYWORDS_LIST, JSON.toJSONString(mFilterKeywords))
                .apply();
    }
}
