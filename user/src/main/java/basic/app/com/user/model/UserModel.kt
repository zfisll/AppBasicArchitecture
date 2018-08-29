package basic.app.com.user.model

import basic.app.com.user.helper.net.UserRetrofit
import basic.app.com.user.model.bean.UserBean
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * 用户Model层
 */
class UserModel {

    /**
     * 用户登录
     */
    fun login(userName: String, password: String, reginCode: String): Observable<UserBean> {
        return UserRetrofit.SINGLETON.service.login(userName, password, reginCode)
                .subscribeOn(Schedulers.io())
                .map {
                    if (it.body != null) it.body else UserBean()
                }
    }
}