package basic.app.com.user.view.activity

import android.text.TextUtils
import android.view.View
import basic.app.com.basiclib.utils.*
import basic.app.com.basiclib.utils.logger.LogUtil
import basic.app.com.basicres.basicclass.BaseActivity
import basic.app.com.user.R
import basic.app.com.user.model.bean.UserBean
import basic.app.com.user.presenter.LoginPresenter
import basic.app.com.user.view.ILoginView
import com.luojilab.router.facade.annotation.RouteNode
import kotlinx.android.synthetic.main.user_activity_login.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : 登录对应View
 */
@RouteNode(path = "/login", desc = "登录页面")
class LoginActivity : BaseActivity<LoginPresenter>(), ILoginView {

    override fun getLayoutResource() = R.layout.user_activity_login

    override fun initLayout(view: View?) {
        super.initLayout(view)
        btnLogin.onClick {
            if (TextUtils.isEmpty(etName.text)) {
                ToastUtil.showToast("用户名不能为空")
                return@onClick
            }
            if (TextUtils.isEmpty(etPassword.text)) {
                ToastUtil.showToast("密码不能为空")
                return@onClick
            }
            if (TextUtils.isEmpty(etRegionCode.text)) {
                ToastUtil.showToast("区号不能为空")
                return@onClick
            }
            showWaiting("正在登录...", false)
            presenter.login(etName.text.toString(), EncryptUtil.getMd5Value(etPassword.text.toString()), etRegionCode.text.toString())
        }

    }

    override fun onLoginSuccess(userBean: UserBean) {
        dismissWaiting()
        LogUtil.json(JsonUtil.writeEntity2JSON(userBean))
        //保存用户信息到SP
        putSharedPreferencesValue("session", userBean.session, FILE_NAME_USER_INFO)
        putSharedPreferencesValue("userName", userBean.nick_name, FILE_NAME_USER_INFO)
        putSharedPreferencesValue("userId", userBean.user_id, FILE_NAME_USER_INFO)
        putSharedPreferencesValue("phone", userBean.phone, FILE_NAME_USER_INFO)
        putSharedPreferencesValue("email", userBean.email, FILE_NAME_USER_INFO)
        putSharedPreferencesValue("avatar", userBean.avatar, FILE_NAME_USER_INFO)
        //结束页面
        finish()
    }

    override fun onLoginFaild(msg: String) {
        dismissWaiting()
        ToastUtil.showToast("登录失败：" + msg)
    }

}
