package sp.phone.forumoperation;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;

import java.math.BigInteger;
import java.util.Locale;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import sp.phone.common.ApiConstants;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.util.StringUtils;

public class TopicTitleHelper {

    private static void handleOldFormat(SpannableStringBuilder builder, String misc, int titleLength) {
        if (misc.equals("~1~~") || misc.equals("~~~1")) {
            builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            String[] miscArray = misc.toLowerCase(Locale.US).split("~");
            for (String aMiscArray : miscArray) {
                switch (aMiscArray) {
                    case "green":
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_green)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case "blue":
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_blue)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case "red":
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_red)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case "orange":
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_orange)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case "sliver":
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.silver)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
    }

    private static void handleNewFormat(SpannableStringBuilder builder, String misc, int titleLength) {
        byte[] bytes = Base64.decode(misc, Base64.DEFAULT);
        if (bytes != null) {
            int pos = 0;
            while (pos < bytes.length) {
                // 1 表示主题bit数据
                if (bytes[pos] == 1) {
                    String miscStr = StringUtils.toBinaryArray(bytes).substring(8);
                    int miscValue = new BigInteger(miscStr, 2).intValue();
                    if ((miscValue & ApiConstants.MASK_FONT_GREEN) == ApiConstants.MASK_FONT_GREEN) {
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_green)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if ((miscValue & ApiConstants.MASK_FONT_BLUE) == ApiConstants.MASK_FONT_BLUE) {
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_blue)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if ((miscValue & ApiConstants.MASK_FONT_RED) == ApiConstants.MASK_FONT_RED) {
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_red)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if ((miscValue & ApiConstants.MASK_FONT_ORANGE) == ApiConstants.MASK_FONT_ORANGE) {
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_orange)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if ((miscValue & ApiConstants.MASK_FONT_SILVER) == ApiConstants.MASK_FONT_SILVER) {
                        builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.silver)), 0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    public static CharSequence handleTitleFormat(ThreadPageInfo entry) {

        String title = StringUtils.removeBrTag(StringUtils.unEscapeHtml(entry.getSubject()));
        SpannableStringBuilder builder = new SpannableStringBuilder(title);
        int type = entry.getType();
        int titleLength = title.length();

        if ((type & ApiConstants.MASK_TYPE_ATTACHMENT) == ApiConstants.MASK_TYPE_ATTACHMENT) {
            String typeStr = " +";
            builder.append(typeStr);
            builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_orange)), builder.length() - typeStr.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ((type & ApiConstants.MASK_TYPE_LOCK) == ApiConstants.MASK_TYPE_LOCK) {
            String typeStr = " [锁定]";
            builder.append(typeStr);
            builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_red)), builder.length() - typeStr.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if ((type & ApiConstants.MASK_TYPE_ASSEMBLE) == ApiConstants.MASK_TYPE_ASSEMBLE) {
            String typeStr = " [合集]";
            builder.append(typeStr);
            builder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.title_blue)), builder.length() - typeStr.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (!TextUtils.isEmpty(entry.getTopicMisc())) {
            String misc = entry.getTopicMisc();
            // ~ 开头的为旧格式
            if (misc.startsWith("~")) {
                handleOldFormat(builder, misc, titleLength);
            } else {
                handleNewFormat(builder, misc, titleLength);
            }
        }

        if (!TextUtils.isEmpty(entry.getBoard())) {
            SpannableStringBuilder boardBuilder = new SpannableStringBuilder();
            boardBuilder.append("  [").append(entry.getBoard()).append("]");
            boardBuilder.setSpan(new ForegroundColorSpan(ContextUtils.getColor(R.color.text_color_disabled)), 0, boardBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(boardBuilder);
        }
        return builder;
    }

}
