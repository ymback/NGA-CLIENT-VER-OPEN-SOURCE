package gov.anzong.androidnga.statistics.bmob;

import android.content.Context;

import cn.bmob.v3.Bmob;

/**
 * Created by Justwen on 2018/8/22.
 */
public class BmobWrapper {

    public BmobWrapper(Context context) {
        Bmob.initialize(context.getApplicationContext(), "a552fafd1056fbf0ce80b7e663f222e6");
    }

    public void startDeviceStatisticsTask(int versionCode) {
        DeviceStatisticsTask.execute(versionCode);
    }
}
