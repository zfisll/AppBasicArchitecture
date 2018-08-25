package basic.app.com.basiclib.utils;

import java.util.Collection;

/**
 * author : user_zf
 * date : 2018/8/25
 * desc : 集合工具类的常用方法
 */

public class CollectionUtil {

    public static int size(Collection<?> data) {
        return data != null ? data.size() : 0;
    }

    public static boolean isEmpty(Collection<?> data) {
        return data == null || data.isEmpty();
    }

}
