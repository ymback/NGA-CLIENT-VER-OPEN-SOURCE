package sp.phone.listener;

import android.view.View;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;

/**
 * Created by Justwen on 2018/4/20.
 */
public class OnAvatarClickListener implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        ThreadRowInfo row = (ThreadRowInfo) view.getTag();
        if (row.getISANONYMOUS()) {
            ActivityUtils.showToast("这白痴匿名了,神马都看不到");
        } else {
            FunctionUtils.Create_Avatar_Dialog(row, view.getContext(), null);
        }

    }
}
