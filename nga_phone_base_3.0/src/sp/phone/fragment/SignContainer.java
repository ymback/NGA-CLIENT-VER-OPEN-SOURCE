package sp.phone.fragment;

import gov.anzong.androidnga.activity.MainActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.adapter.AppendableSignAdapter;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.adapter.SignPageAdapter;
import sp.phone.bean.SignData;
import sp.phone.bean.TopicListInfo;
import sp.phone.fragment.TopiclistContainer.ListRefreshListener;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonSignLoadTask;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SignContainer extends Fragment implements
		OnSignPageLoadFinishedListener {
	final String TAG = SignContainer.class.getSimpleName();
	static final int MESSAGE_SENT = 1;
	int fid;
	int authorid;
	int searchpost;
	int favor;
	String key;
	String table;
	String author;

	PullToRefreshAttacher attacher = null;
	private ListView listView;
	SignPageAdapter adapter;
	boolean canDismiss = true;
	int category = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			category = savedInstanceState.getInt("category", 0);
		}

		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e(TAG,
					"father activity should implement PullToRefreshAttacherOnwer");
		}
		
		listView = new ListView(getActivity());
		listView.setDivider(null);
		adapter = new SignPageAdapter(this.getActivity());
		listView.setAdapter(adapter);
		try {
			OnItemClickListener listener = (OnItemClickListener) getActivity();
			// mPullRefreshListView.setOnItemClickListener(listener);
			listView.setOnItemClickListener(listener);
		} catch (ClassCastException e) {
			Log.e(TAG, "father activity should implenent OnItemClickListener");
		}
		if (attacher != null)
			attacher.addRefreshableView(listView, new ListRefreshListener());
		return listView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		canDismiss = true;
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
			ActivityUtil.getInstance().noticeSaying(this.getActivity());
		else
			transformer.setRefreshingText(ActivityUtil.getSaying());
		if (attacher != null)
			attacher.setRefreshing(true);
	}// 有效

	void refresh() {
		JsonSignLoadTask task = new JsonSignLoadTask(getActivity(), this);
		// ActivityUtil.getInstance().noticeSaying(this.getActivity());
		refresh_saying();
		task.execute("SIGN");
	}// 读取JSON了

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuId;
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
				menuId = R.menu.signpage_menu_left;
			} else {
				menuId = R.menu.signpage_menu;
			}
		} else {
			menuId = R.menu.signpage_menu;
		}
		inflater.inflate(menuId, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.signpage_menuitem_refresh:
			refresh();
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
		adapter.clear();
		adapter.jsonfinishLoad(result);
		listView.setAdapter(adapter);
		if (canDismiss)
			ActivityUtil.getInstance().dismiss();
	}

	public void onCategoryChanged(int position) {
		if (position != category) {
			category = position;
			refresh();
		}else{
			refresh();
		}
	}
	
	class ListRefreshListener implements
			PullToRefreshAttacher.OnRefreshListener {

		@Override
		public void onRefreshStarted(View view) {

			refresh();
		}
	}
}
