package sp.phone.bean;


public class adminForumsData {//给任务具体信息载入用的
    private String fid, fname;

    public void Set_Data(String fid, String fname) {
        this.fid = fid;
        this.fname = fname;
    }

    public String get_fid() {
        return fid;
    }

    public String get_fname() {
        return fname;
    }
}
