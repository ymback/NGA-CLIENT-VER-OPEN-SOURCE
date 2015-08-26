package sp.phone.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

import gov.anzong.androidnga.R;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class MyListenerForReply implements OnClickListener {
    int mPosition;
    ThreadData mdata = new ThreadData();
    Context mcontext;
    private View button;
    private long lastTimestamp = 0;

    public MyListenerForReply(int inPosition, ThreadData data, Context context) {
        mPosition = inPosition;
        mdata = data;
        mcontext = context;
    }

    @Override
    public void onClick(View v) {

        if (System.currentTimeMillis() - this.lastTimestamp <= 3000) {
            return;
        } else {
            this.lastTimestamp = System.currentTimeMillis();
        }

        this.button = v;
        this.button.setEnabled(false);

        (new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void result) {
                MyListenerForReply.this.button.setEnabled(true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Intent intent = new Intent();
                StringBuffer postPrefix = new StringBuffer();
                String mention = null;

                final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
                final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
                ThreadRowInfo row = mdata.getRowList().get(mPosition);
                String content = row.getContent();
                final String name = row.getAuthor();
                final String uid = String.valueOf(row.getAuthorid());
                int page = (row.getLou() + 20) / 20;// 以楼数计算page
                content = content.replaceAll(quote_regex, "");
                content = content.replaceAll(replay_regex, "");
                final String postTime = row.getPostdate();
                final String tidStr = String.valueOf(row.getTid());
                content = FunctionUtil.checkContent(content);
                content = StringUtil.unEscapeHtml(content);
                if (row.getPid() != 0) {
                    mention = name;
                    postPrefix.append("[quote][pid=");
                    postPrefix.append(row.getPid());
                    postPrefix.append(',');
                    if (tidStr != null) {
                        postPrefix.append(tidStr);
                        postPrefix.append(",");
                    }
                    if (page > 0)
                        postPrefix.append(page);
                    postPrefix.append("]");// Topic
                    postPrefix.append("Reply");
                    if (row.getISANONYMOUS()) {// 是匿名的人
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append("-1");
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid][color=gray](");
                        postPrefix.append(row.getLou());
                        postPrefix.append("楼)[/color] (");
                    } else {
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append(uid);
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid] (");
                    }
                    postPrefix.append(postTime);
                    postPrefix.append("):[/b]\n");
                    postPrefix.append(content);
                    postPrefix.append("[/quote]\n");
                }
                if (!StringUtil.isEmpty(mention))
                    intent.putExtra("mention", mention);
                intent.putExtra("prefix",
                        StringUtil.removeBrTag(postPrefix.toString()));
                if (tidStr != null)
                    intent.putExtra("tid", tidStr);
                intent.putExtra("action", "reply");

                if (!StringUtil
                        .isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent.setClass(
                            mcontext,
                            PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intent.setClass(
                            mcontext,
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                mcontext.startActivity(intent);
                if (PhoneConfiguration.getInstance().showAnimation)
                    ((Activity) mcontext).overridePendingTransition(
                            R.anim.zoom_enter, R.anim.zoom_exit);
                return null;
            }
        }).execute();
    }

}
