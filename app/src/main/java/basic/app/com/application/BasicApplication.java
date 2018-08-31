package basic.app.com.application;

import android.app.Application;

import com.luojilab.component.componentlib.router.ui.UIRouter;

import basic.app.com.BuildConfig;
import basic.app.com.basiclib.utils.AppUtil;
import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : App的自定义Application
 */
public class BasicApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //注册UiRouter
        UIRouter.getInstance().registerUI("app");
        AppUtil.setApp(this);
        LogUtil.configLog(BuildConfig.DEBUG, getApplicationContext());
    }
}
