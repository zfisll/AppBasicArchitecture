package basic.app.com.user.runalone.activity

import android.view.View
import basic.app.com.basiclib.baseclass.BasePresenter
import basic.app.com.basicres.basicclass.BaseActivity
import basic.app.com.user.R
import basic.app.com.user.view.fragment.UserFragment

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : 登录对应View
 */
class UserInfoActivity : BaseActivity<BasePresenter<*>>() {

    override fun getLayoutResource() = R.layout.user_activity_info

    override fun initLayout(view: View?) {
        super.initLayout(view)
        replaceFragment(R.id.flContainer, UserFragment.newFragment())
    }


}
