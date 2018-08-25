package basic.app.com.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import basic.app.com.R
import basic.app.com.routerservice.ServiceConfig
import basic.app.com.routerservice.bean.User
import basic.app.com.routerservice.service.IUserService
import com.luojilab.component.componentlib.router.Router
import com.luojilab.component.componentlib.router.ui.UIRouter
import com.luojilab.component.componentlib.service.JsonService
import com.luojilab.router.facade.annotation.RouteNode
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@RouteNode(path="/main", desc = "应用主页")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLoad.onClick {
            //通过反射的方式注册组件
//            Router.registerComponent("basic.app.com.user.applike.UserAppLike")
            val ft = fragmentManager.beginTransaction()
            val userService = Router.getInstance().getService(ServiceConfig.KEY_USER_SERVICE) as IUserService
            ft.replace(R.id.flContainer, userService.getUserFragment("user_zf", 27, "编程打游戏"))
            ft.commitAllowingStateLoss()
        }
        btnJump.onClick {
            val user = User()
            user.userAge = 27
            user.userName = "user_sll"
            user.userHobby = "吃饭睡觉看电视"
            val bundle = Bundle()
            bundle.putString("mUser", JsonService.Factory.getSingletonImpl().toJsonString(user))
            UIRouter.getInstance().openUri(this@MainActivity, "DDComp://user/info", bundle)
        }
    }
}
