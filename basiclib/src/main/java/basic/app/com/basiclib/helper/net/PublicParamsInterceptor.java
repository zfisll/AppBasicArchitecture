package basic.app.com.basiclib.helper.net;

import android.text.TextUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import basic.app.com.basiclib.utils.logger.LogUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 公共参数拦截器，可以添加Header和RequestBody的公共参数
 */
public abstract class PublicParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        Map<String, String> paramsMap = getPublicParams();    //公共参数

        if (request.method().equals("POST") && request.body() != null && request.body().contentType().subtype().equals("x-www-form-urlencoded")) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (paramsMap != null && paramsMap.size() > 0) {
                for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
//                    formBodyBuilder.add( entry.getKey(), entry.getValue());
                    if (entry.getKey() != null && entry.getValue() != null) {
                        formBodyBuilder.addEncoded(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8"));
                    }
                }
            }
            RequestBody formBody = formBodyBuilder.build();
            String postBodyString = bodyToString(request.body());
            postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
            requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));

        } else {
            if (paramsMap != null && paramsMap.size() > 0) {
                HttpUrl.Builder urlBuilder = request.url().newBuilder();

                //遍历 map 将公共参数添加进去组成新的 url
                for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (key != null && value != null) {
                        urlBuilder.addQueryParameter(key, value);
                    }
                }

                //添加至 builder
                HttpUrl url = urlBuilder.build();
                requestBuilder.url(url);
            }
        }

        Map<String, String> headersMap = getPublicHeaders();    //公共头部
        if (headersMap != null && headersMap.size() > 0) {
            //遍历 map 添加至头部
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null && TextUtils.isEmpty(request.headers().get(key))) {
                    requestBuilder.addHeader(key, value);
                }
            }
        }

        //组成新的 request
        request = requestBuilder.build();

        Response response = chain.proceed(request);
        return response;
    }

    private static String bodyToString(final RequestBody requestBody) {
        try {
            final Buffer buffer = new Buffer();
            if (requestBody != null) {
                requestBody.writeTo(buffer);
                return buffer.readUtf8();
            } else {
                return "";
            }
        } catch (final IOException e) {
            LogUtil.e(e.getMessage());
            return "did not work";
        }
    }

    /**
     * 获取公共参数
     */
    public abstract Map<String, String> getPublicParams();

    /**
     * 获取公共头部
     */
    public abstract Map<String, String> getPublicHeaders();
}
