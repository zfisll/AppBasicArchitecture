package basic.app.com.basiclib.baseclass;

import android.support.annotation.ColorRes;
import android.view.View;

/**
 * author : user_zf
 * date : 2018/8/24
 * desc : 抽象界面的基础配置
 */
public interface IUIBaseConfig {
    /**
     * 界面是具备刷新功能
     */
    boolean isRefreshEnable();

    /**
     * 获取根布局的背景色，默认透明
     */
    @ColorRes
    int getRootViewBackgroundColorResId();

    /**
     * 界面是否支持换肤
     */
    boolean supportChangeSkin();

    /**
     * 获取ui的布局文件
     */
    int getLayoutResource();

    /**
     * 初始化ui布局
     */
    void initLayout(View view);
}
