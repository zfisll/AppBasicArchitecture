package basic.app.com.basicres.helper;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.utils.IntentUtil;
import basic.app.com.basiclib.utils.ResourceUtil;
import basic.app.com.basiclib.utils.SharedPreferenceUtilKt;
import basic.app.com.basiclib.utils.ToastUtil;
import basic.app.com.basiclib.utils.logger.LogUtil;
import basic.app.com.basicres.PreferenceKey;
import basic.app.com.basicres.R;

/**
 * author : user_zf
 * date : 2018/6/21
 * desc : App更新下载Apk的帮助类
 */
public class DownloadHelper {

    public static final String APK_DIR = "file:///" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DeviceUtil.getPackageName() + "/apk/";

    /**
     * 使用Android的原生Api DownloadManager进行下载
     * 优点：
     * 1、后台下载，用户可随意操作
     * 2、自动处理下载中断、失败等异常情况
     * 3、通过downloadId防止同一版本重复下载
     * 4、取消安装之后，下次可以不用下载，直接安装
     *
     * @param context        context
     * @param url            下载地址
     * @param newVersionName 新的版本号
     */
    @TargetApi(16)
    public static void startApkDownloadSystemApi(Context context, String url, String newVersionName) {
        //把下载任务加入队列开始下载
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (!isExistNotInstalledHighVersionApk(context, newVersionName, dm)) {
            //创建一个下载请求并设置相关参数
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedOverMetered(true); //允许使用流量下载，即Wifi、流量都可下载
            request.setAllowedOverRoaming(true); //允许漫游下载
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE); //设置通知栏展示：下载中，下载完成之后就消失
            request.setTitle(ResourceUtil.getString(R.string.update_apk_title));  //标题
            request.setDescription(ResourceUtil.getString(R.string.updateing_now)); //描述
            request.setMimeType("application/vnd.android.package-archive");  //下载内容类型：apk安装包
            request.setDestinationUri(Uri.parse(APK_DIR + getNewApkName(newVersionName)));
            Long apkDownloadId = dm.enqueue(request);
            //把apkDownloadId存在common.sp中
            SharedPreferenceUtilKt.putSharedPreferencesValue(context, PreferenceKey.APK_DOWNLOAD_ID, apkDownloadId, SharedPreferenceUtilKt.FILE_NAME_COMMON);
            LogUtil.i("zf_tag : start to download, downloadId = " + apkDownloadId);
        }
    }

    /**
     * 检测是否存在已下载的高版本安装包
     */
    public static boolean isExistNotInstalledHighVersionApk(Context context, String versionName, DownloadManager dm) {
        long downloadId = (Long) SharedPreferenceUtilKt.getSharedPreferencesValue(context,
                PreferenceKey.APK_DOWNLOAD_ID, Long.class, -1L, SharedPreferenceUtilKt.FILE_NAME_COMMON);
        if (downloadId != -1) {
            int status = DownloadHelper.getDownloadStatus(downloadId, dm);
            if (status == DownloadManager.STATUS_SUCCESSFUL) { //downloadId对应的下载任务已经下载成功
                String apkPath = getDownloadApkPath(downloadId, dm);
                if (!TextUtils.isEmpty(apkPath)) {
                    if (isHighVersionApk(context, DeviceUtil.getApkInfoByPath(context, Uri.parse(apkPath).getPath()), versionName)) { //有已下载的高版本安装包
                        IntentUtil.installApk(context, downloadId);
                        return true;
                    }
                }
            } else if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PAUSED || status == DownloadManager.STATUS_PENDING) { //有下载任务正在进行，防止重复下载
                ToastUtil.showToast(ResourceUtil.getString(R.string.updateing_now));
                return true;
            }
            dm.remove(downloadId);
        }
        return false;
    }

    /**
     * 根据downloadId获取下载状态
     *
     * @param downloadId 下载id
     */
    public static int getDownloadStatus(long downloadId, DownloadManager dm) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = dm.query(query);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                }
            } finally {
                cursor.close();
            }
        }
        return -1;
    }

    /**
     * 根据downloadId获取下载apk包的本地Uri
     *
     * @param downloadId 下载id
     */
    public static String getDownloadApkPath(long downloadId, DownloadManager dm) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = dm.query(query);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                }
            } finally {
                cursor.close();
            }
        }
        return "";
    }


    /**
     * 判断新包是否比当前安装版本的 版本号要高
     *
     * @param pi .apk文件的PackageInfo
     */
    public static boolean isHighVersionApk(Context context, PackageInfo pi, String versionName) {
        return pi != null                                                     //pi不为空
                && TextUtils.equals(context.getPackageName(), pi.packageName) // 同一个应用apk包
                && TextUtils.equals(versionName, pi.versionName)              // 是接口中指定的最新版本
                && pi.versionCode > DeviceUtil.getVersionCode(context);      // 新包比当前安装的包版本高
    }

    /**
     * 获取新版本apk的名称
     *
     * @param versionNum 版本号 3.8.3
     * @return basic_3_8_3.apk
     */
    public static String getNewApkName(String versionNum) {
        return "basic_" + versionNum.replace(".", "_") + ".apk";
    }

    public interface OnDownloadFailedListener {
        void onDownloadFailed();
    }
}
