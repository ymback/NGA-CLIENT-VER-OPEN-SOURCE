package sp.phone.adapter.beta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/6/10.
 */
public abstract class BaseAdapterEx<E, T extends RecyclerView.ViewHolder> extends BaseAdapter<E, T> {

    private String mEmptyString;

    private static class EmptyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_empty);
        }
    }


    public BaseAdapterEx(Context context) {
        super(context);
        setEmptyString(context.getString(R.string.error_load_failed));
        setEmptyView(mLayoutInflater.inflate(R.layout.list_empty_view, null, false));
    }

    public void setEmptyString(String emptyString) {
        mEmptyString = emptyString;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent, View emptyView) {
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new EmptyViewHolder(emptyView);
    }

    @Override
    protected void onBindEmptyViewHolder(RecyclerView.ViewHolder holder) {
        EmptyViewHolder viewHolder = (EmptyViewHolder) holder;
        viewHolder.mTextView.setText(mEmptyString);
    }
}
