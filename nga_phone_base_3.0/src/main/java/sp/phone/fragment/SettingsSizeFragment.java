package sp.phone.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ImageUtils;

public class SettingsSizeFragment extends PreferenceFragment implements SeekBar.OnSeekBarChangeListener {

    private PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    private WebView mWebSizeView;

    private TextView mFontSizeView;

    private ImageView mAvatarSizeView;

    private ImageView mEmotionSizeView;

    private float mDefaultFontSize;

    private int mDefaultWebFontSize;

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        mFontSizeView = (TextView) rootView.findViewById(R.id.textView_font_size);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.fontsize_seekBar);
        mDefaultFontSize = mFontSizeView.getTextSize() / getResources().getDisplayMetrics().density;
        final float textSize = mConfiguration.getTopicTitleSize();
        int progress = (int) (100.0f * textSize / mDefaultFontSize);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
        mFontSizeView.setTextSize(textSize);
    }

    private void initWebFontSizeView(View rootView) {
        mWebSizeView = (WebView) rootView.findViewById(R.id.websize_view);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.webszie_bar);
        mDefaultWebFontSize = mWebSizeView.getSettings().getDefaultFontSize();
        final int webSize = mConfiguration.getWebSize();
        int progress = 100 * webSize / mDefaultWebFontSize;
        seekBar.setProgress(progress);
        mWebSizeView.getSettings().setDefaultFontSize(webSize);
        mWebSizeView.setBackgroundColor(Color.TRANSPARENT);
        seekBar.setOnSeekBarChangeListener(this);

        if (ThemeManager.getInstance().isNightMode()) {
            mWebSizeView.loadDataWithBaseURL(null,
                    "<font style='color:#FFFFFF;'>"
                            + getString(R.string.websize_sample_text)
                            + "</font>", "text/html", "utf-8", "");
        } else {
            mWebSizeView.loadDataWithBaseURL(null,
                    "<font style='color:#FF21201B;'>"
                            + getString(R.string.websize_sample_text)
                            + "</font>", "text/html", "utf-8", "");
        }
    }

    private void initAvatarSizeView(View rootView) {
        mAvatarSizeView = (ImageView) rootView.findViewById(R.id.avatarsize);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.avatarsize_seekBar);
        int progress = mConfiguration.getAvatarWidth();
        Drawable defaultAvatar = ContextCompat.getDrawable(mContext, R.drawable.default_avatar);
        Bitmap bitmap = ImageUtils.zoomImageByWidth(defaultAvatar, progress, false);
        mAvatarSizeView.setImageBitmap(bitmap);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void initEmotionSizeView(View rootView) {
        mEmotionSizeView = (ImageView) rootView.findViewById(R.id.emotionsize);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.emotionsize_seekBar);
        int progress = mConfiguration.getEmotionWidth();
        Drawable defaultEmotion = ContextCompat.getDrawable(mContext, R.drawable.acniang_large);
        Bitmap bitmap = ImageUtils.zoomImageByWidth(defaultEmotion, progress, true);
        mEmotionSizeView.setImageBitmap(bitmap);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Bitmap bitmap;
        switch (seekBar.getId()) {
            case R.id.fontsize_seekBar:
                if (progress != 0) {
                    mFontSizeView.setTextSize(mDefaultFontSize * progress / 100.0f);
                }
                break;
            case R.id.webszie_bar:
                if (progress != 0) {
                    mWebSizeView.getSettings().setDefaultFontSize(
                            (int) (mDefaultWebFontSize * progress / 100.0f));
                }
                break;
            case R.id.avatarsize_seekBar:
                if (2 > progress) {
                    progress = 2;
                }
                Drawable defaultAvatar = ContextCompat.getDrawable(mContext, R.drawable.default_avatar);
                bitmap = ImageUtils.zoomImageByWidth(defaultAvatar, progress, false);
                try {
                    ImageUtils.recycleImageView(mAvatarSizeView);
                    mAvatarSizeView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.emotionsize_seekBar:
                if (2 > progress) {
                    progress = 2;
                }
                Drawable defaultEmotion = ContextCompat.getDrawable(mContext, R.drawable.acniang_large);
                bitmap = ImageUtils.zoomImageByWidth(defaultEmotion, progress, true);
                try {
                    ImageUtils.recycleImageView(mEmotionSizeView);
                    mEmotionSizeView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress;
        SharedPreferences share = mContext.getSharedPreferences(PreferenceKey.PERFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        switch (seekBar.getId()) {
            case R.id.fontsize_seekBar:
                float textSize = mDefaultFontSize * seekBar.getProgress() / 100.0f;
                editor.putFloat(PreferenceKey.TEXT_SIZE, textSize);
                editor.apply();
                break;
            case R.id.webszie_bar:
                int webSize = (int) (mDefaultWebFontSize * seekBar.getProgress() / 100.0f);
                editor.putInt(PreferenceKey.WEB_SIZE, webSize);
                editor.apply();
                break;
            case R.id.avatarsize_seekBar:
                progress = seekBar.getProgress();
                if (2 > progress) {
                    progress = 2;
                }
                editor.putInt(PreferenceKey.NICK_WIDTH, progress);
                editor.apply();
                break;
            case R.id.emotionsize_seekBar:
                progress = seekBar.getProgress();
                if (2 > progress) {
                    progress = 2;
                }
                editor.putInt(PreferenceKey.EMO_WIDTH, progress);
                editor.apply();
                break;
        }
    }
}
