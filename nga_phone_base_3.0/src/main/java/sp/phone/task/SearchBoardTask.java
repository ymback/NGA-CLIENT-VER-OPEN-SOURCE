package sp.phone.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sp.phone.bean.Board;
import sp.phone.listener.OnSimpleHttpCallBack;
import sp.phone.retrofit.RetrofitHelper;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/10/12.
 */
public class SearchBoardTask {


    public static void execute(String boardName, OnSimpleHttpCallBack<Board> callBack) {
        RetrofitHelper.getInstance()
                .getService()
                .get("http://bbs.nga.cn/forum.php?&__output=8&key=" + StringUtils.encodeUrl(boardName, "gbk"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(s -> {
                    try {
                        JSONObject obj = JSON.parseObject(s).getJSONObject("data").getJSONObject("0");
                        int fid = obj.getInteger("fid");
                        String title = obj.getString("name");
                        return new Board(fid, title);

                    } catch (Exception e) {

                    }
                    return null;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Board>() {
                    @Override
                    public void onNext(Board board) {
                        callBack.onResult(board);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onResult(null);
                    }
                });
    }
}
