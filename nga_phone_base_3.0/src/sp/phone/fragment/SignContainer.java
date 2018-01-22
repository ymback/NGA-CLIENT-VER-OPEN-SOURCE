package sp.phone.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;

import gov.anzong.androidnga.R;
import sp.phone.adapter.SignPageAdapter;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.SignData;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.common.ThemeManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOwner;
import sp.phone.task.JsonSignLoadTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class SignContainer extends BaseFragment implements
        OnSignPageLoadFinishedListener, PreferenceKey {
    static final int MESSAGE_SENT = 1;
    final String TAG = SignContainer.class.getSimpleName();
    int fid;
    int authorid;
    int searchpost;
    int favor;
    String key;
    String table;
    String author;
    boolean isrefresh = false;
    PullToRefreshAttacher attacher = null;
    SignPageAdapter adapter;
    boolean canDismiss = true;
    int category = 0;
    View headview;
    LayoutInflater inflatera;
    ThemeManager cfg;
    private ListView listView;
    private SignData result;
    private ViewGroup mcontainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cfg = ThemeManager.getInstance();
        mcontainer = container;
        if (savedInstanceState != null) {
            category = savedInstanceState.getInt("category", 0);
        }
        if (cfg.getMode() == ThemeManager.MODE_NIGHT) {
            if (mcontainer != null)
                mcontainer.setBackgroundResource(R.color.night_bg_color);
        }
        this.inflatera = inflater;
        try {
            PullToRefreshAttacherOwner attacherOwner = (PullToRefreshAttacherOwner) getActivity();
            attacher = attacherOwner.getAttacher();

        } catch (ClassCastException e) {
            NLog.e(TAG,
                    "father activity should implement PullToRefreshAttacherOwner");
        }

        return initListView();
    }

    public ListView initListView() {
        listView = new ListView(getActivity());
        listView.setDivider(null);
        adapter = new SignPageAdapter(this.getActivity());
        headview = inflatera.inflate(R.layout.signresult, null);
        headview.setVisibility(View.GONE);
        // refreshheadviewdata(headview);
        listView.addHeaderView(headview, null, false);
        listView.setAdapter(adapter);
        if (attacher != null)
            attacher.addRefreshableView(listView, new ListRefreshListener());
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return listView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.night_mode) != null) {
            if (cfg.getMode() == ThemeManager.MODE_NIGHT) {
                menu.findItem(R.id.night_mode).setIcon(
                        R.drawable.ic_action_brightness_high);
                menu.findItem(R.id.night_mode).setTitle(
                        R.string.change_daily_mode);
            } else {
                menu.findItem(R.id.night_mode).setIcon(
                        R.drawable.ic_action_bightness_low);
                menu.findItem(R.id.night_mode).setTitle(
                        R.string.change_night_mode);
            }
        }
        // getSupportMenuInflater().inflate(R.menu.book_detail, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("unused")
    public void refreshheadviewdata(View headview) {
        NLog.i("SignPageAdapter", "SignPageAdapter");
        int colorId = R.color.shit1;
        boolean isnight = false;
        if (cfg.getMode() == ThemeManager.MODE_NIGHT) {
            colorId = R.color.night_bg_color;
            isnight = true;
        }
        headview.setBackgroundResource(colorId);
        TextView nickName = (TextView) headview.findViewById(R.id.nickName);
        TextView signtime = (TextView) headview.findViewById(R.id.signtime);
        TextView signdate = (TextView) headview.findViewById(R.id.signdate);
        TextView signstate = (TextView) headview.findViewById(R.id.signstate);
        TextView lasttext = (TextView) headview.findViewById(R.id.lasttext);
        TextView signdatedata = (TextView) headview
                .findViewById(R.id.signdatedata);
        TextView statetext = (TextView) headview.findViewById(R.id.statetext);
        ImageView avatarImage = (ImageView) headview
                .findViewById(R.id.avatarImage);
        View lineviewforshow = (View) headview
                .findViewById(R.id.lineviewforshow);
        TextView successnum = (TextView) headview.findViewById(R.id.successnum);
        TextView availablenum = (TextView) headview
                .findViewById(R.id.availablenum);
        String userName;
        String signtimes;
        String signdates;
        String signstates = "未知";
        String availablenums;
        String successnums;

        if (StringUtils.isEmpty(UserManagerImpl.getInstance().getUserName())) {
            userName = "未知";
        } else {
            userName = UserManagerImpl.getInstance().getUserName();
        }
        String userId = "-9999";
        if (!StringUtils.isEmpty(UserManagerImpl.getInstance().getUserId())) {
            userId = UserManagerImpl.getInstance().getUserId();
        }
        if (result != null) {
            if (StringUtils.isEmpty(result.get__SignResult())) {
                signstates = "未知";
            } else {
                signstates = result.get__SignResult();
            }

            if (result.get__is_json_error()) {
                if (result.get__today_alreadysign()) {
                    signtimes = "今天";
                    signdates = "未知";
                    availablenums = "0个";
                    successnums = "0个";
                } else {
                    signtimes = "未知";
                    signdates = "未知";
                    availablenums = "0个";
                    successnums = "0个";
                }
            } else {
                if (StringUtils.isEmpty(result.get__Last_time())) {
                    signtimes = "未知";
                } else {
                    signtimes = result.get__Last_time();
                }
                signdates = String.valueOf(result.get__Continued()) + "/"
                        + String.valueOf(result.get__Sum());
                availablenums = String.valueOf(result.get__Availablerows())
                        + "个";
                successnums = String.valueOf(result.get__Successrows()) + "个";
            }

        } else {
            signtimes = "未知";
            signdates = "未知";
            availablenums = "未知";
            successnums = "未知";
        }
        nickName.setText(userName);
        signtime.setText(signtimes);
        signdate.setText(signdates);
        signstate.setText(signstates);
        availablenum.setText(availablenums);
        successnum.setText(successnums);
        ImageUtil.recycleImageView(avatarImage);
        handleUserAvatat(avatarImage, userId);
    }

    private void nightMode(final MenuItem menu) {
        //changeNightMode(menu);
        isrefresh = true;
        if (mcontainer != null) {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                mcontainer.setBackgroundResource(R.color.night_bg_color);
            } else {
                mcontainer.setBackgroundResource(R.color.shit1);
            }
        }
        jsonfinishLoad(result);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        canDismiss = true;
        this.refresh();
        super.onViewCreated(view, savedInstanceState);
    }// 读取数据

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
            ActivityUtils.getInstance().noticeSaying(this.getActivity());
        else
            transformer.setRefreshingText(ActivityUtils.getSaying());
        if (attacher != null)
            attacher.setRefreshing(true);
    }// 有效

    void refresh() {
        JsonSignLoadTask task = new JsonSignLoadTask(getActivity(), this);
        // ActivityUtils.getInstance().noticeSaying(this.getActivity());
        refresh_saying();
        task.execute("SIGN");
        isrefresh = true;
    }// 读取JSON了

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId;
        menuId = R.menu.signpage_menu;
        inflater.inflate(menuId, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signpage_menuitem_refresh:
                refresh();
                break;
            case R.id.night_mode:// OK
                nightMode(item);
                break;
            case R.id.signpage_menuitem_back:
                getActivity().finish();
            default:
                break;
        }
        return true;
    }

    @Override
    public void jsonfinishLoad(SignData result) {
        if (attacher != null)
            attacher.setRefreshComplete();

        if (result == null)
            return;
        this.result = result;
        adapter.clear();
        adapter.jsonfinishLoad(result);
        if (isrefresh == true) {
            headview.setVisibility(View.VISIBLE);
            refreshheadviewdata(headview);
            isrefresh = false;
        }
        listView.setAdapter(adapter);
        if (canDismiss)
            ActivityUtils.getInstance().dismiss();
    }

    public void handleUserAvatat(ImageView avatarIV, String userId) {
        Bitmap defaultAvatar = null, bitmap = null;
        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
            return;
        }
        if (defaultAvatar == null
                || defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
            Resources res = inflatera.getContext().getResources();
            InputStream is = res.openRawResource(R.raw.default_avatar);
            InputStream is2 = res.openRawResource(R.raw.default_avatar);
            defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
        }
        Object tagObj = avatarIV.getTag();
        if (tagObj instanceof AvatarTag) {
            AvatarTag origTag = (AvatarTag) tagObj;
            if (!origTag.isDefault) {
                ImageUtil.recycleImageView(avatarIV);
                // NLog.d(TAG, "recycle avatar:" + origTag.lou);
            } else {
                // NLog.d(TAG, "default avatar, skip recycle");
            }
        }
        AvatarTag tag = new AvatarTag(Integer.parseInt(userId), true);
        avatarIV.setImageBitmap(defaultAvatar);
        avatarIV.setTag(tag);
        String avatarPath = HttpUtil.PATH_AVATAR + "/" + userId;
        String[] extension = {".jpg", ".png", ".gif", ".jpeg", ".bmp"};
        for (int i = 0; i < 5; i++) {
            File f = new File(avatarPath + extension[i]);
            if (f.exists()) {

                bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath
                        + extension[i]);
                if (bitmap == null) {
                    f.delete();
                }
                long date = f.lastModified();
                if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
                    f.delete();
                }
                break;
            }
        }
        if (bitmap != null) {
            avatarIV.setImageBitmap(bitmap);
            tag.isDefault = false;
        } else {
            avatarIV.setImageBitmap(defaultAvatar);
            tag.isDefault = true;
        }

    }

    public void onCategoryChanged(int position) {
        if (position != category) {
            category = position;
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("category", category);
        canDismiss = false;
        super.onSaveInstanceState(outState);
    }

    class ListRefreshListener implements
            PullToRefreshAttacher.OnRefreshListener {

        @Override
        public void onRefreshStarted(View view) {
            refresh();
        }
    }
}
