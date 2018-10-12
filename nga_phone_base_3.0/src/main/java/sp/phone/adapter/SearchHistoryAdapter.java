package sp.phone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/10/12.
 */
public class SearchHistoryAdapter extends BaseAdapter<String, SearchHistoryAdapter.HistoryViewHolder> {

    public SearchHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_search_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        holder.mTitleView.setText(mDataList.get(position));
        holder.itemView.setTag(mDataList.get(position));
        holder.mDeleteBtn.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.mDeleteBtn.setOnClickListener(mOnClickListener);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleView;

        private ImageView mDeleteBtn;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(android.R.id.title);
            mDeleteBtn = itemView.findViewById(R.id.iv_delete);
        }
    }
}
