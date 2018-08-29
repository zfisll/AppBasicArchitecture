package basic.app.com.user.view.activity

import android.text.TextUtils
import android.view.View
import basic.app.com.basiclib.utils.EncryptUtil
import basic.app.com.basiclib.utils.JsonUtil
import basic.app.com.basiclib.utils.LogUtil
import basic.app.com.basiclib.utils.ToastUtil
import basic.app.com.basicres.basicclass.BaseActivity
import basic.app.com.user.R
import basic.app.com.user.model.bean.UserBean
import basic.app.com.user.presenter.LoginPresenter
import basic.app.com.user.view.ILoginView
import com.luojilab.router.facade.annotation.RouteNode
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.user_activity_login.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.concurrent.TimeUnit

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
            presenter.login(etName.text.toString(), EncryptUtil.getMd5Value(etPassword.text.toString()), etRegionCode.text.toString())
        }

        btnRxjava.onClick {
            startInterval()
        }
    }

    override fun onLoginSuccess(userBean: UserBean) {
        dismissWaiting()
        tvResult.text = JsonUtil.writeEntity2JSON(userBean)
        LogUtil.json(JsonUtil.writeEntity2JSON(userBean))
    }

    override fun onLoginFaild(msg: String) {
        dismissWaiting()
        ToastUtil.showToast("登录失败：" + msg)
    }

    private fun startInterval() {
        Observable.interval(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<Long> {
                    override fun onComplete() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onSubscribe(d: Disposable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNext(t: Long) {
                        LogUtil.i("zf_tag", "now num = " + t)
                    }

                    override fun onError(e: Throwable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
    }

}
