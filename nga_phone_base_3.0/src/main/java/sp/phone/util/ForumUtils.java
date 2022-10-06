package sp.phone.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.common.UserAgent;

/**
 * Created by Justwen on 2018/7/2.
 */
public class ForumUtils {

    public static String getAvailableDomain() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int index = Integer.parseInt(sp.getString(PreferenceKey.KEY_NGA_DOMAIN, "1"));
        return context.getResources().getStringArray(R.array.nga_domain)[index];
    }

    public static String getCurrentUserAgent(){
        Context context = ContextUtils.getContext();
        String uaListStr = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKey.USER_AGENT_LIST, "");
        List<UserAgent> mUserAgents = JSON.parseArray(uaListStr, UserAgent.class);
        AtomicReference<String> ua = new AtomicReference<>("");
        if(mUserAgents==null){
            ua.set("自动");
        }else{
            mUserAgents.forEach((userAgent)->{
                if(userAgent.isEnabled()) ua.set(userAgent.getKeyword());
            });
        }
        return ua.get();
    }

    public static String getAvailableDomainNoHttp() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int index = Integer.parseInt(sp.getString(PreferenceKey.KEY_NGA_DOMAIN, "1"));
        return context.getResources().getStringArray(R.array.nga_domain_no_http)[index];
    }

    /**
     * @param statusCode
     * @return 返回子板块是否被订阅
     */
    public static boolean isBoardSubscribed(int statusCode) {
        // 3,810 返回false
        return statusCode == 7 || statusCode == 558 || statusCode == 542 || statusCode == 2606 || statusCode == 2590
                || statusCode == 4654;
    }

}
