package sp.phone.mvp.model.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.net.UnknownHostException;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.ContextUtils;;

/**
 * Created by Justwen on 2017/11/23.
 */
public class ErrorConvertFactory {

    public static String getErrorMessage(String js) {
        if (js.isEmpty()) {
            return ContextUtils.getString(R.string.network_error);
        } else if (js.contains("未登录")) {
            return "请重新登录";
        } else if (js.contains("无此页")) {
            return ContextUtils.getString(R.string.last_page_prompt);
        } else {
            try {
                JSONObject obj = (JSONObject) JSON.parse(js);
                obj = (JSONObject) obj.get("data");
                obj = (JSONObject) obj.get("__MESSAGE");
                return obj.getString("1");
            } catch (Exception e) {
                return "二哥玩坏了或者你需要重新登录";
            }
        }
    }

    public static String getErrorMessage(Throwable throwable) {
        String error;
        if (throwable instanceof UnknownHostException) {
            error = ContextUtils.getString(R.string.network_error);
        } else {
            error = throwable.getMessage();
        }
        return error;
    }

}
