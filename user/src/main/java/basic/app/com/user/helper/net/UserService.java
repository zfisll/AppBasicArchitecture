package basic.app.com.user.helper.net;

import basic.app.com.user.model.bean.NewsApiBean;
import basic.app.com.user.model.bean.UserBean;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * author : user_zf
 * date : 2018/8/28
 * desc : 定义user组件相关的网路请求接口，Retrofit解析该service
 */
public interface UserService {
    @POST(UserConfig.USER_LOGIN)
    @FormUrlEncoded
    Observable<BaseResponse<UserBean>> login(@Field("user_name") String user_name, @Field("password") String password, @Field("region_code") String regionCode);

    @POST(UserConfig.USER_IMPORTANT_NEWS)
    @FormUrlEncoded
    Observable<BaseResponse<NewsApiBean>> getImportantNews(@Field("count")int count, @Field("last_artid") String lastNewsId);

}
