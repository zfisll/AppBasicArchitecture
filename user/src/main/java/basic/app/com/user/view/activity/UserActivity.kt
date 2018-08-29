package basic.app.com.user.view.activity

import android.app.Activity
import android.os.Bundle
import basic.app.com.routerservice.bean.User
import basic.app.com.user.R
import basic.app.com.user.view.fragment.UserFragment
import com.luojilab.component.componentlib.service.AutowiredService
import com.luojilab.router.facade.annotation.Autowired
import com.luojilab.router.facade.annotation.RouteNode

@RouteNode(path = "/info", desc = "用户信息展示页面")
class UserActivity : Activity() {

    @Autowired
    @JvmField
    var mUser: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //解析Autowired对应的属性内容
        AutowiredService.Factory.getSingletonImpl().autowire(this)

        setContentView(R.layout.user_activity_user)
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.flContainer, UserFragment.newFragment(mUser.userName, mUser.userAge, mUser.userHobby))
        ft.commitAllowingStateLoss()
    }

}
