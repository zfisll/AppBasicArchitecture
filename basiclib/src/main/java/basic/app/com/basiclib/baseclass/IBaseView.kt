package basic.app.com.basiclib.baseclass

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : MVP中的View基类
 */
interface IBaseView {
    /**
     * 显示等待框 canDismissByUser为是否可点击取消，类似交易界面基本是不允许中断操作的
     */
    fun showWaiting(msg: CharSequence, canDismissByUser: Boolean)

    /**
     * 取消等待框
     */
    fun dismissWaiting()

    /**
     * 重置下拉刷新状态
     */
    fun resetRefreshStatus()
}