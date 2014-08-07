package sp.phone.listener;

import noname.gson.parse.NonameReadBody;
import noname.gson.parse.NonameReadResponse;
import gov.anzong.androidnga.R;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

public class MyListenerForNonameReply implements OnClickListener {
	int mPosition;
	private View button;
	private long lastTimestamp = 0;
	Context mcontext;
	NonameReadResponse mData;

	public MyListenerForNonameReply(int inPosition,Context incontext,NonameReadResponse data) {
		mPosition = inPosition;
		mcontext = incontext;
		mData=data;
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
				MyListenerForNonameReply.this.button.setEnabled(true);
			}

			@Override
			protected Void doInBackground(Void... params) {
				Intent intent = new Intent();
				StringBuffer postPrefix = new StringBuffer();
				String mention = null;

				final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
				final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
				NonameReadBody row = mData.data.posts[mPosition];
				String content = row.content;
				final String name = row.hip;
				content = content.replaceAll(quote_regex, "");
				content = content.replaceAll(replay_regex, "");
				final long longposttime = row.ptime;
				String postTime ="";
				if(longposttime!=0){
					postTime = StringUtil.TimeStamp2Date(String.valueOf(longposttime));
				}
				final String tidStr = String.valueOf(mData.data.tid);
				content = FunctionUtil.checkContent(content);
				content = StringUtil.unEscapeHtml(content);
				mention = name;
				postPrefix.append("[quote]");
				postPrefix.append("[b]Post by [hip]");
				postPrefix.append(name);
				postPrefix.append("[/hip] (");
				postPrefix.append(postTime);
				postPrefix.append("):[/b]\n");
				postPrefix.append(content);
				postPrefix.append("[/quote]\n");
				if (!StringUtil.isEmpty(mention))
					intent.putExtra("mention", mention);
				intent.putExtra("prefix",
						StringUtil.removeBrTag(postPrefix.toString()));
				if (tidStr != null)
					intent.putExtra("tid", tidStr);
				intent.putExtra("action", "reply");
					intent.setClass(
							mcontext,
							PhoneConfiguration.getInstance().nonamePostActivityClass);
					mcontext.startActivity(intent);
				if (PhoneConfiguration.getInstance().showAnimation)
					((Activity) mcontext).overridePendingTransition(
							R.anim.zoom_enter, R.anim.zoom_exit);
				return null;
			}
		}).execute();
	}
}
