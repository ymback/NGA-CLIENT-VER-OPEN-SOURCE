package sp.phone.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.HashSet;

import gov.anzong.androidnga.R;
import noname.gson.parse.NonameReadBody;
import noname.gson.parse.NonameReadResponse;
import sp.phone.listener.MyListenerForNonameReply;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class NonameArticleListAdapter extends BaseAdapter implements
        OnLongClickListener {
    private static final String TAG = NonameArticleListAdapter.class
            .getSimpleName();
    static String userDistance = null;
    static String meter = null;
    static String kiloMeter = null;
    static String hide = null;
    static String legend = null;
    static String attachment = null;
    static String comment = null;
    static String sig = null;
    private static Context activity;
    final WebViewClient client;
    private final SparseArray<SoftReference<View>> viewCache;
    private NonameReadResponse mData;

    @SuppressWarnings("static-access")
    public NonameArticleListAdapter(Context activity) {
        super();
        this.activity = activity;
        this.viewCache = new SparseArray<SoftReference<View>>();
        if (userDistance == null)
            initStaticStrings(activity);
        client = new ArticleListWebClient((FragmentActivity) activity);
    }

    private static void initStaticStrings(Context activity) {
        userDistance = activity.getString(R.string.user_distance);
        meter = activity.getString(R.string.meter);
        kiloMeter = activity.getString(R.string.kilo_meter);
        hide = activity.getString(R.string.hide);
        legend = activity.getString(R.string.legend);
        attachment = activity.getString(R.string.attachment);
        comment = activity.getString(R.string.comment);
        sig = activity.getString(R.string.sig);
    }

    private static String buildHeader(NonameReadBody row, String fgColorStr) {
        if (row == null || StringUtil.isEmpty(row.title))
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<h4 style='color:").append(fgColorStr).append("' >")
                .append(row.title).append("</h4>");
        return sb.toString();
    }

    public static String distanceString(long distance) {
        String ret = Long.valueOf(distance).toString() + meter;
        if (distance > 1000) {
            ret = Long.valueOf(distance / 1000).toString() + kiloMeter;
        }
        return ret;
    }

    public static String convertToHtmlText(final NonameReadBody row,
                                           boolean showImage, int imageQuality, final String fgColorStr,
                                           final String bgcolorStr, Context context) {
        if (StringUtil.isEmpty(hide)) {
            if (context != null)
                initStaticStrings(context);
        }
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = StringUtil.decodeForumTag(
                row.content.replaceAll("\n", "<br/>"), showImage, imageQuality,
                imageURLSet);
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
        if (StringUtil.isEmpty(ngaHtml)) {

            ngaHtml = "<font color='red'>[" + hide + "]</font>";
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + buildHeader(row, fgColorStr)
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr
                + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    @Override
    public int getCount() {
        if (null == mData)
            return 0;
        return mData.data.posts.length;
    }

    public NonameReadResponse getData() {
        return mData;
    }

    public void setData(NonameReadResponse data) {
        this.mData = data;
    }

    @Override
    public Object getItem(int position) {
        if (null == mData)
            return null;

        return mData.data.posts[position];
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    private ViewHolder initHolder(final View view) {
        final ViewHolder holder = new ViewHolder();
        holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);

        holder.floorTV = (TextView) view.findViewById(R.id.floor);
        holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
        holder.contentTV = (WebView) view.findViewById(R.id.content);
        holder.contentTV.setHorizontalScrollBarEnabled(false);
        holder.viewBtn = (ImageButton) view.findViewById(R.id.listviewreplybtn);
        /*
		 * holder.levelTV = (TextView) view.findViewById(R.id.level);
		 * holder.aurvrcTV= (TextView) view.findViewById(R.id.aurvrc);
		 * holder.postnumTV = (TextView) view.findViewById(R.id.postnum);
		 */
        return holder;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final NonameReadBody row = mData.data.posts[position];

        int lou = -1;
        if (row != null)
            lou = row.floor;
        ViewHolder holder = null;
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
                view = LayoutInflater.from(activity).inflate(
                        R.layout.relative_nonamearitclelist, parent, false);
                holder = initHolder(view);
                holder.position = position;
                view.setTag(holder);
                viewCache.put(position, new SoftReference<View>(view));
            }
        } else {
            view = LayoutInflater.from(activity).inflate(
                    R.layout.relative_nonamearitclelist, parent, false);
            holder = initHolder(view);
            holder.position = position;
            view.setTag(holder);
            viewCache.put(position, new SoftReference<View>(view));
        }

        if (!PhoneConfiguration.getInstance().showReplyButton) {
            holder.viewBtn.setVisibility(View.GONE);
        } else {
            MyListenerForNonameReply myListenerForReply = new MyListenerForNonameReply(
                    position, activity, mData);
            holder.viewBtn.setOnClickListener(myListenerForReply);
        }
        holder.position = position;
        ThemeManager theme = ThemeManager.getInstance();
        int colorId = theme.getBackgroundColor(position);
        view.setBackgroundResource(colorId);

        if (row == null) {
            return view;
        }

        int fgColorId = ThemeManager.getInstance().getForegroundColor();
        final int fgColor = parent.getContext().getResources()
                .getColor(fgColorId);

        FunctionUtil.handleNickName(row, fgColor, holder.nickNameTV);

		/*
		 * TextView titleTV = holder.titleTV; if
		 * (!StringUtil.isEmpty(row.getSubject()) ) {
		 * titleTV.setText(StringUtil.unEscapeHtml(row.getSubject()));
		 * titleTV.setTextColor(fgColor);
		 *
		 * }
		 */

        final int bgColor = parent.getContext().getResources()
                .getColor(colorId);

        final WebView contentTV = holder.contentTV;

        final String floor = String.valueOf(lou);
        TextView floorTV = holder.floorTV;
        floorTV.setText("[" + floor + " ¥]");
        floorTV.setTextColor(fgColor);
        final long longposttime = row.ptime;
        String postTime = "";
        if (longposttime != 0) {
            postTime = StringUtil.TimeStamp2Date(String.valueOf(longposttime));
        }
        TextView postTimeTV = holder.postTimeTV;
        postTimeTV.setText(postTime);
        postTimeTV.setTextColor(fgColor);
        if (ActivityUtil.isLessThan_4_3()) {
            new Thread(new Runnable() {
                public void run() {
                    FunctionUtil.handleContentTV(contentTV, row, bgColor,
                            fgColor, activity, null, client);
                }
            }).start();
        } else if (ActivityUtil.isLessThan_4_4()) {
            ((Activity) parent.getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    FunctionUtil.handleContentTV(contentTV, row, bgColor,
                            fgColor, activity, null, client);
                }
            });
        } else {
            FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor,
                    activity, null, client);
        }
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        this.viewCache.clear();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof WebView) {
            WebViewTag tag = (WebViewTag) v.getTag();
            tag.lv.showContextMenuForChild(tag.holder);
            return true;
        }
        return false;
    }

    static class ViewHolder {
        TextView nickNameTV;
        WebView contentTV;
        TextView floorTV;
        TextView postTimeTV;
        int position = -1;
        ImageButton viewBtn;
    }

    static class WebViewTag {
        public ListView lv;
        public View holder;
    }

}
