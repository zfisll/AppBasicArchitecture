package basic.app.com.user.view.fragment

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import basic.app.com.basiclib.baseclass.IStateView
import basic.app.com.basicres.basicclass.BaseFragment
import basic.app.com.basicres.widget.RecyclerViewDivider
import basic.app.com.user.R
import basic.app.com.user.model.bean.NewsBean
import basic.app.com.user.presenter.NewsPresenter
import basic.app.com.user.view.INewsView
import basic.app.com.user.view.adapter.NewsAdapter
import kotlinx.android.synthetic.main.user_fragment_news.*

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : 展示用户信息页面
 */
class NewsFragment : BaseFragment<NewsPresenter>(), INewsView {

    private val mAdapter: NewsAdapter by lazy { NewsAdapter() }

    override fun getLayoutResource() = R.layout.user_fragment_news

    override fun initLayout(view: View?) {
        super.initLayout(view)
        rvNews.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        rvNews.adapter = mAdapter
        mAdapter.setOnLoadMoreListener({
            presenter.getNewsList(mAdapter.data[mAdapter.data.size - 1].artid)
        }, rvNews)
        rvNews.addItemDecoration(RecyclerViewDivider.Builder().build())
        setState(IStateView.ViewState.LOADING)
    }

    override fun isRefreshEnable() = true

    override fun onLoadData() {
        presenter.getNewsList("")
    }

    companion object {
        @JvmStatic
        fun newFragment(): NewsFragment {
            return NewsFragment()
        }
    }

    override fun onNewsSuccess(newsList: List<NewsBean>) {
        setState(IStateView.ViewState.SUCCESS)
        mAdapter.setNewData(newsList)
    }

    override fun onNewsEmpty() {
        setState(IStateView.ViewState.EMPTY)
    }

    override fun onNewsFailed() {
        setState(IStateView.ViewState.ERROR)
    }

    override fun onNewsMoreSuccess(newsList: List<NewsBean>) {
        mAdapter.addData(newsList)
        mAdapter.loadMoreComplete()
    }

    override fun onNewsMoreEmpty() {
        mAdapter.loadMoreEnd()
    }

    override fun onNewsMoreFailed() {
        mAdapter.loadMoreFail()
    }
}
