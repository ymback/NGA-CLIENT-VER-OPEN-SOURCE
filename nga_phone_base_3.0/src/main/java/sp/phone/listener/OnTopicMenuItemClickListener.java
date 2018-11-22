package sp.phone.listener;

import android.widget.PopupMenu;

import sp.phone.bean.ThreadRowInfo;

/**
 * Created by Justwen on 2018/4/21.
 */
public interface OnTopicMenuItemClickListener extends PopupMenu.OnMenuItemClickListener {

    void setThreadRowInfo(ThreadRowInfo threadRowInfo);

}
