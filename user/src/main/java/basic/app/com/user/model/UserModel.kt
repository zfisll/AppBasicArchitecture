package basic.app.com.user.model

import basic.app.com.basiclib.baseclass.BaseModel
import basic.app.com.basiclib.utils.CollectionUtil
import basic.app.com.user.helper.net.UserRetrofit
import basic.app.com.user.model.bean.NewsBean
import basic.app.com.user.model.bean.UserBean
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 用户Model层
 */
class UserModel : BaseModel() {

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

    /**
     * 获取重要新闻列表
     */
    fun getImportantNews(count: Int, lastNewsId: String): Observable<List<NewsBean>> {
        return UserRetrofit.SINGLETON.service.getImportantNews(count, lastNewsId)
                .subscribeOn(Schedulers.io())
                .map {
                    if (it.body != null && !CollectionUtil.isEmpty(it.body.list)) it.body.list else arrayListOf()
                }
    }
}