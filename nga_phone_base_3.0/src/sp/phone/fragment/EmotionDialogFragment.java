package sp.phone.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import gov.anzong.androidnga.R;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.utils.ImageUtil;


public class EmotionDialogFragment extends NoframeDialogFragment {

    private GridView gv = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gv = (GridView) inflater.inflate(R.layout.emotion_grid, container, false);
        return gv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        gv.setAdapter(new EmotionGridAdaptor());

        gv.setBackgroundResource(R.color.shit1);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String emotion = (String) parent.getItemAtPosition(position);
                OnEmotionPickedListener father = null;
                try {
                    father = (OnEmotionPickedListener) getActivity();
                } catch (ClassCastException e) {
                    Log.e(this.getClass().getSimpleName(), "father shold implements " + OnEmotionPickedListener.class.getCanonicalName());
                }
                if (father != null)
                    father.onEmotionPicked(emotion);
            }

        });
        super.onViewCreated(view, savedInstanceState);
    }


    class EmotionGridAdaptor extends BaseAdapter {
        private final int[] emotions = {1, 2, 3, 24, 25, 26, 27, 28, 29,
                30, 32, 33, 34, 35, 36, 37, 38, 39,
                4, 40, 41, 42, 43, 5, 6, 7, 8
        };

        @Override
        public int getCount() {

            return emotions.length;
        }

        @Override
        public Object getItem(int position) {

            return "[s:" + emotions[position] + "]";
        }

        @Override
        public long getItemId(int position) {

            return emotions[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {

                convertView = new ImageView(parent.getContext());
            }
            String fileName = "a" + emotions[position] + ".gif";
            try {
                InputStream is = getActivity().getAssets().open(fileName);
                Bitmap bm = BitmapFactory.decodeStream(is);
                Bitmap newbm = ImageUtil.zoomImageByWidth(bm, 55);
                bm.recycle();
                ((ImageView) convertView).setImageBitmap(newbm);
                is.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return convertView;
        }

    }
}
