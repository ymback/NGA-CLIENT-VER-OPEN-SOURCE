package sp.phone.fragment.material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadPageInfo;

/**
 * Created by Justwen on 2017/11/19.
 */

public class TopicListFavoriteFragment extends TopicListFragment implements AdapterView.OnItemLongClickListener {

    @Override
    protected void setTitle() {
        setTitle(R.string.bookmark_title);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(), "长按可删除收藏的帖子", Toast.LENGTH_SHORT).show();
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, final long id) {
        final int finalPosition = position;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ThreadPageInfo info = (ThreadPageInfo) view.getTag();
                        String tidArray = info.getTidarray();
                        mPresenter.removeTopic(tidArray, finalPosition);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(this.getString(R.string.delete_favo_confirm_text))
                .setPositiveButton(R.string.confirm, dialogClickListener)
                .setNegativeButton(R.string.cancle, dialogClickListener);
        final AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public void removeTopic(int position) {
        mAdapter.remove(position);
        mAdapter.notifyItemRemoved(position);
    }
}
