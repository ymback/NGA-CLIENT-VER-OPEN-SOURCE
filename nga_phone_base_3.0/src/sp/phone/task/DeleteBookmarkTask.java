package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.Utils;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;


public class DeleteBookmarkTask extends AsyncTask<String, Integer, String> {
    private final String url = Utils.getNGAHost() + "nuke.php?nuke.php?__lib=topic_favor&__act=topic_favor&raw=3&lite=js&action=del&";
    //	String url = Utils.getNGAHost() + "nuke.php?__lib=topic_favor&__act=topic_favor&raw=3&action=del&";
    //post tidarray:3092111
    private Context context;
    private AdapterView<?> parent;
    private int position;

    public DeleteBookmarkTask(Context context, AdapterView<?> parent, int position) {
        super();
        this.context = context;
        this.parent = parent;
        this.position = position;
    }

    @Override
    protected String doInBackground(String... params) {

        String tidarray = params[0];
        HttpPostClient c = new HttpPostClient(url + tidarray);
        String cookie = PhoneConfiguration.getInstance().getCookie();
        c.setCookie(cookie);
        String body = "__lib=topic_favor&__act=topic_favor&raw=3&lite=js&action=del&" + tidarray;

        String ret = null;
        try {
            InputStream input = null;
            HttpURLConnection conn = c.post_body(body);
            if (conn != null)
                input = conn.getInputStream();

            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                ret = html;//getPostResult(html);

            }

        } catch (IOException e) {

        }
        return ret;
    }

    @Override
    protected void onPreExecute() {
        ActivityUtil.getInstance().noticeSaying(context);
    }

    @Override
    protected void onPostExecute(String result) {
        ActivityUtil.getInstance().dismiss();
        if (StringUtil.isEmpty(result))
            return;

        String msg = StringUtil.getStringBetween(result, 0, "{\"0\":\"", "\"},\"time\"").result;
        //android.R.drawable.ic_search_category_default
        if (!StringUtil.isEmpty(msg)) {
            Toast.makeText(context, msg.trim(), Toast.LENGTH_SHORT).show();
            if (msg.trim().equals("操作成功")) {
                Object a = parent.getAdapter();
                AppendableTopicAdapter adapter = null;
                if (a instanceof AppendableTopicAdapter) {
                    adapter = (AppendableTopicAdapter) a;
                } else if (a instanceof HeaderViewListAdapter) {
                    HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
                    adapter = (AppendableTopicAdapter) ha.getWrappedAdapter();
                    position -= ha.getHeadersCount();
                }
                adapter.remove(position);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCancelled(String result) {
        this.onCancelled();
    }

    @Override
    protected void onCancelled() {
        ActivityUtil.getInstance().dismiss();
    }


}