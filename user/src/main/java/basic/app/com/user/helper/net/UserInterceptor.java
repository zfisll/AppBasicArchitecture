package basic.app.com.user.helper.net;

import java.util.HashMap;
import java.util.Map;

import basic.app.com.basiclib.helper.net.Config;
import basic.app.com.basiclib.helper.net.PublicParamsInterceptor;
import basic.app.com.user.helper.UserHelper;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 用户组建相关接口添加公共参数和Header
 */
public class UserInterceptor extends PublicParamsInterceptor {

    Map<String, String> headersMap;  //头部参数 map
    Map<String, String> paramsMap;  //头部参数 map

    /**
     * 添加公共参数,有公共参数时在此添加
     */
    @Override
    public Map<String, String> getPublicParams() {
        if (paramsMap == null) {
            paramsMap = new HashMap<>();
        }
        return paramsMap;
    }

    /**
     * 添加公共Header参数
     */
    @Override
    public Map<String, String> getPublicHeaders() {
        if (headersMap == null) {
            headersMap = new HashMap<>();
        }
        headersMap.put("QUARTZ-SESSION", UserHelper.getSession());
        headersMap.put("API-VERSION", Config.API_VERSION);
        headersMap.put("DEVICE-TYPE", Config.PLATFORM_OS);
        return headersMap;
    }

}
