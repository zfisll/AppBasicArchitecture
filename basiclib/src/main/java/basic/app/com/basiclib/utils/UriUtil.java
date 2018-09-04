package basic.app.com.basiclib.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * author : user_zf
 * date : 2018/9/4
 * desc : Uri工具，兼容7.0版本的文件系统权限
 */
public class UriUtil {
    /**
     * 获取文件uri
     */
    public static Uri getUri(Context context, File file, String applicationId) {
        if (context == null || file == null) {
            return Uri.EMPTY;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, applicationId + ".fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
