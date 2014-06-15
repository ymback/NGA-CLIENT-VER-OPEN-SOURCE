
package sp.phone.adapter;

import java.util.ArrayList;
import java.util.List;

import sp.phone.bean.MeiziUrlData;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import gov.anzong.androidnga.R;

import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MeiziCategoryAdapter extends BaseAdapter {
	
	
    private List<MeiziUrlData> mData;

    private LayoutInflater mLayoutInflater;

    private MultiColumnListView mListView;
    
    Activity mactivity;

    public MeiziCategoryAdapter(Activity activity, MultiColumnListView listView) {
        mLayoutInflater = activity.getLayoutInflater();
        mData = new ArrayList<MeiziUrlData>();
        mListView = listView;
        mactivity=activity;
    }

    public void addData(List<MeiziUrlData> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;
        if (view != null && view.getTag() != null) {
            holder = (Holder) convertView.getTag();
        } else {
    		if(ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT){
                view = mLayoutInflater.inflate(R.layout.listitem_category_night, null);
    		}else{
                view = mLayoutInflater.inflate(R.layout.listitem_category, null);
    		}
            holder = new Holder(view);
            view.setTag(holder);
        }

        view.setEnabled(!mListView.isItemChecked(position + mListView.getHeaderViewsCount()));

        MeiziUrlData meiziM = mData.get(position);
        ImageLoader.getInstance().displayImage(meiziM.smallPicUrl, holder.image,new SimpleImageLoadingListener(){

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				// TODO Auto-generated method stub
				((ImageView) view).setImageBitmap(loadedImage);
				int bitmapWidth = loadedImage.getWidth();
                int bitmapHeight = loadedImage.getHeight();
                LayoutParams params = view.getLayoutParams();
                params.height = (int) ((float) view.getWidth() / (float) bitmapWidth * (float) bitmapHeight);
                view.requestLayout();
			}
        	 
        });

        return view;
    }

    private class Holder {
        public ImageView image;

        public Holder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
