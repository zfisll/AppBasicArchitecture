package basic.app.com.basiclib.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;


/**
 * author : user_zf
 * date : 2018/9/6
 * desc : 屏幕工具
 */
public class ScreenUtil {
    private static ScreenBroadcastReceiver mScreenReceiver;
    private static ScreenStateListener mScreenStateListener;


    public static void startObserver(Context context, ScreenStateListener listener) {
        mScreenStateListener = listener;
        registerListener(context);
        getScreenState(context);
    }

    public static void shutdownObserver(Context context) {
        unregisterListener(context);
    }

    /**
     * 获取screen状态
     */
    private static void getScreenState(Context context) {
        PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (manager == null) {
            return;
        }
        if (manager.isScreenOn()) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }

    private static void registerListener(Context context) {
        mScreenReceiver = new ScreenBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(mScreenReceiver, filter);
    }

    private static void unregisterListener(Context context) {
        context.unregisterReceiver(mScreenReceiver);
    }


    private static class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                if (mScreenStateListener != null) {
                    mScreenStateListener.onScreenOn();
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                if (mScreenStateListener != null) {
                    mScreenStateListener.onScreenOff();
                }
            }
        }
    }

    public interface ScreenStateListener {// 返回给调用者屏幕状态信息

        void onScreenOn();

        void onScreenOff();
    }
}
