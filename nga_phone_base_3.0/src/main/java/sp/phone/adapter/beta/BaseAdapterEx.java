package sp.phone.adapter.beta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.util.ActivityUtils;

/**
 * Created by Justwen on 2018/6/10.
 */
public abstract class BaseAdapterEx<E, T extends RecyclerView.ViewHolder> extends BaseAdapterNew<E, T> {

    private String mEmptyString;

    public BaseAdapterEx(Context context) {
        super(context);
        setEmptyString(context.getString(R.string.error_load_failed));
        setEmptyView(R.layout.list_empty_view);
        setLoadingView(R.layout.list_loading_view);
    }

    public void setEmptyString(String emptyString) {
        mEmptyString = emptyString;
    }

    @Override
    protected void onBindLoadingViewHolder(RecyclerView.ViewHolder holder) {
        TextView sayingView = holder.itemView.findViewById(R.id.saying);
        sayingView.setText(ActivityUtils.getSaying());
    }

    @Override
    protected void onBindEmptyViewHolder(RecyclerView.ViewHolder holder) {
        TextView sayingView = holder.itemView.findViewById(R.id.saying);
        sayingView.setText(mEmptyString);
    }
}
