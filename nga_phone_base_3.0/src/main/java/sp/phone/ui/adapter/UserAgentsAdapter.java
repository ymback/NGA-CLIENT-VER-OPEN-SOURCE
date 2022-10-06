package sp.phone.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.common.UserAgent;
import sp.phone.view.RecyclerViewEx;

public class UserAgentsAdapter extends RecyclerViewEx.Adapter<UserAgentsAdapter.UserAgentsViewHodler> {
  private Context mContext;

  private View.OnClickListener mOnClickListener;

  private List<UserAgent> mUserAgents;

  public UserAgentsAdapter(Context context, List<UserAgent> userAgents) {
    this.mContext = context;
    this.mUserAgents = userAgents;
  }

  @NonNull
  @Override
  public UserAgentsViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
    View convertView = LayoutInflater.from(mContext).inflate(R.layout.list_user_agents_item, viewGroup, false);
    UserAgentsAdapter.UserAgentsViewHodler viewHolder = new UserAgentsAdapter.UserAgentsViewHodler(convertView);

    viewHolder.itemView.setOnClickListener(mOnClickListener);
    viewHolder.checkView.setOnClickListener(mOnClickListener);

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull UserAgentsViewHodler viewHolder, int position) {
    UserAgent keyword = mUserAgents.get(position);
    viewHolder.userAgentView.setText(keyword.getKeyword());
    viewHolder.checkView.setChecked(keyword.isEnabled());
    viewHolder.userAgentView.setTag(position);
    viewHolder.checkView.setTag(position);
  }

  @Override
  public int getItemCount() {
    return mUserAgents.size();
  }

  public void setOnClickListener(View.OnClickListener onClickListener) {
    this.mOnClickListener = onClickListener;
  }

  public static class UserAgentsViewHodler extends RecyclerView.ViewHolder {

    @BindView(R.id.user_agent)
    TextView userAgentView;

    @BindView(R.id.check)
    Switch checkView;

    public UserAgentsViewHodler(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}