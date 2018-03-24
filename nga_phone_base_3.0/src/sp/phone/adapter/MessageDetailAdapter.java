package sp.phone.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.theme.ThemeManager;
import sp.phone.utils.FunctionUtils;
import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2017/10/15.
 */

public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.MessageViewHolder> implements RecyclerViewEx.IAppendableAdapter {

    private List<MessageDetailInfo> mInfoList = new ArrayList<>();

    private boolean mPrompted;

    private boolean mEndOfList;

    private Context mContext;

    private int mTotalCount;

    static class MessageViewHolder extends RecyclerView.ViewHolder {

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
            ButterKnife.bind(this, itemView);
        }
    }

    public MessageDetailAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_message_content, parent, false));
    }

    private MessageArticlePageInfo getEntry(int position) {
        return mInfoList.get(position / 20).getMessageEntryList().get(position % 20);
    }

    public void setData(MessageDetailInfo data) {
        if (data == null) {
            return;
        } else if (data.get__currentPage() == 1) {
            reset();
        }
        mInfoList.add(data);
        mTotalCount += data.getMessageEntryList().size();
        mEndOfList = data.get__nextPage() <= 0;
        notifyDataSetChanged();
    }

    @Override
    public int getNextPage() {
        return mInfoList.size() + 1;
    }

    @Override
    public boolean hasNextPage() {
        return !mEndOfList;
    }

    private void reset() {
        mTotalCount = 0;
        mInfoList.clear();
        mEndOfList = false;
        mPrompted = false;
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
        //  handleAvatar(holder.avatarImage, entry);

        int colorId = theme.getBackgroundColor(position + 1);
        final int bgColor = res.getColor(colorId);
        int fgColorId = theme.getForegroundColor();
        final int fgColor = res.getColor(fgColorId);
        holder.itemView.setBackgroundResource(colorId);
        FunctionUtils.handleContentTV(holder.content, entry, bgColor, fgColor, mContext);

    }

//    private void handleAvatar(ImageView avatarIV, MessageArticlePageInfo row) {
//
//        final int lou = row.getLou();
//        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.getJs_escap_avatar());//
//        final String userId = row.getFrom();
//        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
//            avatarIV.setImageBitmap(null);
//            return;
//        }
//        if (defaultAvatar == null
//                || defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
//            Resources res = avatarIV.getContext().getResources();
//            InputStream is = res.openRawResource(R.drawable.default_avatar);
//            InputStream is2 = res.openRawResource(R.drawable.default_avatar);
//            this.defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
//        }
//
//        Object tagObj = avatarIV.getTag();
//        if (tagObj instanceof AvatarTag) {
//            AvatarTag origTag = (AvatarTag) tagObj;
//            if (!origTag.isDefault) {
//                ImageUtil.recycleImageView(avatarIV);
//                // NLog.d(TAG, "recycle avatar:" + origTag.lou);
//            } else {
//                // NLog.d(TAG, "default avatar, skip recycle");
//            }
//        }
//
//        AvatarTag tag = new AvatarTag(lou, true);
//        avatarIV.setImageBitmap(defaultAvatar);
//        avatarIV.setTag(tag);
//        if (!StringUtils.isEmpty(avatarUrl)) {
//            final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
//            if (avatarPath != null) {
//                File f = new File(avatarPath);
//                if (f.exists() && !isPending(avatarUrl)) {
//
//                    Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
//                    if (bitmap != null) {
//                        avatarIV.setImageBitmap(bitmap);
//                        tag.isDefault = false;
//                    } else
//                        f.delete();
//                    long date = f.lastModified();
//                    if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
//                        f.delete();
//                    }
//
//                } else {
//                    final boolean downImg = NetUtil.getInstance().isInWifi()
//                            || PhoneConfiguration.getInstance()
//                            .isDownAvatarNoWifi();
//
//                    new AvatarLoadTask(avatarIV, null, downImg, lou, this)
//                            .execute(avatarUrl, avatarPath, userId);
//
//                }
//            }
//        }
//
//    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        handleJsonList(holder, position);
        if (position + 1 == getItemCount()
                && !hasNextPage()
                && !mPrompted) {
            Toast.makeText(mContext, R.string.last_page_prompt_message_detail, Toast.LENGTH_SHORT).show();
            mPrompted = true;
        }
    }

    @Override
    public int getItemCount() {
        return mTotalCount;
    }

}
