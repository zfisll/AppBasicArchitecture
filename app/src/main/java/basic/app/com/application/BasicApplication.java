package basic.app.com.application;

import android.app.Activity;
import android.app.Application;
import android.support.multidex.MultiDex;

import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.util.HashMap;
import java.util.Map;

import basic.app.com.BuildConfig;
import basic.app.com.basiclib.baseclass.SimpleActivityLifecycleCallbacks;
import basic.app.com.basiclib.utils.AppUtil;
import basic.app.com.basiclib.utils.ScreenUtil;
import basic.app.com.basiclib.utils.logger.LogUtil;

/**
 * author : user_zf
 * date : 2018/8/22
 * desc : App的自定义Application
 */
public class BasicApplication extends Application implements ScreenUtil.ScreenStateListener {

    private Activity currentActivity;    //记录当前Activity
    private boolean isScreenOff = false; //屏幕是否锁屏
    private boolean isAppInBackground = true; //应用是否在后台

    private static Map<String, Object> cache = new HashMap<>(); //全局的内存缓存，可以保存此次运行中的一些数据

    @Override
    public void onCreate() {
        super.onCreate();
        //注册UiRouter
        UIRouter.getInstance().registerUI("app");
        //保存全局的Application对象
        AppUtil.setApp(this);
        //配置日志工具
        LogUtil.configLog(BuildConfig.DEBUG, getApplicationContext());
        //支持分包
        MultiDex.install(getApplicationContext());

        //监听Activity的生命周期方法，当调用onResume时置为当前Activity，调用onDestroy时把当前Activity置空
        registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                //不在create方法中操作用的原因：A->B,B->A这个过程中，只调用A的onResume方法，而不会调用onCreate方法
                super.onActivityResumed(activity);
                currentActivity = activity;
                //如果resume前是处于后台状态，那就执行resumeFromBackground操作，同时把isAppInBackground置为false
                if (isAppInBackground) {
                    isAppInBackground = false;
                    resumeFromBackground();
                }
                //此处可以统计Activity的启动
            }

            @Override
            public void onActivityPaused(Activity activity) {
                super.onActivityPaused(activity);
                //此处可以统计Activity的结束
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //不在onStop方法中操作的原因：从A切到桌面，会调用onStop方法，此时如果把currentActivity置空，某些操作就不能执行了，比如检查更新弹出更新对话框
                super.onActivityDestroyed(activity);
                if (activity == currentActivity) {
                    currentActivity = null;
                }
            }
        });
    }

    @Override
    public void onLowMemory() {
        // 可以执行一些自定义的内存释放操作
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        // 可以执行一些数据保存工作
        super.onTerminate();
    }

    /**
     * 获取缓存
     */
    public static Map<String, Object> getCache() {
        if (cache == null) {
            cache = new HashMap<>();
        }
        return cache;
    }

    /**
     * 判断应用是否处于后台状态，有两种情况：
     * 第一种是按Home键后触发的onTrimMemory
     * 第二种是锁屏（因为锁屏是不触发onTrimMemory,所以通过监听Screen状态来处理）
     */
    public boolean isAppInBackground() {
        return isAppInBackground || isScreenOff;
    }

    @Override
    public void onScreenOn() {
        LogUtil.i("app lock screen!!");
        isScreenOff = false;
        //如果亮屏时应用不是在后台，那直接恢复APP相关活动
        if (!isAppInBackground) {
            resumeFromBackground();
        }
    }

    @Override
    public void onScreenOff() {
        LogUtil.i("app light up screen!!");
        isScreenOff = true;
        //如果锁屏时应用在前台，一样走进入后台的动作，暂停APP的相关活动
        if (!isAppInBackground) {
            goToBackground();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isAppInBackground = true;
            goToBackground();
        }
    }

    /**
     * 从后台切到前台需要做的事情放这里
     */
    public void resumeFromBackground() {
        LogUtil.i("app resume from background!!");
    }

    /**
     * 应用切换到后台需要做的事情放在这里
     */
    public void goToBackground() {
        LogUtil.i("app go to background!!");
    }
}
