package sp.phone.listener;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class MyListenerForClient implements OnClickListener {
    int mPosition;
    ThreadData mdata = new ThreadData();
    Context mcontext;
    View mscrollview;

    public MyListenerForClient(int inPosition, ThreadData data, Context context, View scrollview) {
        mPosition = inPosition;
        mdata = data;
        mcontext = context;
        mscrollview = scrollview;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {

        ThreadRowInfo row = mdata.getRowList().get(mPosition);
        String from_client = row.getFromClient();
        String client_model = row.getFromClientModel();
        String deviceinfo = null;
        if (!StringUtil.isEmpty(client_model)) {
            String clientappcode = "";
            if (from_client.indexOf(" ") < 0) {
                clientappcode = from_client;
            } else {
                clientappcode = from_client.substring(0,
                        from_client.indexOf(" "));
            }
            if (clientappcode.equals("1")) {
                if (from_client.length() <= 2) {
                    deviceinfo = "发送自Life Style苹果客户端 机型及系统:未知";
                } else {
                    deviceinfo = "发送自Life Style苹果客户端 机型及系统:"
                            + from_client.substring(2);
                }
            } else if (clientappcode.equals("7")) {
                if (from_client.length() <= 2) {
                    deviceinfo = "发送自NGA苹果官方客户端 机型及系统:未知";
                } else {
                    deviceinfo = "发送自NGA苹果官方客户端 机型及系统:"
                            + from_client.substring(2);
                }
            } else if (clientappcode.equals("8")) {
                if (from_client.length() <= 2) {
                    deviceinfo = "发送自NGA安卓客户端 机型及系统:未知";
                } else {
                    String fromdata = from_client.substring(2);
                    if (fromdata.startsWith("[")
                            && fromdata.indexOf("](Android") > 0) {
                        deviceinfo = "发送自NGA安卓开源版客户端 机型及系统:"
                                + fromdata.substring(1).replace(
                                "](Android", "(Android");
                    } else {
                        deviceinfo = "发送自NGA安卓官方客户端 机型及系统:" + fromdata;
                    }
                }
            } else if (clientappcode.equals("9")) {
                if (from_client.length() <= 2) {
                    deviceinfo = "发送自NGA Windows Phone官方客户端 机型及系统:未知";
                } else {
                    deviceinfo = "发送自NGA Windows Phone官方客户端 机型及系统:"
                            + from_client.substring(2);
                }
            } else if (clientappcode.equals("100")) {
                if (from_client.length() <= 4) {
                    deviceinfo = "发送自安卓浏览器 机型及系统:未知";
                } else {
                    deviceinfo = "发送自安卓浏览器 机型及系统:"
                            + from_client.substring(4);
                }
            } else if (clientappcode.equals("101")) {
                if (from_client.length() <= 4) {
                    deviceinfo = "发送自苹果浏览器 机型及系统:未知";
                } else {
                    deviceinfo = "发送自苹果浏览器 机型及系统:"
                            + from_client.substring(4);
                }
            } else if (clientappcode.equals("102")) {
                if (from_client.length() <= 4) {
                    deviceinfo = "发送自Blackberry浏览器 机型及系统:未知";
                } else {
                    deviceinfo = "发送自Blackberry浏览器 机型及系统:"
                            + from_client.substring(4);
                }
            } else if (clientappcode.equals("103")) {
                if (from_client.length() <= 4) {
                    deviceinfo = "发送自Windows Phone客户端 机型及系统:未知";
                } else {
                    deviceinfo = "发送自Windows Phone客户端 机型及系统:"
                            + from_client.substring(4);
                }
            } else {
                if (from_client.indexOf(" ") < 0) {
                    deviceinfo = "发送自未知浏览器 机型及系统:未知";
                } else {
                    if (from_client.length() == (from_client.indexOf(" ") + 1)) {
                        deviceinfo = "发送自未知浏览器 机型及系统:未知";
                    } else {
                        deviceinfo = "发送自未知浏览器 机型及系统:"
                                + from_client.substring(from_client
                                .indexOf(" ") + 1);
                    }
                }
            }
            final Dialog dialog = new Dialog(mcontext,
                    R.style.ClientDialog);
            dialog.setContentView(R.layout.client_dialog);
            TextView textview = (TextView) dialog
                    .findViewById(R.id.client_device_dialog);
            textview.setText(deviceinfo);

            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();

            WindowManager wm = (WindowManager) mcontext
                    .getSystemService(Context.WINDOW_SERVICE);
            lp.width = (int) (wm.getDefaultDisplay().getWidth()); // 设置宽度
            dialog.getWindow().setAttributes(lp);
            dialog.show();
            dialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    // TODO Auto-generated method stub
                    if (PhoneConfiguration.getInstance().fullscreen) {
                        ActivityUtil.getInstance()
                                .setFullScreen(mscrollview);
                    }
                    dialog.dismiss();
                }

            });
            dialog.setCanceledOnTouchOutside(true);
        }

    }
}
