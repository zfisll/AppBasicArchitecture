package basic.app.com.user.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import basic.app.com.basiclib.baseclass.BasePresenter
import basic.app.com.basiclib.utils.FILE_NAME_USER_INFO
import basic.app.com.basiclib.utils.getSharedPreferencesValue
import basic.app.com.basiclib.utils.imageloader.ImageLoaderUtil
import basic.app.com.basicres.basicclass.BaseFragment
import basic.app.com.user.R
import com.luojilab.component.componentlib.router.ui.UIRouter
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_fragment_user.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.concurrent.TimeUnit

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : 展示用户信息页面
 */
class UserFragment : BaseFragment<BasePresenter<*>>() {

    override fun getLayoutResource() = R.layout.user_fragment_user

    override fun initLayout(view: View?) {
        super.initLayout(view)

    }

    override fun onVisible() {
        super.onVisible()
        //每次可见时刷新页面状态
        onLoadData()
    }

    private fun checkPageStatus() {
        val session = getSharedPreferencesValue("session", String::class.java, "", FILE_NAME_USER_INFO) as String
        if (TextUtils.isEmpty(session)) {
            rlInfo.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            btnLogin.onClick {
                UIRouter.getInstance().openUri(activity, "DDComp://user/login", Bundle())
            }
        } else {
            btnLogin.visibility = View.GONE
            rlInfo.visibility = View.VISIBLE
            //展示用户信息
            val userName = getSharedPreferencesValue("userName", String::class.java, "", FILE_NAME_USER_INFO) as String
            val userId = getSharedPreferencesValue("userId", String::class.java, "", FILE_NAME_USER_INFO) as String
            val phone = getSharedPreferencesValue("phone", String::class.java, "", FILE_NAME_USER_INFO) as String
            val email = getSharedPreferencesValue("email", String::class.java, "", FILE_NAME_USER_INFO) as String
            val avatar = getSharedPreferencesValue("avatar", String::class.java, "", FILE_NAME_USER_INFO) as String
            tvName.text = userName
            tvId.text = userId
            tvPhone.text = phone
            tvEmail.text = email
            ImageLoaderUtil.loadCircleImage(avatar, R.drawable.ic_launcher_background, ivAvatar)
        }
        //1秒钟之后还原刷新状态
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe {
                    resetRefreshStatus()
                }
    }

    override fun isRefreshEnable() = true

    override fun onLoadData() {
        checkPageStatus()
    }

    companion object {
        @JvmStatic
        fun newFragment(): UserFragment {
            return UserFragment()
        }
    }
}
