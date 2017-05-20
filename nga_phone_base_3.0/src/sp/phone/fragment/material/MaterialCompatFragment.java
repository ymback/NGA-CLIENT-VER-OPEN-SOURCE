package sp.phone.fragment.material;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import gov.anzong.androidnga.R;
import sp.phone.fragment.BaseFragment;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ThemeManager;


public class MaterialCompatFragment extends BaseFragment {

    protected PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        updateThemeUi();
        setFullScreen();
        super.onCreate(savedInstanceState);
    }

    protected void updateThemeUi(){
        if (ThemeManager.getInstance().isNightMode()){
            getActivity().setTheme(R.style.MaterialThemeDark);
        } else {
            getActivity().setTheme(R.style.MaterialTheme);
        }
    }

    protected void setFullScreen(){
        int flag;
        if (mConfiguration.fullscreen){
            flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        } else {
            flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getActivity().getWindow().addFlags(flag);
    }
}
