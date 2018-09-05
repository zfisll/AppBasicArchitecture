package basic.app.com.basiclib.helper.net.download;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * author : user_zf
 * date : 2018/9/4
 * desc : 下载Service
 */
public interface DownloadService {

    /**
     * @param url 完整的下载地址,这种写法会忽略 Retrofit 中的 base_url
     * */
    @GET
    @Streaming
    Observable<ResponseBody> download(@Url String url);
}
