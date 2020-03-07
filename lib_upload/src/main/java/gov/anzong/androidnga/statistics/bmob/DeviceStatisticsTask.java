package gov.anzong.androidnga.statistics.bmob;

import java.util.UUID;

/**
 * Created by Justwen on 2018/8/12.
 */
public class DeviceStatisticsTask {

    //每次更新版本后会执行一次
    public static void execute(int versionCode) {
        DeviceBean device = new DeviceBean();
        device.setAndroidId(UUID.randomUUID().toString());
        device.setAndroidVersion(android.os.Build.VERSION.SDK_INT);
        device.setTableName("Version_" + versionCode);
        device.save();
    }
}
