package sp.phone.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.ForumListActivity;
import gov.anzong.androidnga.activity.LoginActivity;
import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.AvatarTag;
import sp.phone.common.PreferenceKey;
import sp.phone.bean.User;
import sp.phone.common.ThemeManager;
import sp.phone.interfaces.PageCategoryOwner;
import sp.phone.presenter.contract.BoardContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.StringUtils;


/**
 * 首页的容器
 * Created by Yang Yihang on 2017/6/29.
 */

public class BoardFragment extends BaseFragment implements BoardContract.View, AdapterView.OnItemClickListener {

    private BoardContract.Presenter mPresenter;

    private ViewPager mViewPager;

    private ViewFlipper mHeaderView;

    private TextView mReplyCountView;

    private BoardPagerAdapter mBoardPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.start_title);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        DrawerLayout drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                setTitle("赞美片总");
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                setTitle(R.string.start_title);
                super.onDrawerClosed(drawerView);
            }
        });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, (Toolbar) view.findViewById(R.id.toolbar), R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        NavigationMenuView menuView = (NavigationMenuView) navigationView.getChildAt(0);
        menuView.setVerticalScrollBarEnabled(false);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.menu_gun);
        View actionView = getLayoutInflater().inflate(R.layout.nav_menu_action_view_gun, null);
        menuItem.setActionView(actionView);
        menuItem.expandActionView();
        mReplyCountView = (TextView) actionView.findViewById(R.id.reply_count);
        navigationView.getHeaderView(0).setBackgroundColor(ThemeManager.getInstance().getPrimaryColor(getContext()));
        mHeaderView = (ViewFlipper) navigationView.getHeaderView(0).findViewById(R.id.viewFlipper);
        updateHeaderView();
        super.onViewCreated(view, savedInstanceState);
        mPresenter.loadBoardInfo();
    }

    @Override
    public void updateHeaderView() {
        SharedPreferences sp = getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        String userListString = sp.getString(PreferenceKey.USER_LIST, "");
        mHeaderView.removeAllViews();
        final List<User> userList;
        if (StringUtils.isEmpty(userListString)) {
            userList = null;
            mHeaderView.addView(getUserView(null, 0));// 传递回一个未登入的
        } else {
            userList = JSON.parseArray(userListString, User.class);
            if (userList.size() == 0) {
                mHeaderView.addView(getUserView(null, 0));// 传递回一个未登入的
            } else {
                for (int i = 0; i < userList.size(); i++) {
                    mHeaderView.addView(getUserView(userList, i));// 传递回一个未登入的
                }
            }
        }
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toggleUser(userList);
            }
        });
        mHeaderView.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_in));
        mHeaderView.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_out));
    }

    @Override
    public void notifyDataSetChanged() {
        mBoardPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                gotoForumList();
                break;
            case R.id.menu_add_id:
                showAddBoardDialog();
                break;
            case R.id.menu_login:
                jumpToLogin();
                break;
            case R.id.menu_clear_recent:
                mPresenter.clearRecentBoards();
                break;
            default:
                return getActivity().onOptionsItemSelected(item);
        }
        return true;
    }

    private void gotoForumList() {
        Intent intent = new Intent(getActivity(), ForumListActivity.class);
        startActivity(intent);
    }

    public boolean isTablet() {
        boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 0x04);// Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return xlarge || large;
    }

    @Override
    public void jumpToLogin() {
        if (isTablet()) {
            DialogFragment df = new LoginFragment();
            df.show(getSupportFragmentManager(), "login");
        } else {
            Intent intent = new Intent();
            intent.setClass(getContext(), LoginActivity.class);
            startActivityForResult(intent, ActivityUtils.REQUEST_CODE_LOGIN);
        }
    }

    private void showAddBoardDialog() {
        final View view = getLayoutInflater().inflate(R.layout.addfid_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view).setTitle(R.string.addfid_title_hint);
        final EditText addFidNameView = (EditText) view.findViewById(R.id.addfid_name);
        final EditText addFidIdView = (EditText) view.findViewById(R.id.addfid_id);
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {

            @SuppressWarnings("unused")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = addFidNameView.getText().toString();
                String fid = addFidIdView.getText().toString();
                canDismiss(dialog, mPresenter.addBoard(fid, name));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                canDismiss(dialog, true);
            }
        });
        builder.create().show();
    }

    private void canDismiss(DialogInterface dialog, boolean canDismiss) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, canDismiss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
            updateHeaderView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public View getUserView(List<User> userList, int position) {
        View privateView = getLayoutInflater().inflate(R.layout.nav_header_view_login_user, null);
        TextView loginState = (TextView) privateView.findViewById(R.id.loginstate);
        TextView loginId = (TextView) privateView.findViewById(R.id.loginnameandid);
        ImageView avatarImage = (ImageView) privateView.findViewById(R.id.avatarImage);
        ImageView nextImage = (ImageView) privateView.findViewById(R.id.nextImage);
        if (userList == null) {
            loginState.setText("未登录");
            loginId.setText("点击下面的登录账号登录");
            nextImage.setVisibility(View.GONE);
        } else {
            if (userList.size() <= 1) {
                nextImage.setVisibility(View.GONE);
            }
            if (userList.size() == 1) {
                loginState.setText("已登录1个账户");
            } else {
                loginState.setText("已登录" + String.valueOf(userList.size() + "个账户,点击切换"));
            }
            if (userList.size() > 0) {
                User user = userList.get(position);
                loginId.setText("当前:" + user.getNickName() + "(" + user.getUserId() + ")");
                handleUserAvatar(avatarImage, user.getUserId());
            }
        }
        return privateView;
    }

    public void handleUserAvatar(ImageView avatarIV, String userId) {// 绝无问题
        Bitmap bitmap = null;
        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
            return;
        }
        Object tagObj = avatarIV.getTag();
        if (tagObj instanceof AvatarTag) {
            AvatarTag origTag = (AvatarTag) tagObj;
            if (!origTag.isDefault) {
                ImageUtil.recycleImageView(avatarIV);
            }
        }
        AvatarTag tag = new AvatarTag(Integer.parseInt(userId), true);
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
            avatarIV.setImageTintList(null);
            avatarIV.setImageBitmap(toRoundCorner(bitmap, 2));
            tag.isDefault = false;
        } else {
            tag.isDefault = true;
        }
    }

    public Bitmap toRoundCorner(Bitmap bitmap, float ratio) { // 绝无问题
        if (bitmap.getWidth() > bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap,
                    (bitmap.getWidth() - bitmap.getHeight()) / 2, 0,
                    bitmap.getHeight(), bitmap.getHeight());
        } else if (bitmap.getWidth() < bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap, 0,
                    (bitmap.getHeight() - bitmap.getWidth()) / 2,
                    bitmap.getWidth(), bitmap.getWidth());
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
                bitmap.getHeight() / ratio, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void onResume() {
        if (mBoardPagerAdapter == null) {
            mBoardPagerAdapter = new BoardPagerAdapter(getChildFragmentManager(), (PageCategoryOwner) mPresenter);
            mViewPager.setAdapter(mBoardPagerAdapter);
            if (((PageCategoryOwner) mPresenter).getCategory(0).size() == 0) {
                mViewPager.setCurrentItem(1);
            }
        } else {
            mBoardPagerAdapter.notifyDataSetChanged();
        }
        mReplyCountView.setText(String.valueOf(PhoneConfiguration.getInstance().getReplyTotalNum()));
        super.onResume();
    }

    @Override
    public void setPresenter(BoardContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public int switchToNextUser() {
        mHeaderView.showPrevious();
        return mHeaderView.getDisplayedChild();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String fidString;
        if (parent != null) {
            fidString = (String) parent.getItemAtPosition(position);
        } else {
            fidString = String.valueOf(id);
        }

        mPresenter.toTopicListPage(position, fidString);
    }
}
