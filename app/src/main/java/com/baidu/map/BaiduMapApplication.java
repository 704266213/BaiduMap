package com.baidu.map;

import android.app.Application;

import com.baidu.map.task.LocationTask;
import com.baidu.map.utils.SHAUtil;
import com.baidu.mapapi.SDKInitializer;

/**
 * 类描述：
 * 创建人：alan
 * 创建时间：2017-07-07 09:32
 * 修改备注：
 */

public class BaiduMapApplication extends Application {


    public LocationTask locationTask;

    @Override
    public void onCreate() {
        super.onCreate();

        locationTask = new LocationTask(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());

        SHAUtil.getSha(this);
    }
}
