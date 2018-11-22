package sp.phone.bean;


public class ReputationData {//给任务具体信息载入用的

    private String mName;

    private String mData;

    public ReputationData(String name, String data) {
        mName = name;
        mData = data;
    }

    public String getName() {
        return mName;
    }

    public String getData() {
        return mData;
    }
}
