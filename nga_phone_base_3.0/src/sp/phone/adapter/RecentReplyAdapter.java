package sp.phone.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import gov.anzong.androidnga.activity.MyApp;
import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class RecentReplyAdapter extends BaseAdapter implements
        PerferenceConstant {

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

        SharedPreferences share = mcontext.getSharedPreferences(PERFERENCE,
                Context.MODE_PRIVATE);
        String str = JSON.toJSONString(list);
        PhoneConfiguration.getInstance().setReplyString(str);
        PhoneConfiguration.getInstance().setReplyTotalNum(list.size());


        String userListString = share.getString(USER_LIST, "");
        List<User> userList = null;
        if (!StringUtil.isEmpty(userListString)) {
            userList = JSON.parseArray(userListString, User.class);
            for (User u : userList) {
                if (u.getUserId().equals(
                        PhoneConfiguration.getInstance().uid)) {
                    MyApp app = (MyApp) ((Activity) mcontext)
                            .getApplication();
                    app.addToUserList(u.getUserId(), u.getCid(),
                            u.getNickName(), str, list.size(), u.getBlackList());
                    break;
                }
            }
        } else {
            Editor editor = share.edit();
            editor.putString(PENDING_REPLYS, str);
            editor.putString(REPLYTOTALNUM,
                    String.valueOf(list.size()));
            editor.apply();
        }
        this.notifyDataSetInvalidated();
    }

    static class ViewHolder {
        public TextView nickTv;
        public TextView titleTv;
        public ImageView avatarIv;
    }
}
