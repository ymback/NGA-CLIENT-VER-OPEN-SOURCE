package com.justwen.androidnga.cloud.base;

import android.content.Context;

public interface ICloudDataBase {

    void init(Context context);

    void uploadVersionInfo(VersionBean versionBean);
}
