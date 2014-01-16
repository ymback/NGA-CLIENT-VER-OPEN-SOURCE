package gov.anzong.androidnga.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListSample extends FragmentActivity implements MenuAdapter.MenuListener {

    private static final String STATE_ACTIVE_POSITION =
            "net.simonvt.menudrawer.samples.LeftDrawerSample.activePosition";

    protected MenuDrawer mMenuDrawer;

    protected MenuAdapter mAdapter;
    protected ListView mList;

    private int mActivePosition = 0;
    
    List<Object> items = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (inState != null) {
            mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
        }

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());

        items.add(new Category("赞美片总"));
        items.add(new Item("登陆账号", R.drawable.ic_login));
        items.add(new Item("yoooo", R.drawable.ic_menu_mylocation));
        //items.add(new Item("最近访问", R.drawable.ic_action_select_all_dark));
        items.add(new Category("分类论坛"));
        items.add(new Item("综合讨论", R.drawable.ic_action_select_all_dark));
        items.add(new Item("大漩涡系列", R.drawable.ic_action_select_all_dark));
        items.add(new Item("职业讨论区", R.drawable.ic_action_select_all_dark));
        items.add(new Item("冒险心得", R.drawable.ic_action_select_all_dark));
        items.add(new Item("麦迪文之塔", R.drawable.ic_action_select_all_dark));
        items.add(new Item("系统软硬件讨论", R.drawable.ic_action_select_all_dark));
        items.add(new Item("其他游戏", R.drawable.ic_action_select_all_dark));
        items.add(new Item("暗黑破坏神", R.drawable.ic_action_select_all_dark));
        items.add(new Item("炉石传说", R.drawable.ic_action_select_all_dark));
        items.add(new Item("英雄联盟", R.drawable.ic_action_select_all_dark));
        items.add(new Item("个人版面", R.drawable.ic_action_select_all_dark));
        items.add(new Category("设置"));
        items.add(new Item("程序设置", R.drawable.action_settings));
        items.add(new Item("添加版面", R.drawable.ic_action_add_to_queue));
        items.add(new Item("关于", R.drawable.ic_action_supertext));

        mList = new ListView(this);

        mAdapter = new MenuAdapter(this, items);
        mAdapter.setListener(this);
        mAdapter.setActivePosition(mActivePosition);
        
        //cacheColorHint
        mList.setCacheColorHint(0x00000000);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mItemClickListener);

        mMenuDrawer.setMenuView(mList);
    }
    
    public void setLocItem(int loc, String itemname){
    	//set item on loc position
    	items.add(loc,new Item(itemname, R.drawable.ic_action_select_all_dark));
    	//reset menu
    	mAdapter = new MenuAdapter(this, items);
    	mList.setCacheColorHint(0x00000000);
    	mList.setAdapter(mAdapter);
    	mMenuDrawer.setMenuView(mList);
    }

    protected abstract void onMenuItemClicked(int position, Item item);

    protected abstract int getDragMode();

    protected abstract Position getDrawerPosition();

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mActivePosition = position;
            mMenuDrawer.setActiveView(view, position);
            mAdapter.setActivePosition(position);
            onMenuItemClicked(position, (Item) mAdapter.getItem(position));
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
    }

    @Override
    public void onActiveViewChanged(View v) {
        mMenuDrawer.setActiveView(v, mActivePosition);
    }
}
