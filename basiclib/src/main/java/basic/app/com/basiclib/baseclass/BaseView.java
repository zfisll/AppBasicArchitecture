package basic.app.com.basiclib.baseclass;

/**
 * Created by user_zf on 16/7/19.
 */
public interface BaseView <T> {
    // TODO: 2018/8/24 可以放在Activity和Fragment基类
    void setPresenter(T presenter);
}
