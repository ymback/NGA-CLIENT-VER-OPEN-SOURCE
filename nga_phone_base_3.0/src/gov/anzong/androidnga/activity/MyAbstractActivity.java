package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;

public abstract class MyAbstractActivity extends Activity {


    protected View view;

    abstract protected int getLayoutId();

    abstract protected int getOptionMenuId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.SetContextTheme(this);
        view = LayoutInflater.from(this).inflate(getLayoutId(), null);
        this.setContentView(view);
        updateThemeUI();
    }


    protected void updateThemeUI() {
        view.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.getOptionMenuId() != 0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(this.getOptionMenuId(), menu);
        }


        int actionNum = ThemeManager.ACTION_IF_ROOM;//SHOW_AS_ACTION_IF_ROOM
        int i = 0;
        for (i = 0; i < menu.size(); i++) {
            ReflectionUtil.setShowAsAction(
                    menu.getItem(i), actionNum);
        }

        //ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }

    protected View getLayoutView() {
        return view;
    }


}
