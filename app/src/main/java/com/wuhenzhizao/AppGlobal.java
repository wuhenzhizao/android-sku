package com.wuhenzhizao;

import android.app.Application;

import com.wuhenzhizao.image.GImageLoader;

import okhttp3.OkHttpClient;

/**
 * Created by liufei on 2017/11/30.
 */

public class AppGlobal extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GImageLoader.init(this, new OkHttpClient.Builder().build());
    }
}
