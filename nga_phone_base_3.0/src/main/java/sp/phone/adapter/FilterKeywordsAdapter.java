package sp.phone.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.common.FilterKeyword;
import sp.phone.view.RecyclerViewEx;

public class FilterKeywordsAdapter extends RecyclerViewEx.Adapter<FilterKeywordsAdapter.FilterKeywordsViewHodler> {
  private Context mContext;

  private View.OnClickListener mOnClickListener;

  private List<FilterKeyword> mKeywords;

  public FilterKeywordsAdapter(Context context, List<FilterKeyword> keywords) {
    this.mContext = context;
    this.mKeywords = keywords;
  }

  @NonNull
  @Override
  public FilterKeywordsViewHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
    View convertView = LayoutInflater.from(mContext).inflate(R.layout.list_filter_keywords_item, viewGroup, false);
    FilterKeywordsAdapter.FilterKeywordsViewHodler viewHolder = new FilterKeywordsAdapter.FilterKeywordsViewHodler(convertView);

    viewHolder.itemView.setOnClickListener(mOnClickListener);
    viewHolder.checkView.setOnClickListener(mOnClickListener);

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull FilterKeywordsViewHodler viewHolder, int position) {
    FilterKeyword keyword = mKeywords.get(position);
    viewHolder.filterKeywordView.setText(keyword.getKeyword());
    viewHolder.checkView.setChecked(keyword.isEnabled());
    viewHolder.filterKeywordView.setTag(position);
    viewHolder.checkView.setTag(position);
  }

  @Override
  public int getItemCount() {
    return mKeywords.size();
  }

  public void setOnClickListener(View.OnClickListener onClickListener) {
    this.mOnClickListener = onClickListener;
  }

  public static class FilterKeywordsViewHodler extends RecyclerView.ViewHolder {

    @BindView(R.id.filter_keyword)
    TextView filterKeywordView;

    @BindView(R.id.check)
    Switch checkView;

    public FilterKeywordsViewHodler(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}