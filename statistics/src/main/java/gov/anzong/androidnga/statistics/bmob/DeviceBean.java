package gov.anzong.androidnga.statistics.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Created by Justwen on 2018/8/12.
 */
public class DeviceBean extends BmobObject {

    private String androidId;

    private int androidVersion;

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public int getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(int androidVersion) {
        this.androidVersion = androidVersion;
    }
}
