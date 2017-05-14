package sp.phone.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashSet;

import gov.anzong.androidnga.R;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.listener.MyListenerForClient;
import sp.phone.listener.MyListenerForReply;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.HtmlUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

/**
 * 帖子详情列表Adapter
 */
public class ArticleListAdapter extends BaseAdapter implements
        AvatarLoadCompleteCallBack {
    private static final String TAG = ArticleListAdapter.class.getSimpleName();
    private Context activity;
    final WebViewClient client;
    private final SparseArray<SoftReference<View>> viewCache;
    private final Object lock = new Object();
    private final HashSet<String> urlSet = new HashSet<String>();
    private ThreadData data;
    private Bitmap defaultAvatar = null;

    public ArticleListAdapter(Context activity) {
        super();
        this.activity = activity;
        this.viewCache = new SparseArray<SoftReference<View>>();
        if (HtmlUtil.userDistance == null)
            HtmlUtil.initStaticStrings(activity);
        client = new ArticleListWebClient((FragmentActivity) activity);
    }

    @Override
    public int getCount() {
        if (null == data)
            return 0;
        return data.getRowNum();
    }

    public ThreadData getData() {
        return data;
    }

    public void setData(ThreadData data) {
        this.data = data;
    }

    @Override
    public Object getItem(int position) {
        if (null == data)
            return null;
        return data.getRowList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isInWifi() {
        ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return wifi == State.CONNECTED;
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
        if (defaultAvatar == null || defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
            Resources res = avatarIV.getContext().getResources();
            InputStream is = res.openRawResource(R.drawable.default_avatar);
            InputStream is2 = res.openRawResource(R.drawable.default_avatar);
            this.defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
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
        avatarIV.setImageBitmap(defaultAvatar);
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
                    final boolean downImg = isInWifi()
                            || PhoneConfiguration.getInstance()
                            .isDownAvatarNoWifi();

                    new AvatarLoadTask(avatarIV, null, downImg, lou, this).execute(avatarUrl, avatarPath, userId);
                }
            }
        }

    }

    private ViewHolder initHolder(final View view) {
        final ViewHolder holder = new ViewHolder();
        holder.articlelistrelativelayout = (RelativeLayout) view.findViewById(R.id.articlelistrelativelayout);
        holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);
        holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
        holder.floorTV = (TextView) view.findViewById(R.id.floor);
        holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
        holder.contentTV = (WebView) view.findViewById(R.id.content);
        holder.contentTV.setHorizontalScrollBarEnabled(false);
        holder.viewBtn = (ImageButton) view.findViewById(R.id.listviewreplybtn);
        holder.clientBtn = (ImageButton) view.findViewById(R.id.clientbutton);
        return holder;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ThreadRowInfo row = data.getRowList().get(position);

        int lou = -1;
        if (row != null)
            lou = row.getLou();
        ViewHolder holder = null;
        boolean needin = false;
        SoftReference<View> ref = viewCache.get(position);
        View cachedView = null;
        if (ref != null) {
            cachedView = ref.get();
        }
        if (cachedView != null) {
            if (((ViewHolder) cachedView.getTag()).position == position) {
                Log.d(TAG, "get view from cache ,floor " + lou);
                return cachedView;
            } else {
                view = LayoutInflater.from(activity).inflate(R.layout.relative_aritclelist, parent, false);
                holder = initHolder(view);
                holder.position = position;
                view.setTag(holder);
                viewCache.put(position, new SoftReference<View>(view));
            }
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.relative_aritclelist, parent, false);
            holder = initHolder(view);
            holder.position = position;
            view.setTag(holder);
            viewCache.put(position, new SoftReference<View>(view));
        }
        if (!PhoneConfiguration.getInstance().showReplyButton) {
            holder.viewBtn.setVisibility(View.GONE);
        } else {
            MyListenerForReply myListenerForReply = new MyListenerForReply(position, data, activity);
            holder.viewBtn.setOnClickListener(myListenerForReply);
        }
        ThemeManager theme = ThemeManager.getInstance();
        int colorId = theme.getBackgroundColor(position);
        view.setBackgroundResource(colorId);

        // colorId = theme.getBackgroundColor(2);

        if (row == null) {
            return view;
        }

        handleAvatar(holder.avatarIV, row);

        int fgColorId = ThemeManager.getInstance().getForegroundColor();
        final int fgColor = parent.getContext().getResources().getColor(fgColorId);

        FunctionUtil.handleNickName(row, fgColor, holder.nickNameTV, activity);

        final int bgColor = parent.getContext().getResources().getColor(colorId);

        final WebView contentTV = holder.contentTV;

        final String floor = String.valueOf(lou);
        TextView floorTV = holder.floorTV;
        floorTV.setText("[" + floor + " 楼]");
        floorTV.setTextColor(fgColor);

        if (!StringUtil.isEmpty(row.getFromClientModel())) {
            MyListenerForClient myListenerForClient = new MyListenerForClient(position, data, activity, parent);
            String from_client_model = row.getFromClientModel();
            if (from_client_model.equals("ios")) {
                holder.clientBtn.setImageResource(R.drawable.ios);// IOS
            } else if (from_client_model.equals("wp")) {
                holder.clientBtn.setImageResource(R.drawable.wp);// WP
            } else if (from_client_model.equals("unknown")) {
                holder.clientBtn.setImageResource(R.drawable.unkonwn);// 未知orBB
            }
            holder.clientBtn.setVisibility(View.VISIBLE);
            holder.clientBtn.setOnClickListener(myListenerForClient);
        }
        if (ActivityUtil.isLessThan_4_3()) {
            new Thread(new Runnable() {
                public void run() {
                    FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor, activity, null, client);
                }
            }).start();
        } else if (ActivityUtil.isLessThan_4_4()) {
            ((Activity) parent.getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor, activity, null, client);
                }
            });
        } else {
            FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor, activity, null, client);
        }
        TextView postTimeTV = holder.postTimeTV;
        postTimeTV.setText(row.getPostdate());
        postTimeTV.setTextColor(fgColor);
        if (needin) {
            view.invalidate();
        }
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        this.viewCache.clear();
        super.notifyDataSetChanged();
    }

    private boolean isPending(String url) {
        boolean ret = false;
        synchronized (lock) {
            ret = urlSet.contains(url);
        }
        return ret;
    }

    @Override
    public void OnAvatarLoadStart(String url) {
        synchronized (lock) {
            this.urlSet.add(url);
        }
    }

    @Override
    public void OnAvatarLoadComplete(String url) {
        synchronized (lock) {
            this.urlSet.remove(url);
        }
    }

    static class ViewHolder {
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
    }

    static class WebViewTag {
        public ListView lv;
        public View holder;
    }

}