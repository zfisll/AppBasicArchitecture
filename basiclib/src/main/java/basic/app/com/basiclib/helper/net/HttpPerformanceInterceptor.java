package basic.app.com.basiclib.helper.net;

/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import basic.app.com.basiclib.utils.AppUtil;
import basic.app.com.basiclib.utils.DeviceUtil;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 从okhttp复制而来，用于后续对所有请求数据做统计分析时统一处理
 *
 * a stable logging format, use your own interceptor.
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * OkHttpClient#interceptors() application interceptor or as a
 * OkHttpClient#networkInterceptors() network interceptor. <p> The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 */
public final class HttpPerformanceInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         * <p>
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END GET
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public interface Logger {
        void log(String message);

        /**
         * A {@link HttpPerformanceInterceptor.Logger} defaults output appropriate for the current platform.
         */
        HttpPerformanceInterceptor.Logger DEFAULT = new HttpPerformanceInterceptor.Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(INFO, message, null);

            }
        };
    }

    public HttpPerformanceInterceptor() {
        this(HttpPerformanceInterceptor.Logger.DEFAULT);
    }

    public HttpPerformanceInterceptor(HttpPerformanceInterceptor.Logger logger) {
        this.logger = logger;
    }

    private final HttpPerformanceInterceptor.Logger logger;

    private volatile HttpPerformanceInterceptor.Level level = HttpPerformanceInterceptor.Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public HttpPerformanceInterceptor setLevel(HttpPerformanceInterceptor.Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public HttpPerformanceInterceptor.Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 以url作为上报事件id
        String label;
        String errNo = "-999";
        StringBuilder logOutput = new StringBuilder();
        HttpPerformanceInterceptor.Level level = this.level;
        long startRequestNs = System.nanoTime();
        Request request = chain.request();
        if (level == HttpPerformanceInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == HttpPerformanceInterceptor.Level.BODY;
        boolean logHeaders = logBody || level == HttpPerformanceInterceptor.Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        label = request.url().toString().split("\\?")[0];
//        logger.log(requestStartMessage);
        logOutput.append(requestStartMessage);
        logOutput.append("\n");
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    logOutput.append("Content-Type: ").append(requestBody.contentType());
                    logOutput.append("\n");
                }
                if (requestBody.contentLength() != -1) {
                    logOutput.append("Content-Length: ").append(requestBody.contentLength());
                    logOutput.append("\n");
                }

            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                //增加需要过滤的敏感header，目前主要是QUARTZ-SESSION
                if ("QUARTZ-SESSION".equalsIgnoreCase(name)) {
                    continue;
                }
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    logOutput.append(name).append(": ").append(headers.value(i));
                    logOutput.append("\n");
                }


            }

            if (hasRequestBody) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                String[] params = buffer.readString(charset).split("&");
                for (String param : params) {
                    if (!TextUtils.isEmpty(param) && param.contains("method") && param.contains("=")) {
                        label += param.split("=")[1];
                        break;
                    }
                }
            }

            if (!logBody || !hasRequestBody) {
                logOutput.append("--> END ").append(request.method());
                logOutput.append("\n");
            } else if (bodyEncoded(request.headers())) {
                logOutput.append("--> END ").append(request.method()).append(" (encoded body omitted)");
                logOutput.append("\n");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                String requestBodyStr = buffer.readString(charset);
                logOutput.append("");
                logOutput.append("\n");
                logOutput.append(requestBodyStr);
                logOutput.append("\n");

                logOutput.append("--> END ").append(request.method()).append(" (").append(requestBody.contentLength()).append("-byte body)");
                logOutput.append("\n");
            }
        }

        long startRespNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startRespNs);
        long allTimeCost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startRequestNs);
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        logOutput.append("<-- ").append(response.code()).append(' ').append(response.message()).append(' ').append(response.request().url()).append(" (读取耗时").append(tookMs).append("ms").append(!logHeaders ? ", "
                + bodySize + " body" : "").append(" 总耗时:").append(allTimeCost).append("ms").append(')');
        logOutput.append("\n");

        if (logHeaders) {
            if (contentLength != 0) {
                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();
                // 获取业务码
                try {
                    JSONObject jsonObject = new JSONObject(buffer.clone().readString(charset));
                    if (jsonObject.has("errno")) {
                        errNo = jsonObject.getString("errno");
                    } else if (jsonObject.has("code")) {
                        errNo = jsonObject.getString("code");
                    } else {
                        errNo = "-999";
                    }
                } catch (JSONException e) {
                    errNo = "-999";
                }
            }

            Headers headers = response.headers();
            logOutput.append("--> response\n");
            for (int i = 0, count = headers.size(); i < count; i++) {
                logOutput.append(headers.name(i)).append(": ").append(headers.value(i));
                logOutput.append("\n");
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                logOutput.append("<-- END HTTP");
                logOutput.append("\n");
            } else if (bodyEncoded(response.headers())) {
                logOutput.append("<-- END HTTP (encoded body omitted)");
                logOutput.append("\n");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        logOutput.append("");
                        logOutput.append("\n");
                        logOutput.append("Couldn't decode the response body; charset is likely malformed.");
                        logOutput.append("\n");
                        logOutput.append("<-- END HTTP");
                        logOutput.append("\n");
                        logger.log(logOutput.toString());
                        report(response, errNo, allTimeCost, label);
                        return response;
                    }
                }

                if (contentLength != 0) {
                    logOutput.append("");
                    logOutput.append("\n");
                    logOutput.append(buffer.clone().readString(charset));
                    logOutput.append("\n");
                }

                logOutput.append("<-- END HTTP (").append(buffer.size()).append("-byte body)");
                logOutput.append("\n");
                logger.log(logOutput.toString());
            }
        }
        // 接口成功率统计Map
        if (logHeaders) {
            report(response, errNo, allTimeCost, label);
        }
        return response;
    }

    /**
     * 上报接口成功率
     */
    private void report(Response response, String errNo, long allTimeCost, String label) {
        Map<String, String> kvMap = new HashMap<>();
        kvMap.put("network", DeviceUtil.getNetType(AppUtil.getApp()));
        kvMap.put("httpCode", String.valueOf(response.code()));
        kvMap.put("errNo", errNo);
        kvMap.put("timeCost", String.valueOf(allTimeCost));

        // fixme: 2018/8/28 此处不打印请求地址的域名，只打印接口名称，不同项目此处需要更换
        label.replaceFirst("http(s)?:\\/\\/[^.]++\\.bluestonehk\\.com", "");
        // 上报接口成功率统计，此处可以选择上传方式，TalkingData...
//        TCAgent.onEvent(AppUtil.getApp(), "App接口", label, kvMap);
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}

