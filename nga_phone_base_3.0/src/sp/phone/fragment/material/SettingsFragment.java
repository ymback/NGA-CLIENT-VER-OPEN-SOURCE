package sp.phone.fragment.material;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SettingsActivity;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.PreferenceConstant;
import sp.phone.fragment.AlertDialogFragment;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

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
        switch (preference.getKey()){
            case PreferenceConstant.DOWNLOAD_IMG_QUALITY_NO_WIFI:
                mConfiguration.imageQuality = Integer.parseInt((String) newValue);
                break;
            case PreferenceConstant.DOWNLOAD_IMG_NO_WIFI:
                mConfiguration.setDownImgNoWifi((Boolean) newValue);
                break;
            case PreferenceConstant.MATERIAL_MODE:
                mConfiguration.setMaterialMode((Boolean) newValue);
                break;
            case PreferenceConstant.NIGHT_MODE:
                ThemeManager.getInstance().setMode((boolean)newValue ? ThemeManager.MODE_NIGHT : ThemeManager.MODE_NORMAL);
                getActivity().finish();
                startActivity(getActivity().getIntent());
                break;
            case PreferenceConstant.DOWNLOAD_AVATAR_NO_WIFI:
                mConfiguration.downAvatarNoWifi = (boolean) newValue;
                break;
            case PreferenceConstant.SHOW_COLORTXT:
                mConfiguration.showColortxt = (boolean) newValue;
                break;
            case PreferenceConstant.REFRESH_AFTERPOST_SETTING_MODE:
                mConfiguration.refresh_after_post_setting_mode = (boolean) newValue;
                break;
            case PreferenceConstant.SWIPEBACK:
                mConfiguration.swipeBack = (boolean) newValue;
                updateSwipeBackOption((Boolean) newValue);
                break;
            case PreferenceConstant.SWIPEBACKPOSITION:
                mConfiguration.swipeenablePosition = Integer.parseInt((String) newValue);
                updateSwipeBackOption(true);
                break;
            case PreferenceConstant.FULLSCREENMODE:
                mConfiguration.fullscreen = (boolean) newValue;
                setFullScreen((Boolean) newValue);
                break;
            case PreferenceConstant.ENABLE_NOTIFIACTION:
                mConfiguration.notification = (boolean) newValue;
                break;
            case PreferenceConstant.NOTIFIACTION_SOUND:
                mConfiguration.notificationSound = (boolean) newValue;
                break;
            case PreferenceConstant.BLACKGUN_SOUND:
                mConfiguration.blackgunsound = Integer.parseInt((String) newValue);
                showBlackGunSound(mConfiguration.blackgunsound);
                break;
            case PreferenceConstant.SHOW_SIGNATURE:
                mConfiguration.showSignature = (boolean) newValue;
                break;
            case PreferenceConstant.SHOW_STATIC:
                mConfiguration.showStatic = (boolean) newValue;
                break;
            case PreferenceConstant.SHOW_LAJIBANKUAI:
                mConfiguration.showLajibankuai = (boolean) newValue;
                break;
            case PreferenceConstant.SHOW_REPLYBUTTON:
                mConfiguration.showReplyButton = (boolean) newValue;
                break;
            case PreferenceConstant.SHOW_ANIMATION:
                mConfiguration.showAnimation = (boolean) newValue;
                break;

        }
        return true;
    }

    private void showIconModeAlertDialog(final SwitchPreference preference, final boolean isChecked){
        String alertString = getString(R.string.change_icon_string);
        final AlertDialogFragment f = AlertDialogFragment
                .create(alertString);
        f.setOkListener(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mConfiguration.iconmode = isChecked;
                SharedPreferences share = mContext.getSharedPreferences(
                        PreferenceConstant.PERFERENCE, Context.MODE_PRIVATE);
                String addFidStr = share.getString(PreferenceConstant.ADD_FID, "");
                List<Board> addFidList;
                BoardCategory addFid = new BoardCategory();
                int iconInt;
                if (isChecked) {
                    iconInt = R.drawable.oldpdefault;
                } else {
                    iconInt = R.drawable.pdefault;
                }
                if (!StringUtil.isEmpty(addFidStr)) {
                    addFidList = JSON
                            .parseArray(addFidStr, Board.class);
                    if (addFidList != null) {
                        int i = 11;// 新增大板块后此处+1
                        for (int j = 0; j < addFidList.size(); j++) {
                            addFid.add(new Board(i, addFidList.get(j)
                                    .getUrl(), addFidList.get(j)
                                    .getName(), iconInt));
                        }
                        addFidStr = JSON.toJSONString(addFid
                                .getBoardList());
                    }
                }
                SharedPreferences.Editor editor = share.edit();
                editor.putBoolean(PreferenceConstant.SHOW_ICON_MODE, isChecked);
                editor.putString(PreferenceConstant.RECENT_BOARD, "");
                editor.putString(PreferenceConstant.ADD_FID, addFidStr);
                editor.apply();
                getActivity().setResult(Activity.RESULT_OK);

            }

        });
        f.setCancleListener(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                preference.setChecked(!isChecked);
                f.dismiss();
            }

        });
        f.show(((SettingsActivity)getActivity()).getSupportFragmentManager(), ALERT_DIALOG_TAG);
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
            case PreferenceConstant.DOWNLOAD_IMG_QUALITY_NO_WIFI:
                Toast.makeText(mContext,R.string.image_quality_claim,Toast.LENGTH_SHORT).show();
                break;
            case PreferenceConstant.SHOW_ICON_MODE:
                showIconModeAlertDialog((SwitchPreference) preference,preference.getSharedPreferences().getBoolean(preference.getKey(),false));
                break;
            case PhoneConfiguration.ADJUST_SIZE:
                FragmentManager fm = getActivity().getFragmentManager();
                fm.beginTransaction().hide(this).add(R.id.container,new SettingsSizeFragment()).addToBackStack(null).commit();
                break;

        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
