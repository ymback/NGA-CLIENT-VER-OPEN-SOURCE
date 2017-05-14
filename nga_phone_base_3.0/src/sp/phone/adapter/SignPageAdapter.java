package sp.phone.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashSet;

import gov.anzong.androidnga.R;
import sp.phone.bean.MissionDetialData;
import sp.phone.bean.SignData;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class SignPageAdapter extends BaseAdapter implements
        OnSignPageLoadFinishedListener {

    protected Context context;
    protected int count = 0;
    private LayoutInflater inflater;
    private SignData signData = null;
    private int selected = -1;


    public SignPageAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public Object getItem(int arg0) {

//		MissionDetialData entry = getEntry(arg0);

        return null;

    }

    public int getCount() {
        return count;
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int position, View view, ViewGroup parent) {

        View convertView = view;// m.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.relative_signmissionstate_list, null);
            TextView missionid = (TextView) convertView.findViewById(R.id.missionid);
            TextView missionname = (TextView) convertView
                    .findViewById(R.id.missionname);
            TextView missiondetial = (TextView) convertView
                    .findViewById(R.id.missiondetial);
            TextView missionstat = (TextView) convertView
                    .findViewById(R.id.missionstat);
            WebView content = (WebView) convertView
                    .findViewById(R.id.content);
            TextView missionidtitle = (TextView) convertView
                    .findViewById(R.id.missionidtitle);
            holder = new ViewHolder();

            holder.missionid = missionid;
            holder.missionname = missionname;
            holder.missiondetial = missiondetial;
            holder.missionstat = missionstat;
            holder.content = content;
            holder.missionidtitle = missionidtitle;
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ThemeManager cfg = ThemeManager.getInstance();
        int colorId = cfg.getBackgroundColor(position);
        if (position == this.selected) {
            if (cfg.mode == ThemeManager.MODE_NIGHT)
                colorId = R.color.topiclist_selected_color;
            else
                colorId = R.color.holo_blue_light;
            ;
        }
        convertView.setBackgroundResource(colorId);

        handleJsonList(holder, position);
        return convertView;

    }

    public void setSelected(int position) {
        this.selected = position;
    }

    private void handleJsonList(ViewHolder holder, int position) {
        MissionDetialData entry = getEntry(position);
        // this.signData.getArticleEntryList().get(position);

        if (entry == null && position != 0) {
            return;
        }
        Resources res = inflater.getContext().getResources();
        ThemeManager theme = ThemeManager.getInstance();
        String missionid;
        String missionname;
        String missiondetial;
        String missionstat;
        String info;
        missionid = String.valueOf(entry.get__id());
        if (StringUtil.isEmpty(entry.get__name())) {
            missionname = "未知";
        } else {
            missionname = entry.get__name();
        }
        if (StringUtil.isEmpty(entry.get__detail())) {
            missiondetial = "未知";
        } else {
            missiondetial = entry.get__detail();
        }
        if (StringUtil.isEmpty(entry.get__stat())) {
            missionstat = "未知";
        } else {
            missionstat = entry.get__stat();
        }

        if (StringUtil.isEmpty(entry.get__info())) {
            info = "";
        } else {
            info = "<h3>" + entry.get__info() + "</h3>";
        }

        int bgColor = res.getColor(theme.getBackgroundColor(position));
        int fgColor = res.getColor(theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
        holder.content.setBackgroundColor(0);
        holder.content.setFocusableInTouchMode(false);
        holder.content.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            holder.content.setLongClickable(false);
        }
        WebSettings setting = holder.content.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        holder.content.setWebViewClient(client);
        holder.content.loadDataWithBaseURL(null, infoToHtmlText(info, FunctionUtil.isShowImage(), FunctionUtil.showImageQuality(), fgColorStr, bgcolorStr),
                "text/html", "utf-8", null);
        holder.missionidtitle.setText("可完成任务ID:");
        if (entry.get__issuccessed() == true) {
            holder.missionidtitle.setText("已完成任务ID:");
        }
        holder.missionid.setText(missionid);//这边有问题
        holder.missionname.setText(missionname);
        holder.missiondetial.setText(missiondetial);
        holder.missionstat.setText(missionstat);
    }

    public String infoToHtmlText(final String info,
                                 boolean showImage, int imageQuality, final String fgColorStr,
                                 final String bgcolorStr) {
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = StringUtil.decodeForumTag(info, showImage,
                imageQuality, imageURLSet);
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
        if (StringUtil.isEmpty(ngaHtml)) {
            ngaHtml = "<font color='red'>[二哥压根不给我任务信息啊]</font>";
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr
                + "' size='2'>"
                + ngaHtml
                + "</font></body>";

        return ngaHtml;
    }

    protected MissionDetialData getEntry(int position) {
        if (signData != null)
            return signData.getEntryList().get(position);
        return null;
    }

    @Override
    public void jsonfinishLoad(SignData result) {
        this.signData = result;
        count = signData.get__Totalrows();
        this.notifyDataSetChanged();

    }

    public void clear() {
        // TODO Auto-generated method stub

    }

    class ViewHolder {
        public TextView missionid;
        public TextView missionname;
        public TextView missiondetial;
        public TextView missionstat;
        public WebView content;
        public TextView missionidtitle;
    }

    class ViewHolderPos0 {
    }

}
