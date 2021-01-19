package com.shao.tbs;

import android.app.Application;

import com.open.file.OpenFileManager;

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
        OpenFileManager.initSDK(this, true,R.color.colorAccent);
        FileUtils.init(this);
    }


}