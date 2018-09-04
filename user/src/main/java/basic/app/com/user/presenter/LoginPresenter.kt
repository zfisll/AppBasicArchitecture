package basic.app.com.user.presenter

import basic.app.com.basiclib.baseclass.BasePresenter
import basic.app.com.user.model.UserModel
import basic.app.com.user.model.bean.UserBean
import basic.app.com.user.view.ILoginView
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : 登录对应Presenter
 */
class LoginPresenter : BasePresenter<ILoginView>() {

    private val mModel: UserModel by lazy { UserModel() }

    /**
     * 登录操作
     */
    fun login(name: String, password: String, regionCode: String) {
        mModel.login(name, password, regionCode)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activityProvider.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(object : Observer<UserBean> {
                    override fun onComplete() {
                        basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", "login complete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", "login onSubscribe")
                    }

                    override fun onNext(t: UserBean) {
                        view.onLoginSuccess(t)
                    }

                    override fun onError(e: Throwable) {
                        view.onLoginFaild(e.message ?: "")
                    }
                })

    }
}