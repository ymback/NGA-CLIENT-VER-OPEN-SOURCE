package sp.phone.adapter;

import gov.anzong.androidnga.R;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import sp.phone.bean.NearbyUser;
import sp.phone.utils.ImageUtil;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearbyUsersAdapter extends BaseAdapter {
	private final List<NearbyUser> list;
	
	

	public NearbyUsersAdapter(List<NearbyUser> list) {
		super();
		this.list = list;
	}

	@Override
	public int getCount() {
		if(list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if(list == null)
			return null;
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		View ret = null;
		if(convertView == null){

			ret = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_user, parent, false);
			
		}else{
			ret =  convertView;
		}
		final NearbyUser u = list.get(position);
		String text = u.getNickName();
		try {
			text = URLDecoder.decode(text,"utf-8");
		} catch (UnsupportedEncodingException e) {

		}

		text = text +"(" + 
				u.getJuli() +
				"รื)";
		TextView tv = (TextView)ret.findViewById(R.id.nickname);
		tv.setText(text);
		final ImageView iv = (ImageView)ret.findViewById(R.id.avatarimg);
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				final Bitmap bitmap = getAvatar(u, parent.getContext());
				iv.post(new Runnable() {
					@Override
					public void run() {
						iv.setImageBitmap(bitmap);
					}
				});
			}
		});
		return ret;
	}
	Bitmap getAvatar(NearbyUser u,Context c){
		String extensions[] = {"jpg","png","bmp","gif","jpeg"};
		Bitmap bitmap = null;
		for(int i = 0 ; i< extensions.length; ++i){
			String avatarPath = ImageUtil.getAvatarById(extensions[i], u.getUserId());
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists())
				{
					bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath, 150);
					break;
				}
			}
			
		}
		Resources res = c.getResources();
		if(bitmap == null)
		{
			
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			bitmap = ImageUtil.loadAvatarFromStream(is, is2, 150);
		}
		
		return bitmap;
	}

}
