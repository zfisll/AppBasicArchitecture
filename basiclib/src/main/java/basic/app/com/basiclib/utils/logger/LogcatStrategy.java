package basic.app.com.basiclib.utils.logger;

import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.LogStrategy;

/**
 * author : user_zf
 * date : 2018/8/29
 * desc : 解决Logger通过Logcat打印日志，格式不对齐问题
 */
public class LogcatStrategy implements LogStrategy {

    private final String DEFAULT_TAG = "PRETTY_LOGGER";
    private int last;

    @Override
    public void log(int priority, String tag, String message) {
        if (TextUtils.isEmpty(tag)) {
            tag = DEFAULT_TAG;
        }
        Log.println(priority, randomKey() + tag, message);
    }


    private String randomKey() {
        int random = (int) (10 * Math.random());
        if (random == last) {
            random = (random + 1) % 10;
        }
        last = random;
        return String.valueOf(random);
    }
}
