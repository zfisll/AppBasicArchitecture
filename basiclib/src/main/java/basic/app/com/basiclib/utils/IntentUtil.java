package basic.app.com.basiclib.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;

/**
 * author : user_zf
 * date : 2018/9/4
 * desc : Intent工具
 */
public class IntentUtil {

    /**
     * 打开文件
     */
    public static void openFile(Context context, File file, String applicationId) {
        if (context != null && file != null) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            Uri contentUri = UriUtil.getUri(context, file, applicationId);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    /**
     * 安装下载的apk
     *
     * @param downloadId apk对应的下载id
     */
    public static void installApk(Context context, long downloadId) {
        if (downloadId == -1) {
            return;
        }
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri apkUri = dm.getUriForDownloadedFile(downloadId);
        if (apkUri != null) {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(install);
        }
    }

    /**
     * 打开浏览器
     */
    public static void openBrowser(Context context, String url) {
        if (context != null && !TextUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
