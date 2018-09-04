package basic.app.com.user.view.adapter

import android.widget.ImageView
import basic.app.com.basiclib.utils.DateUtil
import basic.app.com.basiclib.utils.imageloader.ImageLoaderUtil
import basic.app.com.basiclib.widget.MyBaseQuickAdapter
import basic.app.com.user.R
import basic.app.com.user.model.bean.NewsBean
import com.chad.library.adapter.base.BaseViewHolder

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 新闻列表适配器
 */
class NewsAdapter : MyBaseQuickAdapter<NewsBean>(R.layout.user_item_important_news, listOf()) {
    override fun convert(helper: BaseViewHolder, item: NewsBean) {
        helper.setText(R.id.tvNewsTitle, item.title)
                .setText(R.id.tvNewsTime, DateUtil.timeMillis2StrInCurTimeZone(item.timestamp, DateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM))
        val ivNewsPic = helper.getView<ImageView>(R.id.ivNewsPic)
        ImageLoaderUtil.loadImage(item.logo_url, R.drawable.ic_launcher_background, ivNewsPic)
    }
}