package sp.phone.fragment.material;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.common.ThemeManager;
import sp.phone.fragment.BaseFragment;
import sp.phone.utils.ImageUtil;

public class SettingsSizeFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    private PhoneConfiguration mConfiguration = PhoneConfiguration.getInstance();

    private WebView mWebSizeView;

    private TextView mFontSizeView;

    private ImageView mAvatarSizeView;

    private float mDefaultFontSize;

    private int mDefaultWebFontSize;

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.setting_title_size);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable FrameLayout container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings_size, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        initFontSizeView(rootView);
        initAvatarSizeView(rootView);
        initWebFontSizeView(rootView);
    }

    private void initFontSizeView(View rootView) {
        mFontSizeView = rootView.findViewById(R.id.textView_font_size);
        SeekBar seekBar = rootView.findViewById(R.id.fontsize_seekBar);
        mDefaultFontSize = mFontSizeView.getTextSize();
        final float textSize = mConfiguration.getInt(PreferenceKey.TEXT_SIZE);
        int progress = (int) (100.0f * textSize / mDefaultFontSize);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
        mFontSizeView.setTextSize(textSize);
    }

    private void initWebFontSizeView(View rootView) {
        mWebSizeView = rootView.findViewById(R.id.websize_view);
        SeekBar seekBar = rootView.findViewById(R.id.webszie_bar);
        mDefaultWebFontSize = mWebSizeView.getSettings().getDefaultFontSize();
        final int webSize = mConfiguration.getInt(PreferenceKey.WEB_SIZE);
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
        mAvatarSizeView = rootView.findViewById(R.id.avatarsize);
        SeekBar seekBar = rootView.findViewById(R.id.avatarsize_seekBar);
        int progress = mConfiguration.getInt(PreferenceKey.NICK_WIDTH);
        Drawable defaultAvatar = ContextCompat.getDrawable(mContext, R.drawable.default_avatar);
        Bitmap bitmap = ImageUtil.zoomImageByWidth(defaultAvatar, progress);
        mAvatarSizeView.setImageBitmap(bitmap);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(this);
    }


    @Deprecated
    private LayoutInflater getThemeInflater(LayoutInflater inflater) {
        int themeStyle = R.style.AppThemeDayNight;
        final Context contextThemeWrapper = new ContextThemeWrapper(mContext, themeStyle);
        // clone the inflater using the ContextThemeWrapper
        return inflater.cloneInContext(contextThemeWrapper);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
                Bitmap bitmap = ImageUtil.zoomImageByWidth(defaultAvatar, progress);
                try {
                    ImageUtil.recycleImageView(mAvatarSizeView);
                    mAvatarSizeView.setImageBitmap(bitmap);
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        switch (seekBar.getId()) {
            case R.id.fontsize_seekBar:
                int textSize = (int) (mDefaultFontSize * seekBar.getProgress() / 100.0f);
                editor.putInt(PreferenceKey.TEXT_SIZE, textSize)
                        .apply();
                mConfiguration.putData(PreferenceKey.TEXT_SIZE, textSize);
                break;
            case R.id.webszie_bar:
                int webSize = (int) (mDefaultWebFontSize * seekBar.getProgress() / 100.0f);
                editor.putInt(PreferenceKey.WEB_SIZE, webSize)
                        .apply();
                mConfiguration.putData(PreferenceKey.WEB_SIZE, webSize);
                break;
            case R.id.avatarsize_seekBar:
                int progress = seekBar.getProgress();
                if (2 > progress) {
                    progress = 2;
                }
                editor.putInt(PreferenceKey.NICK_WIDTH, progress)
                        .apply();

                mConfiguration.putData(PreferenceKey.NICK_WIDTH, progress);
                break;
        }
    }
}
