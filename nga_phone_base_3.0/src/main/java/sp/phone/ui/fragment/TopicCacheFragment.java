package sp.phone.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.mvp.model.entity.ThreadPageInfo;

/**
 * @author Justwen
 */
public class TopicCacheFragment extends TopicSearchFragment implements View.OnLongClickListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getActivity(), "长按可删除缓存的帖子", Toast.LENGTH_SHORT).show();
        mAdapter.setOnLongClickListener(this);
    }

    @Override
    public void removeTopic(int position) {
        mAdapter.removeItem(position);
    }

    @Override
    public void removeTopic(ThreadPageInfo pageInfo) {
        mAdapter.removeItem(pageInfo);
    }

    @Override
    public boolean onLongClick(final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(this.getString(R.string.delete_favo_confirm_text))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    ThreadPageInfo info = (ThreadPageInfo) view.getTag();
                    mPresenter.removeCacheTopic(info);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
        return true;
    }

    @Override
    public void onClick(View view) {

    }

}
