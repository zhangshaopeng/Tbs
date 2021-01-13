package com.shao.app.demo;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by ljh
 * on 2016/12/22.
 */
public class App extends Application {
    private static App application;
    public static final String APPNAME = "DyneAssistant";


    public static App getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        //增加这句话
        QbSdk.initX5Environment(this, null);
        ExceptionHandler.getInstance().initConfig(this);
        FileUtils.init(this);
    }


}