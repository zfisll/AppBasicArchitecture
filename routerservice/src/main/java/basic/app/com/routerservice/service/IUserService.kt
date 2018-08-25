package basic.app.com.routerservice.service

import android.app.Fragment

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : 用户组件提供的服务
 */
interface IUserService {
    /**
     * 获取用户信息Fragment
     */
    fun getUserFragment(userName: String, age: Int, hobby: String): Fragment
}