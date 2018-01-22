package sp.phone.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.LauncherSubActivity;
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
        getPreferenceManager().setSharedPreferencesName(PreferenceKey.PERFERENCE);
        addPreferencesFromResource(R.xml.settings);
        mapping(getPreferenceScreen());
        configPreference();
    }

    private void mapping(PreferenceGroup group) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            Preference preference = group.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                mapping((PreferenceGroup) preference);
            } else {
                preference.setOnPreferenceChangeListener(this);
//                if (preference instanceof ListPreference) {
//                    preference.setSummary(((ListPreference) preference).getEntry());
//                }
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
        if (!hidden) {
            getActivity().setTitle(R.string.menu_setting);
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (preference instanceof ListPreference) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }

        String key = preference.getKey();
        switch (key) {
            case PreferenceKey.DOWNLOAD_IMG_NO_WIFI:
                mConfiguration.setDownImgNoWifi((Boolean) newValue);
                break;
            case PreferenceKey.NIGHT_MODE:
                ThemeManager.getInstance().setMode((boolean) newValue ? ThemeManager.MODE_NIGHT : ThemeManager.MODE_NORMAL);
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
            case PreferenceKey.MATERIAL_THEME:
                sp.edit().putString(PreferenceKey.MATERIAL_THEME, (String) newValue).apply();
                ThemeManager.getInstance().setTheme(Integer.parseInt((String) newValue));
                mConfiguration.putData(key, Integer.parseInt((String) newValue));
                getActivity().finish();
                startActivity(getActivity().getIntent());
                break;
            case PreferenceKey.BOTTOM_TAB:
                sp.edit().putBoolean(PreferenceKey.BOTTOM_TAB, (Boolean) newValue).apply();
                mConfiguration.putData(key, (Boolean) newValue);
                break;
            case PreferenceKey.LEFT_HAND:
                sp.edit().putBoolean(preference.getKey(), (Boolean) newValue).apply();
                mConfiguration.putData(key, (Boolean) newValue);
                break;

            case PreferenceKey.HARDWARE_ACCELERATED:
                sp.edit().putBoolean(preference.getKey(), (Boolean) newValue).apply();
                mConfiguration.putData(key, (boolean) newValue);
                break;

            case PreferenceKey.SHOW_ICON_MODE:
                mConfiguration.iconmode = (boolean) newValue;
                getActivity().setResult(Activity.RESULT_OK);
                break;

            case PreferenceKey.SORT_BY_POST:
            case PreferenceKey.FILTER_SUB_BOARD:
                sp.edit().putBoolean(key, (Boolean) newValue).apply();
                mConfiguration.putData(key, (boolean) newValue);
                break;
        }
        return true;
    }

    private void showBlackGunSound(int which) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(
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

    private void setFullScreen(boolean fullScreen) {
        int flag;
        if (fullScreen) {
            flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getActivity().getWindow().addFlags(flag);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case PreferenceKey.ADJUST_SIZE:
            case PreferenceKey.PREF_USER:
            case PreferenceKey.PREF_BLACK_LIST:
                Intent intent = new Intent(getContext(), LauncherSubActivity.class);
                intent.putExtra("fragment", preference.getFragment());
                startActivity(intent);
                break;
            default:
                return super.onPreferenceTreeClick(preferenceScreen, preference);

        }
        return true;
    }

}
