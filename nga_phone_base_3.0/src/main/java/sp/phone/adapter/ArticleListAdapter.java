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

    private View.OnClickListener mOnClientClickListener = new OnClientClickListener();

    private View.OnClickListener mOnReplyClickListener = new OnReplyClickListener();

    private View.OnClickListener mOnProfileClickListener = new OnProfileClickListener();

    private View.OnClickListener mOnAvatarClickListener = new OnAvatarClickListener();

    private View.OnClickListener mMenuTogglerListener;

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        TextView nickNameTV;
        WebViewEx contentTV;
        TextView floorTV;
        TextView postTimeTV;
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
            initHolder(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void initHolder(final View view) {
            nickNameTV = view.findViewById(R.id.nickName);
            floorTV = view.findViewById(R.id.floor);
            postTimeTV = view.findViewById(R.id.postTime);
            contentTV = view.findViewById(R.id.content);
            contentTV.setHorizontalScrollBarEnabled(false);
            replyBtn = view.findViewById(R.id.iv_reply);
        }
    }

    public ArticleListAdapter(Context context) {
        mContext = context;
        if (HtmlUtils.userDistance == null) {
            HtmlUtils.initStaticStrings(mContext);
        }
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    public ThreadData getData() {
        return mData;
    }

    public void setData(ThreadData data) {
        mData = data;
    }

    public void setMenuTogglerListener(View.OnClickListener menuTogglerListener) {
        mMenuTogglerListener = menuTogglerListener;
    }

    public Object getItem(int position) {
        return mData == null ? null : mData.getRowList().get(position);
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
        RxUtils.clicks(viewHolder.menuIv , mMenuTogglerListener);
        RxUtils.clicks(viewHolder.avatarPanel,mOnAvatarClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int position) {

        final ThreadRowInfo row = mData.getRowList().get(position);

        if (row == null) {
            return;
        }

        int lou = row.getLou();
        ThemeManager theme = ThemeManager.getInstance();
        int colorId = theme.getBackgroundColor(position);

        holder.itemView.setBackgroundResource(colorId);


        holder.replyBtn.setTag(row);
        holder.nickNameTV.setTag(row);
        holder.menuIv.setTag(row);
        holder.avatarPanel.setTag(row);

        onBindAvatarView(holder.avatarIv, row);

        int fgColorId = ThemeManager.getInstance().getForegroundColor();
        final int fgColor = ContextCompat.getColor(mContext, fgColorId);

        FunctionUtils.handleNickName(row, fgColor, holder.nickNameTV, mContext);


        final String floor = String.valueOf(lou);
        TextView floorTV = holder.floorTV;
        floorTV.setText(MessageFormat.format("[{0} 楼]", floor));
        floorTV.setTextColor(fgColor);

        onBindDeviceType(holder.clientIv, row);

        onBindWebView(holder.contentTV, row);

        TextView postTimeTV = holder.postTimeTV;
        postTimeTV.setText(row.getPostdate());
        postTimeTV.setTextColor(fgColor);
        holder.scoreTv.setText(MessageFormat.format("顶 : {0}", row.getScore()));
        holder.scoreTv.setTextColor(fgColor);


        holder.contentTV.setTag(position);
        holder.itemView.setTag(position);
    }

    private void onBindWebView(WebViewEx webView, ThreadRowInfo row) {
        String html = row.getFormated_html_data();
        webView.setTextSize(PhoneConfiguration.getInstance().getWebSize());
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    private void onBindDeviceType(ImageView clientBtn, ThreadRowInfo row) {
        String deviceType = row.getFromClientModel();

        if (TextUtils.isEmpty(deviceType)) {
            clientBtn.setVisibility(View.GONE);
        } else {
            switch (deviceType) {
                case DEVICE_TYPE_IOS:
                    clientBtn.setImageResource(R.drawable.ios);// IOS
                    break;
                case DEVICE_TYPE_WP:
                    clientBtn.setImageResource(R.drawable.wp);// WP
                    break;
                case DEVICE_TYPE_ANDROID:
                    clientBtn.setImageResource(R.drawable.ic_android_12dp);
                    break;
                default:
                    clientBtn.setImageResource(R.drawable.unkonwn);// 未知orBB
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
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar());//
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