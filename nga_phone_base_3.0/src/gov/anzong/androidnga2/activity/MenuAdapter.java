package gov.anzong.androidnga2.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import gov.anzong.androidnga2.R;

import java.util.List;

import com.readystatesoftware.viewbadger.BadgeView;

import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ThemeManager;

public class MenuAdapter extends BaseAdapter {

	public interface MenuListener {

		void onActiveViewChanged(View v);
	}

	private Context mContext;

	private List<Object> mItems;

	private MenuListener mListener;

	private int mActivePosition = -1;

	private int mchangeposition = -1;

	public MenuAdapter(Context context, List<Object> items) {
		mContext = context;
		mItems = items;
	}

	public void setListener(MenuListener listener) {
		mListener = listener;
	}

	public void setActivePosition(int activePosition) {
		mActivePosition = activePosition;
	}

	public int getActivePosition() {
		return mActivePosition;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position) instanceof Item ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position) instanceof Item;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	static class ViewHolder {
		private TextView text1;
		private TextView text2;
		private BadgeView badge;
	}

	private ViewHolder initHolder(final View view, final ViewGroup parent) {
		final ViewHolder holder = new ViewHolder();
		holder.text1 = (TextView) view.findViewById(R.id.text1);
		holder.text2 = (TextView) view.findViewById(R.id.text2);
		holder.badge = new BadgeView(parent.getContext(), holder.text2);
		if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
			holder.badge.setBadgeBackgroundColor(holder.text2.getResources()
					.getColor(R.color.night_fore_color));
			holder.badge.setTextColor(holder.text2.getResources().getColor(
					R.color.night_bg_color));
		} else {
			holder.badge.setBadgeBackgroundColor(holder.text2.getResources()
					.getColor(R.color.replynumbernormal));
			holder.badge.setTextColor(holder.text2.getResources().getColor(
					R.color.black));
		}
		holder.text2.setVisibility(View.GONE);
		return holder;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Object item = getItem(position);
		ViewHolder holder = null;
		// int colorbgId,colorfgId ;
		// if (cfg.mode == ThemeManager.MODE_NIGHT){
		// colorbgId = R.color.night_bg_color;
		// colorfgId = R.color.white;
		// }else{
		// colorbgId = R.color.drawerlistcoloritem;
		// colorfgId = R.color.black;
		// }
		final ThemeManager tm = ThemeManager.getInstance();

		if (item instanceof Category) {
			if (v == null) {
				v = LayoutInflater.from(mContext).inflate(
						R.layout.menu_row_category, parent, false);
				v.setBackgroundColor(v.getResources().getColor(
						R.color.drawerlistcolormain));
			}
			((TextView) v).setText(((Category) item).mTitle);

		} else {
			if (v == null) {
				v = LayoutInflater.from(mContext).inflate(
						R.layout.menu_row_item, parent, false);
				if (tm.getMode() == ThemeManager.MODE_NIGHT) {
					v.setBackgroundColor(v.getResources().getColor(
							R.color.night_bg_color));
					v.setBackgroundDrawable(null);
				} else {
					v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.list_selector));
				}
				holder = initHolder(v, parent);
				v.setTag(holder);
				// BitmapDrawable bd=new BitmapDrawable(bmp);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			holder.text1.setText(((Item) item).mTitle);

			if (((Item) item).mTitle.equals("×î½ü±»Åç")) {
				holder.badge.setText(String.valueOf(PhoneConfiguration
						.getInstance().getReplyTotalNum()));
				holder.text2.setVisibility(View.VISIBLE);
				holder.badge.show();
			} else {
				holder.badge.hide();
				holder.text2.setVisibility(View.GONE);
			}
			if (tm.getMode() == ThemeManager.MODE_NIGHT) {
				holder.text1.setTextColor(v.getResources().getColor(
						R.color.night_fore_color));
			} else {
				holder.text1.setTextColor(v.getResources().getColor(
						R.color.black));
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				holder.text1.setCompoundDrawablesRelativeWithIntrinsicBounds(
						((Item) item).mIconRes, 0, 0, 0);
			} else {
				holder.text1.setCompoundDrawablesWithIntrinsicBounds(
						((Item) item).mIconRes, 0, 0, 0);
			}
		}

		if (mListener != null && v != null && position == mActivePosition) {
			mListener.onActiveViewChanged(v);
		}
		
		return v;
	}

}
