package sp.phone.lab.mvp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.model.entity.ThreadPageInfo;

/**
 * Created by Justwen on 2017/11/19.
 */

public class TopicListFavoriteFragment extends TopicListFragment implements View.OnLongClickListener {

    @Override
    protected void setTitle() {
        setTitle(R.string.bookmark_title);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(), "长按可删除收藏的帖子", Toast.LENGTH_SHORT).show();
        mAdapter.setOnLongClickListener(this);
    }

    @Override
    public void removeTopic(int position) {
        mAdapter.remove(position);
    }

    @Override
    public boolean onLongClick(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(this.getString(R.string.delete_favo_confirm_text))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ThreadPageInfo info = (ThreadPageInfo) view.getTag();
                        mPresenter.removeTopic(info, info.getPosition());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
        return true;
    }
}
