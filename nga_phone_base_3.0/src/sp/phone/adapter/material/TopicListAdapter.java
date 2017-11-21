package sp.phone.adapter.material;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;
import sp.phone.view.RecyclerViewEx;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.TopicViewHolder> implements RecyclerViewEx.IAppendAbleAdapter {

    private final List<TopicListInfo> mInfoList = new ArrayList<>();

    private Set<Integer> mTidSet = new HashSet<>();

    private boolean mEndOfList = false;

    private boolean mIsPrompted = false;

    private Context mContext;

    private int mTotalCount;

    private View.OnClickListener mClickListener;

    private AdapterView.OnItemLongClickListener mItemLongClickListener;

    private final static int _FONT_RED = 1, _FONT_BLUE = 2, _FONT_GREEN = 4,
            _FONT_ORANGE = 8, _FONT_SILVER = 16, _FONT_B = 32, _FONT_I = 64,
            _FONT_U = 128;

    public TopicListAdapter(Context context) {
        mContext = context;
    }

    protected ThreadPageInfo getEntry(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position < mInfoList.get(i).get__T__ROWS()) {
                return mInfoList.get(i).getArticleEntryList().get(position);
            }
            position -= mInfoList.get(i).get__T__ROWS();
        }
        return null;
    }

    public void setData(TopicListInfo result) {
        if (mTotalCount != 0) {
            List<ThreadPageInfo> threadList = new ArrayList<>();
            for (int i = 0; i < result.getArticleEntryList().size(); i++) {
                ThreadPageInfo info = result.getArticleEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int tid = info.getTid();
                if (!mTidSet.contains(tid)) {
                    threadList.add(info);
                    mTidSet.add(tid);
                }
            }
            result.set__T__ROWS(threadList.size());
            result.setArticleEntryList(threadList);
        } else {
            for (int i = 0; i < result.getArticleEntryList().size(); i++) {
                ThreadPageInfo info = result.getArticleEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int tid = info.getTid();
                mTidSet.add(tid);
            }

        }
        mInfoList.add(result);
        mTotalCount += result.get__T__ROWS();
        mEndOfList = mTotalCount >= result.get__ROWS();
        notifyDataSetChanged();
    }

    public void clear() {
        mTotalCount = 0;
        mInfoList.clear();
        mTidSet.clear();
        mIsPrompted = false;
    }

    private String getUrl(int position) {
        ThreadPageInfo entry = getEntry(position);
        if (entry == null || entry.getTid() == 0) {
            return null;
        }

        String ret = "tid=" + entry.getTid();
        if (entry.getPid() != 0) {
            return ret + "&pid=" + entry.getPid();
        } else {
            return ret;
        }
    }

    @Override
    public int getNextPage() {
        return mInfoList.size() + 1;
    }

    @Override
    public boolean hasNextPage() {
        return !mEndOfList;
    }

    public void setNextPageEnabled(boolean enabled) {
        mEndOfList = !enabled;
        if (!hasNextPage() && !mIsPrompted) {
            ActivityUtils.showToast(mContext, mContext.getString(R.string.last_page_prompt));
            mIsPrompted = true;
        }
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.relative_topic_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final TopicViewHolder holder, int position) {

        if (position + 1 == mTotalCount
                && !hasNextPage()
                && !mIsPrompted) {
            ActivityUtils.showToast(mContext, mContext.getString(R.string.last_page_prompt));
            mIsPrompted = true;
        }

        ThemeManager cfg = ThemeManager.getInstance();
        int colorId = cfg.getBackgroundColor(position);
        holder.itemView.setOnClickListener(mClickListener);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                return mItemLongClickListener != null && mItemLongClickListener.onItemLongClick(null, holder.itemView, position, getItemId(position));
            }
        });
        holder.itemView.setTag(R.id.title, getUrl(position));
        holder.itemView.setTag(R.id.num, getTidArray(position));
        holder.itemView.setBackgroundResource(colorId);
        handleJsonList(holder, position);
    }

    private void handleJsonList(TopicViewHolder holder, int position) {
        ThreadPageInfo entry = getEntry(position);

        if (entry == null) {
            return;
        }
        ThemeManager theme = ThemeManager.getInstance();
        boolean night = false;
        int nightLinkColor = ContextCompat.getColor(mContext, R.color.night_link_color);
        if (theme.getMode() == ThemeManager.MODE_NIGHT)
            night = true;
        holder.author.setText(entry.getAuthor());
        if (night)
            holder.author.setTextColor(nightLinkColor);

        String lastPoster = entry.getLastposter_org();
        if (StringUtils.isEmpty(lastPoster))
            lastPoster = entry.getLastposter();
        holder.lastReply.setText(lastPoster);
        holder.num.setText(String.valueOf(entry.getReplies()));
        if (night) {
            holder.lastReply.setTextColor(nightLinkColor);
            holder.num.setTextColor(nightLinkColor);
        }
        handleTitleView(holder.title, entry);
    }

    private void handleTitleView(TextView view, ThreadPageInfo entry) {
        ThemeManager theme = ThemeManager.getInstance();
        float size = PhoneConfiguration.getInstance().getTextSize();
        view.setTextColor(ContextCompat.getColor(mContext, theme.getForegroundColor()));
        view.setTextSize(size);
        String title = entry.getContent();
        int type = entry.getType();
        String needAdd = "";
        if ((type & 1024) == 1024) {
            needAdd += " [锁定]";
        }
        if ((type & 8192) == 8192) {
            needAdd += " +";
        }
        int titleLength;
        if (StringUtils.isEmpty(title)) {
            title = entry.getSubject();
            title = StringUtils.unEscapeHtml(title);
            titleLength = title.length();
            title += needAdd;

        } else {
            title = StringUtils.removeBrTag(StringUtils
                    .unEscapeHtml(title));
            titleLength = title.length();
            title += needAdd;
        }
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.title_green));
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.title_blue));
        ForegroundColorSpan redSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.title_red));
        ForegroundColorSpan lockRedSpan = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.title_orange));
        ForegroundColorSpan picOrangeSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.title_orange));
        ForegroundColorSpan sliverSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.silver));

        SpannableStringBuilder builder = new SpannableStringBuilder(title);
        int totalLength = title.length();
        if ((type & 8192) == 8192 && (type & 1024) == 1024 && totalLength >= 6) {//均有
            builder.setSpan(picOrangeSpan, totalLength - 1, totalLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(lockRedSpan, totalLength - 6, totalLength - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ((type & 8192) == 8192 && totalLength > 0) {//只有+
            builder.setSpan(picOrangeSpan, totalLength - 1, totalLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ((type & 1024) == 1024 && totalLength >= 4) {//只有锁定
            builder.setSpan(lockRedSpan, totalLength - 4, totalLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!StringUtils.isEmpty(entry.getTopicMisc())) {
            final String misc = entry.getTopicMisc();
            if (misc.contains("~")) {
                if (misc.equals("~1~~") || misc.equals("~~~1")) {
                    builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    String miscArray[] = misc.toLowerCase(Locale.US).split("~");
                    for (String aMiscArray : miscArray) {
                        switch (aMiscArray) {
                            case "green":
                                builder.setSpan(greenSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "blue":
                                builder.setSpan(blueSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "red":
                                builder.setSpan(redSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "orange":
                                builder.setSpan(orangeSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "sliver":
                                builder.setSpan(sliverSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                        }
                        if (aMiscArray.equals("b") && aMiscArray.equals("i")) {
                            builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (aMiscArray.equals("b")) {
                            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (aMiscArray.equals("i")) {
                            builder.setSpan(new StyleSpan(Typeface.ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (aMiscArray.equals("u")) {
                            builder.setSpan(new UnderlineSpan(), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                    }
                }
            } else {
                byte b[] = Base64.decode(misc, Base64.DEFAULT);
                if (b != null) {
                    if (b.length == 5) {
                        String miscString = toBinary(b);
                        String miscStringStart = miscString.substring(0, 8);
                        BigInteger src1 = new BigInteger(miscStringStart, 2);//转换为BigInteger类型
                        int d1 = src1.intValue();
                        if (d1 == 1) {
                            String miscStringEnd = miscString.substring(8, miscString.length());
                            BigInteger src2 = new BigInteger(miscStringEnd, 2);//转换为BigInteger类型
                            int d2 = src2.intValue();
                            if ((d2 & _FONT_GREEN) == _FONT_GREEN) {
                                builder.setSpan(greenSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((d2 & _FONT_BLUE) == _FONT_BLUE) {
                                builder.setSpan(blueSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((d2 & _FONT_RED) == _FONT_RED) {
                                builder.setSpan(redSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((d2 & _FONT_ORANGE) == _FONT_ORANGE) {
                                builder.setSpan(orangeSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((d2 & _FONT_SILVER) == _FONT_SILVER) {
                                builder.setSpan(sliverSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if ((d2 & _FONT_B) == _FONT_B && (d2 & _FONT_I) == _FONT_I) {
                                builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((d2 & _FONT_I) == _FONT_I) {
                                builder.setSpan(new StyleSpan(Typeface.ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((d2 & _FONT_B) == _FONT_B) {
                                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if ((d2 & _FONT_U) == _FONT_U) {
                                builder.setSpan(new UnderlineSpan(), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }

                        }
                    }
                }
            }
        } else {
            if (!StringUtils.isEmpty(entry.getTitlefont())) {
                final String font = entry.getTitlefont();
                if (font.contains("~")) {
                    if (font.equals("~1~~") || font.equals("~~~1")) {
                        builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        String miscArray[] = font.toLowerCase(Locale.US).split("~");
                        for (String aMiscArray : miscArray) {
                            switch (aMiscArray) {
                                case "green":
                                    builder.setSpan(greenSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    break;
                                case "blue":
                                    builder.setSpan(blueSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    break;
                                case "red":
                                    builder.setSpan(redSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    break;
                                case "orange":
                                    builder.setSpan(orangeSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    break;
                                case "sliver":
                                    builder.setSpan(sliverSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    break;
                            }
                            if (aMiscArray.equals("b") && aMiscArray.equals("i")) {
                                builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if (aMiscArray.equals("b")) {
                                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if (aMiscArray.equals("i")) {
                                builder.setSpan(new StyleSpan(Typeface.ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if (aMiscArray.equals("u")) {
                                builder.setSpan(new UnderlineSpan(), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }

                        }
                    }
                }
            }
        }
        view.setText(builder);
    }

    private String toBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return mTotalCount;
    }

    public void remove(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position >= 0 && position < mInfoList.get(i).get__T__ROWS()) {
                mInfoList.get(i).getArticleEntryList().remove(position);
                mInfoList.get(i).set__T__ROWS(mInfoList.get(i).getArticleEntryList().size());
                mTotalCount--;
            }
            position -= mInfoList.get(i).get__T__ROWS();
        }
    }

    private String getTidArray(int position) {
        for (int i = 0; i < mInfoList.size(); i++) {
            if (position < mInfoList.get(i).get__T__ROWS()) {
                return mInfoList.get(i).getArticleEntryList().get(position).getTidarray();
            }
            position -= mInfoList.get(i).get__T__ROWS();
        }
        return null;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.num)
        public TextView num;

        @BindView(R.id.title)
        public TextView title;

        @BindView(R.id.author)
        public TextView author;

        @BindView(R.id.last_reply)
        public TextView lastReply;

        public TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
