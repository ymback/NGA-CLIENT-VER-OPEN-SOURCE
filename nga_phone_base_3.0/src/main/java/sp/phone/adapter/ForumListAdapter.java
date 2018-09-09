package sp.phone.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.GlideApp;
import sp.phone.common.ApiConstants;
import sp.phone.common.PhoneConfiguration;
import sp.phone.mvp.model.ForumsListModel;
import sp.phone.theme.ThemeManager;

/**
 * 版块列表
 * Created by elrond on 2017/9/29.
 */

public class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ForumsListModel.Forum> mList;
    private int mColor;

    public ForumListAdapter(Context context, List<ForumsListModel.Forum> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mList = list;
        mColor = mContext.getColor(ThemeManager.getInstance().getForegroundColor());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_board_icon, parent, false);
        view.setOnClickListener(mListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ForumsListModel.Forum forum = mList.get(position);
        holder.name.setText(forum.getName());
        holder.itemView.setTag(forum);
        GlideApp.with(mContext)
                .load(String.format(ApiConstants.URL_BOARD_ICON, forum.getId()))
                .placeholder(R.drawable.default_board_icon)
                .dontAnimate()
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon_board_img)
        public ImageView icon;
        @BindView(R.id.text_board_name)
        public TextView name;
        public String fid;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            name.setTextColor(mColor);
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ForumsListModel.Forum forum = (ForumsListModel.Forum) v.getTag();
            Intent intent = new Intent(mContext, PhoneConfiguration.getInstance().topicActivityClass);
            intent.putExtra("tab", "1");
            intent.putExtra("fid", forum.getId());
            intent.putExtra("board_name", forum.getName());
            mContext.startActivity(intent);
        }
    };
}
