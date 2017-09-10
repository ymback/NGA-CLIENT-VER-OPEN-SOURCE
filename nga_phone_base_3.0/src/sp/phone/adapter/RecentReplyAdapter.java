package sp.phone.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.bean.NotificationObject;
import sp.phone.common.PreferenceKey;
import sp.phone.common.UserManagerImpl;
import sp.phone.utils.ImageUtil;

public class RecentReplyAdapter extends BaseAdapter implements
        PreferenceKey {

    private List<NotificationObject> list;

    private Context mcontext;

    public RecentReplyAdapter(List<NotificationObject> list, Context context) {
        super();
        this.list = list;
        this.mcontext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void clean() {
        List list = new ArrayList();
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return list.get(list.size() - 1 - position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pending_relay, parent, false);
            holder = new ViewHolder();
            holder.nickTv = (TextView) convertView.findViewById(R.id.nick_name);
            holder.titleTv = (TextView) convertView.findViewById(R.id.title);
            holder.avatarIv = (ImageView) convertView.findViewById(R.id.avatar_image);
            convertView.setTag(holder);
            convertView.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.list_selector_recent_reply));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nickTv.setText(list.get(list.size() - 1 - position).getNickName());

        holder.titleTv.setText(list.get(list.size() - 1 - position).getTitle());
        handleAvatar(holder.avatarIv, list.get(list.size() - 1 - position).getAuthorId());

        return convertView;
    }

    ;

    void handleAvatar(ImageView avatarIV, int authorid) {
        String extensions[] = {"jpg", "png", "bmp", "gif", "jpeg"};
        Bitmap bitmap = null;
        for (int i = 0; i < extensions.length; ++i) {
            String avatarPath = ImageUtil.getAvatarById(extensions[i], String.valueOf(authorid));
            if (avatarPath != null) {
                File f = new File(avatarPath);
                if (f.exists()) {
                    bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath, 150);
                    break;
                }
            }

        }

        Resources res = avatarIV.getResources();
        if (bitmap == null) {
            InputStream is = res.openRawResource(R.raw.default_avatar);
            InputStream is2 = res.openRawResource(R.raw.default_avatar);
            bitmap = ImageUtil.loadAvatarFromStream(is, is2, 150);
        }

        if (bitmap != null) {
            avatarIV.setImageBitmap(bitmap);
        }

    }

    public void remove(int position) {
        // TODO Auto-generated method stub
        list.remove(list.size() - 1 - position);
        String str = JSON.toJSONString(list);
        UserManagerImpl.getInstance().setReplyString(list.size(),str);
        this.notifyDataSetInvalidated();
    }

    static class ViewHolder {
        public TextView nickTv;
        public TextView titleTv;
        public ImageView avatarIv;
    }
}
