package basic.app.com.basiclib.helper.net;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 网络强求相关的公共配置
 */
public class Config {
    /**
     * 网络请求超时时间,单位毫秒
     */
    public static final int HTTP_TIMEOUT = 10 * 1000;

    /**
     * 全局的 API_VERSION,所有服务器统一使用，用来做接口兼容
     */
    public static final String API_VERSION = "1";

    /**
     * 手机操作系统，接口用来区分客户端为Android、iOS或Web
     */
    public static final String PLATFORM_OS = "Android";
}
