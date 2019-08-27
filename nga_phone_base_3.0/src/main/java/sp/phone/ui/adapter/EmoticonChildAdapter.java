package sp.phone.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;

import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.util.ImageUtils;

/**
 * Created by Justwen on 2018/6/8.
 */
public class EmoticonChildAdapter extends RecyclerView.Adapter<EmoticonChildAdapter.EmoticonViewHolder> {

    private Context mContext;

    private String[] mImageUrls;

    private String mCategoryName;

    private int mHeight;

    private View.OnClickListener mEmoticonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_INSERT_EMOTICON, v.getTag()));
        }
    };

    public EmoticonChildAdapter(Context context, int height) {
        mContext = context;
        mHeight = height;
    }

    public void setData(String categoryName, String[] urls) {
        mImageUrls = urls;
        mCategoryName = categoryName;
    }

    @Override
    public EmoticonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView emoticonView = new ImageView(mContext);
        int padding = 32;
        emoticonView.setPadding(padding, padding, padding, padding);
        emoticonView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight / 3));
        emoticonView.setBackground(mContext.getDrawable(android.R.drawable.list_selector_background));
        emoticonView.setOnClickListener(mEmoticonClickListener);
        return new EmoticonViewHolder(emoticonView);
    }

    @Override
    public void onBindViewHolder(EmoticonViewHolder holder, int position) {
        ImageUtils.recycleImageView(holder.mEmoticonItem);
        try (InputStream is = mContext.getAssets().open(getFileName(position))) {
            Bitmap bm = BitmapFactory.decodeStream(is);
            holder.mEmoticonItem.setImageBitmap(ImageUtils.zoomImageByHeight(bm, 130));
            holder.mEmoticonItem.setTag("[img]" + mImageUrls[position] + "[/img]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(int position) {
        return mCategoryName + "/" + FilenameUtils.getName(mImageUrls[position]);
    }

    @Override
    public int getItemCount() {
        return mImageUrls == null ? 0 : mImageUrls.length;
    }

    static class EmoticonViewHolder extends RecyclerView.ViewHolder {

        ImageView mEmoticonItem;

        public EmoticonViewHolder(View itemView) {
            super(itemView);
            mEmoticonItem = (ImageView) itemView;
        }
    }
}
