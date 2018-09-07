package basic.app.com.user.runalone.application;

import android.app.Application;

import basic.app.com.basiclib.utils.AppUtil;

public class UserApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //把Application保存在AppUtil中，全局可用
        AppUtil.setApp(this);
    }

}