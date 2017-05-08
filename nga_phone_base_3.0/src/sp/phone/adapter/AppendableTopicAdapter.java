package sp.phone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.utils.ActivityUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public class AppendableTopicAdapter extends TopicListAdapter {
    final private List<TopicListInfo> infoList;
    final private PullToRefreshAttacher attacher;
    private final NextJsonTopicListLoader loader;
    Set<Integer> tidSet;
    private Toast toast = null;
    private boolean isEndOfList = false;
    private boolean isPrompted = false;
    private int table;
    private boolean isLoading = false;

    public AppendableTopicAdapter(Context context, PullToRefreshAttacher attacher, NextJsonTopicListLoader loader) {
        super(context);
        infoList = new ArrayList<>();
        tidSet = new HashSet<>();
        this.attacher = attacher;
        this.loader = loader;
    }

    @Override
    protected ThreadPageInfo getEntry(int position) {
        for (int i = 0; i < infoList.size(); i++) {
            if (position < infoList.get(i).get__T__ROWS()) {
                return infoList.get(i).getArticleEntryList().get(position);
            }
            position -= infoList.get(i).get__T__ROWS();
        }
        return null;
    }

    @Override
    public void jsonfinishLoad(TopicListInfo result) {
        isLoading = false;

        if (attacher != null)
            attacher.setRefreshComplete();
        if (result.get__SEARCHNORESULT()) {
            if (toast != null) {
                toast.setText("结果已搜索完毕");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(this.context, "结果已搜索完毕", Toast.LENGTH_SHORT);
                toast.show();
            }
            isLoading = false;
        }
        ActivityUtil.getInstance().dismiss();
        if (count != 0) {
            List<ThreadPageInfo> threadList = new ArrayList<ThreadPageInfo>();
            for (int i = 0; i < result.getArticleEntryList().size(); i++) {
                ThreadPageInfo info = result.getArticleEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int tid = info.getTid();
                if (!tidSet.contains(tid)) {
                    threadList.add(info);
                    tidSet.add(tid);
                }
            }
            result.set__T__ROWS(threadList.size());
            result.setArticleEntryList(threadList);
        } else {
            for (int i = 0; i < result.getArticleEntryList().size(); i++) {
                ThreadPageInfo info = result.getArticleEntryList().get(i);
                if (info == null) {
                    continue;
                }
                int tid = info.getTid();
                tidSet.add(tid);
            }

        }
        infoList.add(result);
        count += result.get__T__ROWS();
        if (count >= (result.get__ROWS())) {
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
        isPrompted = false;
        setSelected(-1);
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

    public void remove(int position) {
        for (int i = 0; i < infoList.size(); i++) {
            if (position < infoList.get(i).get__T__ROWS()) {
                infoList.get(i).getArticleEntryList().remove(position);
                infoList.get(i).set__T__ROWS(infoList.get(i).getArticleEntryList().size());
                count--;
            }
            position -= infoList.get(i).get__T__ROWS();
        }
    }

    public String gettidarray(int position) {
        for (int i = 0; i < infoList.size(); i++) {
            if (position < infoList.get(i).get__T__ROWS()) {
                return infoList.get(i).getArticleEntryList().get(position).getTidarray();
            }
            position -= infoList.get(i).get__T__ROWS();
        }
        return null;
    }
}
