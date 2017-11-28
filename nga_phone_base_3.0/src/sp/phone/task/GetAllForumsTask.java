package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;

import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.ForumListActivity;
import sp.phone.common.PhoneConfiguration;
import sp.phone.mvp.model.ForumsListModel;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;

/**
 * 版块列表
 * Created by elrond on 2017/9/28.
 */

public class GetAllForumsTask extends AsyncTask<String, Integer, ForumsListModel> {
    private static final String URL = "app_api.php?__lib=home&__act=category";
    private Context context;
    private String mUrl;

    public GetAllForumsTask(Context context) {
        this.context = context;
        mUrl = Utils.getNGAHost() + URL;
    }

    @Override
    protected ForumsListModel doInBackground(String... params) {
        String json = HttpUtil.getHtml(mUrl, PhoneConfiguration.getInstance().getCookie());
        return JSON.parseObject(json, ForumsListModel.class);
    }

    @Override
    protected void onPreExecute() {
        ActivityUtils.getInstance().noticeSaying(context);
    }

    @Override
    protected void onPostExecute(ForumsListModel result) {
        ActivityUtils.getInstance().dismiss();
        if (result == null) {
            ActivityUtils.getInstance().noticeError("", context);
        } else if (context instanceof ForumListActivity) {
            ((ForumListActivity) context).notifyResult(result);
        }
    }

    @Override
    protected void onCancelled(ForumsListModel result) {
        this.onCancelled();
    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
    }
}
