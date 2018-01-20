package gov.anzong.androidnga.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.adapter.NearbyUsersAdapter;
import sp.phone.bean.NearbyUser;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.common.ThemeManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.fragment.dialog.AlertDialogFragment;
import sp.phone.fragment.dialog.NearbyAlertDialogFragment;
import sp.phone.interfaces.OnNearbyLoadComplete;
import sp.phone.interfaces.PullToRefreshAttacherOwner;
import sp.phone.task.NearbyUserTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.DeviceUtils;
import sp.phone.utils.NLog;
import sp.phone.utils.PermissionUtils;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtils;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public class NearbyUserActivity extends SwipeBackAppCompatActivity implements PreferenceKey, OnNearbyLoadComplete, PullToRefreshAttacherOwner {

    final private String ALERT_DIALOG_TAG = "alertdialog";
    NearbyUserTask task = null;
    PullToRefreshAttacher attacher = null;
    private ListView lv;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setContentView(R.layout.webview_layout);
        setTheme(R.style.AppTheme);
        lv = new ListView(this);
        this.setContentView(lv);
        getSupportActionBar().setTitle("Yoooo~");

        String alertString = this.getString(R.string.find_nearby_alert_string);
        AlertDialogFragment f = AlertDialogFragment.create(alertString);
        f.setOkListener(new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                initLocation();

            }

        });
        f.setCancleListener(new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }

        });
        f.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
        //initLocation();
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshScrollDistance = 0.3f;
        options.refreshOnUp = true;
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
        try {
            PullToRefreshAttacherOwner attacherOwner = (PullToRefreshAttacherOwner) this;
            attacher = attacherOwner.getAttacher();

        } catch (ClassCastException e) {
            NLog.e("NEARBYUSERACTIVITY", "father activity should implement PullToRefreshAttacherOwner");
        }
    }

    void initLocation() {
        if (!PermissionUtils.hasLocationPermission(this) && DeviceUtils.isGreaterEqual_6_0()) {
            PermissionUtils.requestLocationPermission(this);
            return;
        }

        Location location = PhoneConfiguration.getInstance().location;

        SharedPreferences share = getSharedPreferences(
                PERFERENCE, MODE_PRIVATE);
        String userName = share.getString(USER_NAME, "");
        userName = StringUtils.encodeUrl(userName, "utf-8");
        if (location == null) {
            //Toast.makeText(this, R.string.fail_to_locate, Toast.LENGTH_SHORT).show();
        } else if (StringUtils.isEmpty(userName)) {
            showToast(R.string.nearby_no_login);
        } else {
            ActivityUtils.getInstance().noticeSaying(this);
            task = new NearbyUserTask(location.getLatitude(), location.getLongitude(),
                    userName, UserManagerImpl.getInstance().getUserId(), this);
            task.execute();

        }
    }


    @Override
    public void OnComplete(String result) {
        task = null;
        ActivityUtils.getInstance().dismiss();
        if (StringUtils.isEmpty(result))
            return;
        List<NearbyUser> list = null;
        try {
            list = JSON.parseArray(result, NearbyUser.class);
        } catch (Exception e) {
            return;
        }
        Location myloc = PhoneConfiguration.getInstance().location;
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setJuli(String.valueOf(ActivityUtils.distanceBetween(myloc, list.get(i).getLatitude(), list.get(i).getLongitude())));
        }
        attacher.setRefreshComplete();
        if (list != null && list.size() == 0) {
            showToast(R.string.nearby_no_user);
        }

        NearbyUsersAdapter adapter = new NearbyUsersAdapter(list);
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {

                final NearbyUser u = (NearbyUser) parent.getItemAtPosition(position);
                String Name = u.getNickName();
                if (Name.indexOf("(") > 0) {
                    Name = Name.substring(0, Name.indexOf("("));
                }
                final String Namea = Name;
                String text = null;
                try {
                    text = URLDecoder.decode(Namea, "utf-8");
                } catch (UnsupportedEncodingException e) {
                }
                final String texta = text;
                NearbyAlertDialogFragment f = NearbyAlertDialogFragment.create(getString(R.string.seeingooglemaporprofile));
                f.setOkListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (StringUtils.isEmail(texta)) {
                            showToast("用户名为邮箱,无法通过邮箱获取论坛用户信息");
                        } else {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.putExtra("mode", "username");
                            i.putExtra("username", texta);
                            i.setClass(lv.getContext(), PhoneConfiguration.getInstance().profileActivityClass);
                            startActivity(i);
                        }
                    }

                });
                f.setCancleListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String loc = "https://maps.google.com.hk/?ie=UTF8&hl=zh-cn&q="
                                + u.getLatitude() + "," + u.getLongitude()
                                + "(" + Namea + ")";
                        Uri mapUri = Uri.parse(loc);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(mapUri);

                        startActivity(i);

                    }

                });
                f.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);


            }

        });

        lv.setAdapter(adapter);

    }

    @Override
    public void onProgresUpdate(int progress, int total) {
        String saying = getString(R.string.fuck_gfw_prefix) + "(" + progress + "/"
                + total + ")";
        if (progress > total) {
            saying = this.getString(R.string.fail_to_cross_gfw);
        }
        ActivityUtils.getInstance().noticeError(saying, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final int flags = ThemeManager.ACTION_BAR_FLAG;
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
        }
        return true;
    }

    @Override
    protected void onStop() {
        if (task != null) {
            task.cancel(true);
        }
        super.onStop();
    }

    @Override
    public PullToRefreshAttacher getAttacher() {
        // TODO Auto-generated method stub
        return mPullToRefreshAttacher;
    }

}
