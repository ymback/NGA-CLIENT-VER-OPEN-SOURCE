package sp.phone.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.mvp.model.entity.SubBoard;

/**
 * Created by Justwen on 2018/1/27.
 */

public class BoardSubListAdapter extends RecyclerView.Adapter<BoardSubListAdapter.ViewHolderEx> {

    private List<SubBoard> mBoardList;

    private Context mContext;

    private View.OnClickListener mOnClickListener;

    public BoardSubListAdapter(Context context, List<SubBoard> boardList) {
        mBoardList = boardList;
        mContext = context;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public ViewHolderEx onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_sub_board_item, viewGroup, false);
        return new ViewHolderEx(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderEx viewHolderEx, int i) {
        SubBoard board = mBoardList.get(i);
        viewHolderEx.mCheckableView.setChecked(board.isChecked());
        viewHolderEx.mTitleView.setText(board.getName());
        if (TextUtils.isEmpty(board.getDescription())) {
            viewHolderEx.mSummaryView.setVisibility(View.GONE);
        } else {
            viewHolderEx.mSummaryView.setVisibility(View.VISIBLE);
            viewHolderEx.mSummaryView.setText(board.getDescription());
        }
        viewHolderEx.mCheckableView.setOnClickListener(mOnClickListener);
        viewHolderEx.mCheckableView.setVisibility(board.getType() >= 0 ? View.VISIBLE : View.GONE);
        viewHolderEx.itemView.setOnClickListener(mOnClickListener);
        viewHolderEx.mCheckableView.setTag(board);
        viewHolderEx.itemView.setTag(board);
    }


    @Override
    public int getItemCount() {
        return mBoardList == null ? 0 : mBoardList.size();
    }

    public class ViewHolderEx extends RecyclerView.ViewHolder {

        public TextView mTitleView;

        public TextView mSummaryView;

        public Switch mCheckableView;

        public ViewHolderEx(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mSummaryView = itemView.findViewById(R.id.summary);
            mCheckableView = itemView.findViewById(R.id.check);
        }
    }
}
