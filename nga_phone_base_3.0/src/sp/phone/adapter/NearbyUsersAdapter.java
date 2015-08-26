package sp.phone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.R;
import sp.phone.bean.NearbyUser;
import sp.phone.utils.ImageUtil;

public class NearbyUsersAdapter extends BaseAdapter {
    private final List<NearbyUser> list;
    private Map<String, SoftReference<Bitmap>> mAvatarCache =
            new HashMap<String, SoftReference<Bitmap>>();// avatar memory cache

    public NearbyUsersAdapter(List<NearbyUser> list) {
        super();
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null)
            return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View ret = null;
        if (convertView == null) {

            ret = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_user, parent, false);

        } else {
            ret = convertView;
        }
        final NearbyUser u = list.get(position);
        String text = u.getNickName();
        try {
            text = URLDecoder.decode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {

        }

        text = text + "(" +
                u.getJuli() +
                "米)";
        TextView tv = (TextView) ret.findViewById(R.id.nickname);
        tv.setText(text);
        final ImageView iv = (ImageView) ret.findViewById(R.id.avatarimg);
        Bitmap bitmap = getAvatarFromMemoryCache(u);
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
        } else {
            iv.setImageResource(R.drawable.default_avatar);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = getAvatarFromFile(u, parent.getContext());
                    if (bitmap != null) {
                        iv.post(new Runnable() {
                            @Override
                            public void run() {
                                putAvatarToMemoryCache(u, bitmap);
                                iv.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });
        }
        return ret;
    }

    private Bitmap getAvatarFromFile(NearbyUser u, Context c) {
        String extensions[] = {"jpg", "png", "bmp", "gif", "jpeg"};
        Bitmap bitmap = null;
        for (int i = 0; i < extensions.length; ++i) {
            String avatarPath = ImageUtil.getAvatarById(extensions[i], u.getUserId());
            if (avatarPath != null) {
                File f = new File(avatarPath);
                if (f.exists()) {
                    bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath, 150);
                    break;
                }
            }

        }
        return bitmap;
    }

    /**
     * Returns the user's avatar Bitmap if it is found in the memory cache, or null otherwise.
     *
     * @param user
     * @return avatar image bitmap
     */
    private Bitmap getAvatarFromMemoryCache(NearbyUser user) {
        SoftReference<Bitmap> ref = mAvatarCache.get(user.getUserId());
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    /**
     * Puts the avatar Bitmap to the memory cache.
     *
     * @param user
     * @param avatar
     */
    private void putAvatarToMemoryCache(NearbyUser user, Bitmap avatar) {
        mAvatarCache.put(user.getUserId(), new SoftReference<Bitmap>(avatar));
    }
}
