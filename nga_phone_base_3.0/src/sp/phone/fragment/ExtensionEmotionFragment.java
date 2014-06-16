package sp.phone.fragment;

import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.interfaces.OnEmotionPickedListener;
import gov.anzong.androidnga2.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;


public class ExtensionEmotionFragment extends NoframeDialogFragment {
	private GridView gv = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		gv= (GridView) inflater.inflate(R.layout.extension_emotion_grid, container, false);
		return gv;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		int index = getArguments().getInt("index");
		gv.setAdapter(new ExtensionEmotionAdapter(index));
		gv.setBackgroundColor(0x7f000000);
		gv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String emotion = (String) parent.getItemAtPosition(position);
				OnEmotionPickedListener father = null;
				try{
					father = (OnEmotionPickedListener)getActivity();
				}catch(ClassCastException e){
					Log.e(this.getClass().getSimpleName(), "father shold implements " + OnEmotionPickedListener.class.getCanonicalName());
				}
				if(father != null)
					father.onEmotionPicked(emotion);
			}
			
		});
		super.onViewCreated(view, savedInstanceState);
	}
}
