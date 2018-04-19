package noname.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.anzong.androidnga.R;
import noname.interfaces.NextJsonNonameTopicListLoader;
import noname.gson.parse.NonameThreadBody;
import noname.gson.parse.NonameThreadResponse;
import noname.interfaces.NextJsonNonameTopicListLoader;
import sp.phone.util.ActivityUtils;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public class AppendableNonameTopicAdapter extends NonameTopicListAdapter {
    final private List<NonameThreadResponse> infoList;
    final private PullToRefreshAttacher attacher;
    private final NextJsonNonameTopicListLoader loader;
    Set<Integer> tidSet;
    private Toast toast = null;
    private boolean isEndOfList = false;
    private boolean isPrompted = false;
    private boolean isLoading = false;

    public AppendableNonameTopicAdapter(Context context, PullToRefreshAttacher attacher, NextJsonNonameTopicListLoader loader) {
        super(context);
        infoList = new ArrayList<NonameThreadResponse>();
        tidSet = new HashSet<Integer>();
        this.attacher = attacher;
        this.loader = loader;
    }

    @Override
    protected NonameThreadBody getEntry(int position) {
        int i = (int) position / 20;
        NonameThreadBody tmp = infoList.get(i).data.threads[position % 20];
        if (tmp != null)
            return tmp;

        return null;
    }

    @Override
    public void jsonfinishLoad(NonameThreadResponse result) {
        isLoading = false;
        if (attacher != null)
            attacher.setRefreshComplete();
        if (result == null) {
            return;
        }
        ActivityUtils.getInstance().dismiss();
        for (int i = 0; i < result.data.threads.length; i++) {
            NonameThreadBody info = result.data.threads[i];
            if (info == null) {
                continue;
            }
            int tid = info.tid;
            tidSet.add(tid);
        }

//		}

        infoList.add(result);
        count += result.data.threads.length;
        if (result.data.page == result.data.totalpage) {
            isEndOfList = true;
        } else {
            isEndOfList = false;
        }
        this.notifyDataSetChanged();

    }

    public void clear() {
        count = 0;
        infoList.clear();
        tidSet.clear();
        setSelected(-1);
        isPrompted = false;
    }

    public int getNextPage() {
        return infoList.size() + 1;
    }

    public boolean getIsEnd() {
        return isEndOfList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View ret = super.getView(position, view, parent);
        if (position + 1 == this.getCount() && !isLoading) {
            if (isEndOfList == true) {
                if (isPrompted == false) {
                    if (toast != null) {
                        toast.setText(context.getString(R.string.last_page_prompt));
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(this.context, context.getString(R.string.last_page_prompt), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    isPrompted = true;
                }
            } else {
                isLoading = true;
                loader.loadNextPage(this);
            }
        }
        return ret;
    }


}
