package basic.app.com.user.presenter

import android.text.TextUtils
import basic.app.com.basiclib.baseclass.BasePresenter
import basic.app.com.basiclib.utils.CollectionUtil
import basic.app.com.user.model.UserModel
import basic.app.com.user.model.bean.NewsBean
import basic.app.com.user.view.INewsView
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 重要新闻Presenter层
 */
class NewsPresenter : BasePresenter<INewsView>() {
    private val mModel: UserModel by lazy { UserModel() }

    /**
     * 获取新闻列表
     */
    fun getNewsList(lastNewsId: String) {
        mModel.getImportantNews(20, lastNewsId)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(fragmentProvider.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(object : Observer<List<NewsBean>> {
                    override fun onComplete() {
                        view.resetRefreshStatus()
                    }

                    override fun onSubscribe(d: Disposable) {
                        /* no-op */
                    }

                    override fun onNext(t: List<NewsBean>) {
                        view.resetRefreshStatus()
                        if (CollectionUtil.isEmpty(t)) {
                            if (TextUtils.isEmpty(lastNewsId)) {
                                view.onNewsEmpty()
                            } else {
                                view.onNewsMoreEmpty()
                            }
                        } else {
                            if (TextUtils.isEmpty(lastNewsId)) {
                                view.onNewsSuccess(t)
                            } else {
                                view.onNewsMoreSuccess(t)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.resetRefreshStatus()
                        if (TextUtils.isEmpty(lastNewsId)) {
                            view.onNewsFailed()
                        } else {
                            view.onNewsMoreFailed()
                        }
                    }
                })
    }
}