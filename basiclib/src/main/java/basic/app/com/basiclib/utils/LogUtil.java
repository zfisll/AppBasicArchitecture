package basic.app.com.basiclib.utils;

import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import basic.app.com.basiclib.logger.LogcatStrategy;


/**
 * author : user_zf
 * date : 2018/8/25
 * desc : 日志工具，目前采用logger日志库
 */
public class LogUtil {

    // 是否初始化完成，使用时增加判断，避免没有初始化完成就调用出现crash
    private static boolean isInited = false;

    /**
     * 配置输出到File的日志（测试、上线）
     * 使用默认的DiskLogAdapter，会把日志输出到sd://logger/logs_0.csv文件中
     * Fixme 存在一个问题，不能很好的区分日志属于哪个应用。可以重写一个DiskLogStrategy设置自己的路径
     */
    private static void configFileLog() {
        Logger.addLogAdapter(new DiskLogAdapter(CsvFormatStrategy.newBuilder().build()));
    }

    /**
     * 配置输出到console的日志（开发）
     */
    private static void configConsoleLog() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)             //不显示所在线程
                .methodCount(1)                    //调用方法层级
                .logStrategy(new LogcatStrategy()) //设置自己的strategy，解决LogcatLogStrategy格式不对齐问题
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    /**
     * 在Application中调用此方法，之后就可以在代码中随意调用i和e方法
     *
     * @param isDevelop 开发模式为true，测试和上线模式为false
     */
    public static void configLog(boolean isDevelop, Context context) {
        //check if has permission
        //todo 此处需要检查是否有写存储权限
//        boolean hasPermission = PermissionUtils.selfPermissionGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!false || isDevelop) {
            configConsoleLog();
            isInited = true;
        } else {
            configFileLog();
            isInited = true;
        }
    }

    public static void e(String message) {
        if (!isInited) {
            return;
        }
        Logger.e(message);
    }

    public static void w(String message) {
        if (!isInited) {
            return;
        }
        Logger.w(message);
    }

    public static void e(Throwable e, String message) {
        if (!isInited) {
            return;
        }
        Logger.e(e, message);
    }

    public static void i(String message) {
        Logger.i(message);
    }

    public static void i(String tag, String message) {
        if (!isInited) {
            return;
        }
        Logger.t(tag).i(message);
    }

    public static void d(Object message) {
        if (!isInited) {
            return;
        }
        if (message instanceof String) {
            Logger.t("debug").d(message);
        } else {
            Logger.t("debug").d(ObjectParser.parseObj(message));
        }
    }

    public static void json(String json) {
        if (!isInited) {
            return;
        }
        Logger.json(json);
    }
}
