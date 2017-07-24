package sp.phone.adapter.material;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;

import gov.anzong.androidnga.R;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.listener.ClientListener;
import sp.phone.listener.MyListenerForReply;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.DeviceUtils;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.HtmlUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.StringUtil;

/**
 * 帖子详情列表Adapter
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> implements
        AvatarLoadCompleteCallBack {

    private static final String TAG = ArticleListAdapter.class.getSimpleName();

    private Context mContext;

    private final WebViewClient mWebViewClient;

    private final Object lock = new Object();

    private final HashSet<String> mUrlSet = new HashSet<>();

    private ThreadData mData;

    private Bitmap mDefaultAvatar;

    private LayoutInflater mLayoutInflater;

    private AdapterView.OnItemLongClickListener mItemLongClickListener;

    private int mSelectedItem;

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout articlelistrelativelayout;
        TextView nickNameTV;
        ImageView avatarIV;
        WebView contentTV;
        TextView floorTV;
        TextView postTimeTV;
        TextView levelTV;
        TextView aurvrcTV;
        TextView postnumTV;
        int position = -1;
        ImageButton viewBtn;
        ImageButton clientBtn;
        TextView scoreTV;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            initHolder(itemView);
        }

        private void initHolder(final View view) {
            articlelistrelativelayout = (RelativeLayout) view.findViewById(R.id.articlelistrelativelayout);
            nickNameTV = (TextView) view.findViewById(R.id.nickName);
            avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
            floorTV = (TextView) view.findViewById(R.id.floor);
            postTimeTV = (TextView) view.findViewById(R.id.postTime);
            contentTV = (WebView) view.findViewById(R.id.content);
            contentTV.setHorizontalScrollBarEnabled(false);
            viewBtn = (ImageButton) view.findViewById(R.id.listviewreplybtn);
            clientBtn = (ImageButton) view.findViewById(R.id.clientbutton);
            scoreTV = (TextView) view.findViewById(R.id.score);
        }
    }

    public ArticleListAdapter(Context context) {
        super();
        mContext = context;
        if (HtmlUtil.userDistance == null) {
            HtmlUtil.initStaticStrings(mContext);
        }
        mWebViewClient = new ArticleListWebClient(context);
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    public ThreadData getData() {
        return mData;
    }

    public void setData(ThreadData data) {
        mData = data;
    }

    public void setSelectedItem(int position) {
        mSelectedItem = position;
    }

    public int getSelectedItem() {
        return mSelectedItem;
    }

    public Object getItem(int position) {
        return mData == null ? null : mData.getRowList().get(position);
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.fragment_article_list_item,parent,false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int position) {

        final ThreadRowInfo row = mData.getRowList().get(position);

        int lou = -1;
        if (row != null)
            lou = row.getLou();
        if (!PhoneConfiguration.getInstance().showReplyButton) {
            holder.viewBtn.setVisibility(View.GONE);
        } else {
            MyListenerForReply myListenerForReply = new MyListenerForReply(position, mData, mContext);
            holder.viewBtn.setOnClickListener(myListenerForReply);
        }
        ThemeManager theme = ThemeManager.getInstance();
        int colorId = theme.getBackgroundColor(position);
        holder.itemView.setBackgroundResource(colorId);

        if (row == null) {
            return;
        }

        handleAvatar(holder.avatarIV, row);

        int fgColorId = ThemeManager.getInstance().getForegroundColor();
        final int fgColor = ContextCompat.getColor(mContext,fgColorId);

        FunctionUtil.handleNickName(row, fgColor, holder.nickNameTV, mContext);

        final int bgColor = ContextCompat.getColor(mContext,colorId);

        final WebView contentTV = holder.contentTV;

        final String floor = String.valueOf(lou);
        TextView floorTV = holder.floorTV;
        floorTV.setText("[" + floor + " 楼]");
        floorTV.setTextColor(fgColor);

        if (!StringUtil.isEmpty(row.getFromClientModel())) {
            ClientListener clientListener = new ClientListener(position, mData, mContext);
            String from_client_model = row.getFromClientModel();
            switch (from_client_model) {
                case "ios":
                    holder.clientBtn.setImageResource(R.drawable.ios);// IOS
                    break;
                case "wp":
                    holder.clientBtn.setImageResource(R.drawable.wp);// WP
                    break;
                case "unknown":
                    holder.clientBtn.setImageResource(R.drawable.unkonwn);// 未知orBB
                    break;
            }
            holder.clientBtn.setVisibility(View.VISIBLE);
            holder.clientBtn.setOnClickListener(clientListener);
        }
        FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor, mContext, null, mWebViewClient);
        TextView postTimeTV = holder.postTimeTV;
        postTimeTV.setText(row.getPostdate());
        postTimeTV.setTextColor(fgColor);
        holder.scoreTV.setText("顶: " + row.getScore() + "    踩: " + row.getScore_2());
        holder.scoreTV.setTextColor(fgColor);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return mItemLongClickListener != null && mItemLongClickListener.onItemLongClick(null,holder.itemView,position,getItemId(position));
            }
        });
        holder.contentTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(null,holder.itemView,position,getItemId(position));
                }
                return true;
            }
        });
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.getRowNum();
    }

    @SuppressWarnings("ResourceType")
    private void handleAvatar(ImageView avatarIV, ThreadRowInfo row) {
        final int lou = row.getLou();
        final String avatarUrl = FunctionUtil.parseAvatarUrl(row.getJs_escap_avatar());//
        final String userId = String.valueOf(row.getAuthorid());
        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
            avatarIV.setImageBitmap(null);
            return;
        }
        if (mDefaultAvatar == null || mDefaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
            Resources res = avatarIV.getContext().getResources();
            InputStream is = res.openRawResource(R.drawable.default_avatar);
            InputStream is2 = res.openRawResource(R.drawable.default_avatar);
            mDefaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
        }

        Object tagObj = avatarIV.getTag();
        if (tagObj instanceof AvatarTag) {
            AvatarTag origTag = (AvatarTag) tagObj;
            if (!origTag.isDefault) {
                ImageUtil.recycleImageView(avatarIV);
                // Log.d(TAG, "recycle avatar:" + origTag.lou);
            }
        }

        AvatarTag tag = new AvatarTag(lou, true);
        avatarIV.setImageBitmap(mDefaultAvatar);
        avatarIV.setTag(tag);
        if (!StringUtil.isEmpty(avatarUrl)) {
            final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
            if (avatarPath != null) {
                File f = new File(avatarPath);
                if (f.exists() && !isPending(avatarUrl)) {

                    Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
                    if (bitmap != null) {
                        avatarIV.setImageBitmap(bitmap);
                        tag.isDefault = false;
                    } else
                        f.delete();
                    long date = f.lastModified();
                    if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
                        f.delete();
                    }

                } else {
                    final boolean downImg = DeviceUtils.isWifiConnected(mContext)
                            || PhoneConfiguration.getInstance()
                            .isDownAvatarNoWifi();

                    new AvatarLoadTask(avatarIV, null, downImg, lou, this).execute(avatarUrl, avatarPath, userId);
                }
            }
        }

    }

    private boolean isPending(String url) {
        boolean ret;
        synchronized (lock) {
            ret = mUrlSet.contains(url);
        }
        return ret;
    }

    @Override
    public void OnAvatarLoadStart(String url) {
        synchronized (lock) {
            mUrlSet.add(url);
        }
    }

    @Override
    public void OnAvatarLoadComplete(String url) {
        synchronized (lock) {
            mUrlSet.remove(url);
        }
    }


}