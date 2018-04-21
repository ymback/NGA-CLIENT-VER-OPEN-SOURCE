package sp.phone.listener;

import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.util.ActivityUtils;

/**
 * Created by Justwen on 2018/4/20.
 */
public class OnProfileClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {

        ThreadRowInfo row = (ThreadRowInfo) view.getTag();

        if (row.getISANONYMOUS()) {
            ActivityUtils.showToast("这白痴匿名了,神马都看不到");
        } else {
            ARouter.getInstance()
                    .build(ARouterConstants.ACTIVITY_PROFILE)
                    .withString("mode", "username")
                    .withString("username", row.getAuthor())
                    .navigation();
        }
    }
}
