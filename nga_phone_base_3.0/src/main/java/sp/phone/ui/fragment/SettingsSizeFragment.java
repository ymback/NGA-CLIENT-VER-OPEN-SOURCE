package sp.phone.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhouyou.view.seekbar.SignSeekBar;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.widget.SeekBarEx;
import sp.phone.common.Constants;
import sp.phone.common.PhoneConfiguration;

public class SettingsSizeFragment extends BaseFragment implements  SignSeekBar.OnProgressChangedListener {

    private PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_size, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.setting_title_size);
        super.onResume();
    }

    private void initView(View rootView) {
        initFontSizeView(rootView);
        initAvatarSizeView(rootView);
        initWebFontSizeView(rootView);
        initEmotionSizeView(rootView);

    }

    private void initFontSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_topic_title);
        seekBar.getConfigBuilder()
                .max(Constants.TOPIC_TITLE_SIZE_MAX)
                .min(Constants.TOPIC_TITLE_SIZE_MIN)
                .progress(mConfiguration.getTopicTitleSize())
                .sectionCount(Constants.TOPIC_TITLE_SIZE_MAX - Constants.TOPIC_TITLE_SIZE_MIN)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    private void initWebFontSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_web_size);
        int max = Constants.TOPIC_CONTENT_SIZE_MAX;
        int min = Constants.TOPIC_CONTENT_SIZE_MIN;
        seekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(mConfiguration.getTopicContentSize())
                .sectionCount(max - min)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    private void initAvatarSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_avatar);
        seekBar.getConfigBuilder()
                .max(Constants.AVATAR_SIZE_MAX)
                .min(Constants.AVATAR_SIZE_MIN)
                .progress(mConfiguration.getAvatarSize())
                .sectionCount(Constants.AVATAR_SIZE_MAX - Constants.AVATAR_SIZE_MIN)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    private void initEmotionSizeView(View rootView) {
        SeekBarEx seekBar = rootView.findViewById(R.id.seek_emoticon);
        int max = Constants.EMOTICON_SIZE_MAX;
        int min = Constants.EMOTICON_SIZE_MIN;
        seekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(mConfiguration.getEmoticonSize())
                .sectionCount(max - min)
                .build();
        seekBar.setOnProgressChangedListener(this);
    }

    @Override
    public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {

    }

    @Override
    public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
        switch (signSeekBar.getId()) {
            case R.id.seek_topic_title:
                mConfiguration.setTopicTitleSize(progress);
                break;
            case R.id.seek_avatar:
                mConfiguration.setAvatarSize(progress);
                break;
            case R.id.seek_emoticon:
                mConfiguration.setEmoticonSize(progress);
                break;
            case R.id.seek_web_size:
                mConfiguration.setTopicContentSize(progress);
                break;
            default:
                break;
        }
    }

    @Override
    public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {


    }
}
