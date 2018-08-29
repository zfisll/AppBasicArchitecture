package basic.app.com.user.view

import basic.app.com.basiclib.baseclass.IBaseView
import basic.app.com.user.model.bean.UserBean

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : 登录对应的View
 */
interface ILoginView: IBaseView {
    fun onLoginSuccess(userBean: UserBean)  //登录成功回调方法
    fun onLoginFaild(msg: String)           //登录失败回调
}