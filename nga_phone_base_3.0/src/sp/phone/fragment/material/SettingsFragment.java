package sp.phone.fragment.material;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.common.ThemeManager;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    private static final String ALERT_DIALOG_TAG = "alertdialog";

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mapping(getPreferenceScreen());
        configPreference();
    }

    private void mapping(PreferenceGroup group){
        for (int i = 0 ; i < group.getPreferenceCount(); i++){
            Preference preference = group.getPreference(i);
            if (preference instanceof PreferenceGroup){
                mapping((PreferenceGroup) preference);
            } else {
                preference.setOnPreferenceChangeListener(this);
            }
        }
    }

    private void configPreference() {
        if (ThemeManager.getInstance().isNightMode()) {
            findPreference(PreferenceKey.MATERIAL_THEME).setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.menu_setting);
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            getActivity().setTitle(R.string.menu_setting);
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        switch (preference.getKey()){
            case PreferenceKey.DOWNLOAD_IMG_QUALITY_NO_WIFI:
                mConfiguration.imageQuality = Integer.parseInt((String) newValue);
                break;
            case PreferenceKey.DOWNLOAD_IMG_NO_WIFI:
                mConfiguration.setDownImgNoWifi((Boolean) newValue);
                break;
            case PreferenceKey.MATERIAL_MODE:
                mConfiguration.setMaterialMode((Boolean) newValue);
                break;
            case PreferenceKey.NIGHT_MODE:
                ThemeManager.getInstance().setMode((boolean)newValue ? ThemeManager.MODE_NIGHT : ThemeManager.MODE_NORMAL);
                getActivity().finish();
                startActivity(getActivity().getIntent());
                break;
            case PreferenceKey.DOWNLOAD_AVATAR_NO_WIFI:
                mConfiguration.downAvatarNoWifi = (boolean) newValue;
                break;
            case PreferenceKey.SHOW_COLORTXT:
                mConfiguration.showColortxt = (boolean) newValue;
                break;
            case PreferenceKey.REFRESH_AFTERPOST_SETTING_MODE:
                mConfiguration.refresh_after_post_setting_mode = (boolean) newValue;
                break;
            case PreferenceKey.SWIPEBACK:
                mConfiguration.swipeBack = (boolean) newValue;
                updateSwipeBackOption((Boolean) newValue);
                break;
            case PreferenceKey.SWIPEBACKPOSITION:
                mConfiguration.swipeenablePosition = Integer.parseInt((String) newValue);
                updateSwipeBackOption(true);
                break;
            case PreferenceKey.FULLSCREENMODE:
                mConfiguration.fullscreen = (boolean) newValue;
                setFullScreen((Boolean) newValue);
                break;
            case PreferenceKey.ENABLE_NOTIFIACTION:
                mConfiguration.notification = (boolean) newValue;
                break;
            case PreferenceKey.NOTIFIACTION_SOUND:
                mConfiguration.notificationSound = (boolean) newValue;
                break;
            case PreferenceKey.BLACKGUN_SOUND:
                mConfiguration.blackgunsound = Integer.parseInt((String) newValue);
                showBlackGunSound(mConfiguration.blackgunsound);
                break;
            case PreferenceKey.SHOW_SIGNATURE:
                mConfiguration.showSignature = (boolean) newValue;
                break;
            case PreferenceKey.SHOW_STATIC:
                mConfiguration.showStatic = (boolean) newValue;
                break;
            case PreferenceKey.SHOW_LAJIBANKUAI:
                mConfiguration.showLajibankuai = (boolean) newValue;
                break;
            case PreferenceKey.SHOW_REPLYBUTTON:
                mConfiguration.showReplyButton = (boolean) newValue;
                break;
            case PreferenceKey.SHOW_ANIMATION:
                mConfiguration.showAnimation = (boolean) newValue;
                break;
            case PreferenceKey.MATERIAL_THEME:
                sp.edit().putString(PreferenceKey.MATERIAL_THEME, (String) newValue).apply();
                ThemeManager.getInstance().setTheme(Integer.parseInt((String) newValue));
                getActivity().finish();
                startActivity(getActivity().getIntent());
                break;
            case PreferenceKey.BOTTOM_TAB:
                sp.edit().putBoolean(PreferenceKey.BOTTOM_TAB, (Boolean) newValue).apply();
                mConfiguration.setShowBottomTab((Boolean) newValue);
                break;
            case PreferenceKey.LEFT_HAND:
                sp.edit().putBoolean(preference.getKey(), (Boolean) newValue).apply();
                mConfiguration.setLeftHandMode((Boolean) newValue);
                break;

            case PreferenceKey.HARDWARE_ACCELERATED:
                sp.edit().putBoolean(preference.getKey(), (Boolean) newValue).apply();
                mConfiguration.setHardwareAcceleratedMode((Boolean) newValue);
                break;

            case PreferenceKey.SHOW_ICON_MODE:
                mConfiguration.iconmode = (boolean) newValue;
                getActivity().setResult(Activity.RESULT_OK);
                break ;
        }
        return true;
    }

    private void showBlackGunSound(int which){
        AudioManager audioManager = (AudioManager)mContext.getSystemService(
                        Context.AUDIO_SERVICE);
        AssetFileDescriptor afd;
        MediaPlayer mp = new MediaPlayer();
        switch (which) {
            case 0:
                Uri ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (ringToneUri != null
                        && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    try {
                        mp.reset();
                        mp.setDataSource(mContext, ringToneUri);
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                afd = getResources().openRawResourceFd(R.raw.taijun);
                if (afd != null
                        && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    try {
                        mp.reset();
                        mp.setDataSource(afd.getFileDescriptor(),
                                afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                afd = getResources().openRawResourceFd(
                        R.raw.balckgunoftaijun);
                if (afd != null
                        && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    try {
                        mp.reset();
                        mp.setDataSource(afd.getFileDescriptor(),
                                afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                afd = getResources().openRawResourceFd(
                        R.raw.balckgunofyou);
                if (afd != null
                        && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    try {
                        mp.reset();
                        mp.setDataSource(afd.getFileDescriptor(),
                                afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void setFullScreen(boolean fullScreen){
        int flag;
        if (fullScreen){
            flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getActivity().getWindow().addFlags(flag);
    }

    private void updateSwipeBackOption(boolean isChecked){
        if (isChecked) {
            final float density = getResources().getDisplayMetrics().density;// 获取屏幕密度PPI
            ((SwipeBackAppCompatActivity)getActivity()).getSwipeBackLayout().setEdgeSize(
                    (int) (SwipeBackAppCompatActivity.MY_EDGE_SIZE * density + 0.5f));// 10dp
            int pos;
            switch (PhoneConfiguration.getInstance().swipeenablePosition) {
                case 0:
                    pos = SwipeBackLayout.EDGE_LEFT;
                    break;
                case 1:
                    pos = SwipeBackLayout.EDGE_RIGHT;
                    break;
                case 2:
                    pos = SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT;
                    break;
                default:
                    pos = SwipeBackLayout.EDGE_ALL;
                    break;
            }
            ((SwipeBackAppCompatActivity)getActivity()).getSwipeBackLayout().setEdgeTrackingEnabled(pos);
        } else {
            ((SwipeBackAppCompatActivity)getActivity()).getSwipeBackLayout().setEdgeSize(0);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()){
            case PreferenceKey.DOWNLOAD_IMG_QUALITY_NO_WIFI:
                Toast.makeText(mContext,R.string.image_quality_claim,Toast.LENGTH_SHORT).show();
                break;
            case PhoneConfiguration.ADJUST_SIZE:
                FragmentManager fm = getActivity().getFragmentManager();
                fm.beginTransaction().hide(this).add(R.id.container,new SettingsSizeFragment()).addToBackStack(null).commit();
                break;

        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
