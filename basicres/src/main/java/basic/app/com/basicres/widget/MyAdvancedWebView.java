package basic.app.com.basicres.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import basic.app.com.basiclib.utils.AppUtil;
import basic.app.com.basiclib.utils.DeviceUtil;
import basic.app.com.basiclib.widget.AdvancedWebView;

/**
 * author : user_zf
 * date : 2018/9/3
 * desc : 自定义的WebView
 * todo : 需要设置自己公司的UserAgent
 */
public class MyAdvancedWebView extends AdvancedWebView {

    public MyAdvancedWebView(Context context) {
        super(context);
        initWebView(context);
    }

    public MyAdvancedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView(context);
    }

    public MyAdvancedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView(context);
    }

    private void initWebView(final Context context) {
        setUserAgent(this);
        setCookiesEnabled(true);       //存 cookie
//        setVerticalScrollBarEnabled(false);
//        setHorizontalScrollBarEnabled(false);
    }

    public static void setUserAgent(WebView webView){
        //app 内webview统一添加useragent标示,在原始值后面添加 空格+bluestone+空格+版本号
        String userAgent = webView.getSettings().getUserAgentString();
        userAgent = userAgent+" bluestone "+ DeviceUtil.getVersionName(AppUtil.getApp());
        webView.getSettings().setUserAgentString(userAgent);
    }
}
