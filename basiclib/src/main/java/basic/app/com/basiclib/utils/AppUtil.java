package basic.app.com.basiclib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import java.util.List;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 便于在子组件中获取到应用Application对象，应用初始化的时候把Application对象设置到此工具类中
 */
public class AppUtil {

    private static Application mApp;


    public static void setApp(Application app) {
        mApp = app;
    }

    public static Application getApp() {
        return mApp;
    }

    public static boolean isContextInvalid(Context context) {
        return context == null
                || scanForActivity(context)==null
                || scanForActivity(context).isFinishing();
    }

    public static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity)cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());

        return null;
    }

    /**
     * 获取进程名称
     */
    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return "";
    }

}
