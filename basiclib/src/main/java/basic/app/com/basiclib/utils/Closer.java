package basic.app.com.basiclib.utils;

import java.io.Closeable;

import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/8/30
 * desc : 关闭工具，可以用来close各种对象
 */
public class Closer {

    /**
     * 关闭stream对象
     *
     * @param stream 可关闭的对象
     */
    public static void close(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }

    }

}