package basic.app.com.user.applike;


import com.luojilab.component.componentlib.applicationlike.IApplicationLike;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import basic.app.com.routerservice.ServiceConfig;
import basic.app.com.user.serviceimpl.UserServiceImpl;

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : 用来管理User组件的加载和卸载
 */
public class UserAppLike implements IApplicationLike {
    @Override
    public void onCreate() {
        Router.getInstance().addService(ServiceConfig.KEY_USER_SERVICE, new UserServiceImpl());
        UIRouter.getInstance().registerUI(ServiceConfig.HOST_USER);
    }

    @Override
    public void onStop() {
        Router.getInstance().removeService(ServiceConfig.KEY_USER_SERVICE);
        UIRouter.getInstance().unregisterUI(ServiceConfig.HOST_USER);
    }
}
