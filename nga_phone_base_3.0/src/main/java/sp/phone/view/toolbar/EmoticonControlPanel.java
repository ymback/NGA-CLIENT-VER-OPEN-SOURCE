package sp.phone.view.toolbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import gov.anzong.androidnga.R;
import sp.phone.adapter.EmoticonParentAdapter;

public class EmoticonControlPanel extends LinearLayout {

    public EmoticonControlPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize(int totalHeight) {
        ViewPager emoticonViewPager = findViewById(R.id.bottom_emoticon);
        int height = totalHeight - getResources().getDimensionPixelSize(R.dimen.bottom_emoticon_tab_height);
        emoticonViewPager.getLayoutParams().height = height;
        emoticonViewPager.setAdapter(new EmoticonParentAdapter(getContext(), height));
        TabLayout tabLayout = findViewById(R.id.bottom_emoticon_tab);
        tabLayout.setupWithViewPager(emoticonViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
}