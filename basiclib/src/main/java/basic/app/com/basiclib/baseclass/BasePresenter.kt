package com.bluestone.common.baseclass

import basic.app.com.basiclib.baseclass.IBaseView
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : MVP中Presenter基类
 */
open class BasePresenter<T : IBaseView> {

    //默认情况下，view会变成private并且生成对应的getter和setter方法，但是加上JvmField，view就拥有底层的访问权限，即public，对外可见
    @JvmField
    var view: T? = null

    open fun start() {}

    private var mCompositeSubscription: CompositeSubscription? = null //管理Subscription，每次unsbuscribe之后置空，下次用的时候再new一个

    fun addSubscription(subscription: Subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = CompositeSubscription()
        }
        mCompositeSubscription?.add(subscription)
    }

    open fun cancelSubscription() {
        view = null
        mCompositeSubscription?.unsubscribe()
        mCompositeSubscription = null
    }
}