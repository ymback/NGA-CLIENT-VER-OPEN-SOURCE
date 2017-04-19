package sp.phone.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import sp.phone.adapter.RecentReplyAdapter;
import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.interfaces.OnRecentNotifierFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonCleanRecentNotifierLoadTask;
import sp.phone.task.JsonRecentNotifierLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class RecentReplyListFragment extends Fragment implements OnRecentNotifierFinishedListener, PerferenceConstant {
    String TAG = getClass().getSimpleName();
    PullToRefreshAttacher attacher = null;
    private ListView lv;
    private RecentReplyAdapter adapter;
    private Context mcontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//		lv = new SlideCutListView(getActivity());
        lv = new ListView(getActivity());
        lv.setCacheColorHint(0x00000000);
        lv.setLongClickable(false);
        mcontext = container.getContext();
        try {
            PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
            attacher = attacherOnwer.getAttacher();

        } catch (ClassCastException e) {
            Log.e(TAG,
                    "father activity should implement PullToRefreshAttacherOnwer");
        }
        if (attacher != null)
            attacher.addRefreshableView(lv, new ListRefreshListener());
        setHasOptionsMenu(true);
        return lv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//		lv.setRemoveListener(this);
        String str = PhoneConfiguration.getInstance().getReplyString();
        if (!StringUtil.isEmpty(str)) {
            List<NotificationObject> list = JSON.parseArray(str,
                    NotificationObject.class);
            if (list != null && list.size() != 0) {
                adapter = new RecentReplyAdapter(list, mcontext);
                lv.setAdapter(adapter);
            }
        } else {
            Toast.makeText(getActivity(),
                    "没有最近被喷内容",
                    Toast.LENGTH_SHORT).show();
            if (adapter != null) {
                adapter.clean();
            }
        }
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NotificationObject no = (NotificationObject) parent
                        .getItemAtPosition(position);
                if (no == null) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("tab", "1");
                intent.putExtra("tid", no.getTid());
                intent.putExtra("pid", no.getPid());
                intent.putExtra("authorid", 0);
                intent.putExtra("fromreplyactivity", 1);

                intent.setClass(getActivity(),
                        PhoneConfiguration.getInstance().articleActivityClass);
                getActivity().startActivity(intent);

            }

        });
    }

    @Override
    public void onResume() {
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(lv);
        }
        super.onResume();
    }

    void refresh() {
        JsonRecentNotifierLoadTask task = new JsonRecentNotifierLoadTask(getActivity(),
                this);
        // ActivityUtil.getInstance().noticeSaying(this.getActivity());
        refresh_saying();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        task.execute(config.getCookie());
    }

    private void refresh_saying() {
        DefaultHeaderTransformer transformer = null;

        if (attacher != null) {
            uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
            headerTransformer = attacher.getHeaderTransformer();
            if (headerTransformer != null
                    && headerTransformer instanceof DefaultHeaderTransformer)
                transformer = (DefaultHeaderTransformer) headerTransformer;
        }

        if (transformer == null)
            ActivityUtil.getInstance().noticeSaying(this.getActivity());
        else
            transformer.setRefreshingText(ActivityUtil.getSaying());
        if (attacher != null)
            attacher.setRefreshing(true);
    }

//	@Override
//	public void removeItem(RemoveDirection direction, int position) {
//		// TODO Auto-generated method stub
//		adapter.remove(position);
//	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delectall:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                removerecentlist();
                                JsonCleanRecentNotifierLoadTask task = new JsonCleanRecentNotifierLoadTask(getActivity());
                                task.execute(PhoneConfiguration.getInstance().getCookie());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // Do nothing
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(this.getString(R.string.delete_recentreply_confirm_text))
                        .setPositiveButton(R.string.confirm, dialogClickListener)
                        .setNegativeButton(R.string.cancle, dialogClickListener);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        if (PhoneConfiguration.getInstance().fullscreen) {
                            ActivityUtil.getInstance().setFullScreen(lv);
                        }
                    }

                });
                break;
            case R.id.refresh:
                refresh();
                break;
            default:
                getActivity().finish();
        }
        return true;
    }

    public void removerecentlist() {
        PhoneConfiguration.getInstance().setReplyString("");
        PhoneConfiguration.getInstance().setReplyTotalNum(0);
        SharedPreferences share = getActivity().getSharedPreferences(PERFERENCE,
                Context.MODE_PRIVATE);
        String userListString = share.getString(USER_LIST, "");
        List<User> userList = null;
        if (!StringUtil.isEmpty(userListString)) {
            userList = JSON.parseArray(userListString, User.class);
            for (User u : userList) {
                if (u.getUserId().equals(
                        PhoneConfiguration.getInstance().uid)) {
                    MyApp app = ((MyApp) getActivity().getApplication());
                    app.addToUserList(u.getUserId(), u.getCid(),
                            u.getNickName(), "", 0, u.getBlackList());
                    break;
                }
            }
        } else {
            Editor editor = share.edit();
            editor.putString(PENDING_REPLYS, "");
            editor.putString(REPLYTOTALNUM,
                    "0");
            editor.apply();
        }
        if (adapter != null) {
            adapter.clean();
        }
    }

    @Override
    public void jsonfinishLoad() {
        // TODO Auto-generated method stub
        if (attacher != null)
            attacher.setRefreshComplete();
        String str = PhoneConfiguration.getInstance().getReplyString();
        if (!StringUtil.isEmpty(str)) {
            List<NotificationObject> list = JSON.parseArray(str,
                    NotificationObject.class);
            if (list != null && list.size() != 0) {
                adapter = new RecentReplyAdapter(list, mcontext);
                lv.setAdapter(adapter);
            }
        } else {
            Toast.makeText(getActivity(),
                    "没有最近被喷内容",
                    Toast.LENGTH_SHORT).show();
            if (adapter != null) {
                adapter.clean();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId;
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
                menuId = R.menu.recent_reply_menu;
            } else {
                menuId = R.menu.recent_reply_menu;
            }
        } else {
            menuId = R.menu.recent_reply_menu;
        }
        inflater.inflate(menuId, menu);

    }

    class ListRefreshListener implements
            PullToRefreshAttacher.OnRefreshListener {

        @Override
        public void onRefreshStarted(View view) {

            refresh();
        }
    }
}
