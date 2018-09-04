package basic.app.com.user.view

import basic.app.com.basiclib.baseclass.IBaseView
import basic.app.com.user.model.bean.NewsBean

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 重要新闻View层
 */
interface INewsView: IBaseView {
    fun onNewsSuccess(newsList: List<NewsBean>)    //第一次获取列表成功
    fun onNewsEmpty()                                   //第一次获取列表为空
    fun onNewsFailed()                                  //第一次获取列表失败
    fun onNewsMoreSuccess(newsList: List<NewsBean>)//加载更多成功
    fun onNewsMoreEmpty()                               //加载更多为空
    fun onNewsMoreFailed()                              //加载更多失败
}