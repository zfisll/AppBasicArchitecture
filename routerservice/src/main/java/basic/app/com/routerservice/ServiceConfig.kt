package basic.app.com.routerservice

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : 定义组件相关的一些常量
 */
class ServiceConfig {
    companion object {
        const val KEY_USER_SERVICE = "USER_SERVICE"  //用户组件Key

        const val HOST_USER = "user"                 //用户组件对应的host

        const val COMPONENT_PATH_USER = "basic.app.com.user.applike.UserAppLike" //用户组件ApplicationLike的路径，通过反射来装载组件和卸载组件
    }
}