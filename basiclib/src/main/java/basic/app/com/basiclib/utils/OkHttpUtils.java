package basic.app.com.basiclib.utils;


import basic.app.com.basiclib.BuildConfig;
import basic.app.com.basiclib.helper.net.HttpPerformanceInterceptor;
import okhttp3.Interceptor;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 统一对okhttp会使用到的公共设置做处理，后续可能会将OkHttpClient的相关配置也加进来
 */
public final class OkHttpUtils {

    public static Interceptor getHttpInterceptor(final String tag) {
        HttpPerformanceInterceptor loggingInterceptor = new HttpPerformanceInterceptor(new HttpPerformanceInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (BuildConfig.DEBUG) { //Debug模式额外把日志打印到Logcat
                    LogUtil.i(tag, message);
                }
            }
        });
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpPerformanceInterceptor.Level.BODY : HttpPerformanceInterceptor.Level.HEADERS);
        return loggingInterceptor;
    }

}

