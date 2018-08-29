package basic.app.com.user.helper.net;

import java.util.concurrent.TimeUnit;

import basic.app.com.basiclib.helper.net.Config;
import basic.app.com.basiclib.utils.OkHttpUtils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 用户组件对应的Retrofit
 */
public enum UserRetrofit {
    SINGLETON;

    private static final String TAG = "UserRetrofit";
    private UserService mService;

    UserRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Config.HTTP_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)   //交易下单这类接口不能重试
                .addInterceptor(new UserInterceptor())
                .addInterceptor(OkHttpUtils.getHttpInterceptor(TAG))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(UserConfig.HOST_USER_COMPONENT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mService = retrofit.create(UserService.class);
    }

    public UserService getService() {
        return mService;
    }
}
