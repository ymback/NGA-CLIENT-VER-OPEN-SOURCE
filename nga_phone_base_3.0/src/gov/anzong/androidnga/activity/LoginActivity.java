package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;

import gov.anzong.androidnga.R;
import sp.phone.forumoperation.LoginAction;
import sp.phone.fragment.material.LoginFragment;
import sp.phone.fragment.material.LoginWebFragment;
import sp.phone.utils.ActivityUtils;

@Route(path = ActivityUtils.PATH_LOGIN)
public class LoginActivity extends SwipeBackAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar((Toolbar) findViewById(R.id.toolbar));
        setupFragment();
    }

    private void setupFragment(){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(LoginFragment.class.getSimpleName());
        if (fragment == null){
            // 暂时使用网页版登录
            //fragment = new LoginDialogFragment();
            fragment = new LoginWebFragment();
            fm.beginTransaction().replace(R.id.container,fragment,LoginFragment.class.getSimpleName()).commit();
        }
    }


    private LoginAction getLoginAction(){
        LoginAction loginAction = new LoginAction();
        loginAction.setAction(getIntent().getStringExtra("action"));
        return loginAction;
    }

}
