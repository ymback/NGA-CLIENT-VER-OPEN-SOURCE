package sp.phone.listener;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.util.FunctionUtils;
import sp.phone.util.StringUtils;

public class OnReplyClickListener implements OnClickListener {

    private Context mContext;

    public OnReplyClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View view) {

        ThreadRowInfo row = (ThreadRowInfo) view.getTag();

        view.setEnabled(false);

        (new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void result) {
                view.setEnabled(true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Intent intent = new Intent();
                StringBuffer postPrefix = new StringBuffer();
                String mention = null;

                final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
                final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
                String content = row.getContent();
                final String name = row.getAuthor();
                final String uid = String.valueOf(row.getAuthorid());
                int page = (row.getLou() + 20) / 20;// 以楼数计算page
                content = content.replaceAll(quote_regex, "");
                content = content.replaceAll(replay_regex, "");
                final String postTime = row.getPostdate();
                final String tidStr = String.valueOf(row.getTid());
                content = FunctionUtils.checkContent(content);
                content = StringUtils.unEscapeHtml(content);
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
                if (!StringUtils.isEmpty(mention))
                    intent.putExtra("mention", mention);
                intent.putExtra("prefix",
                        StringUtils.removeBrTag(postPrefix.toString()));
                if (tidStr != null)
                    intent.putExtra("tid", tidStr);
                intent.putExtra("action", "reply");

                if (UserManagerImpl.getInstance().getActiveUser() != null) {// 登入了才能发
                    intent.setClass(
                            mContext,
                            PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intent.setClass(
                            mContext,
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                mContext.startActivity(intent);
                return null;
            }
        }).execute();
    }

}
