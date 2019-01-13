package sp.phone.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.fragment.dialog.AvatarDialogFragment;
import sp.phone.fragment.dialog.BaseDialogFragment;
import sp.phone.listener.OnClientClickListener;
import sp.phone.listener.OnProfileClickListener;
import sp.phone.listener.OnReplyClickListener;
import sp.phone.rxjava.RxUtils;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import sp.phone.util.DeviceUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.HtmlUtils;
import sp.phone.util.ImageUtils;
import sp.phone.view.webview.WebViewEx;

/**
 * 帖子详情列表Adapter
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

    private static final String DEVICE_TYPE_IOS = "ios";

    private static final String DEVICE_TYPE_ANDROID = "android";

    private static final String DEVICE_TYPE_WP = "wp";

    private Context mContext;

    private FragmentManager mFragmentManager;

    private ThreadData mData;

    private LayoutInflater mLayoutInflater;

    private ThemeManager mThemeManager = ThemeManager.getInstance();

    private View.OnClickListener mOnClientClickListener = new OnClientClickListener();

    private View.OnClickListener mOnReplyClickListener = new OnReplyClickListener();

    private View.OnClickListener mOnProfileClickListener = new OnProfileClickListener();

    private View.OnClickListener mOnAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ThreadRowInfo row = (ThreadRowInfo) view.getTag();
            if (row.getISANONYMOUS()) {
                ActivityUtils.showToast("这白痴匿名了,神马都看不到");
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("name", row.getAuthor());
                bundle.putString("url", FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar()));
                BaseDialogFragment.show(mFragmentManager, bundle, AvatarDialogFragment.class);
                //FunctionUtils.Create_Avatar_Dialog(row, view.getContext(), null);
            }
        }
    };

    private View.OnClickListener mMenuTogglerListener;

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_nickName)
        TextView nickNameTV;

        @BindView(R.id.wv_content)
        WebViewEx contentTV;

        @BindView(R.id.tv_floor)
        TextView floorTv;

        @BindView(R.id.tv_post_time)
        TextView postTimeTv;

        @BindView(R.id.iv_reply)
        ImageView replyBtn;

        @BindView(R.id.iv_avatar)
        ImageView avatarIv;

        @BindView(R.id.iv_client)
        ImageView clientIv;

        @BindView(R.id.tv_score)
        TextView scoreTv;

        @BindView(R.id.iv_more)
        ImageView menuIv;

        @BindView(R.id.fl_avatar)
        FrameLayout avatarPanel;

        @BindView(R.id.tv_detail)
        TextView detailTv;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArticleListAdapter(Context context, FragmentManager fm) {
        mContext = context;
        mFragmentManager = fm;
        if (HtmlUtils.hide == null) {
            HtmlUtils.initStaticStrings(mContext);
        }
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setData(ThreadData data) {
        mData = data;
    }

    public void setMenuTogglerListener(View.OnClickListener menuTogglerListener) {
        mMenuTogglerListener = menuTogglerListener;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.fragment_article_list_item, parent, false);
        ArticleViewHolder viewHolder = new ArticleViewHolder(view);
        ViewGroup.LayoutParams lp = viewHolder.avatarIv.getLayoutParams();
        lp.width = lp.height = PhoneConfiguration.getInstance().getAvatarWidth();
        viewHolder.contentTV.setLocalMode();
        RxUtils.clicks(viewHolder.nickNameTV, mOnProfileClickListener);
        RxUtils.clicks(viewHolder.replyBtn, mOnReplyClickListener);
        RxUtils.clicks(viewHolder.clientIv, mOnClientClickListener);
        RxUtils.clicks(viewHolder.menuIv, mMenuTogglerListener);
        RxUtils.clicks(viewHolder.avatarPanel, mOnAvatarClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int position) {

        final ThreadRowInfo row = mData.getRowList().get(position);

        if (row == null) {
            return;
        }

        if (!mThemeManager.isNightMode()) {
            holder.itemView.setBackgroundResource(mThemeManager.getBackgroundColor(position));
        }

        holder.replyBtn.setTag(row);
        holder.nickNameTV.setTag(row);
        holder.menuIv.setTag(row);
        holder.avatarPanel.setTag(row);

        onBindAvatarView(holder.avatarIv, row);
        onBindDeviceType(holder.clientIv, row);
        onBindWebView(holder.contentTV, row);

        int fgColor = mThemeManager.getAccentColor(mContext);
        FunctionUtils.handleNickName(row, fgColor, holder.nickNameTV, mContext);

        holder.floorTv.setText(MessageFormat.format("[{0} 楼]", String.valueOf(row.getLou())));
        holder.postTimeTv.setText(row.getPostdate());
        holder.scoreTv.setText(MessageFormat.format("顶 : {0}", row.getScore()));

        holder.detailTv.setText(String.format("级别：%s   威望：%s   发帖：%s", row.getMemberGroup(), row.getReputation(), row.getPostCount()));

    }

    private void onBindWebView(WebViewEx webView, ThreadRowInfo row) {
        String html = row.getFormattedHtmlData();
        webView.setTextSize(PhoneConfiguration.getInstance().getWebSize());
        webView.getWebViewClientEx().setImgUrls(row.getImageUrls());
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    private void onBindDeviceType(ImageView clientBtn, ThreadRowInfo row) {
        String deviceType = row.getFromClientModel();

        if (TextUtils.isEmpty(deviceType)) {
            clientBtn.setVisibility(View.GONE);
        } else {
            switch (deviceType) {
                case DEVICE_TYPE_IOS:
                    clientBtn.setImageResource(R.drawable.ic_apple_12dp);
                    break;
                case DEVICE_TYPE_WP:
                    clientBtn.setImageResource(R.drawable.ic_windows_12dp);
                    break;
                case DEVICE_TYPE_ANDROID:
                    clientBtn.setImageResource(R.drawable.ic_android_12dp);
                    break;
                default:
                    clientBtn.setImageResource(R.drawable.ic_smartphone_12dp);
                    break;
            }
            clientBtn.setTag(row);
            clientBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.getRowNum();
    }

    private void onBindAvatarView(ImageView avatarIv, ThreadRowInfo row) {
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar());
        final boolean downImg = DeviceUtils.isWifiConnected(mContext)
                || PhoneConfiguration.getInstance()
                .isDownAvatarNoWifi();

        ImageUtils.loadRoundCornerAvatar(avatarIv, avatarUrl, !downImg);
    }

}