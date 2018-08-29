package basic.app.com.basiclib.utils;

import android.app.Application;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 便于在子组件中获取到应用Application对象，应用初始化的时候把Application对象设置到此工具类中
 */
public class AppUtil {

    private static Application mApp;


    public static void setApp(Application app) {
        mApp = app;
    }

    public static Application getApp() {
        return mApp;
    }

}
