package sp.phone.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.common.ApiConstants;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PhoneConfiguration;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.rxjava.RxUtils;
import sp.phone.theme.ThemeManager;
import sp.phone.util.StringUtils;

public class TopicListAdapter extends BaseAppendableAdapter<ThreadPageInfo, TopicListAdapter.TopicViewHolder> {

    private ThemeManager mThemeManager = ThemeManager.getInstance();

    public TopicListAdapter(Context context) {
        super(context);
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TopicViewHolder viewHolder = new TopicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_topic, parent, false));
        viewHolder.title.setTextSize(PhoneConfiguration.getInstance().getTopicTitleSize());
        viewHolder.title.setTextColor(ContextCompat.getColor(mContext, mThemeManager.getForegroundColor()));
        RxUtils.clicks(viewHolder.itemView, mOnClickListener);
        viewHolder.itemView.setOnLongClickListener(mOnLongClickListener);
        return viewHolder;
    }

    @Override
    public void setData(List<ThreadPageInfo> dataList) {
        if (dataList == null) {
            super.setData(null);
        } else {
            super.appendData(dataList);
        }
    }

    @Override
    public void onBindViewHolder(final TopicViewHolder holder, int position) {

        ThreadPageInfo info = getItem(position);
        info.setPosition(position);
        holder.itemView.setTag(info);

        if (!mThemeManager.isNightMode()) {
            holder.itemView.setBackgroundResource(mThemeManager.getBackgroundColor(position));
        }
        handleJsonList(holder, info);
    }

    private void handleJsonList(TopicViewHolder holder, ThreadPageInfo entry) {

        if (entry == null) {
            return;
        }
        holder.author.setText(entry.getAuthor());
        holder.lastReply.setText(entry.getLastPoster());
        holder.num.setText(String.valueOf(entry.getReplies()));
        handleTitleView(holder.title, entry);
    }

    private void handleTitleView(TextView view, ThreadPageInfo entry) {

        String title = StringUtils.removeBrTag(StringUtils.unEscapeHtml(entry.getSubject()));
        SpannableStringBuilder builder = new SpannableStringBuilder(title);
        int type = entry.getType();
        int titleLength = title.length();

        if ((type & ApiConstants.MASK_TYPE_ATTACHMENT) == ApiConstants.MASK_TYPE_ATTACHMENT) {
            String typeStr = " +";
            builder.append(typeStr);
            builder.setSpan(new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.title_orange)), builder.length() - typeStr.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ((type & ApiConstants.MASK_TYPE_LOCK) == ApiConstants.MASK_TYPE_LOCK) {
            String typeStr = " [锁定]";
            builder.append(typeStr);
            builder.setSpan(new ForegroundColorSpan(Color.RED), builder.length() - typeStr.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ((type & ApiConstants.MASK_TYPE_ASSEMBLE) == ApiConstants.MASK_TYPE_ASSEMBLE) {
            String typeStr = " [合集]";
            builder.append(typeStr);
            builder.setSpan(new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.title_blue)), builder.length() - typeStr.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!TextUtils.isEmpty(entry.getTopicMisc())) {
            ForegroundColorSpan greenSpan = new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.title_green));
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.title_blue));
            ForegroundColorSpan redSpan = new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.title_red));
            ForegroundColorSpan orangeSpan = new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.title_orange));
            ForegroundColorSpan sliverSpan = new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.silver));
            String misc = entry.getTopicMisc();
            // ~ 开头的为旧格式
            if (misc.startsWith("~")) {
                if (misc.equals("~1~~") || misc.equals("~~~1")) {
                    builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    String[] miscArray = misc.toLowerCase(Locale.US).split("~");
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
                            case "b":
                                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "i":
                                builder.setSpan(new StyleSpan(Typeface.ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            case "u":
                                builder.setSpan(new UnderlineSpan(), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else {
                byte[] bytes = Base64.decode(misc, Base64.DEFAULT);
                if (bytes != null) {
                    int pos = 0;
                    while (pos < bytes.length) {
                        // 1 表示主题bit数据
                        if (bytes[pos] == 1) {
                            String miscStr = StringUtils.toBinaryArray(bytes).substring(8);
                            int miscValue = new BigInteger(miscStr, 2).intValue();
                            if ((miscValue & ApiConstants.MASK_FONT_GREEN) == ApiConstants.MASK_FONT_GREEN) {
                                builder.setSpan(greenSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((miscValue & ApiConstants.MASK_FONT_BLUE) == ApiConstants.MASK_FONT_BLUE) {
                                builder.setSpan(blueSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((miscValue & ApiConstants.MASK_FONT_RED) == ApiConstants.MASK_FONT_RED) {
                                builder.setSpan(redSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((miscValue & ApiConstants.MASK_FONT_ORANGE) == ApiConstants.MASK_FONT_ORANGE) {
                                builder.setSpan(orangeSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((miscValue & ApiConstants.MASK_FONT_SILVER) == ApiConstants.MASK_FONT_SILVER) {
                                builder.setSpan(sliverSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if ((miscValue & ApiConstants.MASK_FONT_BOLD) == ApiConstants.MASK_FONT_BOLD && (miscValue & ApiConstants.MASK_FONT_ITALIC) == ApiConstants.MASK_FONT_ITALIC) {
                                builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((miscValue & ApiConstants.MASK_FONT_ITALIC) == ApiConstants.MASK_FONT_ITALIC) {
                                builder.setSpan(new StyleSpan(Typeface.ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else if ((miscValue & ApiConstants.MASK_FONT_BOLD) == ApiConstants.MASK_FONT_BOLD) {
                                builder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            if ((miscValue & ApiConstants.MASK_FONT_UNDERLINE) == ApiConstants.MASK_FONT_UNDERLINE) {
                                builder.setSpan(new UnderlineSpan(), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        pos += 4;
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(entry.getBoard())) {
            SpannableStringBuilder boardBuilder = new SpannableStringBuilder();
            boardBuilder.append("  [").append(entry.getBoard()).append("]");
            boardBuilder.setSpan(new ForegroundColorSpan(ApplicationContextHolder.getColor(R.color.text_color_disabled)), 0, boardBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(boardBuilder);
        }
        view.setText(builder);
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
