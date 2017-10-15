package sp.phone.adapter.material;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.NetUtil;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.StringUtils;

/**
 * Created by Justwen on 2017/10/15.
 */

@SuppressWarnings("ResourceType")
public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.MessageViewHolder> implements AvatarLoadCompleteCallBack {

    private MessageDetailInfo mListInfo;

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private Bitmap mDefaultAvatar;

    protected int mCount = 0;

    private final Set<String> mUrlSet = new HashSet<>();

    private Object mLock = new Object();

    public MessageDetailAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(mLayoutInflater.inflate(R.layout.relative_messagedetaillist, parent,false));
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        handleJsonList(holder,position);
    }

    protected MessageArticlePageInfo getEntry(int position) {
        return mListInfo == null ? null : mListInfo.getMessageEntryList().get(position);
    }

    private void handleJsonList(MessageViewHolder holder, int position) {
        final MessageArticlePageInfo entry = getEntry(position);
        if (entry == null) {
            return;
        }
        Resources res = mContext.getResources();
        ThemeManager theme = ThemeManager.getInstance();
        holder.postTime.setText(entry.getTime());
        String floor = String.valueOf(entry.getLou());
        holder.floor.setText("[" + floor + " ¥]");
        holder.nickName.setTextColor(res.getColor(theme.getForegroundColor()));
        holder.postTime.setTextColor(res.getColor(theme.getForegroundColor()));
        holder.floor.setTextColor(res.getColor(theme.getForegroundColor()));


        FunctionUtils.handleNickName(entry, res.getColor(theme.getForegroundColor()), holder.nickName, mContext);
        handleAvatar(holder.avatarImage, entry);

        int colorId = theme.getBackgroundColor(position + 1);
        final int bgColor = res.getColor(colorId);
        int fgColorId = theme.getForegroundColor();
        final int fgColor = res.getColor(fgColorId);
        holder.itemView .setBackgroundResource(colorId);
        FunctionUtils.handleContentTV(holder.content, entry, bgColor, fgColor, mContext);

    }

    private void handleAvatar(ImageView avatarIV, MessageArticlePageInfo row) {

        final int lou = row.getLou();
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar());//
        final String userId = row.getFrom();
        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
            avatarIV.setImageBitmap(null);
            return;
        }
        if (mDefaultAvatar == null
                || mDefaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
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
                // NLog.d(TAG, "recycle avatar:" + origTag.lou);
            } else {
                // NLog.d(TAG, "default avatar, skip recycle");
            }
        }

        AvatarTag tag = new AvatarTag(lou, true);
        avatarIV.setImageBitmap(mDefaultAvatar);
        avatarIV.setTag(tag);
        if (!StringUtils.isEmpty(avatarUrl)) {
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
                    final boolean downImg = NetUtil.getInstance().isInWifi()
                            || PhoneConfiguration.getInstance()
                            .isDownAvatarNoWifi();

                    new AvatarLoadTask(avatarIV, null, downImg, lou, this)
                            .execute(avatarUrl, avatarPath, userId);

                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void setData(MessageDetailInfo data) {
        mListInfo = data;
        mCount = data == null ? 0 : mListInfo.getMessageEntryList().size();
        notifyDataSetChanged();

    }

    private synchronized boolean isPending(String url) {
        return mUrlSet.contains(url);
    }

    @Override
    public synchronized void OnAvatarLoadStart(String url) {
        mUrlSet.add(url);
    }

    @Override
    public synchronized void OnAvatarLoadComplete(String url) {
        mUrlSet.remove(url);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nickName)
        public TextView nickName;

        @BindView(R.id.floor)
        public TextView floor;

        @BindView(R.id.postTime)
        public TextView postTime;

        @BindView(R.id.avatarImage)
        public ImageView avatarImage;

        @BindView(R.id.content)
        public WebView content;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
