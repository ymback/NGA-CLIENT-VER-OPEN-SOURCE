package sp.phone.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.GlideApp;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.listener.OnAvatarClickListener;
import sp.phone.listener.OnClientClickListener;
import sp.phone.listener.OnProfileClickListener;
import sp.phone.listener.OnReplyClickListener;
import sp.phone.rxjava.RxUtils;
import sp.phone.theme.ThemeManager;
import sp.phone.util.DeviceUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.HtmlUtils;
import sp.phone.util.PermissionUtils;
import sp.phone.view.webview.WebViewEx;

/**
 * 帖子详情列表Adapter
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

    private static final String DEVICE_TYPE_IOS = "ios";

    private static final String DEVICE_TYPE_ANDROID = "android";

    private static final String DEVICE_TYPE_WP = "wp";

    private Context mContext;

    private ThreadData mData;

    private LayoutInflater mLayoutInflater;

    private ThemeManager mThemeManager = ThemeManager.getInstance();

    private View.OnClickListener mOnClientClickListener = new OnClientClickListener();

    private View.OnClickListener mOnReplyClickListener = new OnReplyClickListener();

    private View.OnClickListener mOnProfileClickListener = new OnProfileClickListener();

    private View.OnClickListener mOnAvatarClickListener = new OnAvatarClickListener();

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

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArticleListAdapter(Context context) {
        mContext = context;
        if (HtmlUtils.userDistance == null) {
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

        int fgColor = ContextCompat.getColor(mContext, mThemeManager.getSecondTextColor());
        FunctionUtils.handleNickName(row,
                fgColor,// ContextCompat.getColor(mContext, mThemeManager.getForegroundColor()),
                holder.nickNameTV, mContext);

        holder.floorTv.setText(MessageFormat.format("[{0} 楼]", String.valueOf(row.getLou())));
        holder.floorTv.setTextColor(fgColor);
        holder.postTimeTv.setText(row.getPostdate());
        holder.postTimeTv.setTextColor(fgColor);
        holder.scoreTv.setText(MessageFormat.format("顶 : {0}", row.getScore()));
        holder.scoreTv.setTextColor(fgColor);

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
        if (!PermissionUtils.hasStoragePermission(mContext)) {
            avatarIv.setImageResource(R.drawable.default_avatar);
            return;
        }
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar());
        final boolean downImg = DeviceUtils.isWifiConnected(mContext)
                || PhoneConfiguration.getInstance()
                .isDownAvatarNoWifi();
        if (avatarUrl != null) {
            GlideApp.with(mContext)
                    .load(avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .onlyRetrieveFromCache(!downImg)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(avatarIv);
            UserManagerImpl.getInstance().setAvatarUrl(row.getAuthorid(), avatarUrl);
        }
    }

}