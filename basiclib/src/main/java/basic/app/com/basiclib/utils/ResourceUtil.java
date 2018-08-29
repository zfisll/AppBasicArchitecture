package basic.app.com.basiclib.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 获取资源的工具
 */
public class ResourceUtil {
    /**
     * 根据stringid获取String
     */
    public static String getString(@StringRes int stringId) {
        return AppUtil.getApp().getString(stringId);
    }

    /**
     * 根据stringid获取format的String
     */
    public static String getString(int stringId, Object... formatArgs) {
        return AppUtil.getApp().getString(stringId, formatArgs);
    }

    /**
     * 根据drawableid获取Drawable资源
     */
    public static Drawable getDrawable(@DrawableRes int drawableId) {
        return AppUtil.getApp().getResources().getDrawable(drawableId);
    }

    /**
     * 根据colorid获取颜色
     */
    public static int getColor(@ColorRes int colorId) {
        return AppUtil.getApp().getResources().getColor(colorId);
    }
}
