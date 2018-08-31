package basic.app.com.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.View
import basic.app.com.R
import basic.app.com.basiclib.baseclass.BasePresenter
import basic.app.com.basiclib.widget.SimpleFragmentAdapter
import basic.app.com.basicres.basicclass.BaseActivity
import basic.app.com.routerservice.ServiceConfig
import basic.app.com.routerservice.service.IUserService
import com.luojilab.component.componentlib.router.Router
import com.luojilab.router.facade.annotation.RouteNode
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

@RouteNode(path = "/main", desc = "应用主页")
class MainActivity : BaseActivity<BasePresenter<*>>() {

    override fun getLayoutResource() = R.layout.activity_main

    override fun initLayout(view: View?) {
        super.initLayout(view)
        val userService = Router.getInstance().getService(ServiceConfig.KEY_USER_SERVICE) as IUserService
        val fragmentAdapter = SimpleFragmentAdapter(supportFragmentManager, listOf(
                userService.getUserFragment(),
                userService.getUserFragment(),
                userService.getUserFragment(),
                userService.getUserFragment(),
                userService.getUserFragment()

        ))
        vpMain.adapter = fragmentAdapter
        vpMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                /* no-op */
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                /* no-op */
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> rbStock.isChecked = true
                    1 -> rbFinancial.isChecked = true
                    2 -> rbTrade.isChecked = true
                    3 -> rbInfo.isChecked = true
                    4 -> rbMine.isChecked = true
                }
            }
        })

        rgTab.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbStock -> vpMain.currentItem = 0
                R.id.rbFinancial -> vpMain.currentItem = 1
                R.id.rbTrade -> vpMain.currentItem = 2
                R.id.rbInfo -> vpMain.currentItem = 3
                R.id.rbMine -> vpMain.currentItem = 4
            }
        }

        vpMain.currentItem = 0
    }

    /**
     * 通过RxPermissions请求危险权限
     */
    private fun reqeustPermissions() {
        //请求权限
        val rxPermissions = RxPermissions(this)
        rxPermissions.requestEach(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE)
                .subscribe {
                    when {
                        it.granted -> { //授权成功
                            basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", it.name + " granted success")
                        }
                        it.shouldShowRequestPermissionRationale -> { //授权失败，可再次弹框请求
                            basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", it.name + " denied permission with ask again")
                        }
                        else -> { //授权失败，不能再次请求，只能到系统设置页面去修改权限
                            basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", it.name + " denied permission without ask again")
                            //权限被拒绝后判断如果不显示提示，说明用户点击了永不提示，所以要给出对话框引导用户去设置打开权限
                            AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.lib_need_permission, it.name))
                                    .setPositiveButton(R.string.lib_go_setting) { _, _ ->
                                        // Create app settings intent
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        val uri = Uri.fromParts("package", packageName, null)
                                        intent.data = uri
                                        startActivity(intent)
                                    }
                                    .setNegativeButton(getString(R.string.lib_cancel), null)
                                    .show()
                        }
                    }
                }
    }

    /**
     * 用RxLifecycle管理Rxjava的生命周期
     */
    private fun manageRxLifecycle() {
        Observable.interval(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY)) //当Activity走onDestroy方法之前，会结束掉该Observable
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {
                        basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", "onSubscribe")
                    }

                    override fun onNext(t: Long) {
                        basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", "onNext now num = " + t)
                    }

                    override fun onError(e: Throwable) {
                        basic.app.com.basiclib.utils.logger.LogUtil.i("zf_tag", "onError, msg = " + e.message)
                    }
                })
    }
}
