package sp.phone.model.convert;

import com.alibaba.fastjson.JSON;

import java.util.Map;

import sp.phone.bean.TopicListBean;
import sp.phone.model.entity.ThreadPageInfo;
import sp.phone.model.entity.TopicListInfo;
import sp.phone.utils.NLog;

/**
 * Created by Justwen on 2017/11/21.
 */

public class TopicConvertFactory extends ErrorConvertFactory {

    private static final String TAG = TopicConvertFactory.class.getSimpleName();

    public static TopicListInfo getTopicListInfo(String js, int page) {

        TopicListBean topicListBean = JSON.parseObject(js, TopicListBean.class);

        try {
            TopicListInfo listInfo = new TopicListInfo();
            Map<String, TopicListBean.DataBean.TBean> map = topicListBean.getData().get__T();
            for (String key : map.keySet()) {
                TopicListBean.DataBean.TBean tBean = map.get(key);
                ThreadPageInfo pageInfo = new ThreadPageInfo();
                pageInfo.setAuthor(tBean.getAuthor());
                pageInfo.setLastPoster(tBean.getLastposter());
                pageInfo.setSubject(tBean.getSubject());
                pageInfo.setReplies(tBean.getReplies());
                pageInfo.setType(tBean.getType());
                pageInfo.setTopicMisc(tBean.getTopic_misc());
                pageInfo.setTitleFont(tBean.getTitlefont());
                pageInfo.setTid(tBean.getTid());
                pageInfo.setPage(page);
                listInfo.addThreadPage(pageInfo);
            }
            return listInfo;
        } catch (NullPointerException e) {
            NLog.e(TAG, "can not parse :\n" + js);
            return null;
        }

    }

}
