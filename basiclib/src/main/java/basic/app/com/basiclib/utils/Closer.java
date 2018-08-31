package basic.app.com.basiclib.utils;

import android.database.Cursor;

import java.io.Closeable;

/**
 * author : user_zf
 * date : 2018/8/30
 * desc : 关闭工具，可以用来close各种对象
 */
public class Closer {

    public static void close(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (Exception e) {
        }

    }

    public static void close(Cursor stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (Exception e) {
        }

    }

}