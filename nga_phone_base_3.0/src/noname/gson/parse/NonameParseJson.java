package noname.gson.parse;

import com.google.gson.Gson;

public class NonameParseJson {
    public static NonameReadResponse parseRead(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, NonameReadResponse.class);
    }


    public static NonameThreadResponse parseThreadRead(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, NonameThreadResponse.class);
    }


    public static NonamePostResponse parsePost(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, NonamePostResponse.class);
    }
}