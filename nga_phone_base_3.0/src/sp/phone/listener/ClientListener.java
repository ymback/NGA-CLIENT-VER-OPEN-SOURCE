package sp.phone.listener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;

public class ClientListener implements OnClickListener {

    private int mPosition;

    private ThreadData mData;

    private Context mContext;

    public ClientListener(int position, ThreadData data, Context context) {
        mPosition = position;
        mData = data;
        mContext = context;
    }

    @Override
    public void onClick(View v) {

        ThreadRowInfo row = mData.getRowList().get(mPosition);
        String fromClient = row.getFromClient();
        String clientModel = row.getFromClientModel();
        String deviceInfo;
        if (!StringUtils.isEmpty(clientModel)) {
            String clientAppCode;
            if (!fromClient.contains(" ")) {
                clientAppCode = fromClient;
            } else {
                clientAppCode = fromClient.substring(0,
                        fromClient.indexOf(" "));
            }
            switch (clientAppCode) {
                case "1":
                    if (fromClient.length() <= 2) {
                        deviceInfo = "发送自Life Style苹果客户端 机型及系统:未知";
                    } else {
                        deviceInfo = "发送自Life Style苹果客户端 机型及系统:"
                                + fromClient.substring(2);
                    }
                    break;
                case "7":
                    if (fromClient.length() <= 2) {
                        deviceInfo = "发送自NGA苹果官方客户端 机型及系统:未知";
                    } else {
                        deviceInfo = "发送自NGA苹果官方客户端 机型及系统:"
                                + fromClient.substring(2);
                    }
                    break;
                case "8":
                    if (fromClient.length() <= 2) {
                        deviceInfo = "发送自NGA安卓客户端 机型及系统:未知";
                    } else {
                        String fromData = fromClient.substring(2);
                        if (fromData.startsWith("[")
                                && fromData.indexOf("](Android") > 0) {
                            deviceInfo = "发送自NGA安卓开源版客户端 机型及系统:"
                                    + fromData.substring(1).replace(
                                    "](Android", "(Android");
                        } else {
                            deviceInfo = "发送自NGA安卓官方客户端 机型及系统:" + fromData;
                        }
                    }
                    break;
                case "9":
                    if (fromClient.length() <= 2) {
                        deviceInfo = "发送自NGA Windows Phone官方客户端 机型及系统:未知";
                    } else {
                        deviceInfo = "发送自NGA Windows Phone官方客户端 机型及系统:"
                                + fromClient.substring(2);
                    }
                    break;
                case "100":
                    if (fromClient.length() <= 4) {
                        deviceInfo = "发送自安卓浏览器 机型及系统:未知";
                    } else {
                        deviceInfo = "发送自安卓浏览器 机型及系统:"
                                + fromClient.substring(4);
                    }
                    break;
                case "101":
                    if (fromClient.length() <= 4) {
                        deviceInfo = "发送自苹果浏览器 机型及系统:未知";
                    } else {
                        deviceInfo = "发送自苹果浏览器 机型及系统:"
                                + fromClient.substring(4);
                    }
                    break;
                case "102":
                    if (fromClient.length() <= 4) {
                        deviceInfo = "发送自Blackberry浏览器 机型及系统:未知";
                    } else {
                        deviceInfo = "发送自Blackberry浏览器 机型及系统:"
                                + fromClient.substring(4);
                    }
                    break;
                case "103":
                    if (fromClient.length() <= 4) {
                        deviceInfo = "发送自Windows Phone客户端 机型及系统:未知";
                    } else {
                        deviceInfo = "发送自Windows Phone客户端 机型及系统:"
                                + fromClient.substring(4);
                    }
                    break;
                default:
                    if (!fromClient.contains(" ")) {
                        deviceInfo = "发送自未知浏览器 机型及系统:未知";
                    } else {
                        if (fromClient.length() == (fromClient.indexOf(" ") + 1)) {
                            deviceInfo = "发送自未知浏览器 机型及系统:未知";
                        } else {
                            deviceInfo = "发送自未知浏览器 机型及系统:"
                                    + fromClient.substring(fromClient
                                    .indexOf(" ") + 1);
                        }
                    }
                    break;
            }
            ActivityUtils.showToast(mContext,deviceInfo);
        }

    }
}
