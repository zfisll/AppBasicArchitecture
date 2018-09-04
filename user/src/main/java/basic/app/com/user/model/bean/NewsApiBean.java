package basic.app.com.user.model.bean;

import java.util.List;

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 重要新闻列表bean
 */
public class NewsApiBean {
    private List<NewsBean> list;

    public List<NewsBean> getList() {
        return list;
    }

    public void setList(List<NewsBean> list) {
        this.list = list;
    }
}
