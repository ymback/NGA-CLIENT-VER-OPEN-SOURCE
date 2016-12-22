package sp.phone.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode.Callback;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.HashSet;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.util.NetUtil;
import noname.gson.parse.NonameReadBody;
import sp.phone.adapter.NonameArticleListAdapter;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.fragment.ReportDialogFragment;
import sp.phone.proxy.ProxyBridge;

@SuppressLint("DefaultLocale")
public class FunctionUtil {
    static String userDistance = null;
    static String meter = null;
    static String kiloMeter = null;
    static String hide = null;
    static String blacklistban = null;
    static String legend = null;
    static String attachment = null;
    static String comment = null;
    static String sig = null;

    private static void initStaticStrings(Context activity) {
        userDistance = activity.getString(R.string.user_distance);
        meter = activity.getString(R.string.meter);
        kiloMeter = activity.getString(R.string.kilo_meter);
        hide = activity.getString(R.string.hide);
        blacklistban = activity.getString(R.string.blacklistban);
        legend = activity.getString(R.string.legend);
        attachment = activity.getString(R.string.attachment);
        comment = activity.getString(R.string.comment);
        sig = activity.getString(R.string.sig);
    }


    public static void CopyDialog(String content, final Context context, final View scrollview) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.copy_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(view);
        alert.setTitle(R.string.copy_hint);
        final EditText commentdata = (EditText) view
                .findViewById(R.id.copy_data);
        content = content.replaceAll("(?i)" + "<img src='(.+?)'(.+?){0,}>",
                "$1");
        Spanned spanned = Html.fromHtml(content);
        commentdata.setText(spanned);
        commentdata.selectAll();
        alert.setPositiveButton("复制", new DialogInterface.OnClickListener() {
            @SuppressWarnings({"unused", "deprecation"})
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int start = commentdata.getSelectionStart();
                int end = commentdata.getSelectionEnd();
                CharSequence selectText = commentdata.getText().subSequence(
                        start, end);
                Toast toast = null;
                if (selectText.length() > 0) {
                    android.text.ClipboardManager cbm = (android.text.ClipboardManager) context
                            .getSystemService(Activity.CLIPBOARD_SERVICE);
                    cbm.setText(StringUtil.removeBrTag(selectText.toString()));
                    if (toast != null) {
                        toast.setText(R.string.copied_to_clipboard);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(context,
                                R.string.copied_to_clipboard,
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    commentdata.selectAll();
                    if (toast != null) {
                        toast.setText("请选择要复制的内容");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(context, "请选择要复制的内容",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    Field field = dialog.getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(scrollview);
                }
            }

        });
    }

    @SuppressWarnings("static-access")
    public static void handleContentTV(WebView contentTV,
                                       final NonameReadBody row, int position, int bgColor, final Context context, final Callback mActionModeCallback, WebViewClient client) {
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        if (mActionModeCallback != null) {
            contentTV.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    ((ActionBarActivity) context)
                            .startSupportActionMode(mActionModeCallback);
                    return true;
                }

            });
        }
        WebSettings setting = contentTV.getSettings();
        setting.setUserAgentString(context.getString(R.string.clientua) + ((MyApp) ((Activity) context).getApplication()).version);
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.setTag(row.floor);
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.loadDataWithBaseURL(null, fillFormated_html_data(row, position, context),
                "text/html", "utf-8", null);
    }

    @SuppressWarnings("static-access")
    public static void handleContentTV(final WebView contentTV, final MessageArticlePageInfo row, int bgColor, int fgColor, Context context) {
        final WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }


        WebSettings setting = contentTV.getSettings();
        setting.setUserAgentString(context.getString(R.string.clientua) + ((MyApp) ((Activity) context).getApplication()).version);
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);

        contentTV.setTag(row.getLou());
        contentTV.loadDataWithBaseURL(null, row.getFormated_html_data(),
                "text/html", "utf-8", null);
    }

    @SuppressWarnings("static-access")
    public static void handleContentTV(WebView contentTV, final ThreadRowInfo row,
                                       final int position, int bgColor, final Context context, final Callback mActionModeCallback, WebViewClient client) {
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        if (mActionModeCallback != null) {
            contentTV.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    ((ActionBarActivity) context).startSupportActionMode(mActionModeCallback);
                    return true;
                }

            });
        }
//		if (Build.VERSION.SDK_INT >= 11) {
//			contentTV.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//		}
        WebSettings setting = contentTV.getSettings();
        setting.setUserAgentString(context.getString(R.string.clientua) + ((MyApp) ((Activity) context).getApplication()).version);
        setting.setDefaultFontSize(PhoneConfiguration.getInstance().getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(null, row.getFormated_html_data(), "text/html", "utf-8", null);
    }

    public static void errordialogadmin(Context context, final View listView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("这白痴是系统账号,神马都看不到");
        builder.setTitle("看不到");
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }

        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(listView);
                }
            }

        });
    }

    public static void errordialog(Context context, final View listview) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("这白痴匿名了,神马都看不到");
        builder.setTitle("看不到");
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(listview);
                }
            }
        });
    }


    public static void Create_Signature_Dialog_Message(MessageArticlePageInfo row, final Context context, final View scrollview) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.signature_dialog,
                null);
        String name = row.getAuthor();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(view);
        alert.setTitle(name + "的签名");
        // COLOR

        ThemeManager theme = ThemeManager.getInstance();
        int bgColor = context.getResources().getColor(theme.getBackgroundColor(0));
        int fgColor = context.getResources().getColor(theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
        WebView contentTV = (WebView) view.findViewById(R.id.signature);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
                || NetUtil.getInstance().isInWifi();
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV
                .loadDataWithBaseURL(
                        null,
                        FunctionUtil.signatureToHtmlText_Message(row, showImage,
                                ArticleUtil.showImageQuality(), fgColorStr,
                                bgcolorStr, context), "text/html", "utf-8", null);
        alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(scrollview);
                }
            }
        });
    }

    public static String signatureToHtmlText_Message(final MessageArticlePageInfo row,
                                                     boolean showImage, int imageQuality, final String fgColorStr,
                                                     final String bgcolorStr, Context context) {
        initStaticStrings(context);
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = StringUtil.decodeForumTag(row.getSignature(),
                showImage, imageQuality, imageURLSet);
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
        if (StringUtil.isEmpty(ngaHtml)) {
            ngaHtml = "<font color='red'>[" + context.getString(R.string.hide)
                    + "]</font>";
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public static void Create_Signature_Dialog(ThreadRowInfo row, final Context context, final View scrollview) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.signature_dialog,
                null);
        String name = row.getAuthor();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(view);
        alert.setTitle(name + "的签名");
        // COLOR

        ThemeManager theme = ThemeManager.getInstance();
        int bgColor = context.getResources().getColor(theme.getBackgroundColor(0));
        int fgColor = context.getResources().getColor(theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
        WebView contentTV = (WebView) view.findViewById(R.id.signature);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
                || NetUtil.getInstance().isInWifi();
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV
                .loadDataWithBaseURL(
                        null,
                        FunctionUtil.signatureToHtmlText(row, showImage,
                                ArticleUtil.showImageQuality(), fgColorStr,
                                bgcolorStr, context), "text/html", "utf-8", null);
        alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(scrollview);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public static void Create_Vote_Dialog(ThreadRowInfo row, final Context context, final View scrollview, Toast toast) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.vote_dialog, null);
        String name = row.getAuthor();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(view);
        alert.setTitle("投票/投注");
        // COLOR

        ThemeManager theme = ThemeManager.getInstance();
        int bgColor = context.getResources().getColor(theme.getBackgroundColor(0));
        int fgColor = context.getResources().getColor(theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
        final WebView contentTV = (WebView) view.findViewById(R.id.votewebview);
        contentTV.setBackgroundColor(0);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        contentTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
                || NetUtil.getInstance().isInWifi();
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        contentTV.addJavascriptInterface(new ProxyBridge(context, toast), "ProxyBridge");
        contentTV.setFocusableInTouchMode(true);
        contentTV.setFocusable(true);
        contentTV.setHapticFeedbackEnabled(true);
        contentTV.setClickable(true);
        contentTV.requestFocusFromTouch();
        contentTV.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                view.requestFocus(View.FOCUS_DOWN);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                            case MotionEvent.ACTION_UP:
                                if (!v.hasFocus()) {
                                    v.requestFocus(View.FOCUS_DOWN);
                                }
                                break;
                        }
                        return false;
                    }
                });
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final android.webkit.JsResult result) {
                final AlertDialog.Builder b2 = new AlertDialog.Builder(context)
                        .setMessage(message)
                        .setPositiveButton("确定",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                });

                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final android.webkit.JsResult result) {
                final AlertDialog.Builder b1 = new AlertDialog.Builder(
                        context)
                        .setMessage(message)
                        .setPositiveButton("确定",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                })
                        .setNeutralButton("取消",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.cancel();
                                    }
                                })
                        .setOnCancelListener(
                                new AlertDialog.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        result.cancel();
                                    }
                                });
                b1.create();
                b1.show();
                return true;
            }
        });
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                FunctionUtil.VoteToHtmlText(row, showImage, ArticleUtil.showImageQuality(),
                        fgColorStr, bgcolorStr), "text/html", "utf-8", null);
        contentTV.requestLayout();
        alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final Dialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(scrollview);
                }
            }
        });
    }

    public static void Create_Avatar_Dialog_Meaasge(MessageArticlePageInfo row, Context context, final View scrollview) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.signature_dialog,
                null);
        String name = row.getAuthor();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(view);
        alert.setTitle(name + "的头像");
        // COLOR

        ThemeManager theme = ThemeManager.getInstance();
        int bgColor = context.getResources().getColor(theme.getBackgroundColor(0));
        int fgColor = context.getResources().getColor(theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
        WebView contentTV = (WebView) view.findViewById(R.id.signature);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                FunctionUtil.avatarToHtmlText_Message(row, true, ArticleUtil.showImageQuality(),
                        fgColorStr, bgcolorStr, context), "text/html", "utf-8", null);
        alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(scrollview);
                }
            }

        });
    }

    public static void Create_Avatar_Dialog(ThreadRowInfo row, Context context, final View scrollview) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.signature_dialog,
                null);
        String name = row.getAuthor();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(view);
        alert.setTitle(name + "的头像");
        // COLOR

        ThemeManager theme = ThemeManager.getInstance();
        int bgColor = context.getResources().getColor(theme.getBackgroundColor(0));
        int fgColor = context.getResources().getColor(theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        WebViewClient client = new ArticleListWebClient((FragmentActivity) context);
        WebView contentTV = (WebView) view.findViewById(R.id.signature);
        contentTV.setBackgroundColor(0);
        contentTV.setFocusableInTouchMode(false);
        contentTV.setFocusable(false);
        if (ActivityUtil.isGreaterThan_2_2()) {
            contentTV.setLongClickable(false);
        }
        WebSettings setting = contentTV.getSettings();
        setting.setDefaultFontSize(PhoneConfiguration.getInstance()
                .getWebSize());
        setting.setJavaScriptEnabled(false);
        contentTV.setWebViewClient(client);
        contentTV.loadDataWithBaseURL(
                null,
                FunctionUtil.avatarToHtmlText(row, true, ArticleUtil.showImageQuality(),
                        fgColorStr, bgcolorStr, context), "text/html", "utf-8", null);
        alert.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(scrollview);
                }
            }

        });
    }

    public static void handleNickName(MessageArticlePageInfo row, int fgColor,
                                      TextView nickNameTV, Context context) {
        initStaticStrings(context);
        String nickName = row.getAuthor();
        // int now = 0;
        if ("-1".equals(row.getYz()))// nuked
        {
            fgColor = nickNameTV.getResources().getColor(R.color.title_red);
            nickName += "(VIP)";
        } else if (!StringUtil.isEmpty(row.getMute_time())
                && !"0".equals(row.getMute_time())) {
            fgColor = nickNameTV.getResources().getColor(R.color.title_orange);
            nickName += "(" + legend + ")";
        }
        nickNameTV.setText(nickName);
        TextPaint tp = nickNameTV.getPaint();
        tp.setFakeBoldText(true);// bold for Chinese character
        nickNameTV.setTextColor(fgColor);
    }


    public static void handleNickName(NonameReadBody row, int fgColor,
                                      TextView nickNameTV) {

        String nickName = row.hip;
        nickNameTV.setText(nickName);
        TextPaint tp = nickNameTV.getPaint();
        tp.setFakeBoldText(true);// bold for Chinese character
        nickNameTV.setTextColor(fgColor);
    }

    public static void handleNickName(ThreadRowInfo row, int fgColor,
                                      TextView nickNameTV, Context context) {
        initStaticStrings(context);
        String nickName = row.getAuthor();
        // int now = 0;
        if ("-1".equals(row.getYz()))// nuked
        {
            fgColor = nickNameTV.getResources().getColor(R.color.title_red);
            nickName += "(VIP)";
        } else if (!StringUtil.isEmpty(row.getMute_time())
                && !"0".equals(row.getMute_time())) {
            fgColor = nickNameTV.getResources().getColor(R.color.title_orange);
            nickName += "(" + legend + ")";
        }
        if (row.get_isInBlackList()) {
            fgColor = nickNameTV.getResources().getColor(R.color.title_orange);
            nickName += "(" + blacklistban + ")";
        }
        nickNameTV.setText(nickName);
        TextPaint tp = nickNameTV.getPaint();
        tp.setFakeBoldText(true);// bold for Chinese character
        nickNameTV.setTextColor(fgColor);
    }

    public static String fillFormated_html_data(NonameReadBody row, int i, Context context) {

        ThemeManager theme = ThemeManager.getInstance();

        int bgColor = context.getResources().getColor(
                theme.getBackgroundColor(row.floor % 2));
        int fgColor = context.getResources().getColor(
                theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        String formated_html_data = NonameArticleListAdapter.convertToHtmlText(row, isShowImage(), showImageQuality(), fgColorStr, bgcolorStr, context);
        return formated_html_data;
    }

    public static void fillFormated_html_data(ThreadRowInfo row, int i, Context context) {
        ThemeManager theme = ThemeManager.getInstance();
        if (row.getContent() == null) {
            row.setContent(row.getSubject());
            row.setSubject(null);
        }
        if (!StringUtil.isEmpty(row.getFromClient())) {
            if (row.getFromClient().startsWith("103 ") && !StringUtil.isEmpty(row.getContent())) {
                row.setContent(StringUtil.unescape(row.getContent()));
            }
        }
        int bgColor = context.getResources().getColor(theme.getBackgroundColor(i));
        int fgColor = context.getResources().getColor(theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        String formated_html_data = HtmlUtil.convertToHtmlText(row, isShowImage(), showImageQuality(), fgColorStr, bgcolorStr, context);
        row.setFormated_html_data(formated_html_data);
    }

    public static boolean isShowImage() {
        return PhoneConfiguration.getInstance().isDownImgNoWifi() || NetUtil.getInstance().isInWifi();
    }

    public static int showImageQuality() {
        if (NetUtil.getInstance().isInWifi()) {
            return 0;
        } else {
            return PhoneConfiguration.getInstance().imageQuality;
        }
    }

    public static String signatureToHtmlText(final ThreadRowInfo row,
                                             boolean showImage, int imageQuality, final String fgColorStr,
                                             final String bgcolorStr, Context context) {
        initStaticStrings(context);
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = StringUtil.decodeForumTag(row.getSignature(),
                showImage, imageQuality, imageURLSet);
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
        if (StringUtil.isEmpty(ngaHtml)) {
            ngaHtml = row.getAlterinfo();
        }
        if (StringUtil.isEmpty(ngaHtml)) {
            ngaHtml = "<font color='red'>[" + context.getString(R.string.hide)
                    + "]</font>";
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public static String VoteToHtmlText(final ThreadRowInfo row, boolean showImage,
                                        int imageQuality, final String fgColorStr, final String bgcolorStr) {
        if (StringUtil.isEmpty(row.getVote()))
            return "本楼没有投票/投注内容";
        String ngaHtml = String.valueOf(row.getTid()) + ",'" + row.getVote()
                + "'";
        ngaHtml = "<!DOCTYPE html><html><head><meta http-equiv=Content-Type content=\"text/html;charset=utf-8\">"
                + "<script type=\"text/javascript\" src=\"file:///android_asset/vote/vote.js\"></script><link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/vote/vote.css\" />"
                + " </head><body style=\"color:#" + fgColorStr + "\"bgcolor= '#"
                + bgcolorStr
                + "'><span id='votec'></span><script>vote("
                + ngaHtml
                + ")</script></body></html>";
        return ngaHtml;
    }


    public static String parseAvatarUrl(String js_escap_avatar) {
        // "js_escap_avatar":"{ \"t\":1,\"l\":2,\"0\":{ \"0\":\"http://pic2.178.com/53/533387/month_1109/93ba4788cc8c7d6c75453fa8a74f3da6.jpg\",\"cX\":0.47,\"cY\":0.78},\"1\":{ \"0\":\"http://pic2.178.com/53/533387/month_1108/8851abc8674af3adc622a8edff731213.jpg\",\"cX\":0.49,\"cY\":0.68}}"
        if (null == js_escap_avatar)
            return null;

        int start = js_escap_avatar.indexOf("http");
        if (start == 0 || start == -1)
            return js_escap_avatar;
        int end = js_escap_avatar.indexOf("\"", start);//
        if (end == -1)
            end = js_escap_avatar.length();
        String ret = null;
        try {
            ret = js_escap_avatar.substring(start, end);
        } catch (Exception e) {
            Log.e("FunctionUtil", "cann't handle avatar url " + js_escap_avatar);
        }
        return ret;
    }

//	public static String findimgonphone(String avatarlocalurl){
//		
//	}

    public static String avatarToHtmlText_Message(final MessageArticlePageInfo row, boolean showImage,
                                                  int imageQuality, final String fgColorStr, final String bgcolorStr, Context context) {
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = null;
        initStaticStrings(context);
        if (row.getJs_escap_avatar().equals("")) {
            ngaHtml = StringUtil
                    .decodeForumTag(
                            "这家伙是骷髅党,头像什么的没有啦~<br/><img src='file:///android_asset/default_avatar.png' style= 'max-width:100%;' >",
                            showImage, imageQuality, imageURLSet);
        } else {
            ngaHtml = StringUtil.decodeForumTag(
                    "[img]" + parseAvatarUrl(row.getJs_escap_avatar())
                            + "[/img]", showImage, imageQuality, imageURLSet);
        }
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
        if (StringUtil.isEmpty(ngaHtml)) {
            ngaHtml = "<font color='red'>[" + context.getString(R.string.hide)
                    + "]</font>";
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public static String avatarToHtmlText(final ThreadRowInfo row, boolean showImage,
                                          int imageQuality, final String fgColorStr, final String bgcolorStr, Context context) {
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = null;
        initStaticStrings(context);
        if (row.getJs_escap_avatar().equals("")) {
            ngaHtml = StringUtil
                    .decodeForumTag(
                            "这家伙是骷髅党,头像什么的没有啦~<br/><img src='file:///android_asset/default_avatar.png' style= 'max-width:100%;' >",
                            showImage, imageQuality, imageURLSet);
        } else {
            ngaHtml = StringUtil.decodeForumTag(
                    "[img]" + parseAvatarUrl(row.getJs_escap_avatar())
                            + "[/img]", showImage, imageQuality, imageURLSet);
        }
        if (imageURLSet.size() == 0) {
            imageURLSet = null;
        }
        if (StringUtil.isEmpty(ngaHtml)) {
            ngaHtml = row.getAlterinfo();
        }
        if (StringUtil.isEmpty(ngaHtml)) {
            ngaHtml = "<font color='red'>[" + context.getString(R.string.hide)
                    + "]</font>";
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr + "' size='2'>" + ngaHtml + "</font></body>";

        return ngaHtml;
    }

    public static boolean isComment(ThreadRowInfo row) {

        return row.getAlterinfo() == null && row.getAttachs() == null
                && row.getComments() == null
                && row.getJs_escap_avatar() == null && row.getLevel() == null
                && row.getSignature() == null;
    }

    public static void handleReport(ThreadRowInfo row, int tid, FragmentManager fm) {

        DialogFragment df = new ReportDialogFragment();
        Bundle args = new Bundle();
        args.putInt("tid", tid);
        args.putInt("pid", row.getPid());
        df.setArguments(args);
        df.show(fm, null);

    }

    public static void start_send_message(Context context, ThreadRowInfo row) {
        Intent intent_bookmark = new Intent();
        intent_bookmark.putExtra("to", row.getAuthor());
        intent_bookmark.putExtra("action", "new");
        intent_bookmark.putExtra("messagemode", "yes");
        if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
            intent_bookmark.setClass(context,
                    PhoneConfiguration.getInstance().messagePostActivityClass);
        } else {
            intent_bookmark.setClass(context,
                    PhoneConfiguration.getInstance().loginActivityClass);
        }
        context.startActivity(intent_bookmark);
    }


    public static String checkContent(String content) {
        int i;
        boolean mode = false;
        content = content.trim();
        String quotekeyword[][] = {
                {"[customachieve]", "[/customachieve]"},// 0
                {"[wow", "]]"},
                {"[lol", "]]"},
                {"[cnarmory", "]"},
                {"[usarmory", "]"},
                {"[twarmory", "]"},// 5
                {"[euarmory", "]"},
                {"[url", "[/url]"},
                {"[color=", "[/color]"},
                {"[size=", "[/size]"},
                {"[font=", "[/font]"},// 10
                {"[b]", "[/b]"},
                {"[u]", "[/u]"},
                {"[i]", "[/i]"},
                {"[del]", "[/del]"},
                {"[align=", "[/align]"},// 15
                {"[h]", "[/h]"},
                {"[l]", "[/l]"},
                {"[r]", "[/r]"},
                {"[list", "[/list]"},
                {"[img]", "[/img]"},// 20
                {"[album=", "[/album]"},
                {"[code]", "[/code]"},
                {"[code=lua]", "[/code] lua"},
                {"[code=php]", "[/code] php"},
                {"[code=c]", "[/code] c"},// 25
                {"[code=js]", "[/code] javascript"},
                {"[code=xml]", "[/code] xml/html"},
                {"[flash]", "[/flash]"},
                {"[table]", "[/table]"},
                {"[tid", "[/tid]"},// 30
                {"[pid", "[/pid]"}, {"[dice]", "[/dice]"},
                {"[crypt]", "[/crypt]"},
                {"[randomblock]", "[/randomblock]"}, {"[@", "]"},
                {"[t.178.com/", "]"}, {"[collapse", "[/collapse]"},};
        while (content.startsWith("\n")) {
            content = content.replaceFirst("\n", "");
        }
        if (content.length() > 100) {
            content = content.substring(0, 99);
            mode = true;
        }
        for (i = 0; i < 38; i++) {
            while (content.toLowerCase().lastIndexOf(quotekeyword[i][0]) > content
                    .toLowerCase().lastIndexOf(quotekeyword[i][1])) {
                content = content.substring(0, content.toLowerCase()
                        .lastIndexOf(quotekeyword[i][0]));
            }
        }
        if (mode) {
            content = content + "......";
        }
        return content.toString();
    }


    @SuppressWarnings("unused")
    public static String ColorTxt(String bodyString) {
        while (bodyString.startsWith("\n")) {
            bodyString = bodyString.substring(1);
        }
        String existquotetxt = "";
        if (bodyString.toLowerCase().indexOf("[quote]") == 0) {
            existquotetxt = bodyString.substring(0, bodyString.toLowerCase().indexOf("[/quote]")) + "[/quote]";
            bodyString = bodyString.substring(bodyString.toLowerCase().indexOf("[/quote]") + 8);
        }
        int i, ia, ib, itmp = 0, bslenth, tmplenth;
        bslenth = bodyString.length();


        String scolor[] = {"[color=skyblue]", "[color=royalblue]", "[color=blue]", "[color=darkblue]", "[color=orange]", "[color=orangered]", "[color=crimson]", "[color=red]", "[color=firebrick]", "[color=darkred]", "[color=green]", "[color=limegreen]", "[color=seagreen]", "[color=teal]", "[color=deeppink]", "[color=tomato]", "[color=coral]", "[color=purple]", "[color=indigo]", "[color=burlywood]", "[color=sandybrown]", "[color=sienna]", "[color=chocolate]", "[color=silver]"};
        String keyword[][] = {
                {"[customachieve]", "[/customachieve]", "16"},
                {"[wow", "]]", "2"},
                {"[lol", "]]", "2"},
                {"[cnarmory", "]", "1"},
                {"[usarmory", "]", "1"},
                {"[twarmory", "]", "1"},
                {"[euarmory", "]", "1"},
                {"[url", "[/url]", "6"},
                {"[size=", "]", "1"},
                {"[/size]", "[/size]", "7"},
                {"[font=", "]", "1"},
                {"[/font]", "[/font]", "7"},
                {"[b]", "[b]", "3"},
                {"[/b]", "[/b]", "4"},
                {"[u]", "[u]", "3"},
                {"[/u]", "[/u]", "4"},
                {"[i]", "[i]", "3"},
                {"[/i]", "[/i]", "4"},
                {"[del]", "[del]", "5"},
                {"[/del]", "[/del]", "6"},
                {"[align", "]", "1"},
                {"[/align]", "[/align]", "8"},
                {"[l]", "[l]", "3"},
                {"[/l]", "[/l]", "4"},
                {"[h]", "[h]", "3"},
                {"[/h]", "[/h]", "4"},
                {"[r]", "[r]", "3"},
                {"[/r]", "[/r]", "4"},
                {"[img]", "[/img]", "6"},
                {"[album=", "[/album]", "8"},
                {"[code]", "[/code]", "7"},
                {"[code=lua]", "[/code] lua", "11"},
                {"[code=php]", "[/code] php", "11"},
                {"[code=c]", "[/code] c", "9"},
                {"[code=js]", "[/code] javascript", "18"},
                {"[code=xml]", "[/code] xml/html", "16"},
                {"[flash]", "[/flash]", "8"},
                {"[table]", "[table]", "7"},
                {"[/table]", "[/table]", "8"},
                {"[tid", "[/tid]", "6"},
                {"[pid", "[/pid]", "6"},
                {"[dice]", "[/dice]", "7"},
                {"[crypt]", "[/crypt]", "8"},
                {"[randomblock]", "[randomblock]", "13"},
                {"[/randomblock]", "[/randomblock]", "14"},
                {"[@", "]", "1"},
                {"[t.178.com/", "]", "1"},
                {"[tr]", "[tr]", "4"},
                {"[/tr]", "[/tr]", "5"},
                {"[td", "]", "1"},
                {"[/td]", "[/td]", "5"},
                {"[*]", "[*]", "3"},
                {"[list", "]", "1"},
                {"[/list]", "[/list]", "7"},
                {"[collapse", "]", "1"},
                {"[/collapse]", "[/collapse]", "11"}};
        char[] arrtxtchar = bodyString.toCharArray();
        String txtsendout = scolor[(int) (Math.random() * 23)];
        String quotetxt = "";
        for (i = 0; i < bslenth; i++) {
            if (Character.toString(arrtxtchar[i]).equals("\n") == false && Character.toString(arrtxtchar[i]).equals("[") == false && Character.toString(arrtxtchar[i]).equals(" ") == false) {
                txtsendout += arrtxtchar[i] + "[/color]" + scolor[(int) (Math.random() * 23)];/*开始就是普通文字的话就直接加彩色字体了*/
            } else if (Character.toString(arrtxtchar[i]).equals("[")) {//首字符是[要判断
                if (bodyString.toLowerCase().indexOf("[quote]", i - 1) == i) {//是引用的话
                    if (bodyString.toLowerCase().indexOf("[quote]", i - 1) > bodyString.toLowerCase().indexOf("[/quote]", i - 1)) {//这个他妈的引用没完
                        quotetxt = bodyString.substring(i + 7);
                        if (quotetxt.toLowerCase().lastIndexOf("[") >= 0) {//最后还有点留下来
                            quotetxt = quotetxt.substring(0, quotetxt.toLowerCase().lastIndexOf("["));
                        }
                        while (quotetxt.endsWith(".")) {
                            quotetxt = quotetxt.substring(0, quotetxt.length() - 1);
                        }
                        bslenth = bodyString.length();
                        txtsendout = txtsendout.substring(0, txtsendout.toLowerCase().lastIndexOf("[color"));
                        quotetxt = "[quote]" + FunctionUtil.checkContent(quotetxt) + "[/quote]";
                        txtsendout += quotetxt + scolor[(int) (Math.random() * 23)];
                        break;
                    } else {
                        quotetxt = bodyString.substring(i + 7, bodyString.toLowerCase().indexOf("[/quote]", i));
                        while (quotetxt.endsWith(".")) {
                            quotetxt = quotetxt.substring(0, quotetxt.length() - 1);
                        }
                        txtsendout = txtsendout.substring(0, txtsendout.toLowerCase().lastIndexOf("[color"));
                        quotetxt = "[quote]" + FunctionUtil.checkContent(quotetxt) + "[/quote]";
                        txtsendout += quotetxt + scolor[(int) (Math.random() * 23)];
                        i = bodyString.toLowerCase().indexOf("[/quote]", i) + 7;
                    }
                } else if (bodyString.toLowerCase().indexOf("[color", i - 1) == i) {
                    if (bodyString.toLowerCase().indexOf("[/color]", i) >= 0) {
                        txtsendout += bodyString.substring(bodyString.indexOf("]", i) + 1, bodyString.toLowerCase().indexOf("[/color]", i) + 8) + scolor[(int) (Math.random() * 23)];
                        i = bodyString.indexOf("[/color]", i) + 7;
                    } else {
                        bodyString = bodyString.substring(0, i) + bodyString.substring(bodyString.toLowerCase().indexOf("]", i) + 1, bslenth);
                        i = bodyString.indexOf("]", i);
                    }
                } else {
                    for (ia = 0; ia < 56; ia++) {
                        if (bodyString.toLowerCase().indexOf(keyword[ia][0], i - 1) == i) {
                            if (bodyString.toLowerCase().indexOf(keyword[ia][1], i) >= 0) {
                                txtsendout = txtsendout.substring(0, txtsendout.toLowerCase().lastIndexOf("[color"));
                                txtsendout += bodyString.substring(i, bodyString.toLowerCase().indexOf(keyword[ia][1], i)) + keyword[ia][1] + scolor[(int) (Math.random() * 23)];
                                i = bodyString.toLowerCase().indexOf(keyword[ia][1], i) + Integer.parseInt(keyword[ia][2]) - 1;
                            } else {
                                itmp = bodyString.indexOf("]", i);
                                bodyString = bodyString.substring(0, i) + bodyString.substring(bodyString.toLowerCase().indexOf("]", i) + 1, bslenth);
                                i = itmp;
                            }
                            break;
                        }
                    }
                }
            } else if (Character.toString(arrtxtchar[i]).equals(" ") || Character.toString(arrtxtchar[i]).equals("\n")) {
                txtsendout = txtsendout.substring(0, txtsendout.toLowerCase().lastIndexOf("[color"));
                txtsendout += bodyString.substring(i, i + 1) + scolor[(int) (Math.random() * 23)];
            }
        }
        if (txtsendout.toLowerCase().lastIndexOf("[color") >= 0) {
            txtsendout = txtsendout.substring(0, txtsendout.toLowerCase().lastIndexOf("[color"));
        }
        txtsendout = existquotetxt + txtsendout.replaceAll("&nbsp;", " ").trim();
        return txtsendout.toString();
    }

    public static String ColorTxtCheck(String text) {
        String xxtp = "";
        if (PhoneConfiguration.getInstance().showColortxt) {
            xxtp = FunctionUtil.ColorTxt(text.trim());
        } else {
            xxtp = text;
        }
        return xxtp.toString();
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static void handleSupertext(final EditText bodyText, final Context context, final View v) {
        final int index = bodyText.getSelectionStart();
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.supertext_dialog,
                null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(view);
        alert.setTitle(R.string.supertext_hint);
        final Spinner fontcolorSpinner;
        final Spinner fontsizeSpinner;
        RadioGroup selectradio;
        final TextView font_size;
        final TextView font_color;
        final RadioButton atsomeone_button;
        final RadioButton urladd_button;
        final RadioButton quoteadd_button;
        final CheckBox bold_checkbox;// 加粗
        final CheckBox italic_checkbox;// 斜体
        final CheckBox underline_checkbox;// 下划线
        final CheckBox fontcolor_checkbox;
        final CheckBox fontsize_checkbox;
        final CheckBox delline_checkbox;

        font_size = (TextView) view.findViewById(R.id.font_size);
        font_color = (TextView) view.findViewById(R.id.font_color);
        atsomeone_button = (RadioButton) view.findViewById(R.id.atsomeone);
        urladd_button = (RadioButton) view.findViewById(R.id.urladd);
        quoteadd_button = (RadioButton) view.findViewById(R.id.quoteadd);
        selectradio = (RadioGroup) view.findViewById(R.id.radioGroupA);
        bold_checkbox = (CheckBox) view.findViewById(R.id.bold);
        italic_checkbox = (CheckBox) view.findViewById(R.id.italic);
        underline_checkbox = (CheckBox) view.findViewById(R.id.underline);
        underline_checkbox.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        underline_checkbox.getPaint().setAntiAlias(true);// 抗锯齿
        fontcolor_checkbox = (CheckBox) view.findViewById(R.id.fontcolor);
        fontsize_checkbox = (CheckBox) view.findViewById(R.id.fontsize);
        delline_checkbox = (CheckBox) view.findViewById(R.id.delline);
        delline_checkbox.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        delline_checkbox.getPaint().setAntiAlias(true);// 抗锯齿

        final float defaultFontSize = font_size.getTextSize();

        final EditText input = (EditText) view
                .findViewById(R.id.inputsupertext_dataa);

        fontcolorSpinner = (Spinner) view.findViewById(R.id.font_color_spinner);
        fontsizeSpinner = (Spinner) view.findViewById(R.id.fontsize_spinner);
        final int scolorspan[] = {-16777216, -7876885, -13149723, -16776961,
                -16777077, -23296, -47872, -2354116, -65536, -5103070,
                -7667712, -16744448, -13447886, -13726889, -16744320, -60269,
                -40121, -32944, -8388480, -11861886, -2180985, -744352,
                -6270419, -2987746, -4144960,};

        final String scolor[] = {"[color=skyblue]", "[color=royalblue]",
                "[color=blue]", "[color=darkblue]", "[color=orange]",
                "[color=orangered]", "[color=crimson]", "[color=red]",
                "[color=firebrick]", "[color=darkred]", "[color=green]",
                "[color=limegreen]", "[color=seagreen]", "[color=teal]",
                "[color=deeppink]", "[color=tomato]", "[color=coral]",
                "[color=purple]", "[color=indigo]", "[color=burlywood]",
                "[color=sandybrown]", "[color=sienna]", "[color=chocolate]",
                "[color=silver]"};
        final String ssize[] = {"[size=100%]", "[size=110%]", "[size=120%]",
                "[size=130%]", "[size=150%]", "[size=200%]", "[size=250%]",
                "[size=300%]", "[size=400%]", "[size=500%]"};
        final float ssizespan[] = {1.0f, 1.1f, 1.2f, 1.3f, 1.5f, 2.0f, 2.5f,
                3.0f, 4.0f, 5.0f, 1.2f};

        BaseAdapter adapterfontcolor = new BaseAdapter() {

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return view.getResources().getStringArray(R.array.colorchoose).length; // 选项总个数
            }

            @Override
            public Object getItem(int arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public View getView(int arg0, View arg1, ViewGroup arg2) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向
                TextView tv = new TextView(context);
                tv.setText(view.getResources().getStringArray(
                        R.array.colorchoose)[arg0]);// 设置内容
                tv.setTextColor(scolorspan[arg0]);// 设置字体颜色
                ll.addView(tv); // 添加到LinearLayout中
                return ll;
            }
        };
        fontcolorSpinner.setAdapter(adapterfontcolor);
        fontcolorSpinner.setSelection(0);

        BaseAdapter adapterfontsize = new BaseAdapter() {

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return view.getResources().getStringArray(
                        R.array.fontsizechoose).length; // 选项总个数
            }

            @Override
            public Object getItem(int arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public View getView(int arg0, View arg1, ViewGroup arg2) {
                // TODO Auto-generated method stub
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL); // 设置朝向
                TextView tv = new TextView(context);
                tv.setText(view.getResources().getStringArray(
                        R.array.fontsizechoose)[arg0]);// 设置内容
                tv.setTextSize(ssizespan[arg0] * defaultFontSize);// 设置字体大小
                ll.addView(tv); // 添加到LinearLayout中
                return ll;
            }
        };
        fontsizeSpinner.setAdapter(adapterfontsize);
        fontsizeSpinner.setSelection(0);

        // 开始下面两个选项没有的
        font_size.setVisibility(View.GONE);
        font_color.setVisibility(View.GONE);
        fontsizeSpinner.setVisibility(View.GONE);
        fontcolorSpinner.setVisibility(View.GONE);
        // 选中上面那排，下面的就不能选
        selectradio
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // TODO Auto-generated method stub
                        bold_checkbox.setChecked(false);
                        italic_checkbox.setChecked(false);
                        underline_checkbox.setChecked(false);
                        fontcolor_checkbox.setChecked(false);
                        fontsize_checkbox.setChecked(false);
                        delline_checkbox.setChecked(false);
                        font_size.setVisibility(View.GONE);
                        font_color.setVisibility(View.GONE);
                        fontsizeSpinner.setVisibility(View.GONE);
                        fontcolorSpinner.setVisibility(View.GONE);
                    }

                });

        // 选中下面那排，上面的别选了,加粗
        bold_checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            atsomeone_button.setChecked(false);
                            urladd_button.setChecked(false);
                            quoteadd_button.setChecked(false);
                        }
                    }

                });

        // 选中下面那排，上面的别选了，斜体
        italic_checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            atsomeone_button.setChecked(false);
                            urladd_button.setChecked(false);
                            quoteadd_button.setChecked(false);
                        }
                    }

                });

        // 选中下面那排，上面的别选了，下划线
        underline_checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            atsomeone_button.setChecked(false);
                            urladd_button.setChecked(false);
                            quoteadd_button.setChecked(false);
                        }
                    }

                });

        // 选中下面那排，上面的别选了，颜色
        fontcolor_checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            atsomeone_button.setChecked(false);
                            urladd_button.setChecked(false);
                            quoteadd_button.setChecked(false);
                            font_color.setVisibility(View.VISIBLE);
                            fontcolorSpinner.setVisibility(View.VISIBLE);
                        } else {
                            font_color.setVisibility(View.GONE);
                            fontcolorSpinner.setVisibility(View.GONE);
                        }
                    }

                });

        // 选中下面那排，上面的别选了，字号
        fontsize_checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            atsomeone_button.setChecked(false);
                            urladd_button.setChecked(false);
                            quoteadd_button.setChecked(false);
                            font_size.setVisibility(View.VISIBLE);
                            fontsizeSpinner.setVisibility(View.VISIBLE);
                        } else {
                            font_size.setVisibility(View.GONE);
                            fontsizeSpinner.setVisibility(View.GONE);
                        }
                    }

                });

        // 选中下面那排，上面的别选了，删除线
        delline_checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            atsomeone_button.setChecked(false);
                            urladd_button.setChecked(false);
                            quoteadd_button.setChecked(false);
                        }
                    }

                });

        alert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @SuppressWarnings("unused")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputdata = input.getText().toString();
                Toast toast = null;
                // TODO Auto-generated method stub
                if (!inputdata.replaceAll("\\n", "").trim().equals("")) {
                    if (atsomeone_button.isChecked()) {
                        inputdata = "[@" + inputdata + "]";
                    } else if (urladd_button.isChecked()) {
                        if (inputdata.startsWith("http:")
                                || inputdata.startsWith("https:")
                                || inputdata.startsWith("ftp:")
                                || inputdata.startsWith("gopher:")
                                || inputdata.startsWith("news:")
                                || inputdata.startsWith("telnet:")
                                || inputdata.startsWith("mms:")
                                || inputdata.startsWith("rtsp:")) {
                            inputdata = "[url]" + inputdata + "[/url]";
                        } else {

                            if (toast != null) {
                                toast.setText("URL需以http|https|ftp|gopher|news|telnet|mms|rtsp开头");
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                toast = Toast
                                        .makeText(
                                                context,
                                                "URL需以http|https|ftp|gopher|news|telnet|mms|rtsp开头",
                                                Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    } else if (quoteadd_button.isChecked()) {
                        inputdata = "[quote]" + inputdata + "[/quote]";
                    } else {
                        if (fontcolor_checkbox.isChecked()) {
                            if (fontcolorSpinner.getSelectedItemPosition() > 0) {
                                inputdata = scolor[fontcolorSpinner
                                        .getSelectedItemPosition() - 1]
                                        + inputdata + "[/color]";
                            }
                        }
                        if (italic_checkbox.isChecked()) {
                            inputdata = "[i]" + inputdata + "[/i]";
                        }
                        if (bold_checkbox.isChecked()) {
                            inputdata = "[b]" + inputdata + "[/b]";
                        }
                        if (underline_checkbox.isChecked()) {
                            inputdata = "[u]" + inputdata + "[/u]";
                        }
                        if (delline_checkbox.isChecked()) {
                            inputdata = "[del]" + inputdata + "[/del]";
                        }
                        if (fontsize_checkbox.isChecked()) {
                            if (fontsizeSpinner.getSelectedItemPosition() < 10) {
                                inputdata = ssize[fontsizeSpinner
                                        .getSelectedItemPosition()]
                                        + inputdata
                                        + "[/size]";
                            } else {
                                inputdata = "[h]" + inputdata + "[/h]";
                            }
                        }
                    }
                    SpannableString spanString = new SpannableString(inputdata);

                    if (atsomeone_button.isChecked()) {
                        spanString.setSpan(new ForegroundColorSpan(Color.BLUE),
                                0, inputdata.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (urladd_button.isChecked()) {
                        if (input.getText().toString().startsWith("http:")
                                || input.getText().toString()
                                .startsWith("https:")
                                || input.getText().toString()
                                .startsWith("ftp:")
                                || input.getText().toString()
                                .startsWith("gopher:")
                                || input.getText().toString()
                                .startsWith("news:")
                                || input.getText().toString()
                                .startsWith("telnet:")
                                || input.getText().toString()
                                .startsWith("mms:")
                                || input.getText().toString()
                                .startsWith("rtsp:")) {
                            spanString.setSpan(new URLSpan(input.getText()
                                            .toString()), 0, inputdata.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    } else if (quoteadd_button.isChecked()) {
                        spanString.setSpan(new BackgroundColorSpan(-1513240),
                                0, inputdata.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        if (fontcolor_checkbox.isChecked()) {
                            spanString.setSpan(
                                    new ForegroundColorSpan(
                                            scolorspan[fontcolorSpinner
                                                    .getSelectedItemPosition()]),
                                    0, inputdata.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (italic_checkbox.isChecked()) {
                            spanString.setSpan(new StyleSpan(
                                            android.graphics.Typeface.ITALIC), 0,
                                    inputdata.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (bold_checkbox.isChecked()) {
                            spanString.setSpan(new StyleSpan(
                                            android.graphics.Typeface.BOLD), 0,
                                    inputdata.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (underline_checkbox.isChecked()) {
                            spanString.setSpan(new UnderlineSpan(), 0,
                                    inputdata.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (delline_checkbox.isChecked()) {
                            spanString.setSpan(new StrikethroughSpan(), 0,
                                    inputdata.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (fontsize_checkbox.isChecked()) {
                            if (fontsizeSpinner.getSelectedItemPosition() < 10) {
                                spanString.setSpan(
                                        new RelativeSizeSpan(
                                                ssizespan[fontsizeSpinner
                                                        .getSelectedItemPosition()]),
                                        0, inputdata.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else {
                                spanString.setSpan(new BackgroundColorSpan(
                                                Color.GRAY), 0, inputdata.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spanString.setSpan(new RelativeSizeSpan(1.2f),
                                        0, inputdata.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            }
                        }
                    }
                    if (bodyText.getText().toString().replaceAll("\\n", "")
                            .trim().equals("")) {// NO INPUT DATA
                        bodyText.setText("");
                        bodyText.append(spanString);
                    } else {
                        if (index <= 0 || index >= bodyText.length()) {// pos @
                            // begin
                            // / end
                            bodyText.append(spanString);
                        } else {
                            bodyText.getText().insert(index, spanString);
                        }
                    }
                    InputMethodManager imm = (InputMethodManager) bodyText
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                } else {
                    bodyText.setFocusableInTouchMode(true);
                    bodyText.setFocusable(true);
                    InputMethodManager imm = (InputMethodManager) bodyText
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    dialog.dismiss();
                }
            }
        });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                InputMethodManager imm = (InputMethodManager) bodyText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(v);
                }
            }

        });
    }// OK

    public static String getngaClientChecksum(Context context) {
        String str = null;
        String secret = context
                .getString(R.string.checksecret);
        try {
            str = MD5Util.MD5(new StringBuilder(String
                    .valueOf(PhoneConfiguration.getInstance().getUid()))
                    .append(secret).append(System.currentTimeMillis() / 1000L)
                    .toString())
                    + System.currentTimeMillis() / 1000L;
            return str;
        } catch (Exception localException) {
            while (true)
                str = MD5Util.MD5(new StringBuilder(secret).append(
                        System.currentTimeMillis() / 1000L).toString())
                        + System.currentTimeMillis() / 1000L;
        }
    }
}
