package sp.phone.model.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gov.anzong.androidnga.R;
import sp.phone.utils.ResourceUtils;

/**
 * Created by Justwen on 2017/11/23.
 */
public class ErrorConvertFactory {

    public static String getErrorMessage(String js) {
        if (js.isEmpty()) {
            return ResourceUtils.getString(R.string.network_error);
        } else if (js.contains("未登录")) {
            return "请重新登录";
        } else if (js.contains("无此页")) {
            return ResourceUtils.getString(R.string.last_page_prompt);
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

}
