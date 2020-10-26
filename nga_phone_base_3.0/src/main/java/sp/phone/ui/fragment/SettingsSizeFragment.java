package sp.phone.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.zhouyou.view.seekbar.SignSeekBar;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.widget.SeekBarEx;
import gov.anzong.androidnga.common.util.FileUtils;
import sp.phone.common.Constants;
import sp.phone.common.PhoneConfiguration;

public class SettingsSizeFragment extends BaseFragment implements  SignSeekBar.OnProgressChangedListener {

    private PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    private WebView mWebView;

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
        int max = 100;
        int min = 1;
        int size = mConfiguration.getWebViewTextZoom();
        seekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(size)
                .sectionCount(max - min)
                .build();
        seekBar.setOnProgressChangedListener(this);

        mWebView = rootView.findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/html/adjust_size.html");
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
        switch (signSeekBar.getId()) {
            case R.id.seek_web_size:
                mWebView.getSettings().setTextZoom(progress);
                break;
            default:
                break;
        }
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
                mConfiguration.setWebViewTextZoom(progress);
                break;
            default:
                break;
        }
    }

    @Override
    public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {


    }
}
