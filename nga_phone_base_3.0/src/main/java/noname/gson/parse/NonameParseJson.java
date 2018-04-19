package noname.gson.parse;

import com.alibaba.fastjson.JSON;

public class NonameParseJson {
    public static NonameReadResponse parseRead(String json) {
        return JSON.parseObject(json, NonameReadResponse.class);
    }


    public static NonameThreadResponse parseThreadRead(String json) {
        return JSON.parseObject(json, NonameThreadResponse.class);
    }


    public static NonamePostResponse parsePost(String json) {
        return JSON.parseObject(json, NonamePostResponse.class);
    }
}