package sp.phone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetialInfo;
import sp.phone.interfaces.NextJsonMessageDetialLoader;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.FunctionUtil;
import sp.phone.common.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public class AppendableMessageDetialAdapter extends MessageDetialAdapter {
    static Context context;
    final private List<MessageDetialInfo> infoList;
    final private PullToRefreshAttacher attacher;
    private final NextJsonMessageDetialLoader loader;
    Toast toast = null;
    boolean isPrompted = false;
    private int count = 0;
    private boolean isEndOfList = false;
    private boolean isLoading = false;

    @SuppressWarnings("static-access")
    public AppendableMessageDetialAdapter(Context context, PullToRefreshAttacher attacher, NextJsonMessageDetialLoader loader) {
        super(context);
        this.context = context;
        infoList = new ArrayList<MessageDetialInfo>();
        this.attacher = attacher;
        this.loader = loader;
    }

    @Override
    protected MessageArticlePageInfo getEntry(int position) {
        int i = (int) position / 20;
        MessageArticlePageInfo tmp = infoList.get(i).getMessageEntryList().get(position % 20);
        return tmp;
    }//FIXED FC BUG

    @Override
    public void finishLoad(MessageDetialInfo result) {
        isLoading = false;
        if (attacher != null)
            attacher.setRefreshComplete();
        if (result == null) {
            return;
        }
        ActivityUtils.getInstance().dismiss();

        infoList.add(result);
        count += result.getMessageEntryList().size();
        if (result.get__nextPage() > 0) {
            isEndOfList = false;
        } else {
            isEndOfList = true;
        }
        this.notifyDataSetChanged();
    }

    public void notifyDataSetChangedWithModChange() {
        for (int i = 0; i < infoList.size(); i++) {
            for (int j = 0; j < infoList.get(i).getMessageEntryList().size(); j++) {
                sethtmldata(infoList.get(i).getMessageEntryList().get(j), j);
            }
        }
        this.notifyDataSetChanged();
    }

    public void sethtmldata(MessageArticlePageInfo row, int i) {
        fillFormated_html_data(row, i + 1);
    }

    public void fillFormated_html_data(MessageArticlePageInfo row, int i) {

        ThemeManager theme = ThemeManager.getInstance();
        if (row.getContent() == null) {
            row.setContent(row.getSubject());
            row.setSubject(null);
        }
        int bgColor = context.getResources().getColor(
                theme.getBackgroundColor(i));
        int fgColor = context.getResources().getColor(
                theme.getForegroundColor());
        bgColor = bgColor & 0xffffff;
        final String bgcolorStr = String.format("%06x", bgColor);

        int htmlfgColor = fgColor & 0xffffff;
        final String fgColorStr = String.format("%06x", htmlfgColor);

        String formated_html_data = MessageDetialAdapter.convertToHtmlText(row, FunctionUtil.isShowImage(), FunctionUtil.showImageQuality(), fgColorStr, bgcolorStr);
        row.setFormated_html_data(formated_html_data);
    }

    public int getNextPage() {
        return infoList.size() + 1;
    }

    public boolean getIsEnd() {
        return isEndOfList;
    }

    @Override
    public int getCount() {
        return count;
    }

    @SuppressWarnings("static-access")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View ret = super.getView(position, view, parent);
        if (position + 1 == this.getCount() && !isLoading) {
            if (isEndOfList == false) {
                isLoading = true;
                loader.loadNextPage(this);
            } else {
                if (isPrompted == false) {
                    if (toast != null) {
                        toast.setText(context.getString(R.string.last_page_prompt_message_detail));
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(this.context, context.getString(R.string.last_page_prompt_message_detail), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    isPrompted = true;
                }
            }
        }
        return ret;
    }
}
