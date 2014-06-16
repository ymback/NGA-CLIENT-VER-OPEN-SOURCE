package sp.phone.adapter;

import gov.anzong.androidnga2.R;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import sp.phone.bean.NotificationObject;
import sp.phone.utils.ImageUtil;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PendingReplyAdapter extends BaseAdapter {
	private final List<NotificationObject> list;
	
	
	public PendingReplyAdapter(List<NotificationObject> list) {
		super();
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	static class ViewHolder{
		public TextView nickTv;
		public TextView titleTv;
		public ImageView avatarIv;
	};
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.pending_relay, parent, false);	
			holder = new ViewHolder();
			holder.nickTv = (TextView) convertView.findViewById(R.id.nick_name);
			holder.titleTv =  (TextView) convertView.findViewById(R.id.title);
			holder.avatarIv = (ImageView) convertView.findViewById(R.id.avatar_image);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.nickTv.setText(list.get(position).getNickName());
		
		holder.titleTv.setText(list.get(position).getTitle());
		handleAvatar(holder.avatarIv,list.get(position).getAuthorId());

		return convertView;
	}

	void handleAvatar(ImageView avatarIV, int authorid)
	{
		String extensions[] = {"jpg","png","bmp","gif","jpeg"};
		Bitmap bitmap = null;
		for(int i = 0 ; i< extensions.length; ++i){
			String avatarPath = ImageUtil.getAvatarById(extensions[i], String.valueOf(authorid));
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists())
				{
					bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath, 150);
					break;
				}
			}
			
		}
		
		Resources res = avatarIV.getResources();
		if(bitmap == null)
		{
			
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			bitmap = ImageUtil.loadAvatarFromStream(is, is2, 150);
		}
		
		if( bitmap != null){
			avatarIV.setImageBitmap(bitmap);
		}
		
	}
}
