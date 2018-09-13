package basic.app.com.user.runalone.application;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import basic.app.com.basiclib.utils.AppUtil;
import basic.app.com.basiclib.utils.logger.LogUtil;
import basic.app.com.routerservice.ServiceConfig;
import basic.app.com.user.BuildConfig;
import basic.app.com.user.serviceimpl.UserServiceImpl;

public class UserApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //注册UiRouter
        Router.getInstance().addService(ServiceConfig.KEY_USER_SERVICE, new UserServiceImpl());
        UIRouter.getInstance().registerUI(ServiceConfig.HOST_USER);
        //保存全局的Application对象
        AppUtil.setApp(this);
        //配置日志工具
        LogUtil.configLog(BuildConfig.DEBUG, getApplicationContext());
        //支持分包
        MultiDex.install(getApplicationContext());
    }

}